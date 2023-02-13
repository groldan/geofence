package org.geoserver.geofence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.querydsl.core.types.Predicate;

import lombok.SneakyThrows;

import org.geoserver.geofence.jpa.model.CatalogMode;
import org.geoserver.geofence.jpa.model.GeoServerInstance;
import org.geoserver.geofence.jpa.model.GrantType;
import org.geoserver.geofence.jpa.model.IPAddressRange;
import org.geoserver.geofence.jpa.model.LayerAttribute;
import org.geoserver.geofence.jpa.model.LayerAttribute.AccessType;
import org.geoserver.geofence.jpa.model.LayerDetails;
import org.geoserver.geofence.jpa.model.LayerDetails.LayerType;
import org.geoserver.geofence.jpa.model.QRule;
import org.geoserver.geofence.jpa.model.Rule;
import org.geoserver.geofence.jpa.model.RuleIdentifier;
import org.geoserver.geofence.jpa.model.RuleLimits;
import org.geoserver.geofence.jpa.model.SpatialFilterType;
import org.hibernate.TransientObjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest(
        showSql = false,
        properties = {
            "spring.jpa.properties.hibernate.format_sql=true",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.h2geodb.GeoDBDialect"
        })
@ContextConfiguration(classes = GeoFenceJPATestConfiguration.class)
@ActiveProfiles("test")
class JpaRuleRepositoryTest {

    private static final String WORLD =
            "MULTIPOLYGON (((-180 -90, -180 90, 180 90, 180 -90, -180 -90)))";

    private @Autowired JpaGeoServerInstanceRepository instanceRepo;
    private @Autowired JpaRuleRepository repo;

    private @Autowired TestEntityManager em;

    private GeoServerInstance anyInstance;

    private Rule entity;

    @BeforeEach
    void beforeEach() {
        anyInstance = instanceRepo.getInstanceAny();

        entity = new Rule();
        entity.getIdentifier().setInstance(anyInstance);
    }

    @Test
    void testIdentifierDefaultValues() {
        Rule rule = new Rule();
        assertNotNull(rule.getIdentifier());
        RuleIdentifier identifier = rule.getIdentifier();
        assertEquals("*", identifier.getLayer());
        assertEquals("*", identifier.getRequest());
        assertEquals("*", identifier.getRolename());
        assertEquals("*", identifier.getService());
        assertEquals("*", identifier.getSubfield());
        assertEquals("*", identifier.getUsername());
        assertEquals("*", identifier.getWorkspace());
        assertEquals(GrantType.DENY, identifier.getAccess());
        assertEquals(IPAddressRange.noData(), identifier.getAddressRange());
        assertNull(identifier.getInstance());
    }

    @Test
    void testIdentifierDoesNotAllowNull() {
        // non nullable attributes in RuleIdentified can't even be set to null
        RuleIdentifier identifier = entity.getIdentifier();
        assertThrows(NullPointerException.class, () -> identifier.setAccess(null));
        assertThrows(NullPointerException.class, () -> identifier.setAddressRange(null));
        assertThrows(NullPointerException.class, () -> identifier.setLayer(null));
        assertThrows(NullPointerException.class, () -> identifier.setRequest(null));
        assertThrows(NullPointerException.class, () -> identifier.setRolename(null));
        assertThrows(NullPointerException.class, () -> identifier.setService(null));
        assertThrows(NullPointerException.class, () -> identifier.setSubfield(null));
        assertThrows(NullPointerException.class, () -> identifier.setUsername(null));
        assertThrows(NullPointerException.class, () -> identifier.setWorkspace(null));

        entity.getIdentifier().setInstance(null);
        DataIntegrityViolationException expected =
                assertThrows(DataIntegrityViolationException.class, () -> repo.save(entity));
        assertThat(expected)
                .hasMessageContaining("not-null property references a null or transient value")
                .hasMessageContaining("identifier.instance");
    }

    @Test
    void testSaveDuplicateIdentifier_default_values() {
        testSaveDuplicateIdentifier(entity);
    }

