package org.geoserver.geofence.jpa.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class GeoFenceDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("geofence.datasource")
    public DataSourceProperties geofenceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "geofence.datasource")
    public DataSource geofenceDataSource() {
        DataSourceProperties dataSourceProperties = geofenceDataSourceProperties();
        DataSourceBuilder<?> builder = dataSourceProperties.initializeDataSourceBuilder();
        return builder.build();
    }
}
