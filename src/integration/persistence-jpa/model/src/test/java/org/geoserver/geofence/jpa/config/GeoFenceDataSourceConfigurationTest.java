package org.geoserver.geofence.jpa.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

class GeoFenceDataSourceConfigurationTest {

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withUserConfiguration(GeoFenceDataSourceConfiguration.class);

    @Test
    void testConfigured() {

        runner.withPropertyValues( //
                        "geofence.datasource.url=jdbc:h2:mem:geofence-test")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed().hasBean("geofenceDataSource");
                            assertThat(
                                            context.getBean("geofenceDataSource", DataSource.class)
                                                    .getConnection())
                                    .isNotNull();
                            assertThat(context.getBean("geofenceDataSource", DataSource.class))
                                    .isInstanceOf(HikariDataSource.class);
                            HikariDataSource ds =
                                    (HikariDataSource)
                                            context.getBean("geofenceDataSource", DataSource.class);
                            assertThat(ds.getJdbcUrl()).isEqualTo("jdbc:h2:mem:geofence-test");
                        });
    }

    @Test
    void testUnonfigured() {

        runner.withPropertyValues( //
                        "geofence.datasource.url=")
                .run(
                        context -> {
                            assertThat(context)
                                    .hasFailed()
                                    .getFailure()
                                    .hasMessageContaining(
                                            "geofence.datasource.url or geofence.datasource.jndiName is requried");
                        });
    }
}
