package org.geoserver.geofence.jpa.repository;

import org.geoserver.geofence.jpa.model.Rule;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories( //
        basePackageClasses = JpaRuleRepository.class
        // use the default DataSource, EntityManager, and TransactionManager for tests. It's up to
        // the integration layer to configure the appropriate ones
        //        ,entityManagerFactoryRef = "geofenceEntityManager"//
        //        ,transactionManagerRef = "geofenceTransactionManager"//
        )
@EntityScan(basePackageClasses = Rule.class)
public class GeoFenceJPATestConfiguration {}
