/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

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
 * GrantType#ALLOW}</B></TT> or <TT><B>{@link GrantType#DENY}</B></TT> wins, and the access is
 * granted or denied accordingly. <br>
 * Matching rules with accessType=<TT><B>{@link GrantType#LIMIT}</B></TT> are collected and
 * evaluated at the end, only if the request is Allowed by some other rule with lower priority. <br>
 * These rules will have an associated {@link RuleLimits RuleLimits} that defines some restrictions
 * for using the data (such as area limitation).
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
@NoArgsConstructor
public class Rule implements Identifiable, Prioritizable, IPRangeProvider {

    /** The id. */
    private Long id;

    /** Lower numbers have higher priority */
    private long priority;

    private String username;

    private String rolename;

    private GSInstance instance;

    private String service;

    private IPAddressRange addressRange;

    private String request;

    private String workspace;

    private String layer;

    private GrantType access;

    private LayerDetails layerDetails;

    private RuleLimits ruleLimits;

    public Rule(
            long priority,
            String username,
            String rolename,
            GSInstance instance,
            IPAddressRange addressRange,
            String service,
            String request,
            String workspace,
            String layer,
            GrantType access) {
        this.priority = priority;
        this.username = username;
        this.rolename = rolename;
        this.instance = instance;
        this.addressRange = addressRange;
        this.service = service;
        this.request = request;
        this.workspace = workspace;
        this.layer = layer;
        this.access = access;
    }
}
