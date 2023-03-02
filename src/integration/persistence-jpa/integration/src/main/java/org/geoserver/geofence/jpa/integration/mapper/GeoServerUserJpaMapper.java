package org.geoserver.geofence.jpa.integration.mapper;

import org.geoserver.geofence.jpa.model.GeoServerUserGroup;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserGroupRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class GeoServerUserJpaMapper {

    @Autowired JpaGeoServerUserGroupRepository groupRepo;

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "userGroups", ignore = true)
    public abstract org.geoserver.geofence.jpa.model.GeoServerUser map(
            org.geoserver.geofence.users.model.GeoServerUser user);

    @AfterMapping
    void addGroupsToJpaUser(
            @MappingTarget org.geoserver.geofence.jpa.model.GeoServerUser target,
            org.geoserver.geofence.users.model.GeoServerUser source) {

        Set<GeoServerUserGroup> groups =
                source.getUserGroups().stream()
                        .map(
                                groupName ->
                                        groupRepo
                                                .findOneByName(groupName)
                                                .orElseThrow(
                                                        () ->
                                                                new IllegalArgumentException(
                                                                        "GeoServerUserGroup "
                                                                                + groupName
                                                                                + " does not exist")))
                        .collect(Collectors.toSet());

        target.setUserGroups(groups);
    }

    @Mapping(target = "userGroups", source = "userGroups")
    public abstract org.geoserver.geofence.users.model.GeoServerUser map(
            org.geoserver.geofence.jpa.model.GeoServerUser user);

    Set<String> userGroupNames(Set<org.geoserver.geofence.jpa.model.GeoServerUserGroup> groups) {
        return groups == null || groups.isEmpty()
                ? Set.of()
                : groups.stream()
                        .map(org.geoserver.geofence.jpa.model.GeoServerUserGroup::getName)
                        .collect(Collectors.toSet());
    }

    public static String encodeId(Long id) {
        return id == null ? null : Long.toHexString(id);
    }

    public static Long decodeId(String id) {
        return id == null ? null : Long.decode("0x" + id);
    }
}
