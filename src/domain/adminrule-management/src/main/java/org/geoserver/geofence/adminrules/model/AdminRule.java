/*
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.adminrules.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

/**
 * An AdminRule expresses if a given combination of request access is allowed or not.
 *
 * <p>It's used for setting admin privileges on workspaces.
 *
 * <p>AdminRule filtering and selection is almost identical to {@see Rule}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true)
public class AdminRule {

    private Long id;

    private long priority;

    @NonNull @Default
    private AdminRuleIdentifier identifier = AdminRuleIdentifier.builder().build();

    private AdminGrantType access;
}
