package org.geoserver.geofence.config.domain;

import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.geoserver.geofence.users.service.UserAdminService;
import org.geoserver.geofence.users.service.UserGroupAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserAdminServiceConfiguration {
    @Bean
    public UserAdminService geofenceUserAdminService(GeoServerUserRepository userRepository) {
        return new UserAdminService(userRepository);
    }

    @Bean
    public UserGroupAdminService geofenceUserGroupAdminService(
            GeoServerUserGroupRepository userGroupRepository) {
        return new UserGroupAdminService(userGroupRepository);
    }
}
