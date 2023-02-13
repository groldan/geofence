package org.geoserver.geofence.api.v2.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring")
public interface JsonNullableMapper {

    default <T> JsonNullable<T> wrapNullable(T entity) {
        return null == entity ? JsonNullable.undefined() : JsonNullable.of(entity);
    }

    default <T> T unwrapNullable(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    /**
     * Checks whether nullable parameter was passed explicitly.
     *
     * @return true if value was set explicitly, false otherwise
     */
    @Condition
    default <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
