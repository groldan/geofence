package org.geoserver.geofence.jpa.integration.mapper;

import org.geoserver.geofence.adminrules.model.AdminRule;
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
        uses = {AdminRuleIdentifierJpaMapper.class})
public interface AdminRuleJpaMapper {

    public abstract AdminRule toModel(org.geoserver.geofence.jpa.model.AdminRule entity);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract org.geoserver.geofence.jpa.model.AdminRule toEntity(AdminRule model);

    static String encodeId(Long id) {
        return id == null ? null : Long.toHexString(id);
    }

    static Long decodeId(String id) {
        return id == null ? null : Long.decode(id);
    }
}
