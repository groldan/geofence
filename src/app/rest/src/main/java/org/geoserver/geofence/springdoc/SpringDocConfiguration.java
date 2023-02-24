package org.geoserver.geofence.springdoc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "springdoc.swagger-ui.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SpringDocConfiguration {

    @Bean
    SpringDocHomeRedirectController homeController() {
        return new SpringDocHomeRedirectController();
    }
}
