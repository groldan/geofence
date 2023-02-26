package org.geoserver.geofence.autoconfigure.persistence;

import org.geoserver.geofence.jpa.config.GeoFenceJPAIntegrationConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({GeoFenceJPAIntegrationConfiguration.class})
public class JPAIntegrationAutoConfiguration {}
