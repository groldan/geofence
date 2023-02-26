package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleIdentifier;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        //        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
            JsonNullableMapper.class, EnumsApiMapper.class, OptionalApiMapper.class,
            LayerDetailsApiMapper.class, GeometryApiMapper.class, RuleLimitsApiMapper.class,
            IPAddressRangeApiMapper.class
        })
public abstract class RuleApiMapper {

    private @Autowired RuleLimitsApiMapper limitsMapper;
    private @Autowired LayerDetailsApiMapper detailsMapper;

    @Mapping(target = "identifier.instanceName", source = "instancename")
    @Mapping(target = "identifier.access", source = "access")
    @Mapping(target = "identifier.username", source = "userName")
    @Mapping(target = "identifier.rolename", source = "roleName")
    @Mapping(target = "identifier.service", source = "service")
    @Mapping(target = "identifier.request", source = "request")
    @Mapping(target = "identifier.subfield", source = "subfield")
    @Mapping(target = "identifier.workspace", source = "workspace")
    @Mapping(target = "identifier.layer", source = "layer")
    @Mapping(target = "identifier.addressRange", source = "addressRange")
    @Mapping(target = "ruleLimits", source = "limits")
    public abstract org.geoserver.geofence.rules.model.Rule toModel(
            org.geoserver.geofence.api.v2.model.Rule rule);

    @Mapping(target = "instancename", source = "identifier.instanceName")
    @Mapping(target = "access", source = "identifier.access")
    @Mapping(target = "userName", source = "identifier.username")
    @Mapping(target = "roleName", source = "identifier.rolename")
    @Mapping(target = "service", source = "identifier.service")
    @Mapping(target = "request", source = "identifier.request")
    @Mapping(target = "subfield", source = "identifier.subfield")
    @Mapping(target = "workspace", source = "identifier.workspace")
    @Mapping(target = "layer", source = "identifier.layer")
    @Mapping(target = "addressRange", source = "identifier.addressRange")
    @Mapping(target = "limits", source = "ruleLimits")
    public abstract org.geoserver.geofence.api.v2.model.Rule toApi(
            org.geoserver.geofence.rules.model.Rule rule);

    public Rule patch(Rule target, org.geoserver.geofence.api.v2.model.Rule source) {

        RuleIdentifier identifier = updateIdentifier(target.getIdentifier().toBuilder(), source);

        RuleLimits limits = patchLimits(target.getRuleLimits(), source.getLimits());

        org.geoserver.geofence.rules.model.Rule patched = updateEntity(target.toBuilder(), source);

        return patched.withIdentifier(identifier).withRuleLimits(limits);
    }

    private RuleLimits patchLimits(
            RuleLimits target,
            @Valid JsonNullable<org.geoserver.geofence.api.v2.model.RuleLimits> limits) {

        org.geoserver.geofence.api.v2.model.RuleLimits patch = limits.orElse(null);
        if (null != patch) {
            RuleLimits.Builder builder = target == null ? RuleLimits.builder() : target.toBuilder();
            target = limitsMapper.updateLimits(builder, patch);
        }
        return target;
    }

    public LayerDetails patchLayerDetails(
            LayerDetails target,
            @Valid JsonNullable<org.geoserver.geofence.api.v2.model.LayerDetails> source) {

        org.geoserver.geofence.api.v2.model.LayerDetails patch = source.orElse(null);
        if (null != patch) {
            LayerDetails.Builder detailsBuilder =
                    target == null ? LayerDetails.builder() : target.toBuilder();
            target = detailsMapper.updateDetails(detailsBuilder, patch);
        }
        return target;
    }

    @Mapping(target = "identifier", ignore = true)
    @Mapping(target = "ruleLimits", ignore = true)
    abstract Rule updateEntity(
            @MappingTarget Rule.Builder entity, org.geoserver.geofence.api.v2.model.Rule dto);

    @Mapping(target = "instanceName", source = "instancename")
    @Mapping(target = "username", source = "userName")
    @Mapping(target = "rolename", source = "roleName")
    abstract RuleIdentifier updateIdentifier(
            @MappingTarget RuleIdentifier.Builder entity,
            org.geoserver.geofence.api.v2.model.Rule dto);
}
