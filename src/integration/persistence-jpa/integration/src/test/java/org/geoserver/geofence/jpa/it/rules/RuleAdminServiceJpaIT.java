package org.geoserver.geofence.jpa.it.rules;

import org.geoserver.geofence.jpa.config.GeoFenceJPAIntegrationConfiguration;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.geoserver.geofence.rules.service.AbstractRuleAdminServiceIT;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {GeoFenceJPAIntegrationConfiguration.class})
@ActiveProfiles("test") // see config props in src/test/resource/application-test.yaml
public class RuleAdminServiceJpaIT extends AbstractRuleAdminServiceIT {

    private @Autowired RuleRepository repo;
    private @Autowired JpaRuleRepository jpaRepo;

    @BeforeEach
    void setUp() {
        jpaRepo.deleteAll();
        super.ruleAdminService = new RuleAdminService(repo);
    }
}
