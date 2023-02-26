package org.geoserver.geofence.jpa.integration;

import com.querydsl.core.types.Predicate;

import lombok.NonNull;

import org.geoserver.geofence.jpa.integration.mapper.RuleMapper;
import org.geoserver.geofence.jpa.model.GrantType;
import org.geoserver.geofence.jpa.model.LayerDetails;
import org.geoserver.geofence.jpa.model.RuleIdentifier;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.jpa.repository.TransactionReadOnly;
import org.geoserver.geofence.jpa.repository.TransactionRequired;
import org.geoserver.geofence.jpa.repository.TransactionSupported;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

@TransactionSupported
public class RuleRepositoryJPAAdaptor implements RuleRepository {

    private final JpaRuleRepository jparepo;
    private final RuleMapper modelMapper;
    private final PredicateMapper queryMapper;

    public RuleRepositoryJPAAdaptor(JpaRuleRepository jparepo, RuleMapper mapper) {
        Objects.requireNonNull(jparepo);
        Objects.requireNonNull(mapper);
        this.modelMapper = mapper;
        this.jparepo = jparepo;
        this.queryMapper = new PredicateMapper();
    }

    public Optional<Rule> findById(long id) {
        return jparepo.findById(id).map(modelMapper::toModel);
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
        return found.stream().map(modelMapper::toModel);
    }

    @Override
    @TransactionRequired
    public Rule save(Rule rule) {
        Objects.requireNonNull(rule.getId());
        org.geoserver.geofence.jpa.model.Rule entity = getOrThrow(rule.getId());
        modelMapper.updateEntity(entity, rule);
        org.geoserver.geofence.jpa.model.Rule saved = jparepo.save(entity);
        return modelMapper.toModel(saved);
    }

    @Override
    @TransactionRequired
    public Rule create(Rule rule, InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("Rule must have no id");
        if (null == position) position = InsertPosition.FIXED;

        if (InsertPosition.FIXED != position)
            throw new UnsupportedOperationException("implement insert position");

        org.geoserver.geofence.jpa.model.Rule entity = modelMapper.toEntity(rule);
        org.geoserver.geofence.jpa.model.Rule saved = jparepo.save(entity);
        return modelMapper.toModel(saved);
    }

    @Override
    @TransactionRequired
    public boolean delete(long id) {
        return jparepo.deleteById(id);
    }

    @Override
    public boolean existsById(long id) {
        return jparepo.existsById(id);
    }

    @Override
    @TransactionRequired
    public int shift(long priorityStart, long offset) {
        return jparepo.shiftPriority(priorityStart, offset);
    }

    @Override
    @TransactionRequired
    public void swap(long id1, long id2) {

        org.geoserver.geofence.jpa.model.Rule rule1 = getOrThrow(id1);
        org.geoserver.geofence.jpa.model.Rule rule2 = getOrThrow(id2);

        long p1 = rule1.getPriority();
        long p2 = rule2.getPriority();

        rule1.setPriority(p2);
        rule2.setPriority(p1);

        jparepo.saveAll(List.of(rule1, rule2));
    }

    @Override
    @TransactionRequired
    public void setAllowedStyles(@NonNull Long ruleId, Set<String> styles) {

        org.geoserver.geofence.jpa.model.Rule rule = getOrThrow(ruleId);

        if (RuleIdentifier.ANY.equals(rule.getIdentifier().getLayer())) {
            throw new IllegalArgumentException("Rule has no layer, can't set allowed styles");
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
    public void setLimits(Long ruleId, RuleLimits limits) {
        org.geoserver.geofence.jpa.model.Rule rule = getOrThrow(ruleId);
        if (rule.getIdentifier().getAccess() != GrantType.LIMIT) {
            throw new IllegalArgumentException("Rule is not of LIMIT type");
        }

        rule.setRuleLimits(modelMapper.toEntity(limits));

        jparepo.save(rule);
    }

    @Override
    @TransactionRequired
    public void setLayerDetails(
            Long ruleId, org.geoserver.geofence.rules.model.LayerDetails detailsNew) {

        org.geoserver.geofence.jpa.model.Rule rule = getOrThrow(ruleId);

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
            long ruleId) {

        org.geoserver.geofence.jpa.model.Rule jparule = getOrThrow(ruleId);

        if (RuleIdentifier.ANY.equals(jparule.getIdentifier().getLayer())) {
            throw new IllegalArgumentException("Rule " + ruleId + " has not layer set");
        }

        LayerDetails jpadetails = jparule.getLayerDetails();
        if (jpadetails.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(modelMapper.toModel(jpadetails));
    }

    private org.geoserver.geofence.jpa.model.Rule getOrThrow(Long ruleId) {
        org.geoserver.geofence.jpa.model.Rule rule;
        try {
            rule = jparepo.getReferenceById(ruleId);
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Rule " + ruleId + " does not exist");
        }
        return rule;
    }
}
