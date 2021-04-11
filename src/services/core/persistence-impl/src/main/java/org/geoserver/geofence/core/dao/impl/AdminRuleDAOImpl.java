/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.function.Function;
import lombok.Getter;
import org.geoserver.geofence.core.dao.AdminRuleDAO;
import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.jpa.model.JPAAdminRule;
import org.geoserver.geofence.jpa.repository.AdminRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class AdminRuleDAOImpl extends PrioritizableDAOImpl<AdminRule, JPAAdminRule>
        implements AdminRuleDAO {

    private final @Getter Class<JPAAdminRule> persistenceType = JPAAdminRule.class;

    private @Autowired @Getter AdminRuleRepository repository;

    protected @Override Function<JPAAdminRule, AdminRule> persistenceToModel() {
        return MAPPER::map;
    }

    protected @Override Function<AdminRule, JPAAdminRule> modelToPersistence() {
        return MAPPER::map;
    }
}
