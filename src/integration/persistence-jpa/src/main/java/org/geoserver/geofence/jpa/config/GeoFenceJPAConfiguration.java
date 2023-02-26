package org.geoserver.geofence.jpa.config;

import org.geoserver.geofence.jpa.model.Rule;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@Import(GeoFenceJPAPropertiesConfiguration.class)
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories( //
        basePackageClasses = {JpaRuleRepository.class, JpaAdminRuleRepository.class},
        entityManagerFactoryRef = "geofenceEntityManager",
        transactionManagerRef = "geofenceTransactionManager")
public class GeoFenceJPAConfiguration {

    @Bean("geofenceVendorAdapter")
    HibernateJpaVendorAdapter geofenceVendorAdapter(GeofenceJPAPropertiesResolver configProps) {

        HibernateJpaVendorAdapter va = new HibernateJpaVendorAdapter();
        String showSql = configProps.get("jpa.show-sql", "false");
        String generateDdl = configProps.get("jpa.generate-ddl", "false");

        va.setGenerateDdl(false);
        va.setShowSql(Boolean.valueOf(showSql));
        va.setGenerateDdl(Boolean.valueOf(generateDdl));
        va.setDatabasePlatform(configProps.get("jpa.database-platform"));

        // geofenceVendorAdapter.databasePlatform
        // va.setDatabasePlatform(showSql);
        return va;
    }

    @Bean("geofenceEntityManager")
    @DependsOn({"geofenceDataSource", "geofenceVendorAdapter"})
    public LocalContainerEntityManagerFactoryBean geofenceEntityManager( //
            @Qualifier("geofenceVendorAdapter") HibernateJpaVendorAdapter geofenceVendorAdapter,
            @Qualifier("geofenceDataSource") DataSource dataSource,
            GeofenceJPAPropertiesResolver configProps) {

        Map<String, String> jpaProperties = configProps.getMap("jpa.properties");

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName("geofencePersistentUnit");
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(geofenceVendorAdapter);
        emf.setJpaPropertyMap(jpaProperties);
        emf.setPackagesToScan(Rule.class.getPackage().getName());
        return emf;
    }

    // @Bean
    // org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor
    // persistenceAnnotationBeanPostProcessor(){
    // return new
    // org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor();
    // }

    @Bean("geofenceTransactionManager")
    @DependsOn("geofenceEntityManager")
    public JpaTransactionManager geofenceTransactionManager(
            @Qualifier("geofenceEntityManager") final EntityManagerFactory emf) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
