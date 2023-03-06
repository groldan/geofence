package org.geoserver.geofence.authorization.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.MultiPolygon;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.jts.JTS;
import org.geoserver.geofence.rules.model.CatalogMode;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.SpatialFilterType;
import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.model.GeoServerUserGroup;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Abstract {@link RuleReaderService} integration/conformance test working with geometries
 *
 * <p>Concrete implementations must supply the required services in {@link ServiceTestBase}
 */
public abstract class AbstractRuleReaderServiceImpl_GeomTest extends ServiceTestBase {

    @Test
    public void testRuleLimitsAllowedAreaSRIDIsPreserved() {
        // test that the original SRID is present in the allowedArea wkt representation,
        // when retrieving it from the AccessInfo object
        String id;
        {
            Rule r1 =
                    ruleAdminService.insert(
                            rule(
                                    10,
                                    null,
                                    null,
                                    null,
                                    null,
                                    "s1",
                                    "r1",
                                    null,
                                    "w1",
                                    "l1",
                                    GrantType.LIMIT));
            id = r1.getId();
        }

        {
            ruleAdminService.insert(
                    rule(
                            11,
                            null,
                            null,
                            null,
                            null,
                            "s1",
                            "r1",
                            null,
                            "w1",
                            "l1",
                            GrantType.ALLOW));
        }

        // save limits and check it has been saved
        {
            String wkt =
                    "SRID=3857;MULTIPOLYGON(((0.0016139656066815888 -0.0006386457758059581,0.0019599705696027314 -0.0006386457758059581,0.0019599705696027314 -0.0008854090051601674,0.0016139656066815888 -0.0008854090051601674,0.0016139656066815888 -0.0006386457758059581)))";

            MultiPolygon<?> allowedArea = (MultiPolygon<?>) Wkt.fromWkt(wkt);
            RuleLimits limits = RuleLimits.builder().allowedArea(allowedArea).build();
            ruleAdminService.setLimits(id, limits);
        }

        {
            RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
            filter.setWorkspace("w1");
            filter.setService("s1");
            filter.setRequest("r1");
            filter.setLayer("l1");
            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            Geometry<?> area = accessInfo.getArea();
            assertEquals(3857, area.getCoordinateReferenceSystem().getCrsId().getCode());
        }
    }

    @Test
    public void testRuleLimitsAllowedAreaReprojectionWithDifferentSrid() {
        // test that the original SRID is present in the allowedArea wkt representation,
        // when retrieving it from the AccessInfo object
        String id2;
        String id3;
        {
            ruleAdminService.insert(
                    rule(
                            999,
                            null,
                            null,
                            null,
                            null,
                            "s1",
                            "r1",
                            null,
                            "w1",
                            "l1",
                            GrantType.ALLOW));
        }

        {
            Rule r2 =
                    ruleAdminService.insert(
                            rule(
                                    11,
                                    null,
                                    null,
                                    null,
                                    null,
                                    "s1",
                                    "r1",
                                    null,
                                    "w1",
                                    "l1",
                                    GrantType.LIMIT));
            id2 = r2.getId();
        }

        // save limits and check it has been saved
        {
            String wkt =
                    "SRID=3003;MultiPolygon (((1680529.71478682174347341 4849746.00902365241199732, 1682436.7076464940328151 4849731.7422441728413105, 1682446.21883281995542347 4849208.62699576932936907, 1680524.95919364970177412 4849279.96089325752109289, 1680529.71478682174347341 4849746.00902365241199732)))";

            MultiPolygon<?> allowedArea = (MultiPolygon<?>) Wkt.fromWkt(wkt);
            RuleLimits limits = RuleLimits.builder().allowedArea(allowedArea).build();
            ruleAdminService.setLimits(id2, limits);
        }

        {
            Rule r3 =
                    ruleAdminService.insert(
                            rule(
                                    12,
                                    null,
                                    null,
                                    null,
                                    null,
                                    "s1",
                                    "r1",
                                    null,
                                    "w1",
                                    "l1",
                                    GrantType.LIMIT));
            id3 = r3.getId();
        }

        // save limits and check it has been saved
        {
            String wkt =
                    "SRID=23032;MultiPolygon (((680588.67850254673976451 4850060.34823693986982107, 681482.71827003755606711 4850469.32878803834319115, 682633.56349697941914201 4849499.20374245755374432, 680588.67850254673976451 4850060.34823693986982107)))";

            MultiPolygon<?> allowedArea = (MultiPolygon<?>) Wkt.fromWkt(wkt);
            RuleLimits limits = RuleLimits.builder().allowedArea(allowedArea).build();
            ruleAdminService.setLimits(id3, limits);
        }

        {
            RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
            filter.setWorkspace("w1");
            filter.setService("s1");
            filter.setRequest("r1");
            filter.setLayer("l1");
            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            Geometry<?> area = accessInfo.getArea();
            assertEquals(3003, area.getCoordinateReferenceSystem().getCrsId().getCode());
        }
    }

