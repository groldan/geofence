/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.authorization.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.predicate.SpecialFilterType;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.IPAddressRange;
import org.geoserver.geofence.rules.model.LayerAttribute;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.model.GeoServerUserGroup;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract {@link RuleReaderService} integration/conformance test
 *
 * <p>Concrete implementations must supply the required services in {@link ServiceTestBase}
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class AbstractRuleReaderServiceImplTest extends ServiceTestBase {

    @Test
    public void testGetRulesForUsersAndGroup() {

        assertEquals(0, ruleAdminService.count(RuleFilter.any()));

        GeoServerUserGroup p1 = createRole("p1");
        GeoServerUserGroup p2 = createRole("p2");

        String u1 = "TestUser1";
        String u2 = "TestUser2";
        String u3 = "TestUser3";

        GeoServerUser user1 =
                GeoServerUser.builder().name(u1).userGroups(Set.of(p1.getName())).build();
        GeoServerUser user2 =
                GeoServerUser.builder().name(u2).userGroups(Set.of(p2.getName())).build();

        GeoServerUserGroup g3a = createRole("g3a");
        GeoServerUserGroup g3b = createRole("g3b");
        GeoServerUser user3 =
                GeoServerUser.builder()
                        .name(u3)
                        .userGroups(Set.of(g3a.getName(), g3b.getName()))
                        .build();

        userAdminService.insert(user1);
        userAdminService.insert(user2);
        userAdminService.insert(user3);

        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(10)
                        .withUsername(u1)
                        .withRolename("p1")
                        .withService("s1")
                        .withRequest("r1")
                        .withWorkspace("w1")
                        .withLayer("l1"));
        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(20)
                        .withUsername(u2)
                        .withRolename("p2")
                        .withService("s1")
                        .withRequest("r2")
                        .withWorkspace("w2")
                        .withLayer("l2"));
        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(30)
                        .withUsername(u1)
                        .withRolename("p1")
                        .withService("s3")
                        .withRequest("r3")
                        .withWorkspace("w3")
                        .withLayer("l3"));
        ruleAdminService.insert(Rule.allow().withPriority(40).withUsername(u1).withRolename("p1"));
        ruleAdminService.insert(Rule.allow().withPriority(50).withRolename("g3a"));
        ruleAdminService.insert(Rule.allow().withPriority(60).withRolename("g3b"));

        assertEquals(3, getMatchingRules(u1, "*", "Z", "*", "*", "*", "*", "*").size());
        assertEquals(3, getMatchingRules("*", "p1", "Z", "*", "*", "*", "*", "*").size());
        assertEquals(1, getMatchingRules(u1, "*", "Z", "*", null, null, null, null).size());
        assertEquals(0, getMatchingRules("*", "Z", "Z", "*", null, null, null, null).size());
        assertEquals(1, getMatchingRules(u1, "*", "Z", "*", null, null, null, null).size());
        assertEquals(1, getMatchingRules(u1, "*", "Z", "*", null, null, null, null).size());
        assertEquals(1, getMatchingRules(u2, "*", "Z", "*", "*", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "p2", "Z", "*", "*", "*", "*", "*").size());
        assertEquals(2, getMatchingRules(u1, "*", "Z", "*", "s1", "*", "*", "*").size());
        assertEquals(2, getMatchingRules("*", "p1", "Z", "*", "s1", "*", "*", "*").size());
        assertEquals(2, getMatchingRules(u3, "*", "Z", "*", "s1", "*", "*", "*").size());
    }

    private static RuleFilter createFilter(String userName, String groupName, String service) {
        RuleFilter filter;
        filter = new RuleFilter(SpecialFilterType.ANY);
        if (userName != null) filter.setUser(userName);
        if (groupName != null) filter.setRole(groupName);
        if (service != null) filter.setService(service);
        return filter;
    }

    @Test
    public void testGetRulesForGroupOnly() {

        assertEquals(0, ruleAdminService.count(RuleFilter.any()));

        GeoServerUserGroup g1 = createRole("p1");
        GeoServerUserGroup g2 = createRole("p2");

        Rule r1 =
                Rule.allow()
                        .withPriority(10)
                        .withRolename("p1")
                        .withService("s1")
                        .withRequest("r1")
                        .withWorkspace("w1")
                        .withLayer("l1");
        Rule r2 =
                Rule.allow()
                        .withPriority(20)
                        .withRolename("p2")
                        .withService("s1")
                        .withRequest("r2")
                        .withWorkspace("w2")
                        .withLayer("l2");
        Rule r3 =
                Rule.allow()
                        .withPriority(30)
                        .withRolename("p1")
                        .withService("s3")
                        .withRequest("r3")
                        .withWorkspace("w3")
                        .withLayer("l3");
        Rule r4 = Rule.allow().withPriority(40).withRolename("p1");

        r1 = ruleAdminService.insert(r1);
        r2 = ruleAdminService.insert(r2);
        r3 = ruleAdminService.insert(r3);
        r4 = ruleAdminService.insert(r4);

        assertEquals(4, getMatchingRules("*", "*", "*", "*", "*", "*", "*", "*").size());
        assertEquals(3, getMatchingRules("*", "*", "*", "*", "s1", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "*", "*", "*", "ZZ", "*", "*", "*").size());

        assertEquals(3, getMatchingRules("*", "p1", "*", "*", "*", "*", "*", "*").size());
        assertEquals(2, getMatchingRules("*", "p1", "*", "*", "s1", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "p1", "*", "*", "ZZ", "*", "*", "*").size());

        assertEquals(1, getMatchingRules("*", "p2", "*", "*", "*", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "p2", "*", "*", "s1", "*", "*", "*").size());
        assertEquals(0, getMatchingRules("*", "p2", "*", "*", "ZZ", "*", "*", "*").size());

        RuleFilter filter;

        filter = createFilter(null, g1.getName(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter((String) null, null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
    }

    @Test
    public void testGetInfo() {
        GeoServerUserGroup p0 = createRole("p0");
        createUser("u0", p0);
        assertEquals(0, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        List<Rule> rules = new ArrayList<>();

        rules.add(
                ruleAdminService.insert(
                        Rule.allow().withPriority(100 + rules.size()).withService("WCS")));
        rules.add(
                ruleAdminService.insert(
                        Rule.allow()
                                .withPriority(100 + rules.size())
                                .withService("s1")
                                .withRequest("r2")
                                .withWorkspace("w2")
                                .withLayer("l2")));
        rules.add(
                ruleAdminService.insert(
                        Rule.allow()
                                .withPriority(100 + rules.size())
                                .withService("s3")
                                .withRequest("r3")
                                .withWorkspace("w3")
                                .withLayer("l3")));
        rules.add(ruleAdminService.insert(Rule.deny().withPriority(100 + rules.size())));

        assertEquals(4, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        RuleFilter baseFilter = new RuleFilter(SpecialFilterType.ANY);
        baseFilter.setUser("u0");
        baseFilter.setRole("p0");
        baseFilter.setInstance("i0");
        baseFilter.setService("WCS");
        baseFilter.setRequest(SpecialFilterType.ANY);
        baseFilter.setWorkspace("W0");
        baseFilter.setLayer("l0");

        AccessInfo accessInfo;

        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setUser(SpecialFilterType.ANY);

            assertEquals(2, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setRole(SpecialFilterType.ANY);

            assertEquals(2, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setUser(SpecialFilterType.ANY);
            ruleFilter.setService("UNMATCH");

            assertEquals(1, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setRole(SpecialFilterType.ANY);
            ruleFilter.setService("UNMATCH");

            assertEquals(1, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
    }

    @Test
    public void testResolveLazy() {
        assertEquals(0, ruleAdminService.getCountAll());

        List<Rule> rules = new ArrayList<>();

        rules.add(
                ruleAdminService.insert(
                        Rule.allow().withPriority(100 + rules.size()).withService("WCS")));
        rules.add(
                ruleAdminService.insert(
                        Rule.allow()
                                .withPriority(100 + rules.size())
                                .withService("s1")
                                .withRequest("r2")
                                .withWorkspace("w2")
                                .withLayer("l2")));

        LayerDetails details = LayerDetails.builder().build();
        ruleAdminService.setLayerDetails(rules.get(1).getId(), details);

        assertEquals(2, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        AccessInfo accessInfo;

        {
            RuleFilter ruleFilter = new RuleFilter(SpecialFilterType.ANY);
            ruleFilter.setService("s1");
            ruleFilter.setLayer("l2");

            assertEquals(2, ruleAdminService.getList(new RuleFilter(SpecialFilterType.ANY)).size());
            List<Rule> matchingRules = ruleReaderService.getMatchingRules(ruleFilter);
            // LOGGER.info("Matching rules: " + matchingRules);
            assertEquals(1, matchingRules.size());
            accessInfo = ruleReaderService.getAccessInfo(ruleFilter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNull(accessInfo.getArea());
        }
    }

    @Test
    public void testNoDefault() {
        GeoServerUserGroup p0 = createRole("p0");
        createUser("u0", p0);
        assertEquals(0, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        ruleAdminService.insert(Rule.allow().withService("WCS"));

        assertEquals(1, getMatchingRules("u0", "*", "i0", null, "WCS", null, "W0", "l0").size());
        assertEquals(
                GrantType.ALLOW,
                getAccessInfo("u0", "*", "i0", null, "WCS", null, "W0", "l0").getGrant());

        assertEquals(1, getMatchingRules("*", "p0", "i0", null, "WCS", null, "W0", "l0").size());
        assertEquals(
                GrantType.ALLOW,
                getAccessInfo("*", "p0", "i0", null, "WCS", null, "W0", "l0").getGrant());

        assertEquals(
                0, getMatchingRules("u0", "*", "i0", null, "UNMATCH", null, "W0", "l0").size());
        assertEquals(
                GrantType.DENY,
                getAccessInfo("u0", "*", "i0", null, "UNMATCH", null, "W0", "l0").getGrant());

        assertEquals(
                0, getMatchingRules("*", "p0", "i0", null, "UNMATCH", null, "W0", "l0").size());
        assertEquals(
                GrantType.DENY,
                getAccessInfo("*", "p0", "i0", null, "UNMATCH", null, "W0", "l0").getGrant());
    }

    @Test
    public void testGroups() {
        assertEquals(0, ruleAdminService.getCountAll());

        GeoServerUserGroup g1 = createRole("p1");
        GeoServerUserGroup g2 = createRole("p2");

        GeoServerUser u1 = createUser("u1", g1);
        GeoServerUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<>();

        rules.add(
                ruleAdminService.insert(
                        Rule.allow()
                                .withPriority(10 + rules.size())
                                .withRolename("p1")
                                .withService("s1")
                                .withRequest("r1")
                                .withWorkspace("w1")
                                .withLayer("l1")));
        rules.add(
                ruleAdminService.insert(
                        Rule.deny().withPriority(10 + rules.size()).withRolename("p1")));

        // LOGGER.info("SETUP ENDED, STARTING TESTS");
        // ===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        {
            RuleFilter filter;
            filter = new RuleFilter(SpecialFilterType.ANY);
            filter.setUser(u1.getName());
            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
            filter.setService("s1");
            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filter).getGrant());

            filter.setService("s2");
            assertEquals(1, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filter).getGrant());
        }

        {
            RuleFilter filter;
            filter = new RuleFilter(SpecialFilterType.ANY);
            filter.setUser(u2.getName());
            assertEquals(0, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filter).getGrant());
        }
    }

    @Test
    public void testGroupOrder01() throws UnknownHostException {
        assertEquals(0, ruleAdminService.getCountAll());

        GeoServerUserGroup g1 = createRole("p1");
        GeoServerUserGroup g2 = createRole("p2");

        GeoServerUser u1 = createUser("u1", g1);
        GeoServerUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(
                ruleAdminService.insert(
                        Rule.allow().withPriority(10 + rules.size()).withRolename("p1")));
        rules.add(
                ruleAdminService.insert(
                        Rule.deny().withPriority(10 + rules.size()).withRolename("p2")));

        // LOGGER.info("SETUP ENDED, STARTING TESTS");
        // ===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        RuleFilter filterU1 = new RuleFilter(SpecialFilterType.ANY);
        filterU1.setUser(u1.getName());

        RuleFilter filterU2 = new RuleFilter(SpecialFilterType.ANY);
        filterU2.setUser(u2.getName());

        assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());
        assertEquals(1, ruleReaderService.getMatchingRules(filterU2).size());

        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filterU1).getGrant());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filterU2).getGrant());
    }

    @Test
    public void testGroupOrder02() {
        assertEquals(0, ruleAdminService.getCountAll());

        GeoServerUserGroup g1 = createRole("p1");
        GeoServerUserGroup g2 = createRole("p2");

        GeoServerUser u1 = createUser("u1", g1);
        GeoServerUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(
                ruleAdminService.insert(
                        Rule.deny().withPriority(10 + rules.size()).withRolename("p2")));
        rules.add(
                ruleAdminService.insert(
                        Rule.allow().withPriority(10 + rules.size()).withRolename("p1")));

        // LOGGER.info("SETUP ENDED, STARTING TESTS");
        // ===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        RuleFilter filterU1;
        filterU1 = new RuleFilter(SpecialFilterType.ANY);
        filterU1.setUser(u1.getName());

        RuleFilter filterU2;
        filterU2 = new RuleFilter(SpecialFilterType.ANY);
        filterU2.setUser(u2.getName());

        assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());
        assertEquals(1, ruleReaderService.getMatchingRules(filterU2).size());

        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filterU1).getGrant());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filterU2).getGrant());
    }

    @Test
    public void testAttrib() {
        assertEquals(0, ruleAdminService.getCountAll());

        {
            GeoServerUserGroup g1 = createRole("g1");
            GeoServerUserGroup g2 = createRole("g2");
            GeoServerUserGroup g3 = createRole("g3");
            GeoServerUserGroup g4 = createRole("g4");

            createUser("u1", g1);
            createUser("u2", g2);
            createUser("u12", g1, g2);
            createUser("u13", g1, g3);
            createUser("u14", g1, g4);

            {
                Rule r1 = ruleAdminService.insert(Rule.allow().withRolename("g1").withLayer("l1"));

                Set<LayerAttribute> atts =
                        Set.of(
                                LayerAttribute.none().withName("att1").withDataType("String"),
                                LayerAttribute.read().withName("att2").withDataType("String"),
                                LayerAttribute.write().withName("att3").withDataType("String"));
                Set<String> allowedStyles = Set.of("style01", "style02");

                LayerDetails d1 =
                        LayerDetails.builder()
                                .allowedStyles(allowedStyles)
                                .attributes(atts)
                                .build();

                ruleAdminService.setLayerDetails(r1.getId(), d1);
            }
            {
                Rule r1 = ruleAdminService.insert(Rule.allow().withRolename("g2").withLayer("l1"));

                Set<LayerAttribute> atts =
                        Set.of(
                                LayerAttribute.read().withName("att1").withDataType("String"),
                                LayerAttribute.write().withName("att2").withDataType("String"),
                                LayerAttribute.none().withName("att3").withDataType("String"));
                Set<String> allowedStyles = Set.of("style02", "style03");

                LayerDetails d1 =
                        LayerDetails.builder()
                                .allowedStyles(allowedStyles)
                                .attributes(atts)
                                .build();

                ruleAdminService.setLayerDetails(r1.getId(), d1);
            }
            {
                Rule r1 = ruleAdminService.insert(Rule.allow().withRolename("g3").withLayer("l1"));

                LayerDetails d1 = LayerDetails.builder().build();

                ruleAdminService.setLayerDetails(r1.getId(), d1);
            }
            {
                Rule r1 = ruleAdminService.insert(Rule.deny().withRolename("g4").withLayer("l1"));
            }
        }

        // LOGGER.info("SETUP ENDED, STARTING TESTS========================================");

        assertEquals(4, ruleAdminService.getCountAll());

        // ===

        // TEST u1
        {
            RuleFilter filterU1;
            filterU1 = new RuleFilter(SpecialFilterType.ANY);
            filterU1.setUser("u1");

            // LOGGER.info("getMatchingRules ========================================");
            assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());

            // LOGGER.info("getAccessInfo ========================================");
            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filterU1);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        }

        // TEST u2
        {
            RuleFilter filter;
            filter = new RuleFilter(SpecialFilterType.ANY);
            filter.setUser("u2");
            filter.setLayer("l1");

            assertEquals(1, ruleReaderService.getMatchingRules(filter).size());

            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNotNull(accessInfo.getAttributes());
            assertEquals(3, accessInfo.getAttributes().size());
            assertEquals(
                    Set.of(
                            LayerAttribute.read().withName("att1").withDataType("String"),
                            LayerAttribute.write().withName("att2").withDataType("String"),
                            LayerAttribute.none().withName("att3").withDataType("String")),
                    accessInfo.getAttributes());

            assertEquals(2, accessInfo.getAllowedStyles().size());
        }

        // TEST u3
        // merging attributes at higher access level
        // merging styles
        {
            RuleFilter filter;
            filter = new RuleFilter(SpecialFilterType.ANY);
            filter.setUser("u12");
            filter.setLayer("l1");

            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNotNull(accessInfo.getAttributes());
            assertEquals(3, accessInfo.getAttributes().size());
            assertEquals(
                    Set.of(
                            LayerAttribute.read().withName("att1").withDataType("String"),
                            LayerAttribute.write().withName("att2").withDataType("String"),
                            LayerAttribute.write().withName("att3").withDataType("String")),
                    accessInfo.getAttributes());

            assertEquals(3, accessInfo.getAllowedStyles().size());
        }

        // TEST u4
        // merging attributes to full access
        // unconstraining styles

        {
            RuleFilter filter;
            filter = new RuleFilter(SpecialFilterType.ANY);
            filter.setUser("u13");
            filter.setLayer("l1");

            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            // LOGGER.info("attributes: " + accessInfo.getAttributes());
            assertTrue(accessInfo.getAttributes().isEmpty());
            //            assertEquals(3, accessInfo.getAttributes().size());
            //            assertEquals(
            //                    new HashSet(Arrays.asList(
            //                        new LayerAttribute("att1", "String", AccessType.READONLY),
            //                        new LayerAttribute("att2", "String", AccessType.READWRITE),
            //                        new LayerAttribute("att3", "String", AccessType.READWRITE))),
            //                    accessInfo.getAttributes());

            assertTrue(accessInfo.getAllowedStyles().isEmpty());
        }
    }

    /** Added for issue #23 */
    @Test
    public void testNullAllowableStyles() {
        assertEquals(0, ruleAdminService.getCountAll());

        {
            GeoServerUserGroup g1 = createRole("g1");
            GeoServerUserGroup g2 = createRole("g2");

            GeoServerUser u1 = createUser("u1", g1, g2);

            // no details for first rule
            {
                ruleAdminService.insert(
                        Rule.allow().withPriority(30).withRolename("g2").withLayer("l1"));
            }
            // some allowed styles for second rule
            {
                Rule r1 =
                        ruleAdminService.insert(
                                Rule.allow().withPriority(40).withRolename("g1").withLayer("l1"));

                LayerDetails d1 =
                        LayerDetails.builder().allowedStyles(Set.of("style01", "style02")).build();

                ruleAdminService.setLayerDetails(r1.getId(), d1);
            }
        }

        // LOGGER.info("SETUP ENDED, STARTING TESTS========================================");

        assertEquals(2, ruleAdminService.getCountAll());

        // ===

        // TEST u1
        {
            RuleFilter filterU1;
            filterU1 = new RuleFilter(SpecialFilterType.ANY);
            filterU1.setUser("u1");

            // LOGGER.info("getMatchingRules ========================================");
            assertEquals(2, ruleReaderService.getMatchingRules(filterU1).size());

            // LOGGER.info("getAccessInfo ========================================");
            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filterU1);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());

            assertTrue(accessInfo.getAllowedStyles().isEmpty());
        }
    }

    @Test
    public void testIPAddress() {

        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        createRole("g1");
        createRole("g2");

        IPAddressRange ip10 = IPAddressRange.fromCidrSignature("10.10.100.0/24");
        IPAddressRange ip192 = IPAddressRange.fromCidrSignature("192.168.0.0/16");

        Rule r1 =
                Rule.allow()
                        .withPriority(10)
                        .withRolename("g1")
                        .withService("s1")
                        .withRequest("r1")
                        .withWorkspace("w1")
                        .withLayer("l1")
                        .withAddressRange(ip10);
        Rule r2 =
                Rule.allow()
                        .withPriority(20)
                        .withRolename("g2")
                        .withService("s1")
                        .withRequest("r2")
                        .withWorkspace("w2")
                        .withLayer("l2")
                        .withAddressRange(ip10);
        Rule r3 =
                Rule.allow()
                        .withPriority(30)
                        .withRolename("g1")
                        .withService("s3")
                        .withRequest("r3")
                        .withWorkspace("w3")
                        .withLayer("l3")
                        .withAddressRange(ip192);
        Rule r4 = Rule.allow().withPriority(40).withRolename("g1");

        r1 = ruleAdminService.insert(r1);
        r2 = ruleAdminService.insert(r2);
        r3 = ruleAdminService.insert(r3);
        r4 = ruleAdminService.insert(r4);

        // test without address filtering

        assertEquals(4, getMatchingRules("*", "*", "*", "*", "*", "*", "*", "*").size());
        assertEquals(3, getMatchingRules("*", "g1", "*", "*", "*", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "g2", "*", "*", "*", "*", "*", "*").size());
        assertEquals(2, getMatchingRules("*", "g1", "*", "*", "s1", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "*", "*", "*", "ZZ", "*", "*", "*").size());

        // test with  address filtering
        assertEquals(3, getMatchingRules("*", "*", "*", "10.10.100.4", "*", "*", "*", "*").size());
        assertEquals(2, getMatchingRules("*", "g1", "*", "10.10.100.4", "*", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "*", "*", "10.10.1.4", "*", "*", "*", "*").size());
        assertEquals(2, getMatchingRules("*", "*", "*", "192.168.1.1", "*", "*", "*", "*").size());
        assertEquals(1, getMatchingRules("*", "*", "*", null, "*", "*", "*", "*").size());

        List<Rule> matchingRules = getMatchingRules("*", "*", "*", "BAD", "*", "*", "*", "*");
        assertEquals(0, matchingRules.size());
    }

    @Test
    public void testGetRulesForUserOnly() {
        assertEquals(0, ruleAdminService.getCountAll());

        GeoServerUserGroup g1 = createRole("g1");
        GeoServerUserGroup g2 = createRole("g2");
        GeoServerUserGroup g3a = createRole("g3a");
        GeoServerUserGroup g3b = createRole("g3b");

        String u1 = "TestUser1";
        String u2 = "TestUser2";
        String u3 = "TestUser3";

        createUser(u1, g1);
        createUser(u2, g2);
        createUser(u3, g3a, g3b);

        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(10)
                        .withRolename("g1")
                        .withService("s1")
                        .withRequest("r1")
                        .withWorkspace("w1")
                        .withLayer("l1"));
        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(20)
                        .withRolename("g2")
                        .withService("s2")
                        .withRequest("r2")
                        .withWorkspace("w2")
                        .withLayer("l2"));
        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(30)
                        .withRolename("g1")
                        .withService("s3")
                        .withRequest("r3")
                        .withWorkspace("w3")
                        .withLayer("l3"));
        ruleAdminService.insert(Rule.allow().withPriority(40).withRolename("g1"));
        ruleAdminService.insert(Rule.allow().withPriority(50).withRolename("g3a"));
        ruleAdminService.insert(Rule.allow().withPriority(60).withRolename("g3b"));

        RuleFilter filter;

        filter = createFilter(u1, null, null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(u1, null, "s1");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(u1, null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter("anonymous", null, null);
        assertEquals(0, ruleReaderService.getMatchingRules(filter).size());
    }

    @Test
    public void testAdminRules() {

        GeoServerUser user = createUser("auth00");

        ruleAdminService.insert(
                Rule.allow()
                        .withPriority(10)
                        .withUsername(user.getName())
                        .withService("s1")
                        .withRequest("r1")
                        .withWorkspace("w1")
                        .withLayer("l1"));

        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.setWorkspace("w1");

        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());

        // let's add a USER adminrule

        adminruleAdminService.insert(
                AdminRule.user().withPriority(20).withUsername(user.getName()));

        accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());

        // let's add an ADMIN adminrule on workspace w1

        adminruleAdminService.insert(
                AdminRule.admin()
                        .withPriority(10)
                        .withUsername(user.getName())
                        .withWorkspace("w1"));

        accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertTrue(accessInfo.isAdminRights());
    }

    //    @Disabled
    @Test
    public void testMultiRoles() {
        assertEquals(0, ruleAdminService.getCountAll());

        GeoServerUserGroup p1 = createRole("p1");
        GeoServerUserGroup p2 = createRole("p2");
        @SuppressWarnings("unused")
        GeoServerUserGroup p3 = createRole("p3");

        final String u1 = "TestUser1";
        final String u2 = "TestUser2";
        final String u3 = "TestUser3";

        createUser(u1, p1);
        createUser(u2, p2);
        createUser(u3, p1, p2);

        ruleAdminService.insert(
                rule(10, u1, "p1", null, null, "s1", "r1", null, "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(
                rule(20, u2, "p2", null, null, "s1", "r2", null, "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(
                rule(30, u1, null, null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(40, u2, null, null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(50, u3, null, null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(51, u3, "p1", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(52, u3, "p2", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(60, null, "p1", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(70, null, "p2", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(80, null, "p3", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(901, u1, "p2", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(902, u2, "p1", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(
                rule(999, null, null, null, null, null, null, null, null, null, GrantType.ALLOW));

        assertRules(
                createFilter("*", "*"),
                new Integer[] {10, 20, 30, 40, 50, 51, 52, 60, 70, 80, 901, 902, 999});
        assertRules(createFilter("*", null), new Integer[] {30, 40, 50, 999});
        assertRules(createFilter("*", "NO"), new Integer[] {30, 40, 50, 999});
        assertRules(createFilter("*", "p1"), new Integer[] {10, 30, 40, 50, 51, 60, 902, 999});
        assertRules(createFilter("*", "p1,NO"), new Integer[] {10, 30, 40, 50, 51, 60, 902, 999});
        assertRules(
                createFilter("*", "p1,p2"),
                new Integer[] {10, 20, 30, 40, 50, 51, 52, 60, 70, 901, 902, 999});
        assertRules(
                createFilter("*", "p1,p2,NO"),
                new Integer[] {10, 20, 30, 40, 50, 51, 52, 60, 70, 901, 902, 999});

        assertRules(createFilter(null, "*"), new Integer[] {60, 70, 80, 999});
        assertRules(createFilter(null, null), new Integer[] {999});
        assertRules(createFilter(null, "NO"), new Integer[] {999});
        assertRules(createFilter(null, "p1"), new Integer[] {60, 999});
        assertRules(createFilter(null, "p1,NO"), new Integer[] {60, 999});
        assertRules(createFilter(null, "p1,p2"), new Integer[] {60, 70, 999});
        assertRules(createFilter(null, "p1,p2,NO"), new Integer[] {60, 70, 999});

        assertRules(createFilter("NO", "*"), new Integer[] {999});
        assertRules(createFilter("NO", null), new Integer[] {999});
        assertRules(createFilter("NO", "NO"), new Integer[] {999});
        assertRules(createFilter("NO", "p1"), new Integer[] {999});
        assertRules(createFilter("NO", "p1,NO"), new Integer[] {999});
        assertRules(createFilter("NO", "p1,p2"), new Integer[] {999});
        assertRules(createFilter("NO", "p1,p2,NO"), new Integer[] {999});

        assertRules(createFilter(u1, "*"), new Integer[] {10, 30, 60, 999});
        assertRules(createFilter(u1, null), new Integer[] {30, 999});
        assertRules(createFilter(u1, "NO"), new Integer[] {30, 999});
        assertRules(createFilter(u1, "p1"), new Integer[] {10, 30, 60, 999});
        assertRules(createFilter(u1, "p1,NO"), new Integer[] {10, 30, 60, 999});
        assertRules(createFilter(u1, "p1,p2"), new Integer[] {10, 30, 60, 999});
        assertRules(createFilter(u1, "p1,p2,NO"), new Integer[] {10, 30, 60, 999});

        assertRules(createFilter(u3, "*"), new Integer[] {50, 51, 52, 60, 70, 999});
        assertRules(createFilter(u3, null), new Integer[] {50, 999});
        assertRules(createFilter(u3, "NO"), new Integer[] {50, 999});
        assertRules(createFilter(u3, "p1"), new Integer[] {50, 51, 60, 999});
        assertRules(createFilter(u3, "p2"), new Integer[] {50, 52, 70, 999});
        assertRules(createFilter(u3, "p1,NO"), new Integer[] {50, 51, 60, 999});
        assertRules(createFilter(u3, "p1,p2"), new Integer[] {50, 51, 52, 60, 70, 999});
        assertRules(createFilter(u3, "p1,p2,p3"), new Integer[] {50, 51, 52, 60, 70, 999});
        assertRules(createFilter(u3, "p1,p2,NO"), new Integer[] {50, 51, 52, 60, 70, 999});
    }

    private RuleFilter createFilter(String userName, String groupName) {
        return new RuleFilter(userName, groupName, "*", "*", "*", "*", "*", "*", "*");
    }

    private void assertRules(RuleFilter filter, Integer[] expectedPriorities) {
        RuleFilter origFilter = filter.clone();
        List<Rule> rules = ruleReaderService.getMatchingRules(filter);

        Set<Long> pri = rules.stream().map(r -> r.getPriority()).collect(Collectors.toSet());
        Set<Long> exp =
                Arrays.asList(expectedPriorities).stream()
                        .map(i -> i.longValue())
                        .collect(Collectors.toSet());
        assertEquals(exp, pri, "Bad rule set selected for filter " + origFilter);
    }

    private List<Rule> getMatchingRules(
            String userName,
            String profileName,
            String instanceName,
            String sourceAddress,
            String service,
            String request,
            String workspace,
            String layer) {

        RuleFilter filter =
                new RuleFilter(
                        userName,
                        profileName,
                        instanceName,
                        sourceAddress,
                        service,
                        request,
                        null,
                        workspace,
                        layer);
        return ruleReaderService.getMatchingRules(filter);
    }

    private AccessInfo getAccessInfo(
            String userName,
            String roleName,
            String instanceName,
            String sourceAddress,
            String service,
            String request,
            String workspace,
            String layer) {
        return ruleReaderService.getAccessInfo(
                new RuleFilter(
                        userName,
                        roleName,
                        instanceName,
                        sourceAddress,
                        service,
                        request,
                        null,
                        workspace,
                        layer));
    }
}
