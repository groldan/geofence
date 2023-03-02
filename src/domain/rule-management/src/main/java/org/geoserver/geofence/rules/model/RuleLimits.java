/*
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.With;

import org.geolatte.geom.MultiPolygon;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class RuleLimits {

    private MultiPolygon<?> allowedArea;

    @Default private SpatialFilterType spatialFilterType = SpatialFilterType.INTERSECT;

    @Default private CatalogMode catalogMode = CatalogMode.HIDE;

    public static RuleLimits clip() {
        return RuleLimits.builder().spatialFilterType(SpatialFilterType.CLIP).build();
    }

    public static RuleLimits intersect() {
        return RuleLimits.builder().spatialFilterType(SpatialFilterType.INTERSECT).build();
    }
}
