package org.geoserver.geofence.jpa.config;

import org.geoserver.geofence.adminrules.persistence.AdminRuleRepository;
import org.geoserver.geofence.jpa.integration.AdminRuleRepositoryJPAAdaptor;
import org.geoserver.geofence.jpa.integration.RuleRepositoryJPAAdaptor;
import org.geoserver.geofence.jpa.integration.mapper.AdminRuleMapper;
import org.geoserver.geofence.jpa.integration.mapper.RuleMapper;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({GeoFenceDataSourceConfiguration.class, GeoFenceJPAConfiguration.class})
@ComponentScan(basePackageClasses = {RuleMapper.class, AdminRuleMapper.class})
public class GeoFenceJPAIntegrationConfiguration {

    @Bean
    public RuleRepository geofenceRuleRepositoryJpaAdaptor(
            JpaRuleRepository jpaRuleRepository, RuleMapper modelMapper) {

        return new RuleRepositoryJPAAdaptor(jpaRuleRepository, modelMapper);
    }

    @Bean
    public AdminRuleRepository geofenceAdminRuleRepositoryJpaAdaptor(
            JpaAdminRuleRepository jpaAdminRuleRepo, AdminRuleMapper modelMapper) {
        return new AdminRuleRepositoryJPAAdaptor(jpaAdminRuleRepo, modelMapper);
    }
}
