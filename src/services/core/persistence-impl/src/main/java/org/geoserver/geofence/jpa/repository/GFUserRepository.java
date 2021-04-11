/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.repository;

import java.util.Date;
import org.geoserver.geofence.jpa.model.JPAGFUser;
import org.springframework.stereotype.Repository;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Repository
public class GFUserRepository extends BaseRepository<JPAGFUser, Long> {

    @Override
    public void persist(JPAGFUser... entities) {
        Date now = new Date();
        for (JPAGFUser user : entities) {
            user.setDateCreation(now);
        }
        super.persist(entities);
    }
}
