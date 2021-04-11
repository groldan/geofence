/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.repository;

import java.util.Date;
import org.geoserver.geofence.jpa.model.JPAUserGroup;
import org.springframework.stereotype.Repository;

/**
 * Public implementation of the UserGroupDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Repository
public class UserGroupRepository extends BaseRepository<JPAUserGroup, Long> {

    @Override
    public void persist(JPAUserGroup... entities) {
        Date now = new Date();
        for (JPAUserGroup e : entities) {
            e.setDateCreation(now);
        }

        super.persist(entities);
    }

    @Override
    public JPAUserGroup save(JPAUserGroup g) {
        g.setDateCreation(new Date());
        return super.save(g);
    }
}
