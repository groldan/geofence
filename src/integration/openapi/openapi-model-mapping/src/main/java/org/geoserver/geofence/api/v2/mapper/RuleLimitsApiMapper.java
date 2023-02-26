package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.rules.model.RuleLimits;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        //        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {JsonNullableMapper.class, GeometryApiMapper.class, EnumsApiMapper.class})
public interface RuleLimitsApiMapper {

    org.geoserver.geofence.api.v2.model.RuleLimits toApi(
            org.geoserver.geofence.rules.model.RuleLimits limits);

    org.geoserver.geofence.rules.model.RuleLimits toModel(
            org.geoserver.geofence.api.v2.model.RuleLimits limits);

    RuleLimits updateLimits(
            @MappingTarget RuleLimits.RuleLimitsBuilder builder,
            org.geoserver.geofence.api.v2.model.RuleLimits source);
}
