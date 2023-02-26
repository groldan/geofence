/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import org.geolatte.geom.MultiPolygon;

import java.util.Set;

/**
 * @since 4.0
 */
@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class LayerDetails {

    public enum LayerType {
        VECTOR,
        RASTER,
        LAYERGROUP
    }

    private LayerType type;

    private String defaultStyle;

    private String cqlFilterRead;

    private String cqlFilterWrite;

    private MultiPolygon<?> area;

    private SpatialFilterType spatialFilterType;

    private CatalogMode catalogMode;

    @Default @NonNull private Set<String> allowedStyles = Set.of();

    @Default @NonNull private Set<LayerAttribute> attributes = Set.of();

    public static class LayerDetailsBuilder {
        // define (effectively overriding lombok's generated ones) only the builder methods for the
        // collection attributes we want to ensure are immutable
        @SuppressWarnings("unused")
        private Set<String> allowedStyles = Set.of();

        @SuppressWarnings("unused")
        private Set<LayerAttribute> attributes = Set.of();

        public LayerDetailsBuilder allowedStyles(Set<String> allowedStyles) {
            if (allowedStyles != null && !allowedStyles.isEmpty()) {
                this.allowedStyles = Set.copyOf(allowedStyles);
            }
            return this;
        }

        public LayerDetailsBuilder attributes(Set<LayerAttribute> attributes) {
            if (attributes != null && !attributes.isEmpty()) {
                this.attributes = Set.copyOf(attributes);
            }
            return this;
        }
    }
}
