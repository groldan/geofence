package org.geoserver.geofence.config.api;

import org.geoserver.geofence.adminrules.persistence.AdminRuleRepository;
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
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.NativeWebRequest;

@Configuration(proxyBeanMethods = false)
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
    JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Bean
    RulesApiDelegate rulesApiDelegate(
            RuleAdminService rules,
            RuleApiMapper mapper,
            LayerDetailsApiMapper layerDetailsMapper,
            RuleLimitsApiMapper limitsMapper,
            EnumsApiMapper enumsMapper,
            NativeWebRequest request) {

        return new RulesApiImpl(
                rules, mapper, layerDetailsMapper, limitsMapper, enumsMapper, request);
    }

    @Bean
    AdminRulesApiDelegate adminRulesApiDelegate(
            AdminRuleAdminService service,
            AdminRuleApiMapper mapper,
            EnumsApiMapper enumsMapper,
            NativeWebRequest request) {
        return new AdminRulesApiImpl(service, mapper, enumsMapper, request);
    }

    @Bean
    RuleAdminService ruleAdminService(RuleRepository ruleRepository) {
        return new RuleAdminService(ruleRepository);
    }

    @Bean
    AdminRuleAdminService adminRuleAdminService(AdminRuleRepository repository) {
        return new AdminRuleAdminService(repository);
    }
}
