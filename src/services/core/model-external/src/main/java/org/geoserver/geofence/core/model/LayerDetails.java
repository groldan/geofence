/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.LayerType;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * Details may be set only for ules with non-wildcarded profile, instance, workspace,layer.
 *
 * <p><B>TODO</B>
 *
 * <UL>
 *   <LI>What about externally defined styles?
 * </UL>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Data
public class LayerDetails {

    /** The id. */
    private Long id;

    private LayerType type;

    private String defaultStyle;

    private String cqlFilterRead;

    private String cqlFilterWrite;

    private MultiPolygon area;

    private CatalogMode catalogMode;

    private Rule rule;

    private Set<String> allowedStyles = new HashSet<String>();

    /**
     * Feature Attributes associated to the Layer
     *
     * <p>We'll use the pair <TT>(details_id, name)</TT> as PK for the associated table. To do so,
     * we have to perform some trick on the <TT>{@link LayerAttribute#access}</TT> field.
     */
    private Set<LayerAttribute> attributes = new HashSet<LayerAttribute>();
}
