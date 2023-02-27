package org.geoserver.geofence.jpa.repository;

import org.geoserver.geofence.jpa.model.GeoServerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

@TransactionSupported
public interface JpaGeoServerUserRepository
        extends JpaRepository<GeoServerUser, Long>, QuerydslPredicateExecutor<GeoServerUser> {

    boolean deleteById(long id);

    Optional<GeoServerUser> findOneByName(String name);
}
