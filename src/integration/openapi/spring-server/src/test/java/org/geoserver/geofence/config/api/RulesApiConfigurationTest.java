package org.geoserver.geofence.config.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.api.v2.mapper.AdminRuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.server.AdminRulesApiController;
import org.geoserver.geofence.api.v2.server.AdminRulesApiDelegate;
import org.geoserver.geofence.api.v2.server.RulesApiController;
import org.geoserver.geofence.api.v2.server.RulesApiDelegate;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class RulesApiConfigurationTest {

    private ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withConfiguration(UserConfigurations.of(RulesApiConfiguration.class));

    private ApplicationContextRunner withMockRepositories() {
        runner = withMock(GeoServerUserGroupRepository.class);
        runner = withMock(GeoServerUserRepository.class);
        runner = withMock(RuleRepository.class);
        runner = withMock(AdminRuleRepository.class);
        return runner;
    }

    private <T> ApplicationContextRunner withMock(Class<T> beanType) {
        return runner.withBean(beanType, () -> mock(beanType));
    }

    @Test
    void testWithAvailableRepositories() {
        withMockRepositories()
                .run(
                        context -> {
                            assertThat(context)
                                    .hasSingleBean(RulesApiController.class)
                                    .hasSingleBean(RulesApiDelegate.class)
                                    .hasSingleBean(RuleApiMapper.class)
                                    .hasSingleBean(AdminRulesApiController.class)
                                    .hasSingleBean(AdminRulesApiDelegate.class)
                                    .hasSingleBean(AdminRuleApiMapper.class)
                                    .hasSingleBean(JsonNullableModule.class)
                                    .hasSingleBean(JavaTimeModule.class)
                                    .hasSingleBean(EnumsApiMapper.class);
                        });
    }

    @Test
    void testMissingRuleRepository() {
        runner = withMock(GeoServerUserGroupRepository.class);
        runner = withMock(GeoServerUserRepository.class);
        runner = withMock(AdminRuleRepository.class);
        runner.run(
                context -> {
                    assertThat(context)
                            .hasFailed()
                            .getFailure()
                            .isInstanceOf(UnsatisfiedDependencyException.class)
                            .hasMessageContaining("RuleRepository");
                });
    }

    @Test
    void testMissingAdminRuleRepository() {
        runner = withMock(GeoServerUserGroupRepository.class);
        runner = withMock(GeoServerUserRepository.class);
        runner = withMock(RuleRepository.class);
        runner.run(
                context -> {
                    assertThat(context)
                            .hasFailed()
                            .getFailure()
                            .isInstanceOf(UnsatisfiedDependencyException.class)
                            .hasMessageContaining("AdminRuleRepository");
                });
    }

    @Test
    void testMissingGeoServerUserGroupRepository() {
        runner = withMock(GeoServerUserRepository.class);
        runner = withMock(RuleRepository.class);
        runner = withMock(AdminRuleRepository.class);
        runner.run(
                context -> {
                    assertThat(context)
                            .hasFailed()
                            .getFailure()
                            .isInstanceOf(UnsatisfiedDependencyException.class)
                            .hasMessageContaining("GeoServerUserGroupRepository");
                });
    }

    @Test
    void testMissingGeoServerUserRepository() {
        runner = withMock(GeoServerUserGroupRepository.class);
        runner = withMock(RuleRepository.class);
        runner = withMock(AdminRuleRepository.class);
        runner.run(
                context -> {
                    assertThat(context)
                            .hasFailed()
                            .getFailure()
                            .isInstanceOf(UnsatisfiedDependencyException.class)
                            .hasMessageContaining("GeoServerUserRepository");
                });
    }
}
