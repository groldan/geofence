package org.geoserver.geofence.jpa.integration;

import static org.geoserver.geofence.jpa.integration.mapper.RuleJpaMapper.decodeId;

import com.querydsl.core.types.Predicate;

import lombok.NonNull;

import org.geoserver.geofence.jpa.integration.mapper.RuleJpaMapper;
import org.geoserver.geofence.jpa.model.GrantType;
import org.geoserver.geofence.jpa.model.LayerDetails;
import org.geoserver.geofence.jpa.model.QRule;
import org.geoserver.geofence.jpa.model.RuleIdentifier;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.jpa.repository.TransactionReadOnly;
import org.geoserver.geofence.jpa.repository.TransactionRequired;
import org.geoserver.geofence.jpa.repository.TransactionSupported;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleFilter.TextFilter;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.geoserver.geofence.rules.repository.RuleIdentifierConflictException;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

@TransactionSupported
public class RuleRepositoryJpaAdaptor implements RuleRepository {

    private final JpaRuleRepository jparepo;
    private final RuleJpaMapper modelMapper;
    private final PredicateMapper queryMapper;

    private final PriorityResolver<org.geoserver.geofence.jpa.model.Rule> priorityResolver;

    public RuleRepositoryJpaAdaptor(JpaRuleRepository jparepo, RuleJpaMapper mapper) {
        Objects.requireNonNull(jparepo);
        Objects.requireNonNull(mapper);
        this.modelMapper = mapper;
        this.jparepo = jparepo;
        this.queryMapper = new PredicateMapper();
        this.priorityResolver =
                new PriorityResolver<>(jparepo, org.geoserver.geofence.jpa.model.Rule::getPriority);
    }

    @Override
    public Optional<Rule> findById(@NonNull String id) {
        return jparepo.findById(decodeId(id)).map(modelMapper::toModel);
    }

    @Override
    public Optional<Rule> findByPriority(long priority) {
        try {
            return jparepo.findOne(QRule.rule.priority.eq(priority)).map(modelMapper::toModel);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException("There are multiple Rules with priority " + priority);
        }
    }

    @Override
    public int count() {
        return (int) jparepo.count();
    }

    @Override
    public int count(RuleFilter filter) {
        Optional<Predicate> predicate = queryMapper.toPredicate(filter);
        Long count = predicate.map(jparepo::count).orElseGet(jparepo::count);
        return count.intValue();
    }

    @Override
    public Stream<Rule> findAll() {
        return jparepo.findAllNaturalOrder().stream().map(modelMapper::toModel);
    }

    @Override
    public Stream<Rule> query(@NonNull RuleQuery<RuleFilter> query) {

        Optional<? extends Predicate> predicate = queryMapper.toPredicate(query);
        Pageable pageRequest = queryMapper.toPageable(query);

        Page<org.geoserver.geofence.jpa.model.Rule> page;
        if (predicate.isPresent()) {
            page = jparepo.findAllNaturalOrder(predicate.get(), pageRequest);
        } else {
            page = jparepo.findAllNaturalOrder(pageRequest);
        }

        List<org.geoserver.geofence.jpa.model.Rule> found = page.getContent();
        return found.stream().map(modelMapper::toModel).filter(filterByAddress(query.getFilter()));
    }

    private java.util.function.Predicate<? super Rule> filterByAddress(
            Optional<RuleFilter> filter) {
        if (filter.isEmpty()) return r -> true;
        TextFilter textFilter = filter.get().getSourceAddress();

        return textFilter.toIPAddressPredicate(r -> r.getIdentifier().getAddressRange());
    }

    @Override
    @TransactionRequired
    public Rule save(Rule rule) {
        Objects.requireNonNull(rule.getId());
        org.geoserver.geofence.jpa.model.Rule entity = getOrThrowIAE(rule.getId());

        long finalPriority =
                priorityResolver.resolvePriorityUpdate(entity.getPriority(), rule.getPriority());

        modelMapper.updateEntity(entity, rule);
        entity.setPriority(finalPriority);
        checkForDups(entity);

        org.geoserver.geofence.jpa.model.Rule saved = jparepo.save(entity);
        return modelMapper.toModel(saved);
    }

    @Override
    @TransactionRequired
    public Rule create(@NonNull Rule rule, @NonNull InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("Rule must have no id");
        if (rule.getPriority() < 0)
            throw new IllegalArgumentException(
                    "Negative priority is not allowed: " + rule.getPriority());

        final long finalPriority =
                priorityResolver.resolveFinalPriority(rule.getPriority(), position);

        org.geoserver.geofence.jpa.model.Rule entity = modelMapper.toEntity(rule);
        entity.setPriority(finalPriority);
        checkForDups(entity);

        org.geoserver.geofence.jpa.model.Rule saved = jparepo.save(entity);

        return modelMapper.toModel(saved);
    }

