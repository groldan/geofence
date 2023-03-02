package org.geoserver.geofence.api.v2.it.support;

import org.geoserver.geofence.jpa.repository.JpaAdminRuleRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerInstanceRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserGroupRepository;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserRepository;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerContextSupport {

    private @Autowired JpaRuleRepository jpaRuleRepository;
    private @Autowired JpaAdminRuleRepository jpaAdminRuleRepository;
    private @Autowired JpaGeoServerInstanceRepository jpaGeoServerInstanceRepository;
    private @Autowired JpaGeoServerUserRepository jpaGeoServerUserRepository;
    private @Autowired JpaGeoServerUserGroupRepository jpaGeoServerUserGroupRepository;

    public void setUp() {
        jpaRuleRepository.deleteAll();
        jpaAdminRuleRepository.deleteAll();
        jpaGeoServerUserRepository.deleteAll();
        jpaGeoServerUserGroupRepository.deleteAll();
        jpaGeoServerInstanceRepository.deleteAll();
    }
}
