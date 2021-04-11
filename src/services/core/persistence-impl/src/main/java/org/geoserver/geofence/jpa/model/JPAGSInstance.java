/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GeoServer instance.
 *
 * <p><B>TODO</B>: how does a GeoServer instance identify itself?
 */
@Data
@Entity(name = "GSInstance")
@Table(name = "gf_gsinstance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "gsinstance")
public class JPAGSInstance implements JPAIdentifiable {

    private static final long serialVersionUID = -651975281355246975L;

    /** The id. */
    @Id @GeneratedValue @Column private Long id;

    /** The name. */
    @Column(nullable = false, updatable = true)
    private String name;

    /** The description. */
    @Column(nullable = true, updatable = true)
    private String description;

    /** The date creation. */
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    /** The host. */
    @Column(nullable = false, updatable = true)
    private String baseURL;

    @Column(nullable = false, updatable = true)
    private String username;

    @Column(nullable = false, updatable = true)
    private String password;
}