    private void checkForDups(org.geoserver.geofence.jpa.model.Rule rule) {
        if (rule.getIdentifier().getAccess() == GrantType.LIMIT) {
            return;
        }

        RuleIdentifier identifier = rule.getIdentifier();
        List<org.geoserver.geofence.jpa.model.Rule> matches =
                jparepo.findAllByIdentifier(identifier);
        matches.stream()
                .filter(r -> !r.getId().equals(rule.getId()))
                .findFirst()
                .ifPresent(
                        dup -> {
                            throw new RuleIdentifierConflictException(
                                    "A Rule with the same identifier already exists: "
                                            + rule.getIdentifier().toShortString());
                        });
    }

    @Override
    @TransactionRequired
    public boolean delete(@NonNull String id) {
        return 1 == jparepo.deleteById(decodeId(id).longValue());
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return jparepo.existsById(decodeId(id));
    }

    @Override
    @TransactionRequired
    public int shift(long priorityStart, long offset) {
        if (offset <= 0) {
            throw new IllegalArgumentException("Positive offset required");
        }
        int affectedCount = jparepo.shiftPriority(priorityStart, offset);
        return affectedCount > 0 ? affectedCount : -1;
    }

    @Override
    @TransactionRequired
    public void swap(String id1, String id2) {

        org.geoserver.geofence.jpa.model.Rule rule1 = getOrThrowIAE(id1);
        org.geoserver.geofence.jpa.model.Rule rule2 = getOrThrowIAE(id2);

        long p1 = rule1.getPriority();
        long p2 = rule2.getPriority();

        rule1.setPriority(p2);
        rule2.setPriority(p1);

        jparepo.saveAll(List.of(rule1, rule2));
    }

    @Override
    @TransactionRequired
    public void setAllowedStyles(@NonNull String ruleId, Set<String> styles) {

        org.geoserver.geofence.jpa.model.Rule rule = getOrThrowIAE(ruleId);

        if (RuleIdentifier.ANY.equals(rule.getIdentifier().getLayer())) {
            throw new IllegalArgumentException("Rule has no layer, can't set allowed styles");
        }
        if (rule.getLayerDetails() == null || rule.getLayerDetails().isEmpty()) {
            throw new IllegalArgumentException("Rule has no details associated");
        }

        LayerDetails layerDetails = rule.getLayerDetails();
        layerDetails.getAllowedStyles().clear();
        if (styles != null && !styles.isEmpty()) {
            layerDetails.getAllowedStyles().addAll(styles);
        }
        jparepo.save(rule);
    }

    @Override
    @TransactionRequired
    public void setLimits(String ruleId, RuleLimits limits) {
        org.geoserver.geofence.jpa.model.Rule rule = getOrThrowIAE(ruleId);
        if (limits != null && rule.getIdentifier().getAccess() != GrantType.LIMIT) {
            throw new IllegalArgumentException("Rule is not of LIMIT type");
        }

        rule.setRuleLimits(modelMapper.toEntity(limits));

        jparepo.save(rule);
    }

    @Override
    @TransactionRequired
    public void setLayerDetails(
            String ruleId, org.geoserver.geofence.rules.model.LayerDetails detailsNew) {

        org.geoserver.geofence.jpa.model.Rule rule = getOrThrowIAE(ruleId);

        if (rule.getIdentifier().getAccess() != GrantType.ALLOW && detailsNew != null)
            throw new IllegalArgumentException("Rule is not of ALLOW type");

        if (RuleIdentifier.ANY.equals(rule.getIdentifier().getLayer()) && detailsNew != null)
            throw new IllegalArgumentException("Rule does not refer to a fixed layer");

        LayerDetails details = modelMapper.toEntity(detailsNew);
        rule.setLayerDetails(details);
        jparepo.save(rule);
    }

    @Override
    @TransactionReadOnly
    public Optional<org.geoserver.geofence.rules.model.LayerDetails> findLayerDetailsByRuleId(
            @NonNull String ruleId) {

        org.geoserver.geofence.jpa.model.Rule jparule = getOrThrowIAE(ruleId);

        // if (RuleIdentifier.ANY.equals(jparule.getIdentifier().getLayer())) {
        // throw new IllegalArgumentException("Rule " + ruleId + " has not layer set");
        // }

        LayerDetails jpadetails = jparule.getLayerDetails();
        if (jpadetails.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(modelMapper.toModel(jpadetails));
    }

    private org.geoserver.geofence.jpa.model.Rule getOrThrowIAE(@NonNull String ruleId) {
        org.geoserver.geofence.jpa.model.Rule rule;
        try {
            rule = jparepo.getReferenceById(decodeId(ruleId));
            rule.getIdentifier().getLayer();
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Rule " + ruleId + " does not exist");
        }
        return rule;
    }
}
