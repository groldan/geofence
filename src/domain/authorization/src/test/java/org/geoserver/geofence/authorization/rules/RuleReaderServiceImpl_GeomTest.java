package org.geoserver.geofence.authorization.rules;

import org.geoserver.geofence.adminrules.repository.MemoryAdminRuleRepository;
import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.geoserver.geofence.rules.repository.MemoryRuleRepository;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.geoserver.geofence.users.repository.MemoryGeoServerUserGroupRepository;
import org.geoserver.geofence.users.repository.MemoryGeoServerUserRepository;
import org.geoserver.geofence.users.service.DefaultUserRolesResolver;
import org.geoserver.geofence.users.service.UserAdminService;
import org.geoserver.geofence.users.service.UserGroupAdminService;
import org.junit.jupiter.api.BeforeEach;

public class RuleReaderServiceImpl_GeomTest extends AbstractRuleReaderServiceImpl_GeomTest {

    @BeforeEach
    void setUp() {
        super.userAdminService = new UserAdminService(new MemoryGeoServerUserRepository());
        super.userGroupAdminService =
                new UserGroupAdminService(new MemoryGeoServerUserGroupRepository());
        super.ruleAdminService = new RuleAdminService(new MemoryRuleRepository());
        super.adminruleAdminService = new AdminRuleAdminService(new MemoryAdminRuleRepository());

        super.ruleReaderService =
                new RuleReaderServiceImpl(
                        adminruleAdminService,
                        ruleAdminService,
                        new DefaultUserRolesResolver(userAdminService));
    }
}
