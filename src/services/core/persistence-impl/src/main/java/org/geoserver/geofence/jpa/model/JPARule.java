/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

/**
 * A Rule expresses if a given combination of request access is allowed or not.
 *
 * <p>In a given Rule, you may specify a precise combination of filters or a general behavior. <br>
 * Filtering can be done on
 *
 * <UL>
 *   <LI>the requesting user
 *   <LI>the profile associated to the requesting user
 *   <LI>the instance of the accessed geoserver
 *   <LI>the accessed service (e.g.: WMS)
 *   <LI>the requested operation inside the accessed service (e.g.: getMap)
 *   <LI>the workspace in geoserver
 *   <LI>the requested layer
 * </UL>
 *
 * <p><B>Example</B>: In order to allow access to every request to the WMS service in the instance
 * GS1, you will need to create a Rule, by only setting Service=WMS and Instance=GS1, leaving the
 * other fields to <TT>null</TT>.
 *
 * <p>When an access has to be checked for filtering, all the matching rules are read; they are then
 * evaluated according to their priority: the first rule found having accessType <TT><B>{@link
 * JPAGrantType#ALLOW}</B></TT> or <TT><B>{@link JPAGrantType#DENY}</B></TT> wins, and the access is
 * granted or denied accordingly. <br>
 * Matching rules with accessType=<TT><B>{@link JPAGrantType#LIMIT}</B></TT> are collected and
 * evaluated at the end, only if the request is Allowed by some other rule with lower priority. <br>
 * These rules will have an associated {@link JPARuleLimits RuleLimits} that defines some
 * restrictions for using the data (such as area limitation).
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@Entity(name = "Rule")
@Table(
    name = "gf_rule",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "username",
                "rolename",
                "instance_id",
                "service",
                "request",
                "workspace",
                "layer"
            }
        )
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
public class JPARule implements JPAIdentifiable, JPAPrioritizable, JPAIPRangeProvider {
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id @GeneratedValue @Column private Long id;

    /** Lower numbers have higher priority */
    @Column(nullable = false)
    @Index(name = "idx_rule_priority")
    private long priority;

    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "rolename", nullable = true)
    private String rolename;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @ForeignKey(name = "fk_rule_instance")
    private JPAGSInstance instance;

    @Column
    @Index(name = "idx_rule_service")
    private String service;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "low", column = @Column(name = "ip_low")),
        @AttributeOverride(name = "high", column = @Column(name = "ip_high")),
        @AttributeOverride(name = "size", column = @Column(name = "ip_size"))
    })
    private JPAIPAddressRange addressRange;

    @Column
    @Index(name = "idx_rule_request")
    private String request;

    @Column
    @Index(name = "idx_rule_workspace")
    private String workspace;

    @Column
    @Index(name = "idx_rule_layer")
    private String layer;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private JPAGrantType access;

    @OneToOne(
        optional = true,
        cascade = CascadeType.ALL,
        mappedBy = "rule"
    ) // main ref is in LayerDetails
    @ForeignKey(name = "fk_rule_details")
    private JPALayerDetails layerDetails;

    @OneToOne(
        optional = true,
        cascade = CascadeType.ALL,
        mappedBy = "rule"
    ) // main ref is in ruleLimits
    @ForeignKey(name = "fk_rule_limits")
    private JPARuleLimits ruleLimits;
}
