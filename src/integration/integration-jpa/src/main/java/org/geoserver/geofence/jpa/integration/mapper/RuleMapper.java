package org.geoserver.geofence.jpa.integration.mapper;

import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = RuleIdentifierMapper.class,
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RuleMapper {

    Rule toModel(org.geoserver.geofence.jpa.model.Rule entity);

    @Mapping(target = "layerDetails", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    org.geoserver.geofence.jpa.model.Rule toEntity(Rule model);

    @Mapping(target = "layerDetails", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateEntity(@MappingTarget org.geoserver.geofence.jpa.model.Rule entity, Rule model);

    default org.geoserver.geofence.jpa.model.LayerDetails toEntity(Optional<LayerDetails> value) {
        return value == null ? null : value.map(this::toEntity).orElse(null);
    }

    org.geoserver.geofence.jpa.model.LayerDetails toEntity(
            org.geoserver.geofence.rules.model.LayerDetails value);

    org.geoserver.geofence.rules.model.LayerDetails toModel(
            org.geoserver.geofence.jpa.model.LayerDetails value);

    org.geoserver.geofence.jpa.model.RuleLimits toEntity(
            org.geoserver.geofence.rules.model.RuleLimits value);

    org.geoserver.geofence.rules.model.RuleLimits toModel(
            org.geoserver.geofence.jpa.model.RuleLimits value);
}
