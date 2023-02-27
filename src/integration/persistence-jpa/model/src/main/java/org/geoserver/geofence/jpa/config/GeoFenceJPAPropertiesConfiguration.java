package org.geoserver.geofence.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration(proxyBeanMethods = false)
class GeoFenceJPAPropertiesConfiguration {

    @Bean
    GeofenceJPAPropertiesResolver geofenceConfigPropertiesResolver(ConfigurableEnvironment env) {
        return new GeofenceJPAPropertiesResolver(env);
    }
}
