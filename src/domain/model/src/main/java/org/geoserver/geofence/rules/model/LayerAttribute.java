/*
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class LayerAttribute {

    public enum AccessType {

        /** No access to the resource. */
        NONE,

        /** Read only access. */
        READONLY,

        /** Full access. */
        READWRITE
    }

    private String name;

    private String dataType; // should be an enum?

    private AccessType access;

    public static LayerAttribute none() {
        return LayerAttribute.builder().access(AccessType.NONE).build();
    }

    public static LayerAttribute read() {
        return LayerAttribute.builder().access(AccessType.READONLY).build();
    }

    public static LayerAttribute write() {
        return LayerAttribute.builder().access(AccessType.READWRITE).build();
    }
}
