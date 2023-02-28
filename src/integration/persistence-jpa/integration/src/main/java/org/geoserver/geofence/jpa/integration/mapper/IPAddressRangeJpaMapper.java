package org.geoserver.geofence.jpa.integration.mapper;

import org.geoserver.geofence.rules.model.IPAddressRange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR)
interface IPAddressRangeJpaMapper {

    public abstract org.geoserver.geofence.jpa.model.IPAddressRange toEntity(
            IPAddressRange addressRange);

    @Mapping(target = "low", expression = "java(entity.low())")
    @Mapping(target = "high", expression = "java(entity.high())")
    @Mapping(target = "size", expression = "java(entity.size())")
    public abstract IPAddressRange toModel(org.geoserver.geofence.jpa.model.IPAddressRange entity);
}
