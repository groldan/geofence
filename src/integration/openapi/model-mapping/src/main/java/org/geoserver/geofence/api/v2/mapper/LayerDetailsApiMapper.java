package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.rules.model.LayerDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        //        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
            JsonNullableMapper.class,
            GeometryApiMapper.class,
            LayerAttributeApiMapper.class,
            EnumsApiMapper.class
        })
public abstract class LayerDetailsApiMapper {

    @Mapping(target = "allowedArea", source = "area")
    @Mapping(target = "layerAttributes", source = "attributes")
    public abstract org.geoserver.geofence.api.v2.model.LayerDetails map(LayerDetails ld);

    @Mapping(target = "area", source = "allowedArea")
    @Mapping(target = "attributes", source = "layerAttributes")
    public abstract LayerDetails map(org.geoserver.geofence.api.v2.model.LayerDetails ld);

    JsonNullable<org.geoserver.geofence.api.v2.model.LayerDetails> toJsonNullableLayerDetails(
            LayerDetails value) {

        if (value == null) return JsonNullable.undefined();

        return JsonNullable.of(map(value));
    }

    @Mapping(target = "area", source = "allowedArea")
    @Mapping(target = "attributes", source = "layerAttributes")
    public abstract LayerDetails updateDetails(
            @MappingTarget LayerDetails.Builder target,
            org.geoserver.geofence.api.v2.model.LayerDetails source);
}
