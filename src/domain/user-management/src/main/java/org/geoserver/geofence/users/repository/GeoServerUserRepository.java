package org.geoserver.geofence.users.repository;

import lombok.NonNull;

import org.geoserver.geofence.users.model.GeoServerUser;

import java.util.List;
import java.util.Optional;

public interface GeoServerUserRepository {

    GeoServerUser insert(@NonNull GeoServerUser user);

    GeoServerUser save(GeoServerUser user);

    boolean delete(String id);

    Optional<GeoServerUser> findById(String id);

    Optional<GeoServerUser> findByName(String name);

    long countByNameLike(String nameLike);

    List<GeoServerUser> findAllByNameLike(String nameLike);

    List<GeoServerUser> findAllByNameLike(@NonNull String nameLike, int page, int entries);

    List<GeoServerUser> findAll();
}
