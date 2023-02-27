/*
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.authorization.users;

import org.geoserver.geofence.users.model.GeoServerUser;

/**
 * @author ETj (etj at geo-solutions.it)
 */
public interface AuthorizationService {

    /**
     * @param username user name, not null
     * @param password user password in plain text form, not null
     * @return the authenticated user, empty if the user is not found
     * @throws UserNotFoundException if supplied user does not exist
     * @throws InvalidCredentialsException if supplied password does not match the {@link
     *     GeoServerUser} password
     */
    public AuthUser authorize(String username, String password) throws AuthorizationException;
}
