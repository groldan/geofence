/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

/**
 * A User that can access GeoServer resources.
 *
 * <p>A GSUser is <B>not</B> in the domain of the users which can log into Geofence.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@Entity(name = "GSUser")
@Table(name = "gf_gsuser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "GSUser")
public class JPAGSUser implements JPAIdentifiable {

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
    @Index(name = "idx_gsuser_name")
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

    /** Is the GSUser Enabled or not in the system? */
    @Column(nullable = false)
    private boolean enabled = true;

    /** Is the GSUser a GS admin? */
    @Column(nullable = false)
    private boolean admin = false;

    /** Groups to which the user is associated */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "gf_user_usergroups",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Column(name = "u_id")
    @ForeignKey(name = "fk_uug_user", inverseName = "fk_uug_group")
    @Fetch(FetchMode.SUBSELECT) // without this, hibernate will duplicate results(!)
    private Set<JPAUserGroup> groups = new HashSet<JPAUserGroup>();
}
