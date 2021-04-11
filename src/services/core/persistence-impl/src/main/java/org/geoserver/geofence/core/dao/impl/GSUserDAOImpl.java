/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.function.Function;
import lombok.Getter;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.jpa.model.JPAGSUser;
import org.geoserver.geofence.jpa.repository.GSUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GSUserDAOImpl extends BaseDAO<GSUser, JPAGSUser> implements GSUserDAO {

    private final @Getter Class<JPAGSUser> persistenceType = JPAGSUser.class;

    private @Autowired @Getter GSUserRepository repository;

    protected @Override Function<JPAGSUser, GSUser> persistenceToModel() {
        return MAPPER::map;
    }

    protected @Override Function<GSUser, JPAGSUser> modelToPersistence() {
        return MAPPER::map;
    }

    @Override
    public GSUser getFull(String name) {
        return toEntity(getRepository().getFull(name));
    }
}
