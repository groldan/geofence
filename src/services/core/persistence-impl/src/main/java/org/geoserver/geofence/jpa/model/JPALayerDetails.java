/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * Details may be set only for rules with non-wildcarded profile, instance, workspace,layer.
 *
 * <p><B>TODO</B>
 *
 * <UL>
 *   <LI>What about externally defined styles?
 * </UL>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@ToString(exclude = {"rule"}) // or it'll stack-overflow
@EqualsAndHashCode(exclude = {"rule"}) // or it'll stack-overflow
@Entity(name = "LayerDetails")
@Table(name = "gf_layer_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "LayerDetails")
public class JPALayerDetails implements JPAIdentifiable {
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id @Column private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true /*false*/)
    private JPALayerType type;

    @Column private String defaultStyle;

    @Column(length = 4000)
    private String cqlFilterRead;

    @Column(length = 4000)
    private String cqlFilterWrite;

    @Type(type = "org.hibernatespatial.GeometryUserType")
    @Column(name = "area")
    private MultiPolygon area;

    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_mode", nullable = true)
    private JPACatalogMode catalogMode;

    @OneToOne(optional = false)
    //    @Check(constraints="rule.access='LIMIT'") // ??? check this
    @ForeignKey(name = "fk_details_rule")
    private JPARule rule;

    /** Styles allowed for this layer */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "gf_layer_styles", joinColumns = @JoinColumn(name = "details_id"))
    @ForeignKey(name = "fk_styles_layer")
    @Column(name = "styleName")
    private Set<String> allowedStyles = new HashSet<>();

    /**
     * Feature Attributes associated to the Layer
     *
     * <p>We'll use the pair <TT>(details_id, name)</TT> as PK for the associated table. To do so,
     * we have to perform some trick on the <TT>{@link JPALayerAttribute#access}</TT> field.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
        name = "gf_layer_attributes",
        joinColumns = @JoinColumn(name = "details_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"details_id", "name"})
    )
    // override is used to set the pk as {"details_id", "name"}
    //    @AttributeOverride( name="access", column=@Column(name="access", nullable=false) )
    @ForeignKey(name = "fk_attribute_layer")
    @Fetch(FetchMode.SELECT) // without this, hibernate will duplicate results(!)
    private Set<JPALayerAttribute> attributes = new HashSet<>();
}
