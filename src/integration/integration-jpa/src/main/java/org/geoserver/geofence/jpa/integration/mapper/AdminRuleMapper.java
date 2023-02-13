package org.geoserver.geofence.jpa.integration.mapper;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = AdminRuleIdentifierMapper.class)
public interface AdminRuleMapper {

    public abstract AdminRule toModel(org.geoserver.geofence.jpa.model.AdminRule entity);

    public abstract org.geoserver.geofence.jpa.model.AdminRule toEntity(AdminRule model);
}
