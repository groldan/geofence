package org.geoserver.geofence.jpa.it.authorization;

import lombok.Getter;

import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.geoserver.geofence.authorization.rules.RuleReaderServiceImpl;
import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserGroupRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.geoserver.geofence.users.service.DefaultUserRolesResolver;
import org.geoserver.geofence.users.service.UserAdminService;
import org.geoserver.geofence.users.service.UserGroupAdminService;
import org.geoserver.geofence.users.service.UserRolesResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaIntegrationTestSupport {

    private @Autowired @Getter AdminRuleRepository adminRuleRepository;
    private @Autowired @Getter JpaAdminRuleRepository jpaAdminRules;

    private @Autowired @Getter RuleRepository ruleRepository;
    private @Autowired @Getter JpaRuleRepository jpaRules;

    private @Autowired @Getter GeoServerUserGroupRepository userGropuRepository;
    private @Autowired @Getter JpaGeoServerUserGroupRepository jpaGroups;

    private @Autowired @Getter GeoServerUserRepository userRepository;
    private @Autowired @Getter JpaGeoServerUserRepository jpaUsers;

    private @Getter AdminRuleAdminService adminruleAdminService;
    private @Getter RuleAdminService ruleAdminService;
    private @Getter UserGroupAdminService userGroupAdminService;
    private @Getter UserAdminService userAdminService;
    private @Getter RuleReaderServiceImpl ruleReaderService;

    public void setUp() {
        jpaAdminRules.deleteAll();
        jpaRules.deleteAll();
        jpaUsers.deleteAll();
        jpaGroups.deleteAll();

        adminruleAdminService = new AdminRuleAdminService(adminRuleRepository);
        ruleAdminService = new RuleAdminService(ruleRepository);
        userGroupAdminService = new UserGroupAdminService(userGropuRepository);
        userAdminService = new UserAdminService(userRepository);

        UserRolesResolver userRolesResolver = new DefaultUserRolesResolver(userAdminService);

        ruleReaderService =
                new RuleReaderServiceImpl(
                        adminruleAdminService, ruleAdminService, userRolesResolver);
    }

    public void tearDown() {}
}
