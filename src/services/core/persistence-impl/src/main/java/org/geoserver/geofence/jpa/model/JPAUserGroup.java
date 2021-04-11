/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
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
 * A grouping for {@link JPAGSUser}s.
 *
 * <p>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@Entity(name = "UserGroup")
@Table(name = "gf_usergroup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "usergroup")
public class JPAUserGroup implements JPAIdentifiable {
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id @GeneratedValue @Column private Long id;

    /**
     * External Id. An ID used in an external systems. This field should simplify Geofence
     * integration in complex systems.
     */
    @Column(nullable = true, updatable = false, unique = true)
    private String extId;

    /** The name. */
    @Column(nullable = false, updatable = true, unique = true)
    private String name;

    /** The date creation. */
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    /** The enabled. */
    @Column(nullable = false)
    private boolean enabled;
}
