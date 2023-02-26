/*
 * (c) 2014, 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class RuleIdentifier {

    /** The GeoServer instance name this rule belongs to */
    private String instanceName;

    private String username;

    private String rolename;

    private String service;

    private IPAddressRange addressRange;

    private String request;

    private String subfield;

    private String workspace;

    private String layer;

    @NonNull @Default private GrantType access = GrantType.DENY;
}
