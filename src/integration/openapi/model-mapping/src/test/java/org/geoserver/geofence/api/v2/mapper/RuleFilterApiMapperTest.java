package org.geoserver.geofence.api.v2.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.predicate.FilterType;
import org.geoserver.geofence.filter.predicate.SpecialFilterType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;

@Slf4j(topic = "mapper-test")
class RuleFilterApiMapperTest {

    private RuleFilterApiMapper mapper = new RuleFilterApiMapper();

    private static ObjectMapper objectMapper;

    private RuleFilter filter;

    @BeforeAll
    static void createObjectMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void beforeEach() {
        filter = new RuleFilter();
    }

    @Test
    void testIdName_default() {
        filter.getInstance().setType(SpecialFilterType.DEFAULT);
        testRoundtrip(filter);
    }

    @Test
    void testIdName_id() {
        filter.setInstance(1000L);
        testRoundtrip(filter);
        filter.getInstance().setIncludeDefault(false);
        testRoundtrip(filter);
    }

    @Test
    void testIdName_name() {
        filter.setInstance("instance1");
        testRoundtrip(filter);
        filter.getInstance().setIncludeDefault(false);
        testRoundtrip(filter);
    }

    @Test
    void testIdName_any() {
        filter.getInstance().setHeuristically("*");
        assertEquals(FilterType.ANY, filter.getInstance().getType());
        testRoundtrip(filter);
    }

    @Test
    void testRoles_default() {
        filter.getRole().setType(SpecialFilterType.DEFAULT);
        testRoundtrip(filter);
    }

    @Test
    void testRoles_any() {
        filter.getRole().setHeuristically("*");
        assertEquals(FilterType.ANY, filter.getRole().getType());
        testRoundtrip(filter);
    }

    @Test
    void testRoles_single() {
        filter.setRole("r1");
        testRoundtrip(filter);
        filter.getRole().setIncludeDefault(false);
        testRoundtrip(filter);
    }

    @Test
    void testRoles_multiple() {
        filter.setRole("r3,r2,r1");
        testRoundtrip(filter);
        filter.getRole().setIncludeDefault(false);
        testRoundtrip(filter);
    }

    @Test
    void testDefault() {
        RuleFilter model = new RuleFilter();
        testRoundtrip(model);
    }

    @Test
    void testFull() {
        RuleFilter model = new RuleFilter();
        model.setInstance(33L);
        model.getInstance().setIncludeDefault(false);
        model.setLayer("layer");
        model.getRequest().setHeuristically("*");
        model.setService("service");
        model.setRole("r1,r3,r2");
        model.setSourceAddress("192.168.0.1/32");
        model.setSubfield("sf");
        model.setUser("user");
        model.getUser().setIncludeDefault(false);
        model.setWorkspace("ws");
        testRoundtrip(model);
    }

    private void testRoundtrip(RuleFilter model) {
        org.geoserver.geofence.api.v2.model.RuleFilter api = mapper.toApi(model);
        RuleFilter roundtripped = mapper.toModel(api);
        print(model, api, roundtripped);
        assertEquals(model.getInstance(), roundtripped.getInstance());
        assertEquals(model.getLayer(), roundtripped.getLayer());
        assertEquals(model.getRequest(), roundtripped.getRequest());
        assertEquals(model.getRole(), roundtripped.getRole());
        assertEquals(model.getService(), roundtripped.getService());
        assertEquals(model.getSourceAddress(), roundtripped.getSourceAddress());
        assertEquals(model.getSubfield(), roundtripped.getSubfield());
        assertEquals(model.getUser(), roundtripped.getUser());
        assertEquals(model.getWorkspace(), roundtripped.getWorkspace());
        assertEquals(model, roundtripped);
    }

    @SneakyThrows(JsonProcessingException.class)
    private void print(
            RuleFilter model,
            org.geoserver.geofence.api.v2.model.RuleFilter api,
            RuleFilter roundtripped) {
        log.debug("model: {}", model);
        log.debug("api  : {}", objectMapper.writeValueAsString(api));
        log.debug("rtp  : {}", roundtripped);
    }

    @SneakyThrows(JsonProcessingException.class)
    void print(org.geoserver.geofence.api.v2.model.RuleFilter api) {
        String encoded = objectMapper.writeValueAsString(api);
        System.err.println(encoded);
    }
}
