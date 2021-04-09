/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/**
 * A User that can access GeoServer resources.
 *
 * <p>A GSUser is <B>not</B> in the domain of the users which can log into Geofence.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
public class GSUser implements Identifiable {

    /** The id. */
    private Long id;

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
    private Date dateCreation;

    /** Is the GSUser Enabled or not in the system? */
    private boolean enabled = true;

    /** Is the GSUser a GS admin? */
    private boolean admin = false;

    /** Groups to which the user is associated */
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();
}
