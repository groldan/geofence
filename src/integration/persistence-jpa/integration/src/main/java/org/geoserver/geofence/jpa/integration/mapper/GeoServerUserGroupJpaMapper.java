package org.geoserver.geofence.jpa.integration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface GeoServerUserGroupJpaMapper {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    org.geoserver.geofence.jpa.model.GeoServerUserGroup map(
            org.geoserver.geofence.users.model.GeoServerUserGroup group);

    org.geoserver.geofence.users.model.GeoServerUserGroup map(
            org.geoserver.geofence.jpa.model.GeoServerUserGroup group);

    static String encodeId(Long id) {
        return id == null ? null : Long.toHexString(id);
    }

    static Long decodeId(String id) {
        return id == null ? null : Long.decode(id);
    }
}
