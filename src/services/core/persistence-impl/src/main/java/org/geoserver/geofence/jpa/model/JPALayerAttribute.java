/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/** @author ETj (etj at geo-solutions.it) */
@Data
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "LayerAttribute")
public class JPALayerAttribute implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String name;

    @Column(name = "data_type")
    private String datatype; // should be an enum?

    /**
     * Tells if the attribute can be read, written, or not accessed at all.
     *
     * <p>This field should be notnull, but making it so, hibernate will insist to put it into the
     * PK. We'll making it notnull in the {@link JPALayerDetails#attributes parent class}, but this
     * seems not to work. We're enforncing the notnull at the DAO level.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = true /*false*/)
    private JPAAccessType access;
}
