/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class AdminRule implements Identifiable, Prioritizable, IPRangeProvider {

    /** The id. */
    private Long id;

    /** Lower numbers have higher priority */
    private long priority;

    private String username;

    private String rolename;

    private GSInstance instance;

    private IPAddressRange addressRange;

    private String workspace;

    private AdminGrantType access;

    public AdminRule(
            long priority,
            String username,
            String rolename,
            GSInstance instance,
            IPAddressRange addressRange,
            String workspace,
            AdminGrantType access) {
        this.priority = priority;
        this.username = username;
        this.rolename = rolename;
        this.instance = instance;
        this.addressRange = addressRange;
        this.workspace = workspace;
        this.access = access;
    }
}
