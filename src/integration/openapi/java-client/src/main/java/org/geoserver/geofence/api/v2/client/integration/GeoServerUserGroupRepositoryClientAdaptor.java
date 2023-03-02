package org.geoserver.geofence.api.v2.client.integration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.client.UserGroupsApi;
import org.geoserver.geofence.api.v2.mapper.GeoServerUserGroupApiMapper;
import org.geoserver.geofence.users.model.GeoServerUserGroup;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GeoServerUserGroupRepositoryClientAdaptor implements GeoServerUserGroupRepository {

    private final @NonNull UserGroupsApi api;
    private final @NonNull GeoServerUserGroupApiMapper mapper;

    @Override
    public GeoServerUserGroup insert(@NonNull GeoServerUserGroup group) {
        return map(api.createUserGroup(map(group)));
    }

    @Override
    public GeoServerUserGroup save(GeoServerUserGroup group) {
        return map(api.updateUserGroup(map(group)));
    }

    @Override
    public boolean delete(String id) {
        try {
            api.deleteUserGroupById(id);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    @Override
    public Optional<GeoServerUserGroup> findById(String id) {
        try {
            return Optional.of(map(api.getUserGroupById(id)));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<GeoServerUserGroup> findByName(String name) {
        try {
            return Optional.of(map(api.getUserGroupByName(name)));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public long countByNameLike(String nameLike) {
        return api.countAllUserGroupsByNameLike(nameLike);
    }

    @Override
    public List<GeoServerUserGroup> findAllByNameLike(String nameLike) {
        return map(api.getAllUserGroupsByNameLike(nameLike, null, null));
    }

    @Override
    public List<GeoServerUserGroup> findAllByNameLike(String nameLike, int page, int entries) {
        return map(api.getAllUserGroupsByNameLike(nameLike, page, entries));
    }

    @Override
    public List<GeoServerUserGroup> findAll() {
        return map(api.getAllUserGroups());
    }

    private org.geoserver.geofence.api.v2.model.GeoServerUserGroup map(
            org.geoserver.geofence.users.model.GeoServerUserGroup user) {
        return mapper.map(user);
    }

    private org.geoserver.geofence.users.model.GeoServerUserGroup map(
            org.geoserver.geofence.api.v2.model.GeoServerUserGroup user) {
        return mapper.map(user);
    }

    private List<org.geoserver.geofence.users.model.GeoServerUserGroup> map(
            List<org.geoserver.geofence.api.v2.model.GeoServerUserGroup> all) {
        return all.stream().map(this::map).collect(Collectors.toList());
    }
}
