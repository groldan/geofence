/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

/** @author ETj (etj at geo-solutions.it) */
@Data
@Embeddable
public class JPAIPAddressRange implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The lower 64 bits. For IPv4, only the lower 32 are used. */
    private Long low;
    /** The higher 64 bits. For IPv4, this is null */
    private Long high;

    /**
     * CIDR based prefix size. It's equivalent to the number of leading 1 bits in the routing prefix
     * mask. http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing
     */
    private Integer size;
}
