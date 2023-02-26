package org.geoserver.geofence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.querydsl.core.types.Predicate;

import org.geoserver.geofence.jpa.config.GeoFenceDataSourceConfiguration;
import org.geoserver.geofence.jpa.config.GeoFenceJPAConfiguration;
import org.geoserver.geofence.jpa.model.AdminGrantType;
import org.geoserver.geofence.jpa.model.AdminRule;
import org.geoserver.geofence.jpa.model.AdminRuleIdentifier;
import org.geoserver.geofence.jpa.model.GeoServerInstance;
import org.geoserver.geofence.jpa.model.IPAddressRange;
import org.geoserver.geofence.jpa.model.QAdminRule;
import org.hibernate.TransientObjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Transactional
@SpringBootTest(classes = {GeoFenceDataSourceConfiguration.class, GeoFenceJPAConfiguration.class})
@ActiveProfiles("test")
public class JpaAdminRuleRepositoryTest {

    private @Autowired JpaGeoServerInstanceRepository instanceRepo;
    private @Autowired JpaAdminRuleRepository repo;

    private @Autowired EntityManager em;

    private GeoServerInstance anyInstance;

    private AdminRule entity;

    @BeforeEach
    void beforeEach() {
        anyInstance = instanceRepo.getInstanceAny();

        entity = new AdminRule();
        entity.getIdentifier().setInstance(anyInstance);
    }

    @Test
    void testDefaultValues() {
        AdminRule rule = new AdminRule();
        assertNotNull(rule.getIdentifier());
        AdminRuleIdentifier identifier = rule.getIdentifier().setInstance(anyInstance);
        assertEquals("*", identifier.getRolename());
        assertEquals("*", identifier.getUsername());
        assertEquals("*", identifier.getWorkspace());
        assertEquals(AdminGrantType.USER, rule.getAccess());
        assertEquals(IPAddressRange.noData(), identifier.getAddressRange());

        rule = repo.saveAndFlush(rule);
        em.detach(rule);

        AdminRule saved = repo.getReferenceById(rule.getId());
        assertNotSame(rule, saved);
        assertThat(saved).isEqualTo(rule);
    }

    @Test
    void testMandatoryProperties() {
        // non nullable attributes in RuleIdentified can't even be set to null
        AdminRuleIdentifier identifier = entity.getIdentifier();
        assertThrows(NullPointerException.class, () -> identifier.setAddressRange(null));
        assertThrows(NullPointerException.class, () -> identifier.setRolename(null));
        assertThrows(NullPointerException.class, () -> identifier.setUsername(null));
        assertThrows(NullPointerException.class, () -> identifier.setWorkspace(null));
        assertThrows(NullPointerException.class, () -> entity.setAccess(null));

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
        entity.setAccess(AdminGrantType.ADMIN)
                .getIdentifier()
                .setAddressRange(new IPAddressRange(1000L, 2000L, 32))
                .setInstance(anyInstance)
                .setRolename("ROLE_USER")
                .setWorkspace("workspace");

        testSaveDuplicateIdentifier(entity);
    }

    private void testSaveDuplicateIdentifier(AdminRule rule) {
        rule = rule.clone().setId(null);
        AdminRule duplicateKey = rule.clone().setPriority(rule.getPriority() + 1000);
        assertEquals(rule.getIdentifier(), duplicateKey.getIdentifier());

        repo.saveAndFlush(rule);

        assertThrows(DataIntegrityViolationException.class, () -> repo.saveAndFlush(duplicateKey));
    }

