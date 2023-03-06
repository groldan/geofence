package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.api.v2.model.AddressRangeFilter;
import org.geoserver.geofence.api.v2.model.AdminGrantType;
import org.geoserver.geofence.api.v2.model.IdName;
import org.geoserver.geofence.api.v2.model.SetFilter;
import org.geoserver.geofence.api.v2.model.TextFilter;
import org.geoserver.geofence.filter.AdminRuleFilter;
import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.predicate.FilterType;
import org.geoserver.geofence.filter.predicate.IdNameFilter;
import org.geoserver.geofence.filter.predicate.SpecialFilterType;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

import javax.validation.Valid;

public class RuleFilterApiMapper {

    public org.geoserver.geofence.api.v2.model.AdminRuleFilter map(AdminRuleFilter filter) {
        if (filter == null) return null;
        org.geoserver.geofence.api.v2.model.AdminRuleFilter api =
                new org.geoserver.geofence.api.v2.model.AdminRuleFilter();

        api.setGrantType(map(filter.getGrantType()));
        api.setInstance(idNameToApi(filter.getInstance()));
        api.setRoles(setFilterToApi(filter.getRole()));
        api.setSourceAddress(addressRangeToApi(filter.getSourceAddress()));
        api.setUser(textFilterToApi(filter.getUser()));
        api.setWorkspace(textFilterToApi(filter.getWorkspace()));
        return api;
    }

    public AdminRuleFilter map(org.geoserver.geofence.api.v2.model.AdminRuleFilter filter) {
        if (filter == null) return null;
        AdminRuleFilter model = new AdminRuleFilter();
        model.setGrantType(map(filter.getGrantType()));
        idNameToModel(model.getInstance(), filter.getInstance());
        setFilterToModel(model.getRole(), filter.getRoles());
        addressRangeToModel(model.getSourceAddress(), filter.getSourceAddress());
        textFilterToModel(model.getUser(), filter.getUser());
        textFilterToModel(model.getWorkspace(), filter.getWorkspace());
        return model;
    }

    private org.geoserver.geofence.adminrules.model.AdminGrantType map(
            JsonNullable<AdminGrantType> grantType) {

        if (grantType.isPresent() && grantType.get() != null) {
            AdminGrantType type = grantType.get();
            switch (type) {
                case ADMIN:
                    return org.geoserver.geofence.adminrules.model.AdminGrantType.ADMIN;
                case USER:
                    return org.geoserver.geofence.adminrules.model.AdminGrantType.USER;
                default:
                    throw new IllegalArgumentException("Unknown AdminGrantType: " + grantType);
            }
        }
        return null;
    }

    private JsonNullable<AdminGrantType> map(
            org.geoserver.geofence.adminrules.model.AdminGrantType grantType) {
        if (grantType == null) return JsonNullable.undefined();
        switch (grantType) {
            case ADMIN:
                return JsonNullable.of(AdminGrantType.ADMIN);
            case USER:
                return JsonNullable.of(AdminGrantType.USER);
            default:
                throw new IllegalArgumentException("Unknown AdminGrantType: " + grantType);
        }
    }

    public org.geoserver.geofence.api.v2.model.RuleFilter toApi(RuleFilter filter) {
        if (filter == null) return null;
        org.geoserver.geofence.api.v2.model.RuleFilter api =
                new org.geoserver.geofence.api.v2.model.RuleFilter();

        api.setInstance(idNameToApi(filter.getInstance()));
        api.setLayer(textFilterToApi(filter.getLayer()));
        api.setRoles(setFilterToApi(filter.getRole()));
        api.setRequest(textFilterToApi(filter.getRequest()));
        api.setService(textFilterToApi(filter.getService()));
        api.setSourceAddress(addressRangeToApi(filter.getSourceAddress()));
        api.setSubfield(textFilterToApi(filter.getSubfield()));
        api.setUser(textFilterToApi(filter.getUser()));
        api.setWorkspace(textFilterToApi(filter.getWorkspace()));
        return api;
    }

    public RuleFilter toModel(org.geoserver.geofence.api.v2.model.RuleFilter filter) {
        if (filter == null) return null;
        RuleFilter model = new RuleFilter();
        idNameToModel(model.getInstance(), filter.getInstance());
        textFilterToModel(model.getLayer(), filter.getLayer());
        textFilterToModel(model.getRequest(), filter.getRequest());
        setFilterToModel(model.getRole(), filter.getRoles());
        textFilterToModel(model.getService(), filter.getService());
        addressRangeToModel(model.getSourceAddress(), filter.getSourceAddress());
        textFilterToModel(model.getSubfield(), filter.getSubfield());
        textFilterToModel(model.getUser(), filter.getUser());
        textFilterToModel(model.getWorkspace(), filter.getWorkspace());
        return model;
    }

    private JsonNullable<org.geoserver.geofence.api.v2.model.SetFilter> setFilterToApi(
            org.geoserver.geofence.filter.predicate.InSetPredicate<String> filter) {

        switch (filter.getType()) {
            case DEFAULT:
                return JsonNullable.undefined();
            case ANY:
                return JsonNullable.of(new SetFilter().values(Set.of("*")));
            case NAMEVALUE:
                SetFilter value = new SetFilter().values(filter.getValues());
                if (!filter.isIncludeDefault()) {
                    value.includeDefault(filter.isIncludeDefault());
                }
                return JsonNullable.of(value);
            case IDVALUE:
            default:
                throw new IllegalArgumentException(
                        "Unexpected value type for TextFilter: " + filter.getType());
        }
    }

