/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.servicetest;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.jpa.model.JPAAccessType;
import org.geoserver.geofence.jpa.model.JPAGFUser;
import org.geoserver.geofence.jpa.model.JPAGSInstance;
import org.geoserver.geofence.jpa.model.JPAGSUser;
import org.geoserver.geofence.jpa.model.JPAGrantType;
import org.geoserver.geofence.jpa.model.JPALayerAttribute;
import org.geoserver.geofence.jpa.model.JPALayerDetails;
import org.geoserver.geofence.jpa.model.JPARule;
import org.geoserver.geofence.jpa.model.JPAUserGroup;
import org.geoserver.geofence.services.GFUserAdminService;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.UserAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.rest.utils.InstanceCleaner;
import org.springframework.beans.factory.InitializingBean;

/** @author ETj (etj at geo-solutions.it) */
public class MainTest implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);
    private RuleAdminService ruleAdminService;
    private UserGroupAdminService userGroupAdminService;
    private UserAdminService userAdminService;
    private GFUserAdminService gfUserAdminService;
    private InstanceAdminService instanceAdminService;
    private InstanceCleaner instanceCleaner;

    public void afterPropertiesSet() throws Exception {
        LOGGER.info("===== Starting Geofence REST test services =====");

        instanceCleaner.removeAll();
        instanceCleaner.removeAllGFUsers();

        setUpTestRule();
    }

    private void setUpTestRule() {

        ShortGroup sp1 = new ShortGroup();
        sp1.setName("test_profile");

        long p1id = userGroupAdminService.insert(sp1);

        ShortGroup sp2 = new ShortGroup();
        sp2.setName("test_profile2");

        long p2id = userGroupAdminService.insert(sp2);
        JPAUserGroup p2 = userGroupAdminService.get(p2id);

        JPAGFUser u0 = new JPAGFUser();
        u0.setName("admin");
        u0.setPassword("password");
        u0.setEnabled(true);
        u0.setFullName("Sample G.F. Admin");
        u0.setEmailAddress("gf.admin@geofence.net");
        u0.setExtId("sample_geoserver_user");
        gfUserAdminService.insert(u0);

        JPAGSUser u1 = new JPAGSUser();
        u1.setAdmin(true);
        u1.setName("admin");
        u1.setPassword("password");
        u1.getGroups().add(userGroupAdminService.get(p1id));
        u1.setEnabled(true);
        u1.setFullName("Sample G.S. Admin");
        u1.setEmailAddress("gs.admin@geofence.net");
        u1.setExtId("sample_geoserver_user");
        userAdminService.insert(u1);

        JPAGSInstance gs1 = new JPAGSInstance();
        gs1.setName("geoserver01");
        gs1.setUsername("admin");
        gs1.setPassword("geoserver");
        gs1.setBaseURL("http://localhost/geoserver");
        gs1.setDescription("A sample instance");
        instanceAdminService.insert(gs1);

        JPARule r0 =
                new JPARule(
                        5,
                        u1.getName(),
                        p2.getName(),
                        gs1,
                        null,
                        "s0",
                        "r0",
                        null,
                        null,
                        JPAGrantType.ALLOW);
        ruleAdminService.insert(r0);

        final Long r1id;

        {
            JPARule r1 =
                    new JPARule(
                            10, null, null, null, null, "s1", "r1", "w1", "l1", JPAGrantType.ALLOW);
            ruleAdminService.insert(r1);
            r1id = r1.getId();
        }

        // save details and check it has been saved
        final Long lid1;
        {
            JPALayerDetails details = new JPALayerDetails();
            details.getAllowedStyles().add("FIRST_style1");
            details.getAttributes().add(new JPALayerAttribute("FIRST_attr1", JPAAccessType.NONE));
            ruleAdminService.setDetails(r1id, details);
            lid1 = details.getId();
            assert lid1 != null;
        }

        // check details have been set in Rule
        {
            JPARule loaded = ruleAdminService.get(r1id);
            JPALayerDetails details = loaded.getLayerDetails();
            assert details != null;
            assert lid1.equals(details.getId());
            assert 1 == details.getAttributes().size();
            assert 1 == details.getAllowedStyles().size();
            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
        }

        // set new details
        final Long lid2;
        {
            JPALayerDetails details = new JPALayerDetails();
            details.getAttributes().add(new JPALayerAttribute("attr1", JPAAccessType.NONE));
            details.getAttributes().add(new JPALayerAttribute("attr2", JPAAccessType.READONLY));
            details.getAttributes().add(new JPALayerAttribute("attr3", JPAAccessType.READWRITE));

            assert 3 == details.getAttributes().size();

            Set<String> styles = new HashSet<String>();
            styles.add("style1");
            styles.add("style2");
            ruleAdminService.setAllowedStyles(r1id, styles);

            ruleAdminService.setDetails(r1id, details);
            lid2 = details.getId();
            assert lid2 != null;
        }

        // check details
        {
            JPARule loaded = ruleAdminService.get(r1id);
            JPALayerDetails details = loaded.getLayerDetails();
            assert details != null;
            for (JPALayerAttribute layerAttribute : details.getAttributes()) {
                LOGGER.error(layerAttribute);
            }

            assert 3 == details.getAttributes().size();
            assert 2 == details.getAllowedStyles().size();
            assert details.getAllowedStyles().contains("style1");
        }
    }

    // ==========================================================================
    public void setInstanceCleaner(InstanceCleaner instanceCleaner) {
        this.instanceCleaner = instanceCleaner;
    }

    // ==========================================================================
    public void setUserGroupAdminService(UserGroupAdminService service) {
        this.userGroupAdminService = service;
    }

    public void setUserAdminService(UserAdminService service) {
        this.userAdminService = service;
    }

    public void setInstanceAdminService(InstanceAdminService service) {
        this.instanceAdminService = service;
    }

    public void setRuleAdminService(RuleAdminService service) {
        this.ruleAdminService = service;
    }

    public void setGfUserAdminService(GFUserAdminService service) {
        this.gfUserAdminService = service;
    }
}