    @Test
    void testSave_Identifier() {
        GeoServerInstance gsInstance2 =
                new GeoServerInstance()
                        .setName("secondInstance")
                        .setBaseURL("http://localhost:9090/geoserver")
                        .setDescription("Default geoserver instance")
                        .setUsername("admin")
                        .setPassword("geoserver");

        gsInstance2 = instanceRepo.saveAndFlush(gsInstance2);

        AdminRuleIdentifier expected =
                entity.getIdentifier()
                        .setAddressRange(new IPAddressRange(1000L, 2000L, 32))
                        .setInstance(gsInstance2)
                        .setRolename("ROLE_USER")
                        .setUsername("user")
                        .setWorkspace("workspace")
                        .clone();

        AdminRule saved = repo.saveAndFlush(entity);
        em.detach(saved);

        AdminRule found = repo.getReferenceById(saved.getId());
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
    void findAllNaturalOrder() {
        List<AdminRule> expected = addSamplesInReverseNaturalOrder();
        List<AdminRule> actual = repo.findAllNaturalOrder();
        assertEquals(expected, actual);
    }

    @Test
    void findAllNaturalOrderFiltered() {
        final List<AdminRule> all = addSamplesInReverseNaturalOrder();

        QAdminRule qadm = QAdminRule.adminRule;
        Predicate predicate = qadm.priority.gt(2L).and(qadm.identifier.workspace.eq("*"));

        List<AdminRule> expected =
                all.stream()
                        .filter(
                                r ->
                                        r.getPriority() > 2L
                                                && "*".equals(r.getIdentifier().getWorkspace()))
                        .collect(Collectors.toList());

        List<AdminRule> actual = repo.findAllNaturalOrder(predicate);
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllNaturalOrderPaged() {
        final List<AdminRule> expected = addSamplesInReverseNaturalOrder();

        assertNaturalOrderPaged(
                expected,
                (Predicate) null,
                (predicate, pageable) -> repo.findAllNaturalOrder(pageable));
    }

    @Test
    void findAllNaturalOrderFilteredAndPaged() {
        final List<AdminRule> all = addSamplesInReverseNaturalOrder();

        QAdminRule qadmr = QAdminRule.adminRule;
        com.querydsl.core.types.Predicate predicate =
                qadmr.priority.gt(2L).and(qadmr.identifier.workspace.eq("*"));

        List<AdminRule> expected =
                all.stream()
                        .filter(
                                r ->
                                        r.getPriority() > 2L
                                                && "*".equals(r.getIdentifier().getWorkspace()))
                        .collect(Collectors.toList());

        assertNaturalOrderPaged(expected, predicate, repo::findAllNaturalOrder);
    }

    private void assertNaturalOrderPaged(
            final List<AdminRule> all,
            Predicate predicate,
            BiFunction<Predicate, Pageable, Page<AdminRule>> function) {
        final int size = all.size();
        final int pageSize = 2;
        final int pages = 1 + size / pageSize;
        assertThat(pages).isGreaterThan(1);

        for (int pageN = 0; pageN < pages; pageN++) {
            PageRequest request = PageRequest.of(pageN, pageSize);
            Page<AdminRule> page = function.apply(predicate, request);
            int offset = pageN * pageSize;
            int toIndex = Math.min(offset + pageSize, all.size());
            List<AdminRule> expectedContents = all.subList(offset, toIndex);
            assertEquals(expectedContents, page.getContent());
        }

        PageRequest request = PageRequest.of(1 + pages, pageSize);
        assertThat(function.apply(predicate, request).getContent()).isEmpty();

        Pageable unpaged = Pageable.unpaged();
        assertThat(function.apply(predicate, unpaged).getContent()).isEqualTo(all);
    }

    /** Adds sample rules in reverse natural order and returns them in natural order */
    private List<AdminRule> addSamplesInReverseNaturalOrder() {
        AdminRule rule = this.entity;
        List<AdminRule> expected = new ArrayList<>();

        GeoServerInstance gsInstance2 =
                new GeoServerInstance()
                        .setName("secondInstance")
                        .setBaseURL("http://localhost:9090/geoserver")
                        .setDescription("Default geoserver instance")
                        .setUsername("admin")
                        .setPassword("geoserver");

        gsInstance2 = instanceRepo.saveAndFlush(gsInstance2);

        expected.add(rule.clone());

        rule.setAccess(AdminGrantType.ADMIN).getIdentifier().setInstance(gsInstance2);
        expected.add(rule.clone());

        rule.getIdentifier().setAddressRange(new IPAddressRange(1000L, 2000L, 32));
        expected.add(rule.clone());

        rule.getIdentifier().setRolename("rolename");
        expected.add(rule.clone());

        rule.getIdentifier().setUsername("user");
        expected.add(rule.clone());

        rule.getIdentifier().setWorkspace("workspace");
        expected.add(rule.clone());

        IntStream.range(0, expected.size()).forEach(p -> expected.get(p).setPriority(p));

        List<AdminRule> reversed = new ArrayList<>(expected);
        Collections.reverse(reversed);
        for (AdminRule r : reversed) {
            repo.saveAndFlush(r);
        }

        Collections.sort(expected, (r1, r2) -> Long.compare(r1.getPriority(), r2.getPriority()));
        return expected;
    }
}
