/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.function.Function;
import lombok.Getter;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.jpa.model.JPAUserGroup;
import org.geoserver.geofence.jpa.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Public implementation of the UserGroupDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class UserGroupDAOImpl extends BaseDAO<UserGroup, JPAUserGroup> implements UserGroupDAO {

    private final @Getter Class<JPAUserGroup> persistenceType = JPAUserGroup.class;

    private @Autowired @Getter UserGroupRepository repository;

    protected @Override Function<JPAUserGroup, UserGroup> persistenceToModel() {
        return MAPPER::map;
    }

    protected @Override Function<UserGroup, JPAUserGroup> modelToPersistence() {
        return MAPPER::map;
    }
}
