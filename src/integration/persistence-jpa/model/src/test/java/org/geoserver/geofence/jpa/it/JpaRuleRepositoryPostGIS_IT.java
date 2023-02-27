package org.geoserver.geofence.jpa.it;

import org.geoserver.geofence.jpa.config.GeoFenceDataSourceConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAConfiguration;
import org.geoserver.geofence.jpa.repository.JpaRuleRepositoryTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.transaction.Transactional;

@Testcontainers(disabledWithoutDocker = true)
@Transactional
@SpringBootTest(classes = {GeoFenceDataSourceConfiguration.class, GeoFenceJPAConfiguration.class})
// see config props in src/test/resource/application-test.yaml
@ActiveProfiles("test")
class JpaRuleRepositoryPostGIS_IT extends JpaRuleRepositoryTest {

    private static final DockerImageName POSTGIS_IMAGE_NAME =
            DockerImageName.parse("postgis/postgis").asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer<?> postgis = new PostgreSQLContainer<>(POSTGIS_IMAGE_NAME);

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("geofence.datasource.url", () -> postgis.getJdbcUrl());
        registry.add("geofence.datasource.username", postgis::getUsername);
        registry.add("geofence.datasource.password", postgis::getPassword);
    }
}
