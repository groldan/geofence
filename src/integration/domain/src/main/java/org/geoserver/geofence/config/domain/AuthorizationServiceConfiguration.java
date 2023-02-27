package org.geoserver.geofence.config.domain;

import org.geoserver.geofence.authorization.users.AuthorizationService;
import org.geoserver.geofence.authorization.users.AuthorizationServiceImpl;
import org.geoserver.geofence.users.service.UserAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServiceConfiguration {

    @Bean
    public AuthorizationService geofenceAuthorizationService(UserAdminService userService) {
        return new AuthorizationServiceImpl(userService);
    }
}
