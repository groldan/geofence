package org.geoserver.geofence.api.v2.users;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.mapper.GeoServerUserApiMapper;
import org.geoserver.geofence.api.v2.model.GeoServerUser;
import org.geoserver.geofence.api.v2.server.UsersApiDelegate;
import org.geoserver.geofence.users.service.UserAdminService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsersApiImpl implements UsersApiDelegate {

    private final @NonNull UserAdminService service;
    private final @NonNull GeoServerUserApiMapper mapper;

    @Override
    public ResponseEntity<Integer> countAllUsersByNameLike(String nameLike) {
        return ResponseEntity.ok((int) service.getCount(nameLike));
    }

    @Override
    public ResponseEntity<GeoServerUser> createUser(GeoServerUser geoServerUser) {
        return ResponseEntity.status(CREATED).body(map(service.insert(map(geoServerUser))));
    }

    @Override
    public ResponseEntity<Void> deleteUserById(String id) {
        return ResponseEntity.status(service.delete(id) ? OK : NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<List<GeoServerUser>> getAllUsers() {
        return ResponseEntity.ok(map(service.getAll()));
    }

    @Override
    public ResponseEntity<List<GeoServerUser>> getAllUsersByNameLike(
            String nameLike, Integer page, Integer size) {

        return ResponseEntity.ok(map(service.getList(nameLike, page, size)));
    }

    @Override
    public ResponseEntity<GeoServerUser> getUserById(String id) {
        GeoServerUser user = service.get(id).map(this::map).orElse(null);
        return ResponseEntity.status(null == user ? NOT_FOUND : OK).body(user);
    }

    @Override
    public ResponseEntity<GeoServerUser> getUserByName(String name) {
        GeoServerUser user = service.getByName(name).map(this::map).orElse(null);
        return ResponseEntity.status(null == user ? NOT_FOUND : OK).body(user);
    }

    @Override
    public ResponseEntity<GeoServerUser> updateUser(GeoServerUser geoServerUser) {
        return ResponseEntity.ok(map(service.update(map(geoServerUser))));
    }

    private GeoServerUser map(org.geoserver.geofence.users.model.GeoServerUser user) {
        return mapper.map(user);
    }

    private org.geoserver.geofence.users.model.GeoServerUser map(GeoServerUser user) {
        return mapper.map(user);
    }

    private List<GeoServerUser> map(List<org.geoserver.geofence.users.model.GeoServerUser> all) {
        return all.stream().map(this::map).collect(Collectors.toList());
    }
}
