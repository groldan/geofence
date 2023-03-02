package org.geoserver.geofence.users.repository;

import lombok.NonNull;

import org.geoserver.geofence.users.model.GeoServerUserGroup;

import java.util.List;
import java.util.Optional;

public interface GeoServerUserGroupRepository {

    /**
     * @throws IllegalArgumentException if {@code group} has id
     */
    GeoServerUserGroup insert(@NonNull GeoServerUserGroup group);

    /**
     * @throws IllegalArgumentException if {@code group} has no id, does not exist, or its name
     *     changed to one that already exists
     */
    GeoServerUserGroup save(GeoServerUserGroup group);

    boolean delete(String id);

    Optional<GeoServerUserGroup> findById(String id);

    Optional<GeoServerUserGroup> findByName(String name);

    long countByNameLike(String nameLike);

    List<GeoServerUserGroup> findAllByNameLike(String nameLike);

    List<GeoServerUserGroup> findAllByNameLike(String nameLike, int page, int entries);

    List<GeoServerUserGroup> findAll();
}
