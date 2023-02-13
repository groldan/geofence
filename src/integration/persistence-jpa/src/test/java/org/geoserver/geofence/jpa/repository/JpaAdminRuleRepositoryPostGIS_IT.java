package org.geoserver.geofence.jpa.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest(
        showSql = false,
        properties = {
            "spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect",
            "spring.jpa.hibernate.ddl-auto=create"
        })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = GeoFenceJPATestConfiguration.class)
@ActiveProfiles("test")
class JpaAdminRuleRepositoryPostGIS_IT extends JpaAdminRuleRepositoryTest {

    private static final DockerImageName POSTGIS_IMAGE_NAME =
            DockerImageName.parse("postgis/postgis").asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer<?> postgis = new PostgreSQLContainer<>(POSTGIS_IMAGE_NAME);

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgis.getJdbcUrl());
        registry.add("spring.datasource.username", postgis::getUsername);
        registry.add("spring.datasource.password", postgis::getPassword);
    }
}
