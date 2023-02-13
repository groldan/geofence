package org.geoserver.geofence.jpa.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

class GeoFenceDataSourceConfigurationTest {

    private ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class))
                    .withUserConfiguration(GeoFenceDataSourceConfiguration.class);

    @Test
    void testConfigured() {

        runner.withPropertyValues( //
                        "geofence.datasource.url=jdbc:h2:mem:geofence-test",
                        "geofence.datasource.username=sa",
                        "geofence.datasource.password=sa"
                        // driver-class-name not required when using the url property
                        // ,"geofence.datasource.driver-class-name=org.h2.Driver"
                        )
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

        runner.withClassLoader(new FilteredClassLoader(org.h2.Driver.class))
                .run(
                        context -> {
                            assertThat(context)
                                    .hasFailed()
                                    .getFailure()
                                    .hasMessageContaining(
                                            "Failed to determine a suitable driver class");
                        });
    }
}
