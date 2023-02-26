package org.geoserver.geofence.jpa.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {GeoFenceJPAIntegrationConfiguration.class})
@ActiveProfiles("test") // see config props in src/test/resource/application-test.yaml
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
