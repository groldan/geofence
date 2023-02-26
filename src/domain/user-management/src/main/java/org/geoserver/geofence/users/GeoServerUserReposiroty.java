package org.geoserver.geofence.users;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface GeoServerUserReposiroty {

    GeoServerUser insert(@NonNull GeoServerUser user);

    GeoServerUser save(GeoServerUser user);

    boolean delete(long id);

    Optional<GeoServerUser> findById(long id);

    Optional<GeoServerUser> findByName(String name);

    long countByNameLike(String nameLike);

    List<GeoServerUser> findAllByNameLike(String nameLike, Integer page, Integer entries);
}
