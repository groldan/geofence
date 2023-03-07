package org.geoserver.geofence.jpa.integration;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.StringPath;

import lombok.extern.slf4j.Slf4j;

import org.geoserver.geofence.adminrules.model.AdminGrantType;
import org.geoserver.geofence.filter.AdminRuleFilter;
import org.geoserver.geofence.filter.Filter;
import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.RuleQuery;
import org.geoserver.geofence.filter.predicate.FilterType;
import org.geoserver.geofence.filter.predicate.IdNameFilter;
import org.geoserver.geofence.filter.predicate.InSetPredicate;
import org.geoserver.geofence.filter.predicate.TextFilter;
import org.geoserver.geofence.jpa.model.GeoServerInstance;
import org.geoserver.geofence.jpa.model.QAdminRule;
import org.geoserver.geofence.jpa.model.QAdminRuleIdentifier;
import org.geoserver.geofence.jpa.model.QGeoServerInstance;
import org.geoserver.geofence.jpa.model.QRule;
import org.geoserver.geofence.jpa.model.QRuleIdentifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class PredicateMapper {

    public Pageable toPageable(RuleQuery<?> query) {
        if (query.pageNumber().isPresent() || query.pageSize().isPresent()) {
            int page =
                    query.pageNumber()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Page number is mandatory if page size is present"));
            int size =
                    query.pageSize()
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
            return Optional.of(QRule.rule.priority.goe(pstart.getAsLong()));
        }
        return Optional.empty();
    }

    public Optional<Predicate> toPredicate(AdminRuleFilter filter) {
        if (AdminRuleFilter.any().equals(filter)) {
            return Optional.empty();
        }

        QAdminRuleIdentifier qIdentifier = QAdminRule.adminRule.identifier;

        Predicate grantType = map(filter.getGrantType(), QAdminRule.adminRule.access);
        Predicate gsInstance = map(filter.getInstance(), qIdentifier.instance);
        Predicate user = map(filter.getUser(), qIdentifier.username);
        Predicate role = map(filter.getRole(), qIdentifier.rolename);
        // Predicate address = map(filter.getSourceAddress(), identifier.addressRange);
        Predicate ws = map(filter.getWorkspace(), qIdentifier.workspace);
        Predicate predicate =
                new BooleanBuilder()
                        .and(grantType)
                        .and(gsInstance)
                        .and(user)
                        .and(role)
                        // .and(address)
                        .and(ws)
                        .getValue();

        log.trace("Filter    : {}", filter);
        log.trace("Predicate : {}", predicate);
        return Optional.ofNullable(predicate);
    }

    private Predicate map(IdNameFilter filter, QGeoServerInstance qinstance) {
        switch (filter.getType()) {
            case ANY:
                return null;
            case DEFAULT:
                return qinstance.name.eq(GeoServerInstance.ANY);
            case IDVALUE:
                return filter.isIncludeDefault()
                        ? qinstance
                                .name
                                .eq(GeoServerInstance.ANY)
                                .or(qinstance.id.eq(filter.getId()))
                        : qinstance.id.eq(filter.getId());
            case NAMEVALUE:
                return filter.isIncludeDefault()
                        ? qinstance.name.in(GeoServerInstance.ANY, filter.getName())
                        : qinstance.name.eq(filter.getName());
            default:
                throw new IllegalStateException();
        }
    }

    private Predicate map(
            AdminGrantType grantType,
            EnumPath<org.geoserver.geofence.jpa.model.AdminGrantType> access) {

        if (null == grantType) return null;
        switch (grantType) {
            case ADMIN:
                return access.eq(org.geoserver.geofence.jpa.model.AdminGrantType.ADMIN);
            case USER:
                return access.eq(org.geoserver.geofence.jpa.model.AdminGrantType.USER);
            default:
                throw new IllegalArgumentException("Unknown AdminGrantType: " + grantType);
        }
    }

    Optional<Predicate> toPredicate(Filter filter) {
        if (filter instanceof RuleFilter) return toPredicate((RuleFilter) filter);
        if (filter instanceof AdminRuleFilter) return toPredicate((AdminRuleFilter) filter);
        return Optional.empty();
    }

    public Optional<Predicate> toPredicate(RuleFilter filter) {
        if (RuleFilter.any().equals(filter)) {
            return Optional.empty();
        }

        QRuleIdentifier qIdentifier = QRule.rule.identifier;

        Predicate gsInstance = map(filter.getInstance(), qIdentifier.instance);
        Predicate user = map(filter.getUser(), qIdentifier.username);
        Predicate role = map(filter.getRole(), qIdentifier.rolename);

        Predicate service = map(filter.getService(), qIdentifier.service);
        Predicate request = map(filter.getRequest(), qIdentifier.request);
        Predicate subfield = map(filter.getSubfield(), qIdentifier.subfield);

        // Predicate address = map(filter.getSourceAddress(), identifier.addressRange);

        Predicate ws = map(filter.getWorkspace(), qIdentifier.workspace);
        Predicate layer = map(filter.getLayer(), qIdentifier.layer);

        Predicate predicate =
                new BooleanBuilder()
                        .and(gsInstance)
                        .and(user)
                        .and(role)
                        .and(service)
                        .and(request)
                        .and(subfield)
                        // .and(address)
                        .and(ws)
                        .and(layer)
                        .getValue();

        log.trace("Filter    : {}", filter);
        log.trace("Predicate : {}", predicate);
        return Optional.ofNullable(predicate);
    }

    Predicate map(TextFilter filter, StringPath propertyPath) {
        if (null == filter) return null;

        final FilterType type = filter.getType();

        final boolean includeDefault = filter.isIncludeDefault();

        switch (type) {
            case ANY:
                return null;
            case DEFAULT:
                return propertyPath.eq("*");
            case NAMEVALUE:
                {
                    final String text = filter.getText();
                    if (text == null)
                        throw new IllegalArgumentException(
                                "Can't map TextFilter with empty value " + text);

                    if (includeDefault) {
                        return propertyPath.in("*", text);
                    }
                    return propertyPath.eq(text);
                }
            case IDVALUE:
            default:
                throw new IllegalArgumentException(
                        "Unknown or unexpected FilterType for TextFilter: " + type);
        }
    }

    Predicate map(InSetPredicate<String> filter, StringPath propertyPath) {
        if (null == filter) return null;

        final FilterType type = filter.getType();

        final boolean includeDefault = filter.isIncludeDefault();

        switch (type) {
            case ANY:
                return null;
            case DEFAULT:
                return propertyPath.eq("*");
            case NAMEVALUE:
                {
                    final Set<String> values = filter.getValues();
                    if (values == null || values.isEmpty())
                        throw new IllegalArgumentException(
                                "Can't map TextFilter with empty value " + values);

                    if (includeDefault) {
                        return propertyPath.in(
                                Stream.concat(Stream.of("*"), values.stream())
                                        .collect(Collectors.toList()));
                    }
                    return propertyPath.in(values);
                }
            case IDVALUE:
            default:
                throw new IllegalArgumentException(
                        "Unknown or unexpected FilterType for TextFilter: " + type);
        }
    }
}
