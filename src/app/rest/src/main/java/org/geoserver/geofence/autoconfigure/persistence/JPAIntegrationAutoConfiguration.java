package org.geoserver.geofence.autoconfigure.persistence;

import org.geoserver.geofence.jpa.config.GeoFenceDataSourceConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAIntegrationConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    GeoFenceDataSourceConfiguration.class,
    GeoFenceJPAConfiguration.class,
    GeoFenceJPAIntegrationConfiguration.class
})
public class JPAIntegrationAutoConfiguration {}
