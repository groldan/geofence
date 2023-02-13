package org.geoserver.geofence.api.v2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface OptionalApiMapper {

    default <T> Optional<T> wrapOptional(T object) {
        return Optional.ofNullable(object);
    }

    default <T> T unwrapOptional(Optional<T> object) {
        return object == null ? null : object.orElse(null);
    }
}
