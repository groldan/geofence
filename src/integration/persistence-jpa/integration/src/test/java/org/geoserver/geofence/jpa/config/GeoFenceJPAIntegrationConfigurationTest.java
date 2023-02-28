package org.geoserver.geofence.jpa.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.jpa.integration.mapper.AdminRuleJpaMapper;
import org.geoserver.geofence.jpa.integration.mapper.RuleJpaMapper;
import org.geoserver.geofence.rules.repository.RuleRepository;
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
                            .hasSingleBean(RuleJpaMapper.class)
                            .hasSingleBean(AdminRuleJpaMapper.class);
                });
    }
}
