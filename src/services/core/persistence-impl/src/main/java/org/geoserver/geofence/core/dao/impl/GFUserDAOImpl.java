/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.function.Function;
import lombok.Getter;
import org.geoserver.geofence.core.dao.GFUserDAO;
import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.jpa.model.JPAGFUser;
import org.geoserver.geofence.jpa.repository.GFUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GFUserDAOImpl extends BaseDAO<GFUser, JPAGFUser> implements GFUserDAO {

    private final @Getter Class<JPAGFUser> persistenceType = JPAGFUser.class;

    private @Autowired @Getter GFUserRepository repository;

    protected @Override Function<JPAGFUser, GFUser> persistenceToModel() {
        return MAPPER::map;
    }

    protected @Override Function<GFUser, JPAGFUser> modelToPersistence() {
        return MAPPER::map;
    }
}
