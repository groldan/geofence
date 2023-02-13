package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.rules.model.LayerAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {JsonNullableMapper.class, EnumsApiMapper.class})
public abstract class LayerAttributeApiMapper {

    @Autowired private JsonNullableMapper nullableMapper;

    public abstract org.geoserver.geofence.api.v2.model.LayerAttribute map(LayerAttribute la);

    public abstract LayerAttribute map(org.geoserver.geofence.api.v2.model.LayerAttribute la);

    Set<LayerAttribute> unwrapAttributes(
            JsonNullable<Set<org.geoserver.geofence.api.v2.model.LayerAttribute>> value) {
        return Optional.ofNullable(nullableMapper.unwrapNullable(value)).orElse(Set.of()).stream()
                .map(this::map)
                .collect(Collectors.toSet());
    }

    JsonNullable<Set<org.geoserver.geofence.api.v2.model.LayerAttribute>> wrapAttributes(
            Set<LayerAttribute> value) {
        if (value == null || value.isEmpty()) return JsonNullable.undefined();

        Set<org.geoserver.geofence.api.v2.model.LayerAttribute> latts =
                value.stream().map(this::map).collect(Collectors.toSet());
        return nullableMapper.wrapNullable(latts);
    }
}
