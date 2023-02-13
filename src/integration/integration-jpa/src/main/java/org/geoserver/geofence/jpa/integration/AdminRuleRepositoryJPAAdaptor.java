package org.geoserver.geofence.jpa.integration;

import com.querydsl.core.types.Predicate;

import lombok.NonNull;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.adminrules.model.AdminRuleFilter;
import org.geoserver.geofence.adminrules.persistence.AdminRuleRepository;
import org.geoserver.geofence.jpa.integration.mapper.AdminRuleMapper;
import org.geoserver.geofence.jpa.model.QAdminRule;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Transactional(
        transactionManager = "geofenceTransactionManager",
        propagation = Propagation.SUPPORTS)
public class AdminRuleRepositoryJPAAdaptor implements AdminRuleRepository {

    private final JpaAdminRuleRepository jparepo;
    private final AdminRuleMapper modelMapper;
    private final PredicateMapper queryMapper;

    public AdminRuleRepositoryJPAAdaptor(JpaAdminRuleRepository jparepo, AdminRuleMapper mapper) {
        Objects.requireNonNull(jparepo);
        Objects.requireNonNull(mapper);
        this.modelMapper = mapper;
        this.jparepo = jparepo;
        this.queryMapper = new PredicateMapper();
    }

    @Override
    @Transactional(
            transactionManager = "geofenceTransactionManager",
            propagation = Propagation.REQUIRED)
    public AdminRule create(AdminRule rule, InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("Rule must have no id");
        if (null == position) position = InsertPosition.FIXED;

        if (InsertPosition.FIXED != position)
            throw new UnsupportedOperationException("implement insert position");

        org.geoserver.geofence.jpa.model.AdminRule entity = modelMapper.toEntity(rule);
        org.geoserver.geofence.jpa.model.AdminRule saved = jparepo.save(entity);
        return modelMapper.toModel(saved);
    }

    @Override
    public Optional<AdminRule> findById(long id) {
        return jparepo.findById(id).map(modelMapper::toModel);
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
    public List<AdminRule> findAll(@NonNull AdminRuleFilter filter) {

        Optional<? extends Predicate> predicate = queryMapper.toPredicate(filter);

        List<org.geoserver.geofence.jpa.model.AdminRule> found;
        if (predicate.isPresent()) {
            found = jparepo.findAllNaturalOrder(predicate.get());
        } else {
            found = jparepo.findAllNaturalOrder();
        }

        return found.stream().map(modelMapper::toModel).collect(Collectors.toList());
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
    @Transactional(
            transactionManager = "geofenceTransactionManager",
            propagation = Propagation.REQUIRED)
    public AdminRule save(AdminRule rule) {
        Objects.requireNonNull(rule.getId());
        org.geoserver.geofence.jpa.model.AdminRule entity = modelMapper.toEntity(rule);
        org.geoserver.geofence.jpa.model.AdminRule saved = jparepo.save(entity);
        return modelMapper.toModel(saved);
    }

    @Override
    public List<AdminRule> findAll() {
        return jparepo.findAllNaturalOrder().stream()
                .map(modelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminRule> findAll(RuleQuery<AdminRuleFilter> query) {
        Optional<? extends Predicate> predicate = queryMapper.toPredicate(query);
        Pageable pageRequest = queryMapper.toPageable(query);

        Page<org.geoserver.geofence.jpa.model.AdminRule> page;
        if (predicate.isPresent()) {
            page = jparepo.findAllNaturalOrder(predicate.get(), pageRequest);
        } else {
            page = jparepo.findAllNaturalOrder(pageRequest);
        }

        List<org.geoserver.geofence.jpa.model.AdminRule> found = page.getContent();
        return found.stream().map(modelMapper::toModel).collect(Collectors.toList());
    }

    @Override
    @Transactional(
            transactionManager = "geofenceTransactionManager",
            propagation = Propagation.REQUIRED)
    public int shiftPriority(long priorityStart, long offset) {
        return jparepo.shiftPriority(priorityStart, offset);
    }

    @Override
    @Transactional(
            transactionManager = "geofenceTransactionManager",
            propagation = Propagation.REQUIRED)
    public void swap(long id1, long id2) {
        org.geoserver.geofence.jpa.model.AdminRule rule1 = getOrThrow(id1);
        org.geoserver.geofence.jpa.model.AdminRule rule2 = getOrThrow(id2);

        long p1 = rule1.getPriority();
        long p2 = rule2.getPriority();

        rule1.setPriority(p2);
        rule2.setPriority(p1);

        jparepo.saveAll(List.of(rule1, rule2));
    }

    @Override
    @Transactional(
            transactionManager = "geofenceTransactionManager",
            propagation = Propagation.REQUIRED)
    public boolean deleteById(long id) {
        return jparepo.deleteById(id);
    }

    @Override
    @Transactional(
            transactionManager = "geofenceTransactionManager",
            propagation = Propagation.REQUIRED)
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

    private org.geoserver.geofence.jpa.model.AdminRule getOrThrow(Long ruleId) {
        org.geoserver.geofence.jpa.model.AdminRule rule;
        try {
            rule = jparepo.getReferenceById(ruleId);
        } catch (EntityNotFoundException e) {
            throw new NoSuchElementException("AdminRule " + ruleId + " does not exist");
        }
        return rule;
    }
}
