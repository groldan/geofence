package org.geoserver.geofence.jpa.integration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface JPAGeoServerUserMapper {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "userGroups", ignore = true)
    org.geoserver.geofence.jpa.model.GeoServerUser map(
            org.geoserver.geofence.users.model.GeoServerUser user);

    @Mapping(target = "userGroups", source = "userGroups")
    org.geoserver.geofence.users.model.GeoServerUser map(
            org.geoserver.geofence.jpa.model.GeoServerUser user);

    //    default String userGroupName(GeoServerUserGroup g) {
    //        return g == null ? null : g.getName();
    //    }
    //
    default Set<String> userGroupNames(
            Set<org.geoserver.geofence.jpa.model.GeoServerUserGroup> groups) {
        return groups == null || groups.isEmpty()
                ? Set.of()
                : groups.stream()
                        .map(org.geoserver.geofence.jpa.model.GeoServerUserGroup::getName)
                        .collect(Collectors.toSet());
    }
}
