package org.geoserver.geofence.rules.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.geoserver.geofence.rules.model.CatalogMode;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.geoserver.geofence.rules.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

class RuleAdminServiceTest {

    private RuleRepository repository;
    private RuleAdminService service;

    @BeforeEach
    void setUp() throws Exception {
        repository = mock(RuleRepository.class);
        service = new RuleAdminService(repository);
    }

    @Test
    void testRuleAdminServiceConstructor() {
        assertThrows(NullPointerException.class, () -> new RuleAdminService(null));
    }

    @Test
    void insertRule() {
        Rule rule = Rule.allow();
        Rule expected = rule.withId("1");
        when(repository.create(eq(rule), eq(InsertPosition.FIXED))).thenReturn(expected);

        Rule created = service.insert(rule);
        verify(repository, times(1)).create(eq(rule), eq(InsertPosition.FIXED));
        verifyNoMoreInteractions(repository);
        assertSame(expected, created);
    }

    @Test
    void insertRuleNonNullId() {
        IllegalArgumentException expected =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.insert(Rule.deny().withId("100")));
        assertThat(expected.getMessage()).contains("a new Rule must not have id, got 100");
    }

    @Test
    void insertRule_sanitizeFields() {
        Rule rule = Rule.allow().withPriority(10).withService("wms").withRequest("getcapabilities");
        Rule expected = rule.withService("WMS").withRequest("GETCAPABILITIES");

        when(repository.create(eq(expected), eq(InsertPosition.FIXED))).thenReturn(expected);

        assertSame(expected, service.insert(rule));
        verify(repository, times(1)).create(eq(expected), eq(InsertPosition.FIXED));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void insertRuleInsertPosition() {
        Rule rule = Rule.allow();

        when(repository.create(eq(rule), eq(InsertPosition.FROM_START))).thenReturn(rule);

        assertSame(rule, service.insert(rule, InsertPosition.FROM_START));
        verify(repository, times(1)).create(eq(rule), eq(InsertPosition.FROM_START));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateNullId() {
        Rule ruleWithNullId = Rule.limit();
        IllegalArgumentException expected =
                assertThrows(IllegalArgumentException.class, () -> service.update(ruleWithNullId));
        assertThat(expected.getMessage()).contains("Rule has no id");
    }

    @Test
    void testUpdate() {
        Rule rule = Rule.allow().withId("5");
        Rule ret = Rule.allow().withId("5");
        when(repository.save(same(rule))).thenReturn(ret);
        assertSame(ret, service.update(rule));

        verify(repository, times(1)).save(same(rule));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update_sanitizeFields() {
        Rule rule =
                Rule.allow()
                        .withId("1")
                        .withPriority(10)
                        .withService("wms")
                        .withRequest("getcapabilities");
        Rule expected = rule.withService("WMS").withRequest("GETCAPABILITIES");

        when(repository.save(eq(expected))).thenReturn(expected);

        service.update(rule);
        verify(repository, times(1)).save(eq(expected));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shiftNegativeOffset() {
        IllegalArgumentException expected =
                assertThrows(IllegalArgumentException.class, () -> service.shift(0, -1));
        assertThat(expected.getMessage()).contains("Positive offset required");
    }

    @Test
    void shift() {
        service.shift(10L, 100L);
        verify(repository, times(1)).shift(10L, 100L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void swapPriority() {
        service.swapPriority("1", "2");
        verify(repository, times(1)).swap(eq("1"), eq("2"));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGet() {
        Optional<Rule> expected = Optional.of(Rule.deny().withId("10L"));
        when(repository.findById("10L")).thenReturn(expected);
        assertSame(expected, service.get("10L"));
        verify(repository, times(1)).findById("10L");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete() {
        when(repository.delete("1")).thenReturn(false);
        when(repository.delete("2")).thenReturn(true);
        assertFalse(service.delete("1"));
        assertTrue(service.delete("2"));
        verify(repository, times(1)).delete("1");
        verify(repository, times(1)).delete("2");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteRulesByUser() {
        RuleFilter expectedFilter = new RuleFilter().setUser("John");
        testDeleteWithFilter(expectedFilter, () -> service.deleteRulesByUser("John"));
        assertThrows(NullPointerException.class, () -> service.deleteRulesByUser(null));
    }

    @Test
    void deleteRulesByRole() {
        RuleFilter expectedFilter = new RuleFilter().setRole("ROLE_EDITOR");
        testDeleteWithFilter(expectedFilter, () -> service.deleteRulesByRole("ROLE_EDITOR"));
        assertThrows(NullPointerException.class, () -> service.deleteRulesByRole(null));
    }

    @Test
    void testDeleteRulesByInstance() {
        RuleFilter expectedFilter = new RuleFilter().setInstance(101L);
        testDeleteWithFilter(expectedFilter, () -> service.deleteRulesByInstance(101L));
    }

    private void testDeleteWithFilter(
            RuleFilter expectedFilter, Supplier<List<String>> serviceMethodCall) {
        // four rules match the filter
        Rule match1 = Rule.allow().withId("1");
        Rule match2 = Rule.allow().withId("2");
        Rule match3 = Rule.allow().withId("3");
        Rule match4 = Rule.allow().withId("4");

        // but only rules with id 1 and 3 get deleted (lets say someone else deleted them)
        when(repository.delete(eq("1"))).thenReturn(true);
        when(repository.delete(eq("2"))).thenReturn(false);
        when(repository.delete(eq("3"))).thenReturn(true);
        when(repository.delete(eq("4"))).thenReturn(false);

        when(repository.query(eq(RuleQuery.of(expectedFilter))))
                .thenReturn(Stream.of(match1, match2, match3, match4));

        List<String> expectedIds = List.of("1", "3");
        List<String> ret = serviceMethodCall.get();

        verify(repository, times(1)).query(eq(RuleQuery.of(expectedFilter)));
        verify(repository, times(4)).delete(anyString());

        assertThat(ret).isEqualTo(expectedIds);
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(null);
        assertThrows(NullPointerException.class, service::getAll);
        clearInvocations(repository);

        List<Rule> expected = List.of(Rule.allow(), Rule.deny(), Rule.limit());
        when(repository.findAll()).thenReturn(expected.stream());

        List<Rule> actual = service.getAll();
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getList() {
        assertThrows(NullPointerException.class, () -> service.getList(null));

        RuleQuery<RuleFilter> query = RuleQuery.of(new RuleFilter().setRole("role1"));
        when(repository.query(eq(query))).thenReturn(null);
        assertThrows(NullPointerException.class, () -> service.getList(query));
        clearInvocations(repository);

        List<Rule> expected =
                List.of(
                        Rule.allow().withRolename("role1"),
                        Rule.deny().withRolename("role1"),
                        Rule.limit().withRolename("role1"));

        when(repository.query(query)).thenReturn(expected.stream());

        List<Rule> actual = service.getList(query);
        verify(repository, times(1)).query(eq(query));
        verifyNoMoreInteractions(repository);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRule_null_filter() {
        assertThrows(NullPointerException.class, () -> service.getRule(null));
    }

    @Test
    void getRule_multiple_matches() {
        RuleFilter filter = new RuleFilter().setInstance("1").setLayer("states");
        RuleQuery<RuleFilter> query = RuleQuery.of(filter).setPageSize(0).setPageSize(2);

        when(repository.query(eq(query))).thenReturn(List.of(Rule.allow(), Rule.deny()).stream());

        IllegalArgumentException expected =
                assertThrows(IllegalArgumentException.class, () -> service.getRule(filter));
        assertThat(expected.getMessage()).contains("Unexpected rule count for filter");
    }

    @Test
    void getRule_no_match() {
        RuleFilter filter = new RuleFilter().setInstance("1").setLayer("states");
        RuleQuery<RuleFilter> query = RuleQuery.of(filter).setPageSize(0).setPageSize(2);

        when(repository.query(eq(query))).thenReturn(Stream.of());
        assertThat(service.getRule(filter)).isEmpty();
    }

    @Test
    void getRule() {
        RuleFilter filter = new RuleFilter().setInstance("1").setLayer("states");
        RuleQuery<RuleFilter> query = RuleQuery.of(filter).setPageSize(0).setPageSize(2);

        Rule match = Rule.allow().withId("10L");
        when(repository.query(eq(query))).thenReturn(Stream.of(match));

        assertThat(service.getRule(filter)).isPresent().get().isEqualTo(match);
    }

    @Test
    void getRuleByPriority() {
        Optional<Rule> expected = Optional.of(Rule.deny());
        when(repository.findByPriority(eq(0L))).thenReturn(expected);

        Optional<Rule> ret = service.getRuleByPriority(0L);
        verify(repository, times(1)).findByPriority(eq(0L));
        assertThat(ret).isSameAs(expected);
    }

    @Test
    void getCountAll() {
        when(repository.count()).thenReturn(1_000_000);
        assertThat(service.getCountAll()).isEqualTo(1_000_000);
        verify(repository, times(1)).count();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void countFilter() {
        RuleFilter filter = new RuleFilter().setInstance(3L);

        when(repository.count(eq(filter))).thenReturn(1_000_000);
        assertThat(service.count(filter)).isEqualTo(1_000_000);
        verify(repository, times(1)).count(eq(filter));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void setLimits() {
        service.setLimits("1", (RuleLimits) null);
        verify(repository, times(1)).setLimits(eq("1"), isNull());

        RuleLimits limits = RuleLimits.builder().catalogMode(CatalogMode.CHALLENGE).build();
        service.setLimits("1", limits);
        verify(repository, times(1)).setLimits(eq("1"), same(limits));
    }

    @Test
    void getLayerDetails_Rule() {
        assertThrows(NullPointerException.class, () -> service.getLayerDetails((Rule) null));

        Rule withNullId = Rule.allow();
        assertThrows(NullPointerException.class, () -> service.getLayerDetails(withNullId));

        Rule rule = withNullId.withId("100");
        LayerDetails ld = LayerDetails.builder().allowedStyles(Set.of("s1")).build();

        when(repository.findLayerDetailsByRuleId(eq("100"))).thenReturn(Optional.of(ld));

        Optional<LayerDetails> actual = service.getLayerDetails(rule);
        verify(repository, times(1)).findLayerDetailsByRuleId(eq("100"));
        assertThat(actual).isPresent().get().isEqualTo(ld);
    }

    @Test
    void getLayerDetailsLong() {
        LayerDetails ld = LayerDetails.builder().allowedStyles(Set.of("s1")).build();

        when(repository.findLayerDetailsByRuleId(eq("100"))).thenReturn(Optional.of(ld));

        Optional<LayerDetails> actual = service.getLayerDetails("100");
        verify(repository, times(1)).findLayerDetailsByRuleId(eq("100"));
        assertThat(actual).isPresent().get().isEqualTo(ld);
    }

    @Test
    void setLayerDetails() {
        service.setLayerDetails("1", null);
        verify(repository, times(1)).setLayerDetails(eq("1"), isNull());
        clearInvocations(repository);

        LayerDetails ld = LayerDetails.builder().allowedStyles(Set.of("s1")).build();
        service.setLayerDetails("2", ld);
        verify(repository, times(1)).setLayerDetails(eq("2"), same(ld));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void setAllowedStyles() {
        service.setAllowedStyles("1", null);
        verify(repository, times(1)).setAllowedStyles(eq("1"), isNull());
        clearInvocations(repository);

        service.setAllowedStyles("1", Set.of("s1", "s2"));
        verify(repository, times(1)).setAllowedStyles(eq("1"), eq(Set.of("s1", "s2")));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAllowedStyles() {
        LayerDetails ld = LayerDetails.builder().allowedStyles(Set.of("s1")).build();
        when(repository.findLayerDetailsByRuleId(eq("1"))).thenReturn(Optional.of(ld));

        Set<String> actual = service.getAllowedStyles("1");
        verify(repository, times(1)).findLayerDetailsByRuleId(eq("1"));
        verifyNoMoreInteractions(repository);
        assertThat(actual).isEqualTo(ld.getAllowedStyles());
    }
}
