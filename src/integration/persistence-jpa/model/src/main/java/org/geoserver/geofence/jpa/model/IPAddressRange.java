/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class IPAddressRange implements Cloneable {

    public static final long NULL = -1L;

    @Column(nullable = false)
    private long low = NULL;

    @Column(nullable = false)
    private long high = NULL;

    @Column(nullable = false)
    private int size = (int) NULL;

    public @Override IPAddressRange clone() {
        return new IPAddressRange(low, high, size);
    }

    /**
     * @return a new no_data value instance with all fields set to {@code -1}
     */
    public static IPAddressRange noData() {
        return new IPAddressRange();
    }

    public Long low() {
        return low == NULL ? null : low;
    }

    public IPAddressRange low(Long low) {
        this.low = low == null ? NULL : low.longValue();
        return this;
    }

    public Long high() {
        return high == NULL ? null : high;
    }

    public IPAddressRange high(Long high) {
        this.high = high == null ? NULL : high.longValue();
        return this;
    }

    public Integer size() {
        return size == NULL ? null : size;
    }

    public IPAddressRange size(Integer size) {
        this.size = size == null ? (int) NULL : size.intValue();
        return this;
    }
}
