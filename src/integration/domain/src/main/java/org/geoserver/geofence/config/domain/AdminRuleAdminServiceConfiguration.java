package org.geoserver.geofence.config.domain;

import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AdminRuleAdminServiceConfiguration {

    @Bean
    public AdminRuleAdminService adminRuleAdminService(AdminRuleRepository repository) {
        return new AdminRuleAdminService(repository);
    }
}
