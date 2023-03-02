package org.geoserver.geofence.api.v2.client.integration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.client.UsersApi;
import org.geoserver.geofence.api.v2.mapper.GeoServerUserApiMapper;
import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GeoServerUserRepositoryClientAdaptor implements GeoServerUserRepository {

    private final @NonNull UsersApi api;
    private final @NonNull GeoServerUserApiMapper mapper;

    @Override
    public GeoServerUser insert(@NonNull GeoServerUser user) {
        return map(api.createUser(map(user)));
    }

    @Override
    public GeoServerUser save(GeoServerUser user) {
        return map(api.updateUser(map(user)));
    }

    @Override
    public boolean delete(String id) {
        try {
            api.deleteUserById(id);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    @Override
    public Optional<GeoServerUser> findById(String id) {
        try {
            return Optional.of(map(api.getUserById(id)));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<GeoServerUser> findByName(String name) {
        try {
            return Optional.of(map(api.getUserByName(name)));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public long countByNameLike(String nameLike) {
        return api.countAllUsersByNameLike(nameLike);
    }

    @Override
    public List<GeoServerUser> findAllByNameLike(String nameLike) {
        return map(api.getAllUsersByNameLike(nameLike, null, null));
    }

    @Override
    public List<GeoServerUser> findAllByNameLike(@NonNull String nameLike, int page, int entries) {
        return map(api.getAllUsersByNameLike(nameLike, page, entries));
    }

    @Override
    public List<GeoServerUser> findAll() {
        return map(api.getAllUsers());
    }

    private org.geoserver.geofence.api.v2.model.GeoServerUser map(
            org.geoserver.geofence.users.model.GeoServerUser user) {
        return mapper.map(user);
    }

    private org.geoserver.geofence.users.model.GeoServerUser map(
            org.geoserver.geofence.api.v2.model.GeoServerUser user) {
        return mapper.map(user);
    }

    private List<org.geoserver.geofence.users.model.GeoServerUser> map(
            List<org.geoserver.geofence.api.v2.model.GeoServerUser> all) {
        return all.stream().map(this::map).collect(Collectors.toList());
    }
}
