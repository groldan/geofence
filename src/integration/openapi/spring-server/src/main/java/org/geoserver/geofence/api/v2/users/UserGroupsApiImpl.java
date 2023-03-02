package org.geoserver.geofence.api.v2.users;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.mapper.GeoServerUserGroupApiMapper;
import org.geoserver.geofence.api.v2.model.GeoServerUserGroup;
import org.geoserver.geofence.api.v2.server.UserGroupsApiDelegate;
import org.geoserver.geofence.users.service.UserGroupAdminService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserGroupsApiImpl implements UserGroupsApiDelegate {

    private final @NonNull UserGroupAdminService service;
    private final @NonNull GeoServerUserGroupApiMapper mapper;

    @Override
    public ResponseEntity<Integer> countAllUserGroupsByNameLike(String nameLike) {
        return ResponseEntity.ok((int) service.getCount(nameLike));
    }

    @Override
    public ResponseEntity<GeoServerUserGroup> createUserGroup(
            GeoServerUserGroup GeoServerUserGroup) {
        return ResponseEntity.status(CREATED).body(map(service.insert(map(GeoServerUserGroup))));
    }

    @Override
    public ResponseEntity<Void> deleteUserGroupById(String id) {
        return ResponseEntity.status(service.delete(id) ? OK : NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<List<GeoServerUserGroup>> getAllUserGroups() {
        return ResponseEntity.ok(map(service.getAll()));
    }

    @Override
    public ResponseEntity<List<GeoServerUserGroup>> getAllUserGroupsByNameLike(
            String nameLike, Integer page, Integer size) {

        return ResponseEntity.ok(map(service.getList(nameLike, page, size)));
    }

    @Override
    public ResponseEntity<GeoServerUserGroup> getUserGroupById(String id) {
        GeoServerUserGroup user = service.get(id).map(this::map).orElse(null);
        return ResponseEntity.status(null == user ? NOT_FOUND : OK).body(user);
    }

    @Override
    public ResponseEntity<GeoServerUserGroup> getUserGroupByName(String name) {
        GeoServerUserGroup user = service.getByName(name).map(this::map).orElse(null);
        return ResponseEntity.status(null == user ? NOT_FOUND : OK).body(user);
    }

    @Override
    public ResponseEntity<GeoServerUserGroup> updateUserGroup(
            GeoServerUserGroup GeoServerUserGroup) {
        return ResponseEntity.ok(map(service.update(map(GeoServerUserGroup))));
    }

    private GeoServerUserGroup map(org.geoserver.geofence.users.model.GeoServerUserGroup user) {
        return mapper.map(user);
    }

    private org.geoserver.geofence.users.model.GeoServerUserGroup map(GeoServerUserGroup user) {
        return mapper.map(user);
    }

    private List<GeoServerUserGroup> map(
            List<org.geoserver.geofence.users.model.GeoServerUserGroup> all) {
        return all.stream().map(this::map).collect(Collectors.toList());
    }
}
