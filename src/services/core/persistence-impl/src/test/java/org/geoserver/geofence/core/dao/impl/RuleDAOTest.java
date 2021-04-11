/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import org.geoserver.geofence.core.model.AccessType;
import org.geoserver.geofence.core.model.CatalogMode;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.GrantType;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.InsertPosition;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.jpa.model.JPALayerDetails;
import org.junit.Test;
import org.locationtech.jts.geom.MultiPolygon;

/** @author ETj (etj at geo-solutions.it) */
public class RuleDAOTest extends BaseDAOTest {

    private Rule createRule() {
        GSUser user = createUserAndGroup("rule_test");
        userDAO.persist(user);

        return createRule(user);
    }

    private Rule createRule(GSUser persistentUser) {
        Rule rule = new Rule();
        rule.setUsername(persistentUser.getName());
        rule.setPriority(0);
        rule.setAccess(GrantType.ALLOW);
        rule = ruleDAO.persist(rule);
        return rule;
    }

    @Test
    public void testPersistRule() throws Exception {

        final String username = "rule_test";
        Long uid;
        Long rid;
        {
            GSUser user = createUserAndGroup(username);
            userDAO.persist(user);
            uid = user.getId();

            Rule rule = createRule(user);
            rid = rule.getId();
        }

        // test save & load
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());
            assertEquals(username, loaded.getUsername());
            assertEquals(GrantType.ALLOW, loaded.getAccess());

