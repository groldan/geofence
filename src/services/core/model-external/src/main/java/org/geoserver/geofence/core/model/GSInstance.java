/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.util.Date;
import lombok.Data;

/**
 * A GeoServer instance.
 *
 * <p><B>TODO</B>: how does a GeoServer instance identify itself?
 */
@Data
public class GSInstance implements Identifiable {

    /** The id. */
    private Long id;

    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The date creation. */
    private Date dateCreation;

    /** The host. */
    private String baseURL;

    private String username;

    private String password;
}
