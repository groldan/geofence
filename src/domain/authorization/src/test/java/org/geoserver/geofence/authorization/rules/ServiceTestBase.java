/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.authorization.rules;

import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.IPAddressRange;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleIdentifier;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.model.GeoServerUserGroup;
import org.geoserver.geofence.users.service.UserAdminService;
import org.geoserver.geofence.users.service.UserGroupAdminService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ETj (etj at geo-solutions.it)
 */
public class ServiceTestBase {

    protected RuleAdminService ruleAdminService;
    protected AdminRuleAdminService adminruleAdminService;
    protected RuleReaderService ruleReaderService;
    protected UserAdminService userAdminService;
    protected UserGroupAdminService userGroupAdminService;

    protected GeoServerUserGroup createRole(String name) {
        return userGroupAdminService.insert(GeoServerUserGroup.builder().name(name).build());
    }

    protected GeoServerUser createUser(String name, GeoServerUserGroup... groups) {
        Set<String> userGroups =
                Arrays.stream(groups).map(GeoServerUserGroup::getName).collect(Collectors.toSet());
        return userAdminService.insert(
                GeoServerUser.builder().name(name).userGroups(userGroups).build());
    }

    protected Rule rule(
            long priority,
            String username,
            String rolename,
            String instance,
            IPAddressRange addressRange,
            String service,
            String request,
            String subfield,
            String workspace,
            String layer,
            GrantType access) {

        RuleIdentifier identifier =
                RuleIdentifier.builder()
                        .username(username)
                        .rolename(rolename)
                        .instanceName(instance)
                        .addressRange(addressRange)
                        .service(service)
                        .request(request)
                        .subfield(subfield)
                        .workspace(workspace)
                        .layer(layer)
                        .access(access)
                        .build();
        return Rule.builder().priority(priority).identifier(identifier).build();
    }
}
