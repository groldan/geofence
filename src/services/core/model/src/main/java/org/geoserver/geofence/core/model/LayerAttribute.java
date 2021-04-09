/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geoserver.geofence.core.model.enums.AccessType;

/** @author ETj (etj at geo-solutions.it) */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayerAttribute implements Cloneable {

    private String name;

    private String datatype; // should be an enum?

    /**
     * Tells if the attribute can be read, written, or not accessed at all.
     *
     * <p>This field should be notnull, but making it so, hibernate will insist to put it into the
     * PK. We'll making it notnull in the {@link LayerDetails#attributes parent class}, but this
     * seems not to work. We're enforncing the notnull at the DAO level.
     */
    private AccessType access;

    public LayerAttribute(String name, AccessType access) {
        this.name = name;
        this.access = access;
    }

    @Override
    public LayerAttribute clone() {
        try {
            return (LayerAttribute) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Unexpected exception", ex);
        }
    }
}
