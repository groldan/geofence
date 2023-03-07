package org.geoserver.geofence.jpa.integration;

import static org.geoserver.geofence.jpa.integration.mapper.AdminRuleJpaMapper.decodeId;

import com.querydsl.core.types.Predicate;

import lombok.NonNull;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.adminrules.repository.AdminRuleIdentifierConflictException;
import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.filter.AdminRuleFilter;
import org.geoserver.geofence.filter.RuleQuery;
import org.geoserver.geofence.filter.predicate.IPAddressRangeFilter;
import org.geoserver.geofence.jpa.integration.mapper.AdminRuleJpaMapper;
import org.geoserver.geofence.jpa.model.QAdminRule;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.TransactionRequired;
import org.geoserver.geofence.jpa.repository.TransactionSupported;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@TransactionSupported
public class AdminRuleRepositoryJpaAdaptor implements AdminRuleRepository {

    private final JpaAdminRuleRepository jparepo;
    private final AdminRuleJpaMapper modelMapper;
    private final PredicateMapper queryMapper;
    private final PriorityResolver<org.geoserver.geofence.jpa.model.AdminRule> priorityResolver;

    public AdminRuleRepositoryJpaAdaptor(
            JpaAdminRuleRepository jparepo, AdminRuleJpaMapper mapper) {
        Objects.requireNonNull(jparepo);
        Objects.requireNonNull(mapper);
        this.modelMapper = mapper;
        this.jparepo = jparepo;
        this.queryMapper = new PredicateMapper();
        this.priorityResolver =
                new PriorityResolver<>(
                        jparepo, org.geoserver.geofence.jpa.model.AdminRule::getPriority);
    }

    @Override
    @TransactionRequired
    public AdminRule create(AdminRule rule, InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("Rule must have no id");
        if (rule.getPriority() < 0)
            throw new IllegalArgumentException(
                    "Negative priority is not allowed: " + rule.getPriority());

        final long finalPriority =
                priorityResolver.resolveFinalPriority(rule.getPriority(), position);

        org.geoserver.geofence.jpa.model.AdminRule entity = modelMapper.toEntity(rule);
        entity.setPriority(finalPriority);

        org.geoserver.geofence.jpa.model.AdminRule saved;
        try {
            // gotta use saveAndFlush to catch the exception before the method returns and the tx is
            // committed
            jparepo.flush();
            saved = jparepo.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new AdminRuleIdentifierConflictException(
                    "An AdminRule with the same identifier already exists: "
                            + rule.getIdentifier().toShortString(),
                    e);
        }
        return modelMapper.toModel(saved);
    }

    @Override
    public Optional<AdminRule> findById(@NonNull String id) {
        return jparepo.findById(decodeId(id).longValue()).map(modelMapper::toModel);
    }

    @Override
    public int count() {
        return (int) jparepo.count();
    }

    @Override
    public int count(AdminRuleFilter filter) {
        Optional<? extends Predicate> predicate = queryMapper.toPredicate(filter);
        if (predicate.isEmpty()) return (int) jparepo.count(predicate.get());
        return (int) jparepo.count();
    }

    @Override
    public Optional<AdminRule> findOne(AdminRuleFilter filter) {
        Predicate predicate =
                queryMapper
                        .toPredicate(filter)
                        .orElseThrow(
                                () -> new IllegalArgumentException("No filter predicate provided"));

        try {
            return jparepo.findOne(predicate).map(modelMapper::toModel);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("Filter matches more than one AdminRule", e);
        }
    }

    @Override
    public Optional<AdminRule> findOneByPriority(long priority) {
        Predicate predicate = QAdminRule.adminRule.priority.goe(priority);
        try {
            return jparepo.findOne(predicate).map(modelMapper::toModel);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("Filter matches more than one AdminRule", e);
        }
    }

    @Override
    public Optional<AdminRule> findFirst(AdminRuleFilter adminRuleFilter) {

        Optional<Predicate> predicate = queryMapper.toPredicate(adminRuleFilter);
        PageRequest first = PageRequest.of(0, 1);
        Page<org.geoserver.geofence.jpa.model.AdminRule> found;
        if (predicate.isPresent()) {
            found = jparepo.findAllNaturalOrder(predicate.get(), first);
        } else {
            found = jparepo.findAllNaturalOrder(first);
        }
        List<org.geoserver.geofence.jpa.model.AdminRule> contents = found.getContent();
        return Optional.ofNullable(contents.isEmpty() ? null : contents.get(0))
                .map(modelMapper::toModel);
    }

