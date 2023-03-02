package org.geoserver.geofence.config.api;

import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.geoserver.geofence.api.v2.mapper.AdminRuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapper;
import org.geoserver.geofence.api.v2.rules.AdminRulesApiImpl;
import org.geoserver.geofence.api.v2.rules.RulesApiImpl;
import org.geoserver.geofence.api.v2.server.AdminRulesApiController;
import org.geoserver.geofence.api.v2.server.AdminRulesApiDelegate;
import org.geoserver.geofence.api.v2.server.RulesApiController;
import org.geoserver.geofence.api.v2.server.RulesApiDelegate;
import org.geoserver.geofence.config.domain.AdminRuleAdminServiceConfiguration;
import org.geoserver.geofence.config.domain.RuleAdminServiceConfiguration;
import org.geoserver.geofence.config.domain.UserAdminServiceConfiguration;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
    JacksonObjectMapperConfiguration.class,
    RuleAdminServiceConfiguration.class,
    AdminRuleAdminServiceConfiguration.class,
    UserAdminServiceConfiguration.class
})
@ComponentScan(basePackageClasses = RuleApiMapper.class)
public class RulesApiConfiguration {

    @Bean
    public RulesApiController rulesApiController(RulesApiDelegate delegate) {
        return new RulesApiController(delegate);
    }

    @Bean
    public AdminRulesApiController adminRulesApiController(AdminRulesApiDelegate delegate) {
        return new AdminRulesApiController(delegate);
    }

    @Bean
    RulesApiDelegate rulesApiDelegate(
            RuleAdminService rules,
            RuleApiMapper mapper,
            LayerDetailsApiMapper layerDetailsMapper,
            RuleLimitsApiMapper limitsMapper,
            EnumsApiMapper enumsMapper) {

        return new RulesApiImpl(rules, mapper, layerDetailsMapper, limitsMapper, enumsMapper);
    }

    @Bean
    AdminRulesApiDelegate adminRulesApiDelegate(
            AdminRuleAdminService service, AdminRuleApiMapper mapper, EnumsApiMapper enumsMapper) {
        return new AdminRulesApiImpl(service, mapper, enumsMapper);
    }
}
