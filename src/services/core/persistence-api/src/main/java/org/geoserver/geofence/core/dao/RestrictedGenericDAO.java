/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import java.util.List;

/**
 * Public interface to define a restricted set of operation wrt to ones defined in GenericDAO. This
 * may be useful if some constraints are implemented in the DAO, so that fewer point of access are
 * allowed.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RestrictedGenericDAO<ENTITY> {

    List<ENTITY> findAll();

    ENTITY find(Long id);

    ENTITY persist(ENTITY entity);

    ENTITY merge(ENTITY entity);

    boolean remove(ENTITY entity);

    boolean removeById(Long id);

    void removeAllById(Long... ids);

    int countAll();
}
