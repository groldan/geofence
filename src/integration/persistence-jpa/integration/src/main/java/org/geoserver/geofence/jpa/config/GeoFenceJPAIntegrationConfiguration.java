package org.geoserver.geofence.jpa.config;

import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.jpa.integration.AdminRuleRepositoryJpaAdaptor;
import org.geoserver.geofence.jpa.integration.GeoServerUserGroupRepositoryJpaAdaptor;
import org.geoserver.geofence.jpa.integration.GeoServerUserRepositoryJpaAdaptor;
import org.geoserver.geofence.jpa.integration.RuleRepositoryJpaAdaptor;
import org.geoserver.geofence.jpa.integration.mapper.AdminRuleJpaMapper;
import org.geoserver.geofence.jpa.integration.mapper.GeoServerUserGroupJpaMapper;
import org.geoserver.geofence.jpa.integration.mapper.GeoServerUserJpaMapper;
import org.geoserver.geofence.jpa.integration.mapper.RuleJpaMapper;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserGroupRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({GeoFenceDataSourceConfiguration.class, GeoFenceJPAConfiguration.class})
@ComponentScan(basePackageClasses = {RuleJpaMapper.class, AdminRuleJpaMapper.class})
public class GeoFenceJPAIntegrationConfiguration {

    @Bean
    public RuleRepository geofenceRuleRepositoryJpaAdaptor(
            JpaRuleRepository jpaRuleRepository, RuleJpaMapper modelMapper) {

        return new RuleRepositoryJpaAdaptor(jpaRuleRepository, modelMapper);
    }

    @Bean
    public AdminRuleRepository geofenceAdminRuleRepositoryJpaAdaptor(
            JpaAdminRuleRepository jpaAdminRuleRepo, AdminRuleJpaMapper modelMapper) {
        return new AdminRuleRepositoryJpaAdaptor(jpaAdminRuleRepo, modelMapper);
    }

    @Bean
    public GeoServerUserRepository geofenceGeoServerUserRepositoryJpaAdaptor(
            JpaGeoServerUserRepository jpaRepo, GeoServerUserJpaMapper mapper) {
        return new GeoServerUserRepositoryJpaAdaptor(jpaRepo, mapper);
    }

    @Bean
    public GeoServerUserGroupRepository geofenceGeoServerUserGroupRepositoryJpaAdaptor(
            JpaGeoServerUserGroupRepository jpaRepo, GeoServerUserGroupJpaMapper mapper) {
        return new GeoServerUserGroupRepositoryJpaAdaptor(jpaRepo, mapper);
    }
}
