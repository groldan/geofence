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

/** A User that can access Geofence. */
@Data
@Entity(name = "GFUser")
@Table(name = "gf_gfuser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "GFUser")
public class JPAGFUser implements JPAIdentifiable {

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
    @Column(nullable = false, unique = true)
    private String name;

    /** The user name. */
    @Column(nullable = true)
    private String fullName;

    /** The password. */
    @Column(nullable = true)
    private String password;

    /** The email address. */
    @Column(nullable = true)
    private String emailAddress;

    /** The date of creation of this user */
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    /** Is the GFUser Enabled or not in the system? */
    @Column(nullable = false)
    private boolean enabled = true;
}