    private void setFilterToModel(
            org.geoserver.geofence.filter.predicate.InSetPredicate<String> target,
            JsonNullable<org.geoserver.geofence.api.v2.model.SetFilter> source) {

        if (!source.isPresent() || source.get() == null) return;

        SetFilter setFilter = source.get();
        if (setFilter.getIncludeDefault().isPresent()
                && null != setFilter.getIncludeDefault().get()) {
            target.setIncludeDefault(setFilter.getIncludeDefault().get().booleanValue());
        }
        if (setFilter.getValues().isPresent() && null != setFilter.getValues().get()) {
            Set<String> values = setFilter.getValues().get();
            if (values.contains("*")) {
                target.setType(SpecialFilterType.ANY);
            } else if (!values.isEmpty()) {
                target.setValues(values);
            }
        }
    }

    private JsonNullable<org.geoserver.geofence.api.v2.model.TextFilter> textFilterToApi(
            org.geoserver.geofence.filter.predicate.TextFilter filter) {

        switch (filter.getType()) {
            case DEFAULT:
                return JsonNullable.undefined();
            case ANY:
                return JsonNullable.of(new TextFilter().value("*"));
            case NAMEVALUE:
                TextFilter value = new TextFilter().value(filter.getText());
                if (!filter.isIncludeDefault()) {
                    value.includeDefault(filter.isIncludeDefault());
                }
                return JsonNullable.of(value);
            case IDVALUE:
            default:
                throw new IllegalArgumentException(
                        "Unexpected value type for TextFilter: " + filter.getType());
        }
    }

    private void textFilterToModel(
            org.geoserver.geofence.filter.predicate.TextFilter target,
            JsonNullable<org.geoserver.geofence.api.v2.model.TextFilter> source) {

        if (source.isPresent() && source.get() != null) {
            TextFilter textFilter = source.get();
            JsonNullable<Boolean> includeDefault = textFilter.getIncludeDefault();
            JsonNullable<String> value = textFilter.getValue();

            if (value.isPresent() && value.get() != null) {
                target.setHeuristically(value.get());
                if (includeDefault.isPresent() && includeDefault.get() != null)
                    target.setIncludeDefault(includeDefault.get());
            } else {
                if (includeDefault.isPresent()
                        && includeDefault.get() != null
                        && includeDefault.get().booleanValue())
                    target.setType(SpecialFilterType.DEFAULT);
                else target.setType(SpecialFilterType.ANY);
            }
        }
    }

    private JsonNullable<org.geoserver.geofence.api.v2.model.AddressRangeFilter> addressRangeToApi(
            org.geoserver.geofence.filter.predicate.IPAddressRangeFilter filter) {

        switch (filter.getType()) {
            case DEFAULT:
                return JsonNullable.undefined();
            case ANY:
                return JsonNullable.of(new AddressRangeFilter().value("*"));
            case NAMEVALUE:
                AddressRangeFilter value = new AddressRangeFilter().value(filter.getText());
                if (!filter.isIncludeDefault()) {
                    value.includeDefault(filter.isIncludeDefault());
                }
                return JsonNullable.of(value);
            case IDVALUE:
            default:
                throw new IllegalArgumentException(
                        "Unexpected value type for TextFilter: " + filter.getType());
        }
    }

    private void addressRangeToModel(
            org.geoserver.geofence.filter.predicate.IPAddressRangeFilter target,
            JsonNullable<org.geoserver.geofence.api.v2.model.AddressRangeFilter> source) {

        if (source.isPresent() && source.get() != null) {
            AddressRangeFilter addrFilter = source.get();
            JsonNullable<Boolean> includeDefault = addrFilter.getIncludeDefault();
            JsonNullable<String> value = addrFilter.getValue();

            if (value.isPresent() && value.get() != null) {
                target.setHeuristically(value.get());
                if (includeDefault.isPresent() && includeDefault.get() != null)
                    target.setIncludeDefault(includeDefault.get());
            } else {
                if (includeDefault.isPresent()
                        && includeDefault.get() != null
                        && includeDefault.get().booleanValue())
                    target.setType(SpecialFilterType.DEFAULT);
                else target.setType(SpecialFilterType.ANY);
            }
        }
    }

    private JsonNullable<IdName> idNameToApi(IdNameFilter source) {
        if (source.getType() == FilterType.DEFAULT) return JsonNullable.undefined();

        IdName idName = new IdName();
        if (!source.isIncludeDefault()) {
            // true is the default value, only set it if false
            idName.includeDefault(false);
        }
        if (source.getType() == FilterType.ANY) {
            idName.name("*");
        } else if (source.getType() == FilterType.IDVALUE) {
            idName.id(source.getId());
        } else if (source.getType() == FilterType.NAMEVALUE) {
            idName.name(source.getName());
        }
        return JsonNullable.of(idName);
    }

    private void idNameToModel(IdNameFilter target, @Valid JsonNullable<IdName> source) {
        if (source.isPresent() && source.get() != null) {
            IdName idName = source.get();
            JsonNullable<Boolean> includeDefault = idName.getIncludeDefault();
            JsonNullable<Long> id = idName.getId();
            JsonNullable<String> name = idName.getName();

            if (id.isPresent() && id.get() != null) {
                target.setId(id.get());
                if (includeDefault.isPresent() && includeDefault.get() != null)
                    target.setIncludeDefault(includeDefault.get());
            } else if (name.isPresent() && name.get() != null) {
                target.setHeuristically(name.get());
                if (includeDefault.isPresent() && includeDefault.get() != null)
                    target.setIncludeDefault(includeDefault.get());
            } else {
                if (includeDefault.isPresent()
                        && includeDefault.get() != null
                        && includeDefault.get().booleanValue())
                    target.setType(SpecialFilterType.DEFAULT);
                else target.setType(SpecialFilterType.ANY);
            }
        }
    }
}
