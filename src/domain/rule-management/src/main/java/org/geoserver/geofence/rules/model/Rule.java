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
@Builder(toBuilder = true)
public class Rule {

    private static final RuleIdentifier EMPTY_IDENTIFIER = RuleIdentifier.builder().build();

    private Long id;

    private long priority;

    @NonNull @Default private RuleIdentifier identifier = EMPTY_IDENTIFIER;

    private RuleLimits ruleLimits;

    public Rule withAccess(@NonNull GrantType access) {
        return withIdentifier(getIdentifier().withAccess(access));
    }

    public Rule withInstance(@NonNull String geoserverInstanceName) {
        return withIdentifier(getIdentifier().withInstanceName(geoserverInstanceName));
    }
}
