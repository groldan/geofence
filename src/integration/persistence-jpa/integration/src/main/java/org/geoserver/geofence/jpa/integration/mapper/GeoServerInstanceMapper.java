package org.geoserver.geofence.jpa.integration.mapper;

import lombok.NonNull;

import org.geoserver.geofence.jpa.model.GeoServerInstance;
import org.geoserver.geofence.jpa.repository.JpaGeoServerInstanceRepository;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

@Mapper(
        componentModel = "spring",
        // in case something changes in the model, make the code generation fail so we make sure the
        // mapper stays in sync
        unmappedTargetPolicy = ReportingPolicy.ERROR)
abstract class GeoServerInstanceMapper {

    private @Autowired JpaGeoServerInstanceRepository geoserverInstances;

    static final String ANY = org.geoserver.geofence.jpa.model.GeoServerInstance.ANY;

    /**
     * Resolves the String to {@link org.geoserver.geofence.jpa.model.GeoServerInstance} mapping
     * using the instance name
     */
    public @NonNull GeoServerInstance instanceNameToGeoServer(String instanceName)
            throws EntityNotFoundException {
        return instanceName == null
                ? geoserverInstances.getInstanceAny()
                : geoserverInstances
                        .findByName(instanceName)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "GeoServer instance "
                                                        + instanceName
                                                        + " does not exist"));
    }

    public String instanceName(GeoServerInstance instance) {
        return instance == null || ANY.equals(instance.getName()) ? null : instance.getName();
    }
}
