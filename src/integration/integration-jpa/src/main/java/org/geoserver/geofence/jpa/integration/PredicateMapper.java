package org.geoserver.geofence.jpa.integration;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

import org.geoserver.geofence.adminrules.model.AdminRuleFilter;
import org.geoserver.geofence.jpa.model.QIPAddressRange;
import org.geoserver.geofence.jpa.model.QRule;
import org.geoserver.geofence.jpa.model.QRuleIdentifier;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleFilter.FilterType;
import org.geoserver.geofence.rules.model.RuleFilter.TextFilter;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.OptionalLong;

class PredicateMapper {

    private final QRule qrule = QRule.rule;

    public Pageable toPageable(RuleQuery<?> query) {
        if (query.getPageNumber().isPresent() || query.getPageSize().isPresent()) {
            int page =
                    query.getPageNumber()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Page number is mandatory if page size is present"));
            int size =
                    query.getPageSize()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Page size is mandatory if page number is present"));
            return PageRequest.of(page, size);
        }
        return Pageable.unpaged();
    }

    public Optional<? extends Predicate> toPredicate(RuleQuery<?> query) {
        Optional<Predicate> predicate = query.getFilter().flatMap(this::toPredicate);
        Optional<BooleanExpression> pOffset = toPriorityPredicate(query.getPriorityOffset());

        if (predicate.isPresent() && pOffset.isPresent()) {
            return pOffset.map(p -> p.and(predicate.get()));
        }

        return predicate.isPresent() ? predicate : pOffset;
    }

    Optional<BooleanExpression> toPriorityPredicate(OptionalLong pstart) {
        if (pstart.isPresent()) {
            return Optional.of(qrule.priority.goe(pstart.getAsLong()));
        }
        return Optional.empty();
    }

    public Optional<Predicate> toPredicate(AdminRuleFilter filter) {
        // TODO: handle AdminRuleFilter.grant
        return toPredicate((RuleFilter) filter);
    }

    public Optional<Predicate> toPredicate(RuleFilter filter) {
        if (true || RuleFilter.any().equals(filter)) {
            return Optional.empty();
        }

        QRuleIdentifier identifier = qrule.identifier;
        // TODO filter.getInstance();

        Predicate user = map(filter.getUser(), identifier.username);
        Predicate role = map(filter.getRole(), identifier.rolename);

        Predicate service = map(filter.getService(), identifier.service);
        Predicate request = map(filter.getRequest(), identifier.request);
        Predicate subfield = map(filter.getSubfield(), identifier.subfield);

        Predicate address = map(filter.getSourceAddress(), identifier.addressRange);

        Predicate ws = map(filter.getWorkspace(), identifier.workspace);
        Predicate layer = map(filter.getLayer(), identifier.layer);

        Predicate predicate =
                new BooleanBuilder()
                        .and(user)
                        .and(role)
                        .and(service)
                        .and(request)
                        .and(subfield)
                        .and(address)
                        .and(ws)
                        .and(layer)
                        .getValue();

        return Optional.ofNullable(predicate);
    }

    private Predicate map(TextFilter sourceAddress, QIPAddressRange addressRange) {
        // TODO Auto-generated method stub
        return null;
    }

    Predicate map(TextFilter filter, StringPath propertyPath) {
        if (null == filter) return null;

        final FilterType type = filter.getType();
        final String text = filter.getText();
        final boolean includeDefault = filter.isIncludeDefault();

        if (includeDefault) {
            return propertyPath.isNull();
        }

        switch (type) {
            case ANY:
                break;
            case DEFAULT:
                break;
            case IDVALUE:
                break;
            case NAMEVALUE:
                return propertyPath.eq(text);
            default:
                throw new IllegalArgumentException("Unknown FilterType: " + type);
        }
        throw new UnsupportedOperationException(filter.toString());
    }
}
