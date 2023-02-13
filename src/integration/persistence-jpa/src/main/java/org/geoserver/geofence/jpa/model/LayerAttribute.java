/*
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "LayerAttribute")
public class LayerAttribute implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    public enum AccessType {

        /** No access to the resource. */
        NONE,

        /** Read only access. */
        READONLY,

        /** Full access. */
        READWRITE
    }

    @Column(nullable = false)
    private String name;

    @Column(name = "data_type")
    private String dataType; // should be an enum?

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = true /* false */)
    private AccessType access;

    public @Override LayerAttribute clone() {
        try {
            return (LayerAttribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
