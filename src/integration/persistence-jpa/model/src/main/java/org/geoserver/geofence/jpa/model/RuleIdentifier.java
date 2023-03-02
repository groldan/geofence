/*
 * (c) 2014, 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.jpa.model;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Uniquely identifies a {@link Rule}, all properties are mandatory in order for the {@link Rule}'s
 * unique constraint to be enforced by the database, which otherwise will consider {@literal NULL !=
 * NULL}.
 *
 * @since 4.0
 */
@Data
@Accessors(chain = true)
@Embeddable
public class RuleIdentifier implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    public static final String ANY = "*";

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private GrantType access = GrantType.DENY;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            insertable = true,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_rule_instance"))
    @Fetch(FetchMode.JOIN)
    private GeoServerInstance instance;

    @NonNull
    @Column(name = "username", nullable = false)
    private String username = ANY;

    @NonNull
    @Column(name = "rolename", nullable = false)
    private String rolename = ANY;

    @NonNull
    @Column(nullable = false)
    private String service = ANY;

    @NonNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "low", column = @Column(name = "ip_low")),
        @AttributeOverride(name = "high", column = @Column(name = "ip_high")),
        @AttributeOverride(name = "size", column = @Column(name = "ip_size"))
    })
    private IPAddressRange addressRange = new IPAddressRange();

    @NonNull
    @Column(nullable = false)
    private String request = ANY;

    @NonNull
    @Column(nullable = false)
    private String subfield = ANY;

    @NonNull
    @Column(nullable = false)
    private String workspace = ANY;

    @NonNull
    @Column(nullable = false)
    private String layer = ANY;

    public @Override RuleIdentifier clone() {
        RuleIdentifier clone;
        try {
            clone = (RuleIdentifier) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.addressRange = addressRange.clone();
        return clone;
    }

    public String username() {
        return ANY.equals(username) ? null : username;
    }

    public String rolename() {
        return ANY.equals(rolename) ? null : rolename;
    }

    public String service() {
        return ANY.equals(service) ? null : service;
    }

    public String request() {
        return ANY.equals(request) ? null : request;
    }

    public String subfield() {
        return ANY.equals(subfield) ? null : subfield;
    }

    public String workspace() {
        return ANY.equals(workspace) ? null : workspace;
    }

    public String layer() {
        return ANY.equals(layer) ? null : layer;
    }

    public String toShortString() {
        StringBuilder builder = new StringBuilder();
        addNonNull(builder, "access", access);
        addNonNull(builder, "instanceName", instance == null ? null : instance.getName());
        addNonNull(builder, "username", username);
        addNonNull(builder, "rolename", rolename);
        addNonNull(builder, "addressRange", addressRange);
        addNonNull(builder, "service", service);
        addNonNull(builder, "request", request);
        addNonNull(builder, "subfield", subfield);
        addNonNull(builder, "workspace", workspace);
        addNonNull(builder, "layer", layer);
        return builder.toString();
    }

    private void addNonNull(StringBuilder builder, String prop, Object value) {
        if (null != value) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(prop).append(": ").append(value);
        }
    }
}
