package org.geoserver.geofence.api.v2.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {JsonNullableMapper.class})
public interface GeoServerUserApiMapper {

    org.geoserver.geofence.api.v2.model.GeoServerUser map(
            org.geoserver.geofence.users.model.GeoServerUser user);

    org.geoserver.geofence.users.model.GeoServerUser map(
            org.geoserver.geofence.api.v2.model.GeoServerUser user);
}
