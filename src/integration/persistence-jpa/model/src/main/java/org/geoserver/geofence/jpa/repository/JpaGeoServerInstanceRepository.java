package org.geoserver.geofence.jpa.repository;

import lombok.NonNull;

import org.geoserver.geofence.jpa.model.GeoServerInstance;
import org.geoserver.geofence.jpa.model.RuleIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

@TransactionSupported
public interface JpaGeoServerInstanceRepository
        extends JpaRepository<GeoServerInstance, Long>,
                QuerydslPredicateExecutor<GeoServerInstance> {

    Optional<GeoServerInstance> findByName(String instanceName);

    /**
     * Returns the mandatory instance that means a rule is not associated with any particular
     * geoserver instance.
     *
     * <p>{@link RuleIdentifier#getInstance()} must not be {@code null} in order for the unique
     * constraint to be enforced, otherwise the database will consider {@literal NULL != NULL}.
     *
     * <p>The application layer must set this instance as a {@link RuleIdentifier}'s instance before
     * saving if it was {@code null}
     *
     * @return
     */
    @TransactionRequired
    default @NonNull GeoServerInstance getInstanceAny() {
        return findByName(GeoServerInstance.ANY).orElseGet(() -> save(GeoServerInstance.any()));
    }
}
