/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.geoserver.geofence.jpa.repository.JpaGeoServerInstanceRepository;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A GeoServer instance.
 *
 * <p><B>TODO</B>: how does a GeoServer instance identify itself?
 */
@Entity(name = "GSInstance")
@Table(name = "gf_gsinstance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "gsinstance")
@Data
@Accessors(chain = true)
@ToString(exclude = {"password"})
public class GeoServerInstance implements Serializable, Cloneable {

    private static final long serialVersionUID = -2584592064221812813L;

    /**
     * Name used for the instance that's always present and signifies any instance in a query. The
     * "any" instance preserves referential integrity with {@link RuleIdentifier} and allows to
     * enforce the unique constraint, which would fail to be enforced if {@link
     * RuleIdentifier#getInstance()} were {@code null}
     *
     * @see JpaGeoServerInstanceRepository#getInstanceAny()
     */
    public static final String ANY = "*";

    @Id @GeneratedValue @Column private Long id;

    /** The name. */
    @Column(nullable = false, updatable = true)
    private String name;

    /** The description. */
    @Column(nullable = true, updatable = true)
    private String description;

    /** The date creation. */
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    /** The host. */
    @Column(nullable = false, updatable = true)
    private String baseURL;

    @Column(nullable = false, updatable = true)
    private String username;

    @Column(nullable = false, updatable = true)
    private String password;

    public @Override GeoServerInstance clone() {
        try {
            return (GeoServerInstance) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static GeoServerInstance any() {
        return new GeoServerInstance()
                .setName(ANY)
                .setDescription("This instance represents a NULL value and must not be removed")
                .setBaseURL("")
                .setUsername("")
                .setPassword("");
    }
}
