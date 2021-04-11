/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.AdminRuleDAO;
import org.geoserver.geofence.core.dao.GFUserDAO;
import org.geoserver.geofence.core.dao.GSInstanceDAO;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.RuleDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.jpa.repository.GeoFenceJPAConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** {@link Configuration @Configuration} to create all DAO beans backed by JPA repositories */
@Configuration
@Import(GeoFenceJPAConfiguration.class)
public class GeoFenceDAOImplConfiguration {

    public @Bean GFUserDAO gfUserDAO() {
        return new GFUserDAOImpl();
    }

    public @Bean GSInstanceDAO instanceDAO() {
        return new GSInstanceDAOImpl();
    }

    public @Bean GSUserDAO gsUserDAO() {
        return new GSUserDAOImpl();
    }

    public @Bean AdminRuleDAO adminRuleDAO() {
        return new AdminRuleDAOImpl();
    }

    public @Bean RuleDAO ruleDAO() {
        return new RuleDAOImpl();
    }

    public @Bean UserGroupDAO userGroupDAO() {
        return new UserGroupDAOImpl();
    }
}
