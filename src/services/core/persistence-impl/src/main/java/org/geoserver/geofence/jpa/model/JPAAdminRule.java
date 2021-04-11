/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

/**
 * An AdminRule expresses if a given combination of request access is allowed or not.
 *
 * <p>It's used for setting admin privileges on workspaces.
 *
 * <p>AdminRule filtering and selection is almost identical to {@see Rule}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@Entity(name = "AdminRule")
@Table(
    name = "gf_adminrule",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "rolename", "instance_id", "workspace"})
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
public class JPAAdminRule implements JPAIdentifiable, JPAPrioritizable, JPAIPRangeProvider {

    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id @GeneratedValue @Column private Long id;

    /** Lower numbers have higher priority */
    @Column(nullable = false)
    @Index(name = "idx_adminrule_priority")
    private long priority;

    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "rolename", nullable = true)
    private String rolename;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @ForeignKey(name = "fk_adminrule_instance")
    private JPAGSInstance instance;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "low", column = @Column(name = "ip_low")),
        @AttributeOverride(name = "high", column = @Column(name = "ip_high")),
        @AttributeOverride(name = "size", column = @Column(name = "ip_size"))
    })
    private JPAIPAddressRange addressRange;

    @Column
    @Index(name = "idx_adminrule_workspace")
    private String workspace;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private JPAAdminGrantType access;
}
