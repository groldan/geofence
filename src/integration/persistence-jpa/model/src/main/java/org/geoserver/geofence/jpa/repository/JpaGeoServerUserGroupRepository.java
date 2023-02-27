package org.geoserver.geofence.jpa.repository;

import org.geoserver.geofence.jpa.model.GeoServerUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

@TransactionSupported
public interface JpaGeoServerUserGroupRepository
        extends JpaRepository<GeoServerUserGroup, Long>,
                QuerydslPredicateExecutor<GeoServerUserGroup> {

    boolean deleteById(long id);

    Optional<GeoServerUserGroup> findOneByName(String name);
}