    @Test
    void testSaveDuplicateIdentifier() {
        entity.getIdentifier()
                .setAccess(GrantType.LIMIT)
                .setAddressRange(new IPAddressRange(1000L, 2000L, 32))
                .setInstance(anyInstance)
                .setLayer("layer")
                .setRequest("GetCapabilities")
                .setRolename("ROLE_USER")
                .setService("WCS")
                .setSubfield("subfield")
                .setUsername("user")
                .setWorkspace("workspace");

        testSaveDuplicateIdentifier(entity);
    }

    private void testSaveDuplicateIdentifier(Rule rule) {
        rule = rule.clone().setId(null);
        Rule duplicateKey = rule.clone().setPriority(rule.getPriority() + 1000);
        assertEquals(rule.getIdentifier(), duplicateKey.getIdentifier());

        repo.saveAndFlush(rule);

        assertThrows(DataIntegrityViolationException.class, () -> repo.saveAndFlush(duplicateKey));
    }

    @Test
    void testSave_Identifier_defaultValues() {

        RuleIdentifier expected = entity.getIdentifier().clone();

        Rule saved = repo.saveAndFlush(entity);
        assertSame(entity, saved);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getIdentifier()).isEqualTo(expected);
    }

    @Test
    void testSave_Identifier() {
        GeoServerInstance gsInstance2 =
                new GeoServerInstance()
                        .setName("secondInstance")
                        .setBaseURL("http://localhost:9090/geoserver")
                        .setDateCreation(new java.sql.Date(100000))
                        .setDescription("Default geoserver instance")
                        .setUsername("admin")
                        .setPassword("geoserver");

        em.persistAndFlush(gsInstance2);

        RuleIdentifier expected =
                entity.getIdentifier()
                        .setAccess(GrantType.DENY)
                        .setAddressRange(new IPAddressRange(1000L, 2000L, 32))
                        .setInstance(gsInstance2)
                        .setLayer("layer")
                        .setRequest("GetCapabilities")
                        .setRolename("ROLE_USER")
                        .setService("WCS")
                        .setSubfield("subfield")
                        .setUsername("user")
                        .setWorkspace("workspace")
                        .clone();

        Rule saved = repo.saveAndFlush(entity);
        em.detach(saved);

        Rule found = repo.getReferenceById(saved.getId());
        assertThat(found.getIdentifier()).isNotSameAs(saved.getIdentifier()).isEqualTo(expected);
    }

    @Test
    void testSave_fails_on_dettached_GeoServerInstance() {

        GeoServerInstance unsavedGsInstance =
                new GeoServerInstance()
                        .setName("unsaved")
                        .setBaseURL("http://localhost:8080/geoserver");

        entity.getIdentifier().setInstance(unsavedGsInstance).clone();

        InvalidDataAccessApiUsageException expected =
                assertThrows(
                        InvalidDataAccessApiUsageException.class, () -> repo.saveAndFlush(entity));
        assertThat(expected.getCause())
                .isInstanceOf(IllegalStateException.class)
                .getCause()
                .isInstanceOf(TransientObjectException.class);
    }

    @Test
    void testRuleLimits() {
        assertNull(entity.getRuleLimits());

        Rule saved = em.persistAndFlush(entity);
        assertNull(saved.getRuleLimits());

        final MultiPolygon allowedArea = geom(WORLD);

        RuleLimits expected = new RuleLimits();
        saved.setRuleLimits(expected);
        saved.getRuleLimits()
                .setAllowedArea(allowedArea)
                .setCatalogMode(CatalogMode.MIXED)
                .setSpatialFilterType(SpatialFilterType.CLIP)
                .clone();

        saved = em.persistAndFlush(entity);
        em.detach(saved);

        assertNotNull(saved.getRuleLimits());

        Rule found = repo.getReferenceById(saved.getId());
        assertThat(found.getRuleLimits()).isNotSameAs(saved.getRuleLimits()).isEqualTo(expected);
    }

    @Test
    void testLayerDetails() {
        assertNull(entity.getLayerDetails());
        entity.setLayerDetails(new LayerDetails());
        final MultiPolygon area = geom(WORLD);

        Set<LayerAttribute> attributes = Set.of(latt("att1"), latt("att2"));
        LayerDetails expected =
                entity.getLayerDetails()
                        .setAllowedStyles(Set.of("s1", "s2"))
                        .setArea(area)
                        .setAttributes(attributes)
                        .setCatalogMode(CatalogMode.CHALLENGE)
                        .setCqlFilterRead("a=b")
                        .setCqlFilterWrite("foo=bar")
                        .setDefaultStyle("defstyle")
                        .setSpatialFilterType(SpatialFilterType.CLIP)
                        .setType(LayerType.LAYERGROUP)
                        .clone();

        Rule saved = em.persistAndFlush(entity);
        em.detach(saved);

        Rule found = repo.getReferenceById(saved.getId());
        assertThat(found.getLayerDetails())
                .isNotSameAs(saved.getLayerDetails())
                .isEqualTo(expected);

        // verify multiple attributes don't result in duplicates as it used to be at least in the
        // old entity comments
        assertThat(repo.findAllById(List.of(saved.getId()))).singleElement();
        assertThat(repo.findAll()).singleElement();
    }

    @Test
    void testLayerDetails_unset_allowedStyles() {
        entity.setLayerDetails(new LayerDetails());
        entity.getLayerDetails()
                .setAllowedStyles(Set.of("s1", "s2"))
                .setCatalogMode(CatalogMode.CHALLENGE)
                .clone();

        Rule rule = em.persistAndFlush(entity);
        em.detach(rule);
        final long ruleId = rule.getId();

        Rule found = repo.getReferenceById(ruleId);
        assertNotSame(rule, found);

        found.getLayerDetails().setAllowedStyles(null);
        repo.saveAndFlush(found);
        em.detach(found);

        rule = repo.getReferenceById(ruleId);
        assertNotSame(found, rule);
        assertThat(rule.getLayerDetails().getAllowedStyles()).isEmpty();
    }

    @Test
    void testLayerDetails_update_allowedStyles() {
        entity.setLayerDetails(new LayerDetails());
        entity.getLayerDetails()
                .setAllowedStyles(Set.of("s1", "s2"))
                .setCatalogMode(CatalogMode.CHALLENGE)
                .clone();

        Rule rule = em.persistAndFlush(entity);
        em.detach(rule);
        final long ruleId = rule.getId();

        Rule found = repo.getReferenceById(ruleId);
        assertNotSame(rule, found);

        Set<String> newStyles = Set.of("newstyle1", "s1", "newstyle2");

        found.getLayerDetails().getAllowedStyles().clear();
        found.getLayerDetails().getAllowedStyles().addAll(newStyles);
        repo.saveAndFlush(found);
        em.detach(found);

        rule = repo.getReferenceById(ruleId);
        assertNotSame(found, rule);
        assertThat(rule.getLayerDetails().getAllowedStyles())
                .isEqualTo(Set.of("newstyle1", "s1", "newstyle2"));
    }

    /** {@link JpaRuleRepository#findAllNaturalOrder()} */
    @Test
    void findAllNaturalOrder() {
        List<Rule> expected = addSamplesInReverseNaturalOrder();
        List<Rule> actual = repo.findAllNaturalOrder();
        assertEquals(expected, actual);
    }

    /** {@link JpaRuleRepository#findAllNaturalOrder(Predicate)} */
    @Test
    void findAllNaturalOrderFiltered() {
        final List<Rule> all = addSamplesInReverseNaturalOrder();

        QRule qRule = QRule.rule;
        Predicate predicate = qRule.priority.gt(2L).and(qRule.identifier.layer.eq("*"));

        List<Rule> expected =
                all.stream()
                        .filter(
                                r ->
                                        r.getPriority() > 2L
                                                && "*".equals(r.getIdentifier().getLayer()))
                        .collect(Collectors.toList());

        List<Rule> actual = repo.findAllNaturalOrder(predicate);
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual).isEqualTo(expected);
    }

    /** {@link JpaRuleRepository#findAllNaturalOrder(Pageable)} */
    @Test
    void findAllNaturalOrderPaged() {
        final List<Rule> expected = addSamplesInReverseNaturalOrder();

        assertNaturalOrderPaged(
                expected,
                (Predicate) null,
                (predicate, pageable) -> repo.findAllNaturalOrder(pageable));
    }

    /** {@link JpaRuleRepository#findAllNaturalOrder(Predicate, Pageable)} */
    @Test
    void findAllNaturalOrderFilteredAndPaged() {
        final List<Rule> all = addSamplesInReverseNaturalOrder();

        QRule qRule = QRule.rule;
        Predicate predicate = qRule.priority.gt(2L).and(qRule.identifier.layer.eq("*"));

        List<Rule> expected =
                all.stream()
                        .filter(
                                r ->
                                        r.getPriority() > 2L
                                                && "*".equals(r.getIdentifier().getLayer()))
                        .collect(Collectors.toList());

        assertNaturalOrderPaged(expected, predicate, repo::findAllNaturalOrder);
    }

    private void assertNaturalOrderPaged(
            final List<Rule> all,
            Predicate predicate,
            BiFunction<Predicate, Pageable, Page<Rule>> function) {
        final int size = all.size();
        final int pageSize = 2;
        final int pages = 1 + size / pageSize;
        assertThat(pages).isGreaterThan(1);

        for (int pageN = 0; pageN < pages; pageN++) {
            PageRequest request = PageRequest.of(pageN, pageSize);
            Page<Rule> page = function.apply(predicate, request);
            int offset = pageN * pageSize;
            int toIndex = Math.min(offset + pageSize, all.size());
            List<Rule> expectedContents = all.subList(offset, toIndex);
            assertEquals(expectedContents, page.getContent());
        }

        PageRequest request = PageRequest.of(1 + pages, pageSize);
        assertThat(function.apply(predicate, request).getContent()).isEmpty();

        Pageable unpaged = Pageable.unpaged();
        assertThat(function.apply(predicate, unpaged).getContent()).isEqualTo(all);
    }

    /** Adds sample rules in reverse natural order and returns them in natural order */
    private List<Rule> addSamplesInReverseNaturalOrder() {
        Rule rule = this.entity;
        List<Rule> expected = new ArrayList<>();

        expected.add(rule.clone());

        rule.getIdentifier().setAccess(GrantType.LIMIT);
        expected.add(rule.clone());

        rule.getIdentifier().setAddressRange(new IPAddressRange(1000L, 2000L, 32));
        expected.add(rule.clone());

        rule.getIdentifier().setService("service");
        expected.add(rule.clone());

        rule.getIdentifier().setRequest("request");
        expected.add(rule.clone());

        rule.getIdentifier().setRolename("rolename");
        expected.add(rule.clone());

        rule.getIdentifier().setUsername("user");
        expected.add(rule.clone());

        rule.getIdentifier().setWorkspace("workspace");
        expected.add(rule.clone());

        rule.getIdentifier().setLayer("layer");
        expected.add(rule.clone());

        rule.getIdentifier().setAccess(GrantType.ALLOW);
        expected.add(rule.clone());

        rule.getIdentifier().setSubfield("subfield");
        expected.add(rule.clone());

        IntStream.range(0, expected.size()).forEach(p -> expected.get(p).setPriority(p));

        List<Rule> reversed = new ArrayList<>(expected);
        Collections.reverse(reversed);
        repo.saveAllAndFlush(reversed);

        Collections.sort(expected, (r1, r2) -> Long.compare(r1.getPriority(), r2.getPriority()));
        return expected;
    }

    private LayerAttribute latt(String name) {
        return new LayerAttribute().setAccess(AccessType.NONE).setDataType("Integer").setName(name);
    }

    @SneakyThrows({ParseException.class})
    private MultiPolygon geom(String wkt) {
        return (MultiPolygon) new WKTReader().read(wkt);
    }
}
