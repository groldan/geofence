package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.adminrules.model.AdminRuleIdentifier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
            JsonNullableMapper.class,
            OptionalApiMapper.class,
            GeometryApiMapper.class,
            IPAddressRangeApiMapper.class,
            EnumsApiMapper.class
        })
public abstract class AdminRuleApiMapper {

    protected @Autowired EnumsApiMapper enums;

    @Mapping(target = "identifier.instanceName", source = "instancename")
    @Mapping(target = "identifier.username", source = "userName")
    @Mapping(target = "identifier.rolename", source = "roleName")
    @Mapping(target = "identifier.workspace", source = "workspace")
    @Mapping(target = "identifier.addressRange", source = "addressRange")
    public abstract org.geoserver.geofence.adminrules.model.AdminRule map(
            org.geoserver.geofence.api.v2.model.AdminRule rule);

    @Mapping(target = "instancename", source = "identifier.instanceName")
    @Mapping(target = "userName", source = "identifier.username")
    @Mapping(target = "roleName", source = "identifier.rolename")
    @Mapping(target = "workspace", source = "identifier.workspace")
    @Mapping(target = "addressRange", source = "identifier.addressRange")
    public abstract org.geoserver.geofence.api.v2.model.AdminRule map(
            org.geoserver.geofence.adminrules.model.AdminRule rule);

    @Mapping(target = "identifier", ignore = true)
    abstract AdminRule updateEntity(
            @MappingTarget AdminRule.AdminRuleBuilder entity,
            org.geoserver.geofence.api.v2.model.AdminRule dto);

    @Mapping(target = "instanceName", source = "instancename")
    @Mapping(target = "username", source = "userName")
    @Mapping(target = "rolename", source = "roleName")
    abstract AdminRuleIdentifier updateIdentifier(
            @MappingTarget AdminRuleIdentifier.AdminRuleIdentifierBuilder entity,
            org.geoserver.geofence.api.v2.model.AdminRule dto);

    public AdminRule patch(
            org.geoserver.geofence.adminrules.model.AdminRule target,
            org.geoserver.geofence.api.v2.model.AdminRule source) {

        AdminRuleIdentifier identifier =
                updateIdentifier(target.getIdentifier().toBuilder(), source);

        org.geoserver.geofence.adminrules.model.AdminRule patched =
                updateEntity(target.toBuilder(), source);

        return patched.withIdentifier(identifier);
    }
}
