package org.geoserver.geofence.jpa.config;

import org.geoserver.geofence.jpa.model.AdminRule;
import org.geoserver.geofence.jpa.model.Rule;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
@EnableJpaRepositories( //
        basePackageClasses = {JpaRuleRepository.class, JpaAdminRuleRepository.class},
        entityManagerFactoryRef = "geofenceEntityManager",
        transactionManagerRef = "geofenceTransactionManager")
@EntityScan(basePackageClasses = {Rule.class, AdminRule.class})
public class GeoFenceJPAConfiguration {

    //    @Bean
    //    @ConfigurationProperties(prefix = "geofence.jpa")
    //    private JpaProperties geofenceJpaProperties() {
    //        return new JpaProperties();
    //    }

    @Bean("geofenceEntityManager")
    public LocalContainerEntityManagerFactoryBean geofenceEntityManager( //
            @Qualifier("geofenceDataSource") DataSource dataSource, //
            // @Qualifier("geofenceJpaProperties")
            JpaProperties jpaProperties, //
            EntityManagerFactoryBuilder builder) {

        Map<String, ?> properties = jpaProperties.getProperties();
        //                Map.of(//
        //                "hibernate.hbm2ddl.auto", "update",
        //                // env.getProperty("hibernate.hbm2ddl.auto"));
        //                "hibernate.format_sql", "true", "hibernate.dialect",
        //                "org.hibernate.spatial.dialect.h2geodb.GeoDBDialect",
        // "hibernate.show_sql", "true"
        //        // env.getProperty("hibernate.dialect"));
        //        );
        return builder.dataSource(dataSource)
                .packages(Rule.class)
                .persistenceUnit("geofencePersistentUnit")
                .properties(properties)
                .build();
    }

    @Bean("geofenceTransactionManager")
    public JpaTransactionManager geofenceTransactionManager( //
            @Qualifier("geofenceEntityManager") final EntityManagerFactory emf) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
