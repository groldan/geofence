/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.access;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.With;

import org.geoserver.geofence.rules.model.CatalogMode;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.LayerAttribute;
import org.locationtech.jts.geom.Geometry;

import java.util.Set;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder
public class AccessInfo {

    /** Default "allow everything" AccessInfo */
    public static final AccessInfo ALLOW_ALL = AccessInfo.builder().grant(GrantType.ALLOW).build();

    /** Default "deny everything" AccessInfo */
    public static final AccessInfo DENY_ALL = AccessInfo.builder().grant(GrantType.DENY).build();

    /** The resulting grant: allow or deny. */
    @Default private GrantType grant = GrantType.DENY;

    @Default private boolean adminRights = false;

    private Geometry area;

    private Geometry clipArea;

    private CatalogMode catalogMode;

    private String defaultStyle;

    private String cqlFilterRead;

    private String cqlFilterWrite;

    private Set<LayerAttribute> attributes;

    private Set<String> allowedStyles;

    //    public void setGrant(GrantType grant) {
    //        if(grant != GrantType.ALLOW && grant != GrantType.DENY)
    //            throw new IllegalArgumentException("Bad grant type " + grant);
    //        this.grant = grant;
    //    }

}
