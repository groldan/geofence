package org.geoserver.geofence.autoconfigure.springdoc;

import org.geoserver.geofence.config.springdoc.SpringDocHomeRedirectConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(
        name = "springdoc.swagger-ui.enabled",
        havingValue = "true",
        matchIfMissing = true)
@Import(SpringDocHomeRedirectConfiguration.class)
public class SpringDocHomeRedirectAutoConfiguration {}