    @Override
    @TransactionRequired
    public AdminRule save(AdminRule rule) {
        Objects.requireNonNull(rule.getId());
        org.geoserver.geofence.jpa.model.AdminRule entity = getOrThrowIAE(rule.getId());

        long finalPriority =
                priorityResolver.resolvePriorityUpdate(entity.getPriority(), rule.getPriority());

        modelMapper.updateEntity(entity, rule);
        entity.setPriority(finalPriority);

        try {
            jparepo.flush();
            org.geoserver.geofence.jpa.model.AdminRule saved = jparepo.saveAndFlush(entity);
            return modelMapper.toModel(saved);
        } catch (DataIntegrityViolationException e) {
            throw new AdminRuleIdentifierConflictException(
                    "An AdminRule with the same identifier already exists: "
                            + rule.getIdentifier().toShortString(),
                    e);
        }
    }

    @Override
    public List<AdminRule> findAll() {
        return jparepo.findAllNaturalOrder().stream()
                .map(modelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminRule> findAll(@NonNull AdminRuleFilter filter) {
        return findAll(RuleQuery.of(filter));
    }

    @Override
    public List<AdminRule> findAll(RuleQuery<AdminRuleFilter> query) {
        Optional<? extends Predicate> predicate = queryMapper.toPredicate(query);
        Pageable pageRequest = queryMapper.toPageable(query);

        // REVISIT: if filter contains a non-any address range filter, can't apply paging to the db
        // query
        Page<org.geoserver.geofence.jpa.model.AdminRule> page;
        if (predicate.isPresent()) {
            page = jparepo.findAllNaturalOrder(predicate.get(), pageRequest);
        } else {
            page = jparepo.findAllNaturalOrder(pageRequest);
        }

        List<org.geoserver.geofence.jpa.model.AdminRule> found = page.getContent();
        return found.stream()
                .map(modelMapper::toModel)
                .filter(filterByAddress(query.getFilter()))
                .collect(Collectors.toList());
    }

    private java.util.function.Predicate<? super AdminRule> filterByAddress(
            Optional<AdminRuleFilter> filter) {
        if (filter.isEmpty()) return r -> true;
        IPAddressRangeFilter ipFilter = filter.get().getSourceAddress();

        return ipFilter.toIPAddressPredicate(r -> r.getIdentifier().getAddressRange());
    }

    @Override
    @TransactionRequired
    public int shiftPriority(long priorityStart, long offset) {
        return jparepo.shiftPriority(priorityStart, offset);
    }

    @Override
    @TransactionRequired
    public void swap(@NonNull String id1, @NonNull String id2) {
        org.geoserver.geofence.jpa.model.AdminRule rule1 = getOrThrowIAE(id1);
        org.geoserver.geofence.jpa.model.AdminRule rule2 = getOrThrowIAE(id2);

        long p1 = rule1.getPriority();
        long p2 = rule2.getPriority();

        rule1.setPriority(p2);
        rule2.setPriority(p1);

        jparepo.saveAll(List.of(rule1, rule2));
    }

    @Override
    @TransactionRequired
    public boolean deleteById(@NonNull String id) {
        return 1 == jparepo.deleteById(decodeId(id).longValue());
    }

    @Override
    @TransactionRequired
    public int delete(AdminRuleFilter filter) {
        Optional<? extends Predicate> predicate = queryMapper.toPredicate(filter);
        if (predicate.isPresent()) {

            List<org.geoserver.geofence.jpa.model.AdminRule> matches =
                    jparepo.findAllNaturalOrder(predicate.get());
            jparepo.deleteAll(matches);
            return matches.size();
        }
        throw new IllegalArgumentException(
                "A predicate must be provided, deleting all AdminRules is not allowed");
    }

    private org.geoserver.geofence.jpa.model.AdminRule getOrThrowIAE(@NonNull String ruleId) {
        org.geoserver.geofence.jpa.model.AdminRule rule;
        try {
            rule = jparepo.getReferenceById(decodeId(ruleId).longValue());
        } catch (EntityNotFoundException e) {
            throw new NoSuchElementException("AdminRule " + ruleId + " does not exist");
        }
        return rule;
    }
}
