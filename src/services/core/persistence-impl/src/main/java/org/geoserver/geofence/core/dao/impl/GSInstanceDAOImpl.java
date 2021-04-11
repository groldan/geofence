/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.function.Function;
import lombok.Getter;
import org.geoserver.geofence.core.dao.GSInstanceDAO;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.jpa.model.JPAGSInstance;
import org.geoserver.geofence.jpa.repository.GSInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Public implementation of the GSInstanceDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GSInstanceDAOImpl extends BaseDAO<GSInstance, JPAGSInstance> implements GSInstanceDAO {

    private final @Getter Class<JPAGSInstance> persistenceType = JPAGSInstance.class;

    private @Autowired @Getter GSInstanceRepository repository;

    protected @Override Function<JPAGSInstance, GSInstance> persistenceToModel() {
        return MAPPER::map;
    }

    protected @Override Function<GSInstance, JPAGSInstance> modelToPersistence() {
        return MAPPER::map;
    }
}
