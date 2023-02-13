package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.adminrules.model.AdminGrantType;
import org.geoserver.geofence.rules.model.CatalogMode;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.LayerAttribute;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.SpatialFilterType;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EnumsApiMapper {

    LayerDetails.LayerType map(org.geoserver.geofence.api.v2.model.LayerDetails.TypeEnum value);

    org.geoserver.geofence.api.v2.model.LayerDetails.TypeEnum map(LayerDetails.LayerType value);

    CatalogMode map(org.geoserver.geofence.api.v2.model.CatalogMode value);

    org.geoserver.geofence.api.v2.model.CatalogMode map(CatalogMode value);

    SpatialFilterType map(org.geoserver.geofence.api.v2.model.SpatialFilterType value);

    org.geoserver.geofence.api.v2.model.SpatialFilterType map(SpatialFilterType value);

    org.geoserver.geofence.api.v2.model.InsertPosition map(
            org.geoserver.geofence.rules.model.InsertPosition pos);

    org.geoserver.geofence.rules.model.InsertPosition map(
            org.geoserver.geofence.api.v2.model.InsertPosition pos);

    org.geoserver.geofence.api.v2.model.LayerAttribute.AccessEnum accessType(
            LayerAttribute.AccessType value);

    LayerAttribute.AccessType accessType(
            org.geoserver.geofence.api.v2.model.LayerAttribute.AccessEnum value);

    GrantType grantType(org.geoserver.geofence.api.v2.model.GrantType value);

    org.geoserver.geofence.api.v2.model.GrantType grantType(GrantType value);

    AdminGrantType adminGrantType(org.geoserver.geofence.api.v2.model.AdminGrantType value);

    org.geoserver.geofence.api.v2.model.AdminGrantType adminGrantType(AdminGrantType value);
}
