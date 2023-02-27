/*
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A grouping for {@link GeoServerUser}s.
 *
 * <p>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "UserGroup")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "gf_usergroup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "usergroup")
public class GeoServerUserGroup extends Auditable implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8457036587275531556L;

    /** The id. */
    @Id @GeneratedValue @Column private Long id;

    /**
     * External Id. An ID used in an external systems. This field should simplify Geofence
     * integration in complex systems.
     */
    @Column(nullable = true, updatable = false, unique = true)
    private String extId;

    /** The name. */
    @Column(nullable = false, updatable = true, unique = true)
    private String name;

    /** The date creation. */
    @Column(updatable = false)
    private LocalDateTime creationDate;

    /** The enabled. */
    @Column(nullable = false)
    private boolean enabled;
}
