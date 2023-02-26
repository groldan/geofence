/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.users;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;

/**
 * A grouping for {@link GeoServerUser}s.
 *
 * <p>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true)
public class UserGroup {

    /** The identity assigned by the storage provider. */
    private Long id;

    /**
     * External Id. An ID used in an external systems. This field should simplify Geofence
     * integration in complex systems.
     */
    private String extId;

    /** The unique name identifier. */
    @NonNull private String name;

    /** The date creation. */
    private LocalDate creationDate;

    /** Whether the group is enabled or not */
    private boolean enabled;
}
