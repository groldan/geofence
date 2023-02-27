/*
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * An AdminRule expresses if a given combination of request access is allowed or not.
 *
 * <p>It's used for setting admin privileges on workspaces.
 *
 * <p>AdminRule filtering and selection is almost identical to {@see Rule}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity(name = "AdminRule")
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "gf_adminrule",
        uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = {
                        "instance_id",
                        "username",
                        "rolename",
                        "workspace",
                        "ip_low",
                        "ip_high",
                        "ip_size",
                    })
        },
        indexes = {
            @Index(name = "idx_adminrule_priority", columnList = "priority"),
            @Index(name = "idx_adminrule_username", columnList = "username"),
            @Index(name = "idx_adminrule_rolename", columnList = "rolename"),
            @Index(name = "idx_adminrule_workspace", columnList = "workspace"),
            @Index(name = "idx_adminrule_grant_type", columnList = "grant_type")
        })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
public class AdminRule extends Auditable implements Cloneable {
    private static final long serialVersionUID = 422357467611162461L;

    @Id @GeneratedValue @Column private Long id;

    private long priority;

    @Embedded private AdminRuleIdentifier identifier = new AdminRuleIdentifier();

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private AdminGrantType access = AdminGrantType.USER;

    // visible for testing
    public @Override AdminRule clone() {
        AdminRule clone;
        try {
            clone = (AdminRule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.identifier = identifier.clone();
        return clone;
    }
}
