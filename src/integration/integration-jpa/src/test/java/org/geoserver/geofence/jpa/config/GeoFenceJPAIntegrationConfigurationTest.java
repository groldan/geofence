package org.geoserver.geofence.jpa.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.geoserver.geofence.adminrules.persistence.AdminRuleRepository;
import org.geoserver.geofence.jpa.integration.mapper.AdminRuleMapper;
import org.geoserver.geofence.jpa.integration.mapper.RuleMapper;
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class GeoFenceJPAIntegrationConfigurationTest {

    private ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withPropertyValues("geofence.datasource.url=jdbc:h2:mem:geofence-test")
                    .withUserConfiguration(GeoFenceJPAIntegrationConfiguration.class);

    @Test
    void testGeofenceRuleRepositoryJpaAdaptor() {

        runner.run(
                context -> {
                    assertThat(context)
                            .hasNotFailed()
                            .hasSingleBean(RuleRepository.class)
                            .hasSingleBean(AdminRuleRepository.class)
                            .hasSingleBean(RuleMapper.class)
                            .hasSingleBean(AdminRuleMapper.class);
                });
    }
}
