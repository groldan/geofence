/* (c) 2021 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.jpa.mapper;

import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.InsertPosition;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.jpa.model.JPAAdminRule;
import org.geoserver.geofence.jpa.model.JPAGFUser;
import org.geoserver.geofence.jpa.model.JPAGSInstance;
import org.geoserver.geofence.jpa.model.JPAGSUser;
import org.geoserver.geofence.jpa.model.JPAIPAddressRange;
import org.geoserver.geofence.jpa.model.JPAInsertPosition;
import org.geoserver.geofence.jpa.model.JPALayerAttribute;
import org.geoserver.geofence.jpa.model.JPALayerDetails;
import org.geoserver.geofence.jpa.model.JPARule;
import org.geoserver.geofence.jpa.model.JPARuleLimits;
import org.geoserver.geofence.jpa.model.JPAUserGroup;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ModelMapper {

    AdminRule map(JPAAdminRule rule);

    JPAAdminRule map(AdminRule rule);

    Rule map(JPARule rule);

    /** Mapper method for new object */
    JPARule map(Rule rule);

    /**
     * Mapper method for existing object, the target is given by the dao from the attached object
     */
    @Mapping(ignore = true, target = "ruleLimits")
    @Mapping(ignore = true, target = "layerDetails")
    JPARule map(Rule rule, @MappingTarget JPARule target);

    default @AfterMapping void setRuleLimitJpaRule(Rule source, @MappingTarget JPARule target) {
        if (target != null) {
            JPARuleLimits ruleLimits = target.getRuleLimits();
            if (ruleLimits != null) {
                ruleLimits.setId(target.getId());
                ruleLimits.setRule(target);
            }
            JPALayerDetails layerDetails = target.getLayerDetails();
            if (layerDetails != null) {
                layerDetails.setId(target.getId());
                layerDetails.setRule(target);
            }
        }
    }

    RuleLimits map(JPARuleLimits limits);

    /** Mapper for new object */
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "rule")
    JPARuleLimits map(RuleLimits limits);

    /** The target is given by the rule DAO from the attached or new object as appropriate */
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "rule")
    JPARuleLimits map(RuleLimits limits, @MappingTarget JPARuleLimits target);

    LayerDetails map(JPALayerDetails layerDetails);

    /** Mapper for new object */
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "rule")
    JPALayerDetails map(LayerDetails layerDetails);

    /** The target is given by the rule DAO from the attached or new object as appropriate */
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "rule")
    JPALayerDetails map(LayerDetails layerDetails, @MappingTarget JPALayerDetails target);

    GFUser map(JPAGFUser user);

    JPAGFUser map(GFUser user);

    GSInstance map(JPAGSInstance instance);

    JPAGSInstance map(GSInstance instance);

    GSUser map(JPAGSUser user);

    JPAGSUser map(GSUser user);

    IPAddressRange map(JPAIPAddressRange addressRange);

    JPAIPAddressRange map(IPAddressRange addressRange);

    LayerAttribute map(JPALayerAttribute layerAtt);

    JPALayerAttribute map(LayerAttribute layerAtt);

    UserGroup map(JPAUserGroup group);

    JPAUserGroup map(UserGroup group);

    JPAInsertPosition map(InsertPosition position);
}
