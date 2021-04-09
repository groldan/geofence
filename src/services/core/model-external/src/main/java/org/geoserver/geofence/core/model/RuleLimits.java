/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import lombok.Data;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * Defines general limits (such as an Area ) for a {@link Rule}. <br>
 * RuleLimits may be set only for rules with a {@link GrantType#LIMIT} access type.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
public class RuleLimits {

    /** The id. */
    private Long id;

    private Rule rule;

    private MultiPolygon allowedArea;

    private CatalogMode catalogMode;
}
