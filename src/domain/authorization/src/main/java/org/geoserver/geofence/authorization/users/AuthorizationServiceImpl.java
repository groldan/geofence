/*
 * (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.authorization.users;

import static org.geoserver.geofence.authorization.users.AuthUser.Role.ADMIN;
import static org.geoserver.geofence.authorization.users.AuthUser.Role.USER;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.service.UserAdminService;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final @NonNull UserAdminService userAdminService;

    @Override
    public AuthUser authorize(@NonNull String username, @NonNull String password)
            throws AuthorizationException {

        GeoServerUser user =
                userAdminService
                        .get(username)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                "User " + username + " does not exist"));

        if (!user.getPassword().equals(password))
            throw new InvalidCredentialsException("Invalid credentials for user " + username);

        return toAuthUser(user);
    }

    private AuthUser toAuthUser(GeoServerUser user) {
        return AuthUser.builder().name(user.getName()).role(user.isAdmin() ? ADMIN : USER).build();
    }
}
