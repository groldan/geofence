package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.rules.model.IPAddressRange;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IPAddressRangeApiMapper {

    default String addressRangeToString(IPAddressRange range) {
        return range == null ? null : IPAddressRange.getCidrSignature(range);
    }

    default IPAddressRange stringToAddressRange(String range) {
        return range == null ? null : IPAddressRange.fromCidrSignature(range);
    }
}
