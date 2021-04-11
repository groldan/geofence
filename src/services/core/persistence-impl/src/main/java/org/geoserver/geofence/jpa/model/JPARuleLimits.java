/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * Defines general limits (such as an Area ) for a {@link JPARule}. <br>
 * RuleLimits may be set only for rules with a {@link JPAGrantType#LIMIT} access type.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@ToString(exclude = {"rule"}) // or it'll stack-overflow
@EqualsAndHashCode(exclude = {"rule"}) // or it'll stack-overflow
@Entity(name = "RuleLimits")
@Table(name = "gf_rule_limits", uniqueConstraints = @UniqueConstraint(columnNames = "rule_id"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "RuleLimits")
public class JPARuleLimits implements JPAIdentifiable {
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id @Column private Long id;

    @OneToOne(optional = false)
    @Check(constraints = "rule.access='LIMIT'") // ??? check this
    @ForeignKey(name = "fk_limits_rule")
    private JPARule rule;

    @Type(type = "org.hibernatespatial.GeometryUserType")
    @Column(name = "area")
    private MultiPolygon allowedArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_mode", nullable = true)
    private JPACatalogMode catalogMode;
}
