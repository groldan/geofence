/*
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.users.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A User that can access GeoServer resources.
 *
 * <p>A GSUser is <B>not</B> in the domain of the users which can log into Geofence.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
@ToString(exclude = "password")
public class GeoServerUser {

    private String id;

    /**
     * External Id. An ID used in an external systems. This field should simplify Geofence
     * integration in complex systems.
     */
    private String extId;

    /** The name. */
    private String name;

    /** The user name. */
    private String fullName;

    /** The password. */
    private String password;

    /** The email address. */
    private String emailAddress;

    /** The date of creation of this user */
    private LocalDateTime createdDate;

    /** Is the GSUser Enabled or not in the system? */
    private boolean enabled = true;

    /** Is the GSUser a GS admin? */
    @Default private boolean admin = false;

    /** Groups to which the user is associated */
    @NonNull private Set<String> userGroups;

    public static class Builder {
        private Set<String> userGroups = Set.of();

        public Builder userGroups(Set<String> groups) {
            if (null == groups) this.userGroups = Set.of();
            else this.userGroups = Set.copyOf(groups);
            return this;
        }
    }
}
