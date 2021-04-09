/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.util.Date;
import lombok.Data;

/**
 * A grouping for {@link GSUser}s.
 *
 * <p>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
public class UserGroup implements Identifiable {

    /** The id. */
    private Long id;

    /**
     * External Id. An ID used in an external systems. This field should simplify Geofence
     * integration in complex systems.
     */
    private String extId;

    /** The name. */
    private String name;

    /** The date creation. */
    private Date dateCreation;

    /** The enabled. */
    private boolean enabled;
}
