/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.util.Date;
import lombok.Data;

/** A User that can access Geofence. */
@Data
public class GFUser implements Identifiable {

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

    /** Is the GFUser Enabled or not in the system? */
    private boolean enabled = true;
}
