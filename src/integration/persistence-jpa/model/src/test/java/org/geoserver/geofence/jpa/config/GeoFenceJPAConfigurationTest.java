package org.geoserver.geofence.jpa.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerInstanceRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.TestDatabaseAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

class GeoFenceJPAConfigurationTest {

    private ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of( //
                                    DataSourceAutoConfiguration.class,
                                    TestDatabaseAutoConfiguration.class))
                    .withUserConfiguration(GeoFenceJPAConfiguration.class);

    @Test
    void testGeofenceEntityManagerFailsWithMissingDataSource() {
        runner.run(
                context -> {
                    assertThat(context)
                            .hasFailed()
                            .getFailure()
                            .hasMessageContaining(
                                    "Error creating bean with name 'geofenceEntityManager'")
                            .getCause()
                            .isInstanceOf(NoSuchBeanDefinitionException.class)
                            .hasMessageContaining("geofenceDataSource");
                });
    }

    @Test
    void testGeofenceEntityManager() {
        runner
                // geofenceDataSource will be replaced by TestDatabaseAutoConfiguration
                .withBean("geofenceDataSource", DataSource.class, () -> mock(DataSource.class))
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context).hasBean("geofenceEntityManager");
                            assertThat(context.getBean("geofenceEntityManager"))
                                    .isInstanceOf(EntityManagerFactory.class);
                        });
    }

    @Test
    void testGeofenceTransactionManager() {
        runner
                // geofenceDataSource will be replaced by TestDatabaseAutoConfiguration
                .withBean("geofenceDataSource", DataSource.class, () -> mock(DataSource.class))
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context).hasBean("geofenceTransactionManager");
                            assertThat(context.getBean("geofenceTransactionManager"))
                                    .isInstanceOf(JpaTransactionManager.class);
                        });
    }

    @Test
    void testJpaRepositories() {
        runner
                // geofenceDataSource will be replaced by TestDatabaseAutoConfiguration
                .withBean("geofenceDataSource", DataSource.class, () -> mock(DataSource.class))
                .run(
                        context -> {
                            assertThat(context)
                                    .hasNotFailed()
                                    .hasSingleBean(JpaGeoServerInstanceRepository.class)
                                    .hasSingleBean(JpaRuleRepository.class)
                                    .hasSingleBean(JpaAdminRuleRepository.class);
                        });
    }
}