            loaded.setAccess(GrantType.DENY);
            ruleDAO.merge(loaded);
        }

        {
            Rule loaded2 = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded2);
            assertEquals(GrantType.DENY, loaded2.getAccess());
        }

        ruleDAO.removeById(rid);
        userDAO.removeById(uid);
        assertNull("Rule not deleted", ruleDAO.find(rid));
    }

    @Test
    public void testPersistLayerDetails() throws Exception {

        long rid = createRule().getId();

        // add details
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setDefaultStyle("default");
            details.getAttributes().add(new LayerAttribute("a1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("a2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("a3", AccessType.READWRITE));
            details.getAttributes().add(new LayerAttribute("a4", AccessType.READWRITE));
            loaded.setLayerDetails(details);
            ruleDAO.merge(loaded);
        }

        // check everything's fine
        {
            Rule rule = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", rule);
            LayerDetails details = rule.getLayerDetails();
            assertNotNull(details);
            assertEquals("default", details.getDefaultStyle());
            assertEquals(4, details.getAttributes().size());
        }
        // modify the details
        {
            Rule rule = ruleDAO.find(rid);
            LayerDetails details = rule.getLayerDetails();
            details.setDefaultStyle("another");
            ruleDAO.merge(rule);

            rule = ruleDAO.find(rid);
            details = rule.getLayerDetails();
            assertEquals("another", details.getDefaultStyle());
        }

        // try removing the details
        {
            Rule rule = ruleDAO.find(rid);
            LayerDetails details = rule.getLayerDetails();
            assertNotNull(details);
            rule.setLayerDetails(null);

            rule = ruleDAO.merge(rule);
            assertNull(rule.getLayerDetails());

            rule = ruleDAO.find(rid);
            assertNull(rule.getLayerDetails());
        }
    }

    @Test
    public void testPersistRuleLimits() throws Exception {

        long rid = createRule().getId();

        // add limits
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getRuleLimits());

            RuleLimits limits = new RuleLimits();
            limits.setAllowedArea(buildMultiPolygon());
            limits.setCatalogMode(CatalogMode.CHALLENGE);

            loaded.setRuleLimits(limits);
            ruleDAO.merge(loaded);
        }

        // check everything's fine
        {
            Rule rule = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", rule);
            RuleLimits limits = rule.getRuleLimits();
            assertNotNull(limits);
            assertNotNull(limits.getAllowedArea());
            assertEquals(CatalogMode.CHALLENGE, limits.getCatalogMode());
        }
        // modify the limits
        {
            Rule rule = ruleDAO.find(rid);
            RuleLimits limits = rule.getRuleLimits();
            limits.setCatalogMode(CatalogMode.MIXED);
            ruleDAO.merge(rule);

            rule = ruleDAO.find(rid);
            assertEquals(CatalogMode.MIXED, rule.getRuleLimits().getCatalogMode());
        }

        // try removing the details
        {
            Rule rule = ruleDAO.find(rid);
            assertNotNull(rule.getRuleLimits());
            rule.setRuleLimits(null);

            rule = ruleDAO.merge(rule);
            assertNull(rule.getRuleLimits());

            rule = ruleDAO.find(rid);
            assertNull(rule.getRuleLimits());
        }
    }

    @Test
    public void testAttributeNullAccessType() throws Exception {

        long rid = createRule().getId();

        // add details
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setDefaultStyle("default");
            details.getAttributes().add(new LayerAttribute("a1", null));
            loaded.setLayerDetails(details);
            try {
                ruleDAO.merge(loaded);
                fail("Expected NPE");
            } catch (NullPointerException expected) {
                assertThat(
                        expected.getMessage(), containsString("Null access type for attribute a1"));
            }
        }
    }

    @Test
    public void testChangeLayerDetails() throws Exception {

        final long rid = createRule().getId();

        // create rule and details
        {
            Rule loaded = ruleDAO.find(rid);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setDefaultStyle("default");
            details.getAllowedStyles().add("s1");
            details.getAttributes().add(new LayerAttribute("a1", AccessType.NONE));
            loaded.setLayerDetails(details);

            Rule merged = ruleDAO.merge(loaded);
            assertEquals(details, merged.getLayerDetails());

            loaded = ruleDAO.find(rid);
            assertEquals(details, loaded.getLayerDetails());
        }

        {
            Rule loaded = ruleDAO.find(rid);
            LayerDetails oldDetails = loaded.getLayerDetails();
            assertNotNull(oldDetails);
            loaded.setLayerDetails(null);

            // remove old details
            ruleDAO.merge(loaded);
        }

        // create new details
        {
            Rule loaded = ruleDAO.find(rid);
            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setDefaultStyle("default2");
            details.getAllowedStyles().add("s2");
            details.getAttributes().add(new LayerAttribute("z1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("z2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("z3", AccessType.READWRITE));
            loaded.setLayerDetails(details);
            ruleDAO.merge(loaded);

            loaded = ruleDAO.find(rid);
            assertEquals(details, loaded.getLayerDetails());
        }
    }

    @Test
    public void testLayerDetailsPK() {

        long rid = createRule().getId();

        // add details from detailsDAO
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setDefaultStyle("default");
            details.getAttributes().add(new LayerAttribute("a1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("a1", AccessType.READONLY));
            loaded.setLayerDetails(details);

            try {
                ruleDAO.merge(loaded);
                fail("Dup attribute name not recognised");
            } catch (IllegalArgumentException expected) {
                assertThat(expected.getMessage(), containsString("Duplicate attribute names: a1"));
            } catch (Exception unexpected) {
                throw unexpected;
            }
        }
    }

    @Test
    public void testRuleDetails() {
        final Long id;

        {
            Rule r1 = new Rule(10, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
            // ruleAdminService.insert(r1);
            r1 = ruleDAO.persist(r1);
            id = r1.getId();
            assertNotNull(id);
        }

        // set new details
        {
            LayerDetails details = new LayerDetails();
            details.getAllowedStyles().add("style1");
            details.getAllowedStyles().add("style2");
            details.getAttributes().add(new LayerAttribute("attr1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("attr2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("attr3", AccessType.READWRITE));

            setLayerDetails(id, details);
        }

        // check details
        {
            // Rule loaded = ruleAdminService.get(id);
            Rule loaded = ruleDAO.find(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertEquals(3, details.getAttributes().size());
            assertEquals(2, details.getAllowedStyles().size());
            assertTrue(details.getAllowedStyles().contains("style1"));
            assertTrue(details.getAllowedStyles().contains("style2"));
        }

        // add geom
        {
            Rule loaded = ruleDAO.find(id);
            LayerDetails details = loaded.getLayerDetails();

            MultiPolygon mpoly = buildMultiPolygon();
            details.setArea(mpoly);
            setLayerDetails(id, details);
        }

        // check geom
        {
            Rule loaded = ruleDAO.find(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertNotNull(details.getArea());
        }

        // remove details
        {
            assertNotNull(ruleDAO.find(id).getLayerDetails());
            setLayerDetails(id, null);
            assertNull(ruleDAO.find(id).getLayerDetails());
        }

        // remove Rule and cascade on details
        {
            LayerDetails details = new LayerDetails();
            // ruleAdminService.setDetails(id, details);
            setLayerDetails(id, details);
            // Rule loaded = ruleAdminService.get(id);
            Rule loaded = ruleDAO.find(id);
            assertNotNull(loaded.getLayerDetails());
            assertNotNull(layerDetailsRepository.find(id));

            ruleDAO.removeById(id);

            assertNull(ruleDAO.find(id));
            assertNull(layerDetailsRepository.find(id));
        }
    }

    public void setLayerDetails(Long ruleId, LayerDetails details) {
        Rule rule = ruleDAO.find(ruleId);
        if (rule == null) throw new RuntimeException("Rule not found");

        if (rule.getLayer() == null && details != null)
            throw new RuntimeException("Rule does not refer to a fixed layer");

        if (rule.getAccess() != GrantType.ALLOW && details != null)
            throw new RuntimeException("Rule is not of ALLOW type");

        // remove old details if any
        if (rule.getLayerDetails() != null) {
            rule.setLayerDetails(null);
            ruleDAO.merge(rule);
        }

        rule = ruleDAO.find(ruleId);
        if (rule.getLayerDetails() != null)
            throw new IllegalStateException("LayerDetails (1) should be null");

        if (layerDetailsRepository.find(ruleId) != null)
            throw new IllegalStateException("LayerDetails (2) should be null");

        List<JPALayerDetails> allDetails = layerDetailsRepository.findAll();
        if (!allDetails.isEmpty()) {
            LOGGER.error("LayerDetails (3) should be null --> " + allDetails);
            throw new IllegalStateException(
                    "LayerDetails (3) should be null --> " + allDetails.size());
        }

        LOGGER.info("Setting details " + details + "for " + rule);
        rule.setLayerDetails(details);
        Rule merged = ruleDAO.merge(rule);
        assertEquals(details, merged.getLayerDetails());
        assertEquals(details, ruleDAO.find(rule.getId()).getLayerDetails());
    }

    @Test
    public void testDupRuleTest() throws Exception {

        {
            Rule rule1 =
                    new Rule(10, null, null, null, null, "s", null, null, null, GrantType.ALLOW);
            Rule rule2 =
                    new Rule(10, null, null, null, null, "s", null, null, null, GrantType.ALLOW);

            ruleDAO.persist(rule1);

            try {
                ruleDAO.persist(rule2);
                fail("Dup'd rule not detected");
            } catch (Exception e) {
                // ok
            }
        }
    }

    @Test
    public void testDupRule2Test() throws Exception {
        {
            Rule rule1 =
                    new Rule(
                            10,
                            null,
                            null,
                            null,
                            new IPAddressRange("1.2.3.4/32"),
                            "s",
                            null,
                            null,
                            null,
                            GrantType.ALLOW);
            Rule rule2 =
                    new Rule(
                            10,
                            null,
                            null,
                            null,
                            new IPAddressRange("1.2.3.4/32"),
                            "s",
                            null,
                            null,
                            null,
                            GrantType.ALLOW);

            ruleDAO.persist(rule1);

            try {
                ruleDAO.persist(rule2);
                fail("Dup'd rule not detected (addressRange)");
            } catch (Exception e) {
                // ok
            }
        }
    }

    @Test
    public void testDupRule3Test() throws Exception {
        {
            Rule rule1 =
                    new Rule(
                            10,
                            null,
                            null,
                            null,
                            new IPAddressRange("1.2.3.5/32"),
                            "s",
                            null,
                            null,
                            null,
                            GrantType.ALLOW);
            Rule rule2 =
                    new Rule(
                            10,
                            null,
                            null,
                            null,
                            new IPAddressRange("1.2.3.4/32"),
                            "s",
                            null,
                            null,
                            null,
                            GrantType.ALLOW);

            ruleDAO.persist(rule1);

            try {
                ruleDAO.persist(rule2);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Invalid dup'd rule (addressRange)");
            }
        }
    }

    @Test
    public void testShift() {
        assertEquals(0, ruleDAO.countAll());

        Rule r1 = new Rule(10, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, null, null, null, "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, null, null, null, "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, null, null, null, "s4", "r3", "w3", "l3", GrantType.ALLOW);

        ruleDAO.persist(r1);
        ruleDAO.persist(r2);
        ruleDAO.persist(r3);
        ruleDAO.persist(r4);

        int n = ruleDAO.shift(20, 5);
        assertEquals(3, n);

        List<Rule> loaded = ruleDAO.findAllByService("s3");
        assertEquals(1, loaded.size());
        assertEquals(35, loaded.get(0).getPriority());

        // perform another shift: since there are no rule in there, the shift sould be
        // skipped
        n = ruleDAO.shift(20, 5);
        assertEquals(-1, n);
    }

    @Test
    public void testSwap() {
        assertEquals(0, ruleDAO.countAll());

        Rule r1 = new Rule(10, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, null, null, null, "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, null, null, null, "s3", "r3", "w3", "l3", GrantType.ALLOW);

        r1 = ruleDAO.persist(r1);
        r2 = ruleDAO.persist(r2);
        r3 = ruleDAO.persist(r3);

        ruleDAO.swap(r1.getId(), r2.getId());

        assertEquals(20, ruleDAO.find(r1.getId()).getPriority());
        assertEquals(10, ruleDAO.find(r2.getId()).getPriority());
        assertEquals(30, ruleDAO.find(r3.getId()).getPriority());
    }

    @Test
    public void testPersistRulePosition() throws Exception {

        long id1;
        {
            assertEquals(0, ruleDAO.countAll());
            Rule rule1 =
                    new Rule(1000, null, null, null, null, "s", null, null, null, GrantType.ALLOW);
            rule1 = ruleDAO.persist(rule1, InsertPosition.FROM_START);
            id1 = rule1.getId();
        }

        {
            Rule loaded = ruleDAO.find(id1);
            assertNotNull(loaded);
            assertEquals(1, loaded.getPriority());
        }

        ruleDAO.persist(
                new Rule(10, null, null, null, null, "s10", null, null, null, GrantType.ALLOW));
        ruleDAO.persist(
                new Rule(20, null, null, null, null, "s20", null, null, null, GrantType.ALLOW));

        {
            assertEquals(3, ruleDAO.countAll());
            Rule rule1 =
                    new Rule(1000, null, null, null, null, "sZ", null, null, null, GrantType.ALLOW);
            rule1 = ruleDAO.persist(rule1, InsertPosition.FROM_START);
            assertEquals(21, rule1.getPriority());
        }

        {
            Rule rule1 =
                    new Rule(
                            1, null, null, null, null, "second", null, null, null, GrantType.ALLOW);
            rule1 = ruleDAO.persist(rule1, InsertPosition.FROM_START);
            assertEquals(10, rule1.getPriority());
        }

        {
            Rule rule1 =
                    new Rule(0, null, null, null, null, "last", null, null, null, GrantType.ALLOW);
            rule1 = ruleDAO.persist(rule1, InsertPosition.FROM_END);
            assertEquals(23, rule1.getPriority());
        }

        {
            Rule rule1 =
                    new Rule(1, null, null, null, null, "last2", null, null, null, GrantType.ALLOW);
            rule1 = ruleDAO.persist(rule1, InsertPosition.FROM_END);
            assertEquals(23, rule1.getPriority());
        }
    }

    @Test
    public void testIPRangeTest() throws Exception {

        Long rid;
        {
            GSUser user = createUserAndGroup("rule_test");
            userDAO.persist(user);

            Rule rule = new Rule();
            rule.setUsername("rule_test");
            rule.setPriority(0);
            rule.setAddressRange(new IPAddressRange("10.11.0.0/16"));
            rule.setAccess(GrantType.ALLOW);
            rule = ruleDAO.persist(rule);

            rid = rule.getId();
        }

        // test save & load by id
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNotNull(loaded.getAddressRange());
            assertEquals("10.11.0.0/16", loaded.getAddressRange().getCidrSignature());
        }

        // test search
        {
            IPAddressRange addressRange = new IPAddressRange("10.11.0.0/16");
            List<Rule> loadedList = ruleDAO.findAllByAddressRange(addressRange);
            assertEquals(1, loadedList.size());

            Rule loaded = loadedList.get(0);

            // DO UPDATE
            loaded.setAddressRange(new IPAddressRange("111.222.223.0/24"));
            ruleDAO.merge(loaded);
        }

        // test update
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNotNull(loaded.getAddressRange());
            assertEquals("111.222.223.0/24", loaded.getAddressRange().getCidrSignature());

            // DO REMOVE
            loaded.setAddressRange(null);
            ruleDAO.merge(loaded);
        }

        // test remove
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getAddressRange());
        }
    }
}
