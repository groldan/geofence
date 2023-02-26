/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.authorization.users;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true)
public class AuthUser {

    public static enum Role {
        ADMIN,
        USER
    }

    private String name;
    private Role role;
}
