package org.geoserver.geofence.jpa.integration.mapper;

import org.geoserver.geofence.adminrules.model.AdminRuleIdentifier;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {GeoServerInstanceMapper.class, IPAddressRangeJpaMapper.class})
abstract class AdminRuleIdentifierJpaMapper {

    static final String ANY = org.geoserver.geofence.jpa.model.AdminRuleIdentifier.ANY;

    @Mapping(target = "instanceName", source = "instance")
    @Mapping(target = "username", expression = "java(i.username())")
    @Mapping(target = "rolename", expression = "java(i.rolename())")
    @Mapping(target = "workspace", expression = "java(i.workspace())")
    public abstract AdminRuleIdentifier toModel(
            org.geoserver.geofence.jpa.model.AdminRuleIdentifier i);

    @Mapping(target = "instance", source = "instanceName")
    @Mapping(target = "username", defaultValue = ANY)
    @Mapping(target = "rolename", defaultValue = ANY)
    @Mapping(target = "workspace", defaultValue = ANY)
    public abstract org.geoserver.geofence.jpa.model.AdminRuleIdentifier toEntity(
            AdminRuleIdentifier i);
}
