package org.geoserver.geofence.api.v2.it.support;

import org.geoserver.geofence.config.api.RulesApiConfiguration;
import org.geoserver.geofence.config.api.UserManagementApiConfiguration;
import org.geoserver.geofence.config.domain.AdminRuleAdminServiceConfiguration;
import org.geoserver.geofence.config.domain.RuleAdminServiceConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAIntegrationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
    RulesApiConfiguration.class,
    UserManagementApiConfiguration.class,
    RuleAdminServiceConfiguration.class,
    AdminRuleAdminServiceConfiguration.class,
    GeoFenceJPAIntegrationConfiguration.class
})
public class IntegrationTestsApplication {

    public static void main(String... args) {
        try {
            SpringApplication.run(IntegrationTestsApplication.class, args);
        } catch (RuntimeException e) {
            System.exit(-1);
        }
    }
}
