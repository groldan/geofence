package org.geoserver.geofence.config.domain;

import org.geoserver.geofence.rules.repository.RuleRepository;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RuleAdminServiceConfiguration {

    @Bean
    public RuleAdminService ruleAdminService(RuleRepository ruleRepository) {
        return new RuleAdminService(ruleRepository);
    }
}