    public void testRuleSpatialFilterTypeClipSameGroup() {

        // test that when we have two rules referring to the same group
        // one having a filter type Intersects and the other one having filter type Clip
        // the result is a clip area obtained by the intersection of the two.
        GeoServerUserGroup g1 = createRole("group11");
        GeoServerUserGroup g2 = createRole("group12");
        GeoServerUser user = createUser("auth11", g1, g2);

        ruleAdminService.insert(
                rule(
                        9999,
                        null,
                        null,
                        null,
                        null,
                        "s11",
                        "r11",
                        null,
                        "w11",
                        "l11",
                        GrantType.ALLOW));
        String id =
                ruleAdminService
                        .insert(
                                rule(
                                        10,
                                        user.getName(),
                                        "group11",
                                        null,
                                        null,
                                        "s11",
                                        "r11",
                                        null,
                                        "w11",
                                        "l11",
                                        GrantType.LIMIT))
                        .getId();

        String areaWKT =
                "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon<?> area = (MultiPolygon<?>) Wkt.fromWkt(areaWKT);
        RuleLimits limits =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.CLIP)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area)
                        .build();

        ruleAdminService.setLimits(id, limits);

        String id2 =
                ruleAdminService
                        .insert(
                                rule(
                                        11,
                                        user.getName(),
                                        "group12",
                                        null,
                                        null,
                                        "s11",
                                        "r11",
                                        null,
                                        "w11",
                                        "l11",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT2 =
                "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon<?> area2 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT2);

        RuleLimits limits2 =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.INTERSECT)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area2)
                        .build();
        ruleAdminService.setLimits(id2, limits2);
        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w11");
        filter.setLayer("l11");

        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());

        // area in same group, the result should an itersection of the
        // two allowed area as a clip geometry.
        org.locationtech.jts.geom.Geometry testArea = JTS.to(area).intersection(JTS.to(area2));
        testArea.normalize();
        assertNull(accessInfo.getArea());
        assertNotNull(accessInfo.getClipArea());

        org.locationtech.jts.geom.Geometry resultArea = JTS.to(accessInfo.getClipArea());
        resultArea.normalize();
        assertTrue(testArea.equalsExact(resultArea, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeIntersectsSameGroup() {

        // test that when we have two rules referring to the same group
        // both having a filter type Intersects
        // the result is an intersect area obtained by the intersection of the two.
        GeoServerUserGroup g1 = createRole("group13");
        GeoServerUserGroup g2 = createRole("group14");
        GeoServerUser user = createUser("auth12", g1, g2);

        ruleAdminService.insert(
                rule(
                        9999,
                        null,
                        null,
                        null,
                        null,
                        "s11",
                        "r11",
                        null,
                        "w11",
                        "l11",
                        GrantType.ALLOW));
        String id =
                ruleAdminService
                        .insert(
                                rule(
                                        13,
                                        user.getName(),
                                        "group13",
                                        null,
                                        null,
                                        "s11",
                                        "r11",
                                        null,
                                        "w11",
                                        "l11",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT =
                "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon<?> area = (MultiPolygon<?>) Wkt.fromWkt(areaWKT);

        RuleLimits limits =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.INTERSECT)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area)
                        .build();

        ruleAdminService.setLimits(id, limits);

        String id2 =
                ruleAdminService
                        .insert(
                                rule(
                                        14,
                                        user.getName(),
                                        "group14",
                                        null,
                                        null,
                                        "s11",
                                        "r11",
                                        null,
                                        "w11",
                                        "l11",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT2 =
                "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon<?> area2 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT2);

        RuleLimits limits2 =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.INTERSECT)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area2)
                        .build();

        ruleAdminService.setLimits(id2, limits2);
        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w11");
        filter.setLayer("l11");

        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());

        // area in same group, the result should an itersection of the
        // two allowed area as an intersects geometry.
        org.locationtech.jts.geom.Geometry testArea = JTS.to(area).intersection(JTS.to(area2));
        testArea.normalize();
        assertNull(accessInfo.getClipArea());
        assertNotNull(accessInfo.getArea());

        org.locationtech.jts.geom.Geometry resultArea = JTS.to(accessInfo.getArea());
        resultArea.normalize();
        assertTrue(testArea.equalsExact(resultArea, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeEnlargeAccess() {
        // test the access enalargement behaviour with the SpatialFilterType.
        // the user belongs to two groups. One with an allowedArea of type intersects,
        // the other one with an allowed area of type clip. They should be returned
        // separately in the final rule.

        GeoServerUserGroup g1 = createRole("group22");
        GeoServerUserGroup g2 = createRole("group23");
        GeoServerUser user = createUser("auth22", g1, g2);

        ruleAdminService.insert(
                rule(
                        999,
                        null,
                        null,
                        null,
                        null,
                        "s22",
                        "r22",
                        null,
                        "w22",
                        "l22",
                        GrantType.ALLOW));

        String id =
                ruleAdminService
                        .insert(
                                rule(
                                        15,
                                        null,
                                        "group22",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT =
                "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon<?> area = (MultiPolygon<?>) Wkt.fromWkt(areaWKT);
        RuleLimits limits =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.INTERSECT)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area)
                        .build();
        ruleAdminService.setLimits(id, limits);

        String id2 =
                ruleAdminService
                        .insert(
                                rule(
                                        16,
                                        null,
                                        "group23",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT2 =
                "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon<?> area2 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT2);
        RuleLimits limits2 =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.CLIP)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area2)
                        .build();
        ruleAdminService.setLimits(id2, limits2);
        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w22");
        filter.setLayer("l22");
        filter.setUser(user.getName());
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());

        // we got a user in two groups one with an intersect spatialFilterType
        // and the other with a clip spatialFilterType. The two area should haven
        // been kept separated
        assertNotNull(accessInfo.getArea());
        assertNotNull(accessInfo.getClipArea());

        // the intersects should be equal to the originally defined
        // allowed area
        org.locationtech.jts.geom.Geometry intersects = JTS.to(accessInfo.getArea());
        intersects.normalize();
        assertTrue(intersects.equalsExact(JTS.to(area), 10.0E-15));

        org.locationtech.jts.geom.Geometry clip = JTS.to(accessInfo.getClipArea());
        clip.normalize();
        org.locationtech.jts.geom.MultiPolygon area2Jts = JTS.to(area2);
        area2Jts.normalize();
        assertTrue(clip.equalsExact(area2Jts, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeFourRules() {
        // the user belongs to two groups and there are two rules for each group:
        // INTERSECTS and CLIP for the first, and CLIP CLIP for the second.
        // The expected result is only one allowedArea of type clip
        // obtained by the intersection of the firs two, united with the intersection of the second
        // two.
        // the first INTERSECTS is resolve as CLIP because during constraint resolution the more
        // restrictive
        // type is chosen.

        GeoServerUserGroup g1 = createRole("group31");
        GeoServerUserGroup g2 = createRole("group32");
        GeoServerUser user = createUser("auth33", g1, g2);

        ruleAdminService.insert(
                rule(
                        999,
                        null,
                        null,
                        null,
                        null,
                        "s22",
                        "r22",
                        null,
                        "w22",
                        "l22",
                        GrantType.ALLOW));

        String id =
                ruleAdminService
                        .insert(
                                rule(
                                        17,
                                        null,
                                        "group31",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT =
                "SRID=4326;MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon<?> area = (MultiPolygon<?>) Wkt.fromWkt(areaWKT);
        RuleLimits limits =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.INTERSECT)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area)
                        .build();

        ruleAdminService.setLimits(id, limits);
        assertThat(ruleAdminService.get(id).get().getRuleLimits()).isEqualTo(limits);

        String id2 =
                ruleAdminService
                        .insert(
                                rule(
                                        18,
                                        null,
                                        "group31",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT2 =
                "SRID=4326;MultiPolygon (((-1.46109090909091011 5.68500000000000139, -0.68600000000000083 5.7651818181818193, -0.73945454545454625 2.00554545454545519, -1.54127272727272846 1.9610000000000003, -1.46109090909091011 5.68500000000000139)))";
        MultiPolygon<?> area2 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT2);
        RuleLimits limits2 =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.CLIP)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area2)
                        .build();
        ruleAdminService.setLimits(id2, limits2);
        assertThat(ruleAdminService.get(id2).get().getRuleLimits()).isEqualTo(limits2);

        String id3 =
                ruleAdminService
                        .insert(
                                rule(
                                        19,
                                        null,
                                        "group32",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT3 =
                "SRID=4326;MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon<?> area3 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT3);
        RuleLimits limits3 =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.CLIP)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area3)
                        .build();

        ruleAdminService.setLimits(id3, limits3);
        assertThat(ruleAdminService.get(id3).get().getRuleLimits()).isEqualTo(limits3);

        String id4 =
                ruleAdminService
                        .insert(
                                rule(
                                        20,
                                        null,
                                        "group32",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT4 =
                "SRID=4326;MultiPolygon (((-1.30963636363636482 5.96118181818181991, 1.78181818181818175 4.84754545454545571, -0.90872727272727349 2.26390909090909132, -1.30963636363636482 5.96118181818181991)))";
        MultiPolygon<?> area4 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT4);
        RuleLimits limits4 =
                RuleLimits.builder()
                        .spatialFilterType(SpatialFilterType.CLIP)
                        .catalogMode(CatalogMode.HIDE)
                        .allowedArea(area4)
                        .build();
        ruleAdminService.setLimits(id4, limits4);

        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w22");
        filter.setLayer("l22");
        filter.setUser(user.getName());
        List<Rule> match = ruleAdminService.getList(filter);
        // assertThat(match.stream().map(Rule::getId).collect(Collectors.toList())).isEqualTo(List.of(id, id2,id3));
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());
        // we should have only the clip geometry
        assertNull(accessInfo.getArea());
        assertNotNull(accessInfo.getClipArea());

        // the intersects should be equal to the originally defined
        // allowed area
        org.locationtech.jts.geom.Geometry expectedResult =
                JTS.to(area)
                        .intersection(JTS.to(area2))
                        .union(JTS.to(area3).intersection(JTS.to(area4)));
        expectedResult.normalize();
        org.locationtech.jts.geom.Geometry clip = JTS.to(accessInfo.getClipArea());
        clip.normalize();
        assertTrue(clip.equalsExact(expectedResult, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeFourRules2() {
        // the user belongs to two groups and there are two rules for each group:
        // CLIP and CLIP for the first, and INTERSECTS INTERSECTS for the second.
        // The expected result are two allowedArea the first of type clip and second of type
        // intersects.

        GeoServerUserGroup g1 = createRole("group41");
        GeoServerUserGroup g2 = createRole("group42");
        GeoServerUser user = createUser("auth44", g1, g2);

        ruleAdminService.insert(
                rule(
                        999,
                        null,
                        null,
                        null,
                        null,
                        "s22",
                        "r22",
                        null,
                        "w22",
                        "l22",
                        GrantType.ALLOW));

        String id =
                ruleAdminService
                        .insert(
                                rule(
                                        21,
                                        null,
                                        "group41",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT =
                "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon<?> area = (MultiPolygon<?>) Wkt.fromWkt(areaWKT);
        RuleLimits limits =
                RuleLimits.clip().withCatalogMode(CatalogMode.HIDE).withAllowedArea(area);
        ruleAdminService.setLimits(id, limits);

        String id2 =
                ruleAdminService
                        .insert(
                                rule(
                                        22,
                                        null,
                                        "group41",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT2 =
                "MultiPolygon (((-1.46109090909091011 5.68500000000000139, -0.68600000000000083 5.7651818181818193, -0.73945454545454625 2.00554545454545519, -1.54127272727272846 1.9610000000000003, -1.46109090909091011 5.68500000000000139)))";
        MultiPolygon<?> area2 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT2);
        RuleLimits limits2 =
                RuleLimits.clip().withCatalogMode(CatalogMode.HIDE).withAllowedArea(area2);
        ruleAdminService.setLimits(id2, limits2);

        String id3 =
                ruleAdminService
                        .insert(
                                rule(
                                        23,
                                        null,
                                        "group42",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT3 =
                "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon<?> area3 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT3);
        RuleLimits limits3 =
                RuleLimits.intersect().withCatalogMode(CatalogMode.HIDE).withAllowedArea(area3);
        ruleAdminService.setLimits(id3, limits3);

        String id4 =
                ruleAdminService
                        .insert(
                                rule(
                                        24,
                                        null,
                                        "group42",
                                        null,
                                        null,
                                        "s22",
                                        "r22",
                                        null,
                                        "w22",
                                        "l22",
                                        GrantType.LIMIT))
                        .getId();
        String areaWKT4 =
                "MultiPolygon (((-1.30963636363636482 5.96118181818181991, 1.78181818181818175 4.84754545454545571, -0.90872727272727349 2.26390909090909132, -1.30963636363636482 5.96118181818181991)))";
        MultiPolygon<?> area4 = (MultiPolygon<?>) Wkt.fromWkt(areaWKT4);
        RuleLimits limits4 =
                RuleLimits.intersect().withCatalogMode(CatalogMode.HIDE).withAllowedArea(area4);
        ruleAdminService.setLimits(id4, limits4);

        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w22");
        filter.setLayer("l22");
        filter.setUser(user.getName());
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());

        // we should have both
        assertNotNull(accessInfo.getArea());
        assertNotNull(accessInfo.getClipArea());

        // the intersects should be equal to the originally defined
        // allowed area
        org.locationtech.jts.geom.Geometry expectedIntersects =
                JTS.to(area3).intersection(JTS.to(area4));
        expectedIntersects.normalize();
        org.locationtech.jts.geom.Geometry intersects = JTS.to(accessInfo.getArea());
        intersects.normalize();
        System.out.println(intersects.toString());
        System.out.println(expectedIntersects.toString());
        assertTrue(expectedIntersects.equalsExact(intersects, 10.0E-15));

        org.locationtech.jts.geom.Geometry clip = JTS.to(accessInfo.getClipArea());
        clip.normalize();
        org.locationtech.jts.geom.Geometry expectedClip = JTS.to(area2).intersection(JTS.to(area));
        expectedClip.normalize();
        assertTrue(expectedClip.equalsExact(clip, 10.0E-15));
    }
}
