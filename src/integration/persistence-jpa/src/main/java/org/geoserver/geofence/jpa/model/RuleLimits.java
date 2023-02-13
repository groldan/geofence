/*
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.experimental.Accessors;

import org.locationtech.jts.geom.MultiPolygon;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@Accessors(chain = true)
@Embeddable
public class RuleLimits implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Column(name = "limits_area")
    private MultiPolygon allowedArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "limits_spatial_filter_type", nullable = true)
    private SpatialFilterType spatialFilterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "limits_catalog_mode", nullable = true)
    private CatalogMode catalogMode;

    boolean isEmpty() {
        return allowedArea == null && spatialFilterType == null && catalogMode == null;
    }

    public @Override RuleLimits clone() {
        RuleLimits clone;
        try {
            clone = (RuleLimits) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (null != allowedArea) {
            clone.allowedArea = (MultiPolygon) allowedArea.copy();
        }
        return clone;
    }
}
