/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.locationtech.jts.geom.MultiPolygon;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.UniqueConstraint;

/**
 * @since 4.0
 */
@Data
@Accessors(chain = true)
@Embeddable
public class LayerDetails implements Serializable, Cloneable {

    private static final long serialVersionUID = 1;

    public enum LayerType {
        VECTOR,
        RASTER,
        LAYERGROUP
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ld_type", nullable = true /* false */)
    private LayerType type;

    @Column(name = "ld_default_style")
    private String defaultStyle;

    @Column(name = "ld_cql_filter_read", length = 4000)
    private String cqlFilterRead;

    @Column(name = "ld_cql_filter_write", length = 4000)
    private String cqlFilterWrite;

    @Column(name = "ld_area")
    private MultiPolygon area;

    @Enumerated(EnumType.STRING)
    @Column(name = "ld_spatial_filter_type", nullable = true)
    private SpatialFilterType spatialFilterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ld_catalog_mode", nullable = true)
    private CatalogMode catalogMode;

    /** Styles allowed for this layer */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
            name = "gf_layer_styles",
            joinColumns =
                    @JoinColumn(
                            name = "details_id",
                            foreignKey = @ForeignKey(name = "fk_styles_layer")))
    @Column(name = "ld_styleName")
    private Set<String> allowedStyles;

    /**
     * We'll use the pair <TT>(details_id, name)</TT> as PK for the associated table. To do so, we
     * have to perform some trick on the <TT>{@link LayerAttribute#access}</TT> field.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
            name = "gf_layer_attributes",
            joinColumns = @JoinColumn(name = "details_id"),
            uniqueConstraints =
                    @UniqueConstraint(
                            name = "gf_layer_attributes_name",
                            columnNames = {"details_id", "name"}),
            foreignKey = @ForeignKey(name = "fk_attribute_layer"))
    // Note: used to be FetchMode.SELECT, but no duplicates are returned now and this avoids N+1
    // queries
    @Fetch(FetchMode.JOIN)
    private Set<LayerAttribute> attributes;

    public @Override LayerDetails clone() {
        LayerDetails clone;

        try {
            clone = (LayerDetails) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (area != null) {
            clone.area = (MultiPolygon) area.copy();
        }
        if (allowedStyles != null) {
            clone.allowedStyles = new HashSet<>(allowedStyles);
        }
        if (attributes != null) {
            clone.attributes =
                    attributes.stream()
                            .map(LayerAttribute::clone)
                            .collect(Collectors.toCollection(HashSet::new));
        }
        return clone;
    }

    boolean isEmpty() {
        return (allowedStyles == null || allowedStyles.isEmpty())
                && area == null
                && (attributes == null || attributes.isEmpty())
                && catalogMode == null
                && cqlFilterRead == null
                && cqlFilterWrite == null
                && defaultStyle == null
                && spatialFilterType == null
                && type == null;
    }
}
