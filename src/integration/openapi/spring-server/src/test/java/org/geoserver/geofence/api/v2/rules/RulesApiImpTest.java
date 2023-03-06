package org.geoserver.geofence.api.v2.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapperImpl;
import org.geoserver.geofence.api.v2.mapper.GeometryApiMapper;
import org.geoserver.geofence.api.v2.mapper.GeometryApiMapperImpl;
import org.geoserver.geofence.api.v2.mapper.IPAddressRangeApiMapperImpl;
import org.geoserver.geofence.api.v2.mapper.JsonNullableMapper;
import org.geoserver.geofence.api.v2.mapper.JsonNullableMapperImpl;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapper;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapperImpl;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapperImpl;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapperImpl;
import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.RuleQuery;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.repository.RuleIdentifierConflictException;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class RulesApiImpTest {

    private RuleAdminService service;
    private @Autowired RulesApiImpl api;
    private RuleApiMapper mapper;
    private EnumsApiMapper enumsMapper;

    @BeforeEach
    void setUp() throws Exception {
        service = mock(RuleAdminService.class);

        enumsMapper = new EnumsApiMapperImpl();
        JsonNullableMapper nullable = new JsonNullableMapperImpl();
        GeometryApiMapper geom = new GeometryApiMapperImpl();
        RuleLimitsApiMapper ruleLimits = new RuleLimitsApiMapperImpl(nullable, geom, enumsMapper);
        IPAddressRangeApiMapperImpl ipaddr = new IPAddressRangeApiMapperImpl();
        mapper = new RuleApiMapperImpl(nullable, enumsMapper, ruleLimits, ipaddr);
        LayerDetailsApiMapper detailsMapper =
                new LayerDetailsApiMapperImpl(nullable, geom, null, enumsMapper);
        api = new RulesApiImpl(service, mapper, detailsMapper, ruleLimits, enumsMapper);
    }

    @Test
    void testCreateRule() {
        Rule ret = Rule.allow().withId("1");
        when(service.insert(eq(Rule.allow()))).thenReturn(ret);

        assertResponse(() -> api.createRule(mapper.toApi(Rule.allow()), null), CREATED, ret);

        verify(service, times(1)).insert(eq(Rule.allow()));
        verifyNoMoreInteractions(service);
        clearInvocations(service);

        when(service.insert(any(), any()))
                .thenThrow(new RuleIdentifierConflictException("Duplicate identifier"));

        assertError(create(Rule.deny(), InsertPosition.FROM_END), CONFLICT, "Duplicate identifier");
        verify(service, times(1)).insert(eq(Rule.deny()), eq(InsertPosition.FROM_END));
    }

    private Supplier<ResponseEntity<org.geoserver.geofence.api.v2.model.Rule>> create(
            Rule modelRule, InsertPosition modelPos) {
        return () -> api.createRule(mapper.toApi(modelRule), enumsMapper.map(modelPos));
    }

    private void assertResponse(
            Supplier<ResponseEntity<org.geoserver.geofence.api.v2.model.Rule>> call,
            HttpStatus status,
            Rule expected) {

        ResponseEntity<org.geoserver.geofence.api.v2.model.Rule> responseEntity = call.get();
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);
        assertThat(responseEntity.getBody()).isEqualTo(mapper.toApi(expected));
    }

    private <T> void assertError(
            Supplier<ResponseEntity<T>> call, HttpStatus status, String reason) {

        ResponseEntity<T> responseEntity = call.get();
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);
        assertThat(responseEntity.getHeaders().get("X-Reason")).singleElement();
        assertThat(responseEntity.getHeaders().get("X-Reason").get(0)).contains(reason);
    }

    @Test
    void testDeleteRuleById() {
        when(service.delete("id1")).thenReturn(true);
        when(service.delete("id2")).thenReturn(false);
        assertThat(api.deleteRuleById("id1").getStatusCode()).isEqualTo(OK);
        assertThat(api.deleteRuleById("id2").getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void testGetRules() {
        RuleQuery<RuleFilter> expectedQuery = RuleQuery.of(1, 10);
        List<Rule> expected = List.of(Rule.allow(), Rule.deny());
        when(service.getList(eq(expectedQuery))).thenReturn(expected);

        List<Rule> actual = assertList(() -> api.getRules(1, 10), OK);
        assertThat(actual).isEqualTo(expected);
        verify(service, times(1)).getList(eq(expectedQuery));
    }

    private List<Rule> assertList(
            Supplier<ResponseEntity<List<org.geoserver.geofence.api.v2.model.Rule>>> call,
            HttpStatus status) {
        ResponseEntity<List<org.geoserver.geofence.api.v2.model.Rule>> response = call.get();
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().stream().map(mapper::toModel).collect(Collectors.toList());
    }

    @Disabled("Filter mapping not yet implemented")
    @Test
    void testQueryRules() {
        // org.geoserver.geofence.api.v2.model.RuleFilter filter;
        // api.queryRules(1, 10, 1000, filter);
        fail("Not yet implemented");
    }

    @Test
    void testGetRuleById() {
        Rule found = Rule.allow().withId("id1");
        when(service.get(eq("id1"))).thenReturn(Optional.of(found));
        when(service.get(eq("id2"))).thenReturn(Optional.empty());

        assertResponse(() -> api.getRuleById("id1"), OK, found);
        assertResponse(() -> api.getRuleById("id2"), NOT_FOUND, null);
    }

    @Test
    void testFindOneRuleByPriority() {
        Rule found = Rule.allow().withId("id1");
        when(service.getRuleByPriority(eq(1000L))).thenReturn(Optional.of(found));
        when(service.getRuleByPriority(eq(1001L))).thenReturn(Optional.empty());

        assertResponse(() -> api.findOneRuleByPriority(1000L), OK, found);
        assertResponse(() -> api.findOneRuleByPriority(1001L), NOT_FOUND, null);
    }

    @Test
    void testCountAllRules() {
        when(service.getCountAll()).thenReturn(37);

        assertThat(api.countAllRules().getStatusCode()).isEqualByComparingTo(OK);
        assertThat(api.countAllRules().getBody()).isEqualByComparingTo(37);
    }

    @Disabled("Filter mapping not yet implemented")
    @Test
    void testCountRules() {
        fail("Not yet implemented");
    }

    @Test
    void testRuleExistsById() {
        Rule found = Rule.allow().withId("id1");
        when(service.get(eq("id1"))).thenReturn(Optional.of(found));
        when(service.get(eq("id2"))).thenReturn(Optional.empty());

        assertThat(api.ruleExistsById("id1").getStatusCode()).isEqualByComparingTo(OK);
        assertThat(api.ruleExistsById("id1").getBody()).isTrue();

        assertThat(api.ruleExistsById("id2").getStatusCode()).isEqualByComparingTo(OK);
        assertThat(api.ruleExistsById("id2").getBody()).isFalse();
    }

    @Test
    void testSetRuleAllowedStyles() {
        doThrow(new IllegalArgumentException("message1"))
                .when(service)
                .setAllowedStyles("id1", Set.of());
        assertError(() -> api.setRuleAllowedStyles("id1", Set.of()), BAD_REQUEST, "message1");
        clearInvocations(service);

        assertThat(api.setRuleAllowedStyles("id1", Set.of("s1", "s2")).getStatusCode())
                .isEqualTo(OK);

        verify(service, times(1)).setAllowedStyles(eq("id1"), eq(Set.of("s1", "s2")));
    }
}
