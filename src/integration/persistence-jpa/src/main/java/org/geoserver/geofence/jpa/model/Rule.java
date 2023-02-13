/*
 * (c) 2014, 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Accessors(chain = true)
@Entity(name = "Rule")
@Table(
        name = "gf_rule",
        // NOTE unique constraints don't work with null values, so all RuleIdentifier attributes
        // have default values
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "gf_rule_priority_by_instance",
                    columnNames = {"instance_id", "priority"}),
            @UniqueConstraint(
                    name = "gf_rule_identifier",
                    columnNames = {
                        "instance_id",
                        "username",
                        "rolename",
                        "service",
                        "ip_low",
                        "ip_high",
                        "ip_size",
                        "request",
                        "subfield",
                        "workspace",
                        "layer",
                        "grant_type"
                    })
        },
        indexes = {
            @Index(name = "idx_rule_priority", columnList = "priority"),
            @Index(name = "idx_rule_service", columnList = "service"),
            @Index(name = "idx_rule_request", columnList = "request"),
            @Index(name = "idx_rule_workspace", columnList = "workspace"),
            @Index(name = "idx_rule_layer", columnList = "layer")
        })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
public class Rule implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue @Column private Long id;

    @Column(nullable = false)
    private long priority;

    @Embedded private RuleIdentifier identifier = new RuleIdentifier();

    @Embedded private LayerDetails layerDetails;

    @Embedded private RuleLimits ruleLimits;

    private @PostLoad void nullify() {
        if (layerDetails != null && layerDetails.isEmpty()) {
            layerDetails = null;
        }
        if (ruleLimits != null && ruleLimits.isEmpty()) {
            ruleLimits = null;
        }
    }

    public @Override Rule clone() {
        Rule clone;
        try {
            clone = (Rule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.identifier = identifier.clone();
        clone.layerDetails = layerDetails == null ? null : layerDetails.clone();
        clone.ruleLimits = ruleLimits == null ? null : ruleLimits.clone();
        return clone;
    }
}
