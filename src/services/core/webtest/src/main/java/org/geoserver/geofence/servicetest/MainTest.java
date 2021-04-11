/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.servicetest;

import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.RuleFilter;
import org.geoserver.geofence.core.dao.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.jpa.model.JPAAccessType;
import org.geoserver.geofence.jpa.model.JPAGSInstance;
import org.geoserver.geofence.jpa.model.JPAGSUser;
import org.geoserver.geofence.jpa.model.JPAGrantType;
import org.geoserver.geofence.jpa.model.JPALayerAttribute;
import org.geoserver.geofence.jpa.model.JPALayerDetails;
import org.geoserver.geofence.jpa.model.JPARule;
import org.geoserver.geofence.jpa.model.JPARuleLimits;
import org.geoserver.geofence.jpa.model.JPAUserGroup;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.RuleReaderService;
import org.geoserver.geofence.services.UserAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.dto.AccessInfo;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.dto.ShortUser;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** @author ETj (etj at geo-solutions.it) */
public class MainTest implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);

    private XmlWebApplicationContext applicationContext;

    private UserAdminService userAdminService;
    private UserGroupAdminService userGroupAdminService;
    private InstanceAdminService instanceAdminService;
    private RuleAdminService ruleAdminService;
    private RuleReaderService ruleReaderService;

    protected static final String MULTIPOLYGONWKT =
            "MULTIPOLYGON(((48 62, 48 63, 49 63, 49 62, 48 62)))";

    public void afterPropertiesSet() throws Exception {
        /**
         * *********************************************************************
         *
         * <p>WARNING, READ CAREFULLY BEFORE CHANGING ANYTHING IN THIS SETUP
         *
         * <p>This test setup is used for the ResorceAccessManager integration tests, which expect
         * the webtest to be running in Jetty with these exact contents. If you need to add more or
         * modify the contents please also make sure you're not breaking the build in those tests.
         * If you blinding modify the class and I find the tests got broken this is the destiny that
         * awaits you: http://en.wikipedia.org/wiki/Impalement
         *
         * <p>Signed: Andrea Vlad Dracul Aime
         *
         * <p>*********************************************************************
         */
        LOGGER.info("===== RESETTING DB DATA =====");
        removeAll();

        LOGGER.info("===== Creating Profiles (not actually needed while testing GS) =====");
        ShortGroup shortProfile = new ShortGroup();
        shortProfile.setName("basic");
        long pid1 = userGroupAdminService.insert(shortProfile);
        JPAUserGroup p1 = userGroupAdminService.get(pid1);

        ShortGroup shortProfile2 = new ShortGroup();
        shortProfile2.setName("advanced");
        long pid2 = userGroupAdminService.insert(shortProfile2);
        JPAUserGroup p2 = userGroupAdminService.get(pid2);

        LOGGER.info("===== Creating Users =====");
        String citeUsername = "cite";
        JPAGSUser citeUser = createUser(citeUsername);
        citeUser.getGroups().add(p1);
        userAdminService.insert(citeUser);

        String wmsUsername = "wmsuser";
        JPAGSUser wmsUser = createUser(wmsUsername);
        wmsUser.getGroups().add(p1);
        userAdminService.insert(wmsUser);

        String areaUsername = "area";
        JPAGSUser areaUser = createUser(areaUsername);
        areaUser.getGroups().add(p1);
        userAdminService.insert(areaUser);

        String statesUsername = "u-states";
        JPAGSUser uStates = createUser(statesUsername);
        uStates.getGroups().add(p1);
        userAdminService.insert(uStates);

        LOGGER.info("===== Creating Rules =====");

        JPALayerDetails ld1 = new JPALayerDetails();
        ld1.getAllowedStyles().add("style1");
        ld1.getAllowedStyles().add("style2");
        ld1.getAttributes().add(new JPALayerAttribute("attr1", JPAAccessType.NONE));
        ld1.getAttributes().add(new JPALayerAttribute("attr2", JPAAccessType.READONLY));
        ld1.getAttributes().add(new JPALayerAttribute("attr3", JPAAccessType.READWRITE));

        int priority = 0;

        /* Cite user rules */
        // allow user cite full control over the cite workspace
        ruleAdminService.insert(
                new JPARule(
                        priority++,
                        citeUsername,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "cite",
                        null,
                        JPAGrantType.ALLOW));
        // allow only getmap, getcapatbilities and reflector usage on workspace sf
        ruleAdminService.insert(
                (new JPARule(
                        priority++,
                        citeUsername,
                        null,
                        null,
                        null,
                        "wms",
                        "GetMap",
                        "sf",
                        null,
                        JPAGrantType.ALLOW)));
        ruleAdminService.insert(
                (new JPARule(
                        priority++,
                        citeUsername,
                        null,
                        null,
                        null,
                        "wms",
                        "GetCapabilities",
                        "sf",
                        null,
                        JPAGrantType.ALLOW)));
        ruleAdminService.insert(
                (new JPARule(
                        priority++,
                        citeUsername,
                        null,
                        null,
                        null,
                        "wms",
                        "reflect",
                        "sf",
                        null,
                        JPAGrantType.ALLOW)));
        // allow only GetMap and GetFeature the topp workspace

        /* wms user rules */
        ruleAdminService.insert(
                (new JPARule(
                        priority++,
                        wmsUsername,
                        null,
                        null,
                        null,
                        "wms",
                        null,
                        null,
                        null,
                        JPAGrantType.ALLOW)));

        /* all powerful but only in a restricted area */
        JPARule areaRestriction =
                new JPARule(
                        priority++,
                        areaUsername,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JPAGrantType.LIMIT);
        JPARuleLimits limits = new JPARuleLimits();
        limits.setAllowedArea((MultiPolygon) new WKTReader().read(MULTIPOLYGONWKT));
        long ruleId = ruleAdminService.insert(areaRestriction);
        ruleAdminService.setLimits(ruleId, limits);
        ruleAdminService.insert(
                (new JPARule(
                        priority++,
                        areaUsername,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JPAGrantType.ALLOW)));

        /* some users for interactive testing with the default data directory */
        // uStates can do whatever, but only on topp:states
        ruleAdminService.insert(
                new JPARule(
                        priority++,
                        statesUsername,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "topp",
                        "states",
                        JPAGrantType.ALLOW));

        // deny everything else
        ruleAdminService.insert(
                new JPARule(
                        priority++,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JPAGrantType.DENY));
        new Thread(
                        new Runnable() {

                            @Override
                            public void run() {
                                boolean success = false;
                                int cnt = 5;

                                while (!success && cnt-- > 0) {
                                    try {
                                        LOGGER.info("Waiting 5 secs...");
                                        Thread.sleep(5000);

                                        LOGGER.info("Trying creating spring remoting client...");
                                        instantiateAndRunSpringRemoting();

                                        success = true;

                                    } catch (InterruptedException ex) {
                                    } catch (Exception e) {
                                        LOGGER.warn(
                                                "Failed creating spring remoting client..."
                                                        + e.getMessage());
                                    }
                                }
                            }
                        })
                .start();

        try {
            LOGGER.info("===== User List =====");

            List<ShortUser> users = userAdminService.getList(null, null, null);
            for (ShortUser loop : users) {
                LOGGER.info("   User -> " + loop);
            }

            LOGGER.info("===== Rules =====");
            List<ShortRule> rules = ruleAdminService.getAll();
            for (ShortRule shortRule : rules) {
                LOGGER.info("   Rule -> " + shortRule);
            }

        } finally {
        }
    }

    public void instantiateAndRunSpringRemoting() {
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
        httpInvokerProxyFactoryBean.setServiceInterface(
                org.geoserver.geofence.services.RuleReaderService.class);
        httpInvokerProxyFactoryBean.setServiceUrl(
                "http://localhost:9191/geofence/remoting/RuleReader");
        httpInvokerProxyFactoryBean.afterPropertiesSet();
        RuleReaderService rrs = (RuleReaderService) httpInvokerProxyFactoryBean.getObject();

        RuleFilter filter1 =
                new RuleFilter(SpecialFilterType.DEFAULT, true)
                        .setUser("pippo")
                        .setInstance("gs1")
                        .setService("WMS");
        AccessInfo accessInfo = rrs.getAccessInfo(filter1);
        LOGGER.info(accessInfo);

        RuleFilter filter2 =
                new RuleFilter(SpecialFilterType.DEFAULT, true)
                        .setUser("pippo")
                        .setInstance("gs1")
                        .setService("WCS");
        AccessInfo accessInfo2 = rrs.getAccessInfo(filter2);
        LOGGER.info(accessInfo2);
    }

    // ==========================================================================

    protected JPAGSUser createUser(String baseName) {
        JPAGSUser user = new JPAGSUser();
        user.setName(baseName);
        return user;
    }

    // ==========================================================================

    protected void removeAll() throws NotFoundServiceEx {
        LOGGER.info("***** removeAll()");
        removeAllRules();
        removeAllUsers();
        removeAllProfiles();
        removeAllInstances();
    }

    protected void removeAllRules() throws NotFoundServiceEx {
        List<ShortRule> list = ruleAdminService.getAll();
        for (ShortRule item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = ruleAdminService.delete(item.getId());
            if (!ret) throw new IllegalStateException("Rule not removed");
        }

        if (ruleAdminService.getCountAll() != 0)
            throw new IllegalStateException("Rules have not been properly deleted");
    }

    protected void removeAllUsers() throws NotFoundServiceEx {
        List<ShortUser> list = userAdminService.getList(null, null, null);
        for (ShortUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userAdminService.delete(item.getId());
            if (!ret) throw new IllegalStateException("User not removed");
        }

        if (userAdminService.getCount(null) != 0)
            throw new IllegalStateException("Users have not been properly deleted");
    }

    protected void removeAllProfiles() throws NotFoundServiceEx {
        List<ShortGroup> list = userGroupAdminService.getList(null, null, null);
        for (ShortGroup item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userGroupAdminService.delete(item.getId());
            if (!ret) throw new IllegalStateException("Group not removed");
        }

        if (userGroupAdminService.getCount(null) != 0)
            throw new IllegalStateException("Groups have not been properly deleted");
    }

    protected void removeAllInstances() throws NotFoundServiceEx {
        List<JPAGSInstance> list = instanceAdminService.getAll();
        for (JPAGSInstance item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = instanceAdminService.delete(item.getId());
            if (!ret) throw new IllegalStateException("GSInstance not removed");
        }

        if (instanceAdminService.getCount(null) != 0)
            throw new IllegalStateException("Instances have not been properly deleted");
    }

    // ==========================================================================

    public void setInstanceAdminService(InstanceAdminService instanceAdminService) {
        this.instanceAdminService = instanceAdminService;
    }

    public void setUserGroupAdminService(UserGroupAdminService userGroupAdminService) {
        this.userGroupAdminService = userGroupAdminService;
    }

    public void setRuleAdminService(RuleAdminService ruleAdminService) {
        this.ruleAdminService = ruleAdminService;
    }

    public void setUserAdminService(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    public void setRuleReaderService(RuleReaderService ruleReaderService) {
        this.ruleReaderService = ruleReaderService;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = (XmlWebApplicationContext) ac;
    }
}
