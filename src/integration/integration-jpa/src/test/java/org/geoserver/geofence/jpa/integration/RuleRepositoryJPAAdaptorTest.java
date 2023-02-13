package org.geoserver.geofence.jpa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geoserver.geofence.jpa.config.GeoFenceDataSourceConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAIntegrationConfiguration;
import org.geoserver.geofence.jpa.model.GeoServerInstance;
import org.geoserver.geofence.jpa.repository.JpaGeoServerInstanceRepository;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(
        classes = {
            GeoFenceDataSourceConfiguration.class,
            GeoFenceJPAConfiguration.class,
            GeoFenceJPAIntegrationConfiguration.class
        },
        properties = {
            "spring.jpa.show-sql=true",
            "spring.jpa.properties.hibernate.format_sql=true",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.h2geodb.GeoDBDialect",
            "spring.jpa.properties.hibernate.hbm2ddl.auto=update",
            "geofence.datasource.url=jdbc:h2:mem:geofence-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "geofence.datasource.username=sa",
            "geofence.datasource.password=sa"
        })
@EnableAutoConfiguration
class RuleRepositoryJPAAdaptorTest {

    private static final String WORLD =
            "MULTIPOLYGON (((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))";

    private @Autowired JpaGeoServerInstanceRepository jpaInstances;

    private @Autowired RuleRepository repo;

    private String geoserverInstanceName;

    @BeforeEach
    void setup() {
        GeoServerInstance jpaGs =
                new GeoServerInstance()
                        .setName("defaultInstance")
                        .setBaseURL("http://localhost")
                        .setUsername("admin")
                        .setPassword("gs");
        jpaInstances.saveAndFlush(jpaGs);
        this.geoserverInstanceName = jpaGs.getName();
    }

    @Test
    void count() {
        assertThat(repo.count()).isZero();
        Rule r1 =
                Rule.builder()
                        .build()
                        .withInstance(geoserverInstanceName)
                        .withPriority(1)
                        .withAccess(GrantType.ALLOW);

        r1 = repo.create(r1, InsertPosition.FIXED);
        assertThat(repo.count()).isOne();

        Rule r2 = r1.withId(null).withPriority(2).withAccess(GrantType.LIMIT);
        r2 = repo.create(r2, InsertPosition.FIXED);
        assertThat(repo.count()).isEqualTo(2);

        assertThat(repo.findAll().count()).isEqualTo(2);

        repo.findById(r1.getId());
        repo.findById(r2.getId());

        List<Rule> collect = repo.findAll().collect(Collectors.toList());
        assertEquals(2, collect.size());
    }
}
