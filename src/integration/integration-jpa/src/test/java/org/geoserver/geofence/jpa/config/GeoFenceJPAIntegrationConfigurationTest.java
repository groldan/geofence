package org.geoserver.geofence.jpa.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.geoserver.geofence.adminrules.persistence.AdminRuleRepository;
import org.geoserver.geofence.jpa.integration.mapper.RuleMapper;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerInstanceRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class GeoFenceJPAIntegrationConfigurationTest {

    private ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withBean(
                            JpaGeoServerInstanceRepository.class,
                            () -> mock(JpaGeoServerInstanceRepository.class))
                    .withBean(JpaRuleRepository.class, () -> mock(JpaRuleRepository.class))
                    .withBean(
                            JpaAdminRuleRepository.class, () -> mock(JpaAdminRuleRepository.class))
                    .withUserConfiguration(GeoFenceJPAIntegrationConfiguration.class);

    @Test
    void testGeofenceRuleRepositoryJpaAdaptor() {

        runner.run(
                context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(RuleMapper.class);
                    assertThat(context).hasNotFailed().hasSingleBean(RuleRepository.class);
                    assertThat(context).hasNotFailed().hasSingleBean(AdminRuleRepository.class);
                });
    }
}
