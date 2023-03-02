package org.geoserver.geofence.rules.service;

import org.geoserver.geofence.rules.repository.MemoryRuleRepository;
import org.junit.jupiter.api.BeforeEach;

public class RuleAdminServiceIT extends AbstractRuleAdminServiceIT {

    @BeforeEach
    void setUp() {
        super.ruleAdminService = new RuleAdminService(new MemoryRuleRepository());
    }
}
