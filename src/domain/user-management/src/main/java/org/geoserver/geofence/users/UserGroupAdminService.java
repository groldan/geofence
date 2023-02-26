/*
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.users;

import java.util.List;
import java.util.Optional;

/**
 * Operations on {@link UserGroup UserGroup}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface UserGroupAdminService {

    // ==========================================================================
    // Basic operations

    UserGroup insert(UserGroup group);

    UserGroup update(UserGroup group);

    boolean delete(long id);

    Optional<UserGroup> get(long id);

    Optional<UserGroup> get(String name);

    long getCount(String nameLike);

    List<UserGroup> getList(String nameLike, Integer page, Integer entries);
}
