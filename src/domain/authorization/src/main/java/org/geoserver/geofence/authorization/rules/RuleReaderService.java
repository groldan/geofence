/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.authorization.rules;

import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;

import java.util.List;

/**
 * Operations on
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RuleReaderService {

    /** Return info on resource accessibility. */
    AccessInfo getAccessInfo(RuleFilter filter);

    /**
     * info about admin authorization on a given workspace.
     *
     * <p>Returned AccessInfo will always be ALLOW, with the computed adminRights.
     */
    AccessInfo getAdminAuthorization(RuleFilter filter);

    /**
     * Return the unprocessed {@link Rule} list matching a given filter, sorted by priority.
     *
     * <p>Use {@link getAccessInfo(RuleFilter) getAccessInfo(RuleFilter)} if you need the resulting
     * coalesced access info.
     */
    List<Rule> getMatchingRules(RuleFilter filter);

    // ==========================================================================

}
