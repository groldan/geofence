package org.geoserver.geofence.config.api;

import org.geoserver.geofence.api.v2.mapper.GeoServerUserApiMapper;
import org.geoserver.geofence.api.v2.mapper.GeoServerUserGroupApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.server.UserGroupsApiController;
import org.geoserver.geofence.api.v2.server.UserGroupsApiDelegate;
import org.geoserver.geofence.api.v2.server.UsersApiController;
import org.geoserver.geofence.api.v2.server.UsersApiDelegate;
import org.geoserver.geofence.api.v2.users.UserGroupsApiImpl;
import org.geoserver.geofence.api.v2.users.UsersApiImpl;
import org.geoserver.geofence.config.domain.UserAdminServiceConfiguration;
import org.geoserver.geofence.users.service.UserAdminService;
import org.geoserver.geofence.users.service.UserGroupAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({JacksonObjectMapperConfiguration.class, UserAdminServiceConfiguration.class})
@ComponentScan(basePackageClasses = RuleApiMapper.class)
public class UserManagementApiConfiguration {

    @Bean
    public UsersApiController usersApiController(UsersApiDelegate delegate) {
        return new UsersApiController(delegate);
    }

    @Bean
    public UserGroupsApiController userGroupsApiController(UserGroupsApiDelegate delegate) {
        return new UserGroupsApiController(delegate);
    }

    @Bean
    UsersApiDelegate usersApiDelegate(UserAdminService users, GeoServerUserApiMapper mapper) {
        return new UsersApiImpl(users, mapper);
    }

    @Bean
    UserGroupsApiDelegate userGroupsApiDelegate(
            UserGroupAdminService userGroups, GeoServerUserGroupApiMapper mapper) {
        return new UserGroupsApiImpl(userGroups, mapper);
    }
}
