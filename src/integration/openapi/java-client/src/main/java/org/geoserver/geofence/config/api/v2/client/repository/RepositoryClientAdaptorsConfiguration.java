package org.geoserver.geofence.config.api.v2.client.repository;

import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.api.v2.client.AdminRulesApi;
import org.geoserver.geofence.api.v2.client.RulesApi;
import org.geoserver.geofence.api.v2.client.UserGroupsApi;
import org.geoserver.geofence.api.v2.client.UsersApi;
import org.geoserver.geofence.api.v2.client.integration.AdminRuleRepositoryClientAdaptor;
import org.geoserver.geofence.api.v2.client.integration.GeoServerUserGroupRepositoryClientAdaptor;
import org.geoserver.geofence.api.v2.client.integration.GeoServerUserRepositoryClientAdaptor;
import org.geoserver.geofence.api.v2.client.integration.RuleRepositoryClientAdaptor;
import org.geoserver.geofence.api.v2.mapper.AdminRuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.GeoServerUserApiMapper;
import org.geoserver.geofence.api.v2.mapper.GeoServerUserGroupApiMapper;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapper;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackageClasses = RuleApiMapper.class)
public class RepositoryClientAdaptorsConfiguration {

    @Bean
    RuleRepository geofenceRepositoryClientAdaptor(
            RulesApi apiClient,
            RuleApiMapper mapper,
            EnumsApiMapper enumsMapper,
            RuleLimitsApiMapper limitsMapper,
            LayerDetailsApiMapper detailsMapper) {

        return new RuleRepositoryClientAdaptor(
                apiClient, mapper, enumsMapper, limitsMapper, detailsMapper);
    }

    @Bean
    AdminRuleRepository geofenceAdminRuleRepositoryClientAdaptor(
            AdminRulesApi apiClient, AdminRuleApiMapper mapper, EnumsApiMapper enumsMapper) {
        return new AdminRuleRepositoryClientAdaptor(apiClient, mapper, enumsMapper);
    }

    @Bean
    GeoServerUserRepository geofenceGeoServerUserRepositoryClientAdaptor(
            UsersApi api, GeoServerUserApiMapper mapper) {
        return new GeoServerUserRepositoryClientAdaptor(api, mapper);
    }

    @Bean
    GeoServerUserGroupRepository geofenceGeoServerUserGroupRepositoryClientAdaptor(
            UserGroupsApi api, GeoServerUserGroupApiMapper mapper) {
        return new GeoServerUserGroupRepositoryClientAdaptor(api, mapper);
    }
}
