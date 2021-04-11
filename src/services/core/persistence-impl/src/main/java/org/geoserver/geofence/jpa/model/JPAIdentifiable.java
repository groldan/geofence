/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import java.io.Serializable;

/**
 * For now just a common super-interface to define the common identifier property, and force {@code
 * Serializable} in all entities in case they're used with ehcache diskstore; in the future could
 * also define the auditing properties (i.e.
 * {@code @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy})
 *
 * @author ETj (etj at geo-solutions.it)
 */
public interface JPAIdentifiable extends Serializable {

    Long getId();

    void setId(Long id);
}
