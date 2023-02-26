/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.util;

import org.geolatte.geom.jts.JTS;
import org.geoserver.geofence.authorization.rules.AccessInfo;
import org.geoserver.geofence.rules.model.CatalogMode;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.LayerAttribute;
import org.geoserver.geofence.rules.model.LayerAttribute.AccessType;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author "etj (Emanuele Tajariol @ GeoSolutions)"
 */
public class AccessInfoUtils {
    /**
     * @return a WPSAccessInfo where the WKT of the AccessInfo should not be taken into
     *     consideration since the geometries are more up-to-date.
     */
    public static WPSAccessInfo intersect(AccessInfo... accessInfoArr) {

        AccessInfo ret = null;
        Geometry areaRet = null;
        Geometry clipRet = null;

        for (AccessInfo accessInfo : accessInfoArr) {
            if (accessInfo.getGrant() == GrantType.DENY) {
                return new WPSAccessInfo(AccessInfo.DENY_ALL); // shortcut
            }

            Geometry area = JTS.to(accessInfo.getArea());
            Geometry clip = JTS.to(accessInfo.getClipArea());

            if (ret == null) { // get first entry as base entry
                ret = accessInfo; // no clone, AccessInfo is immutable
                areaRet = area;
                clipRet = clip;
                continue;
            }

            areaRet = GeomHelper.reprojectAndIntersect(areaRet, area);
            clipRet = GeomHelper.reprojectAndIntersect(clipRet, clip);

            AccessInfo.Builder builder = ret.toBuilder();

            builder.catalogMode(getStricter(ret.getCatalogMode(), accessInfo.getCatalogMode()));

            // CQL (read + write)
            builder.cqlFilterRead(
                    intersectCQL(ret.getCqlFilterRead(), accessInfo.getCqlFilterRead()));
            builder.cqlFilterWrite(
                    intersectCQL(ret.getCqlFilterWrite(), accessInfo.getCqlFilterWrite()));

            // Attributes
            builder.attributes(
                    intersectAttributes(ret.getAttributes(), accessInfo.getAttributes()));

            // AdminRights
            builder.adminRights(ret.isAdminRights() && accessInfo.isAdminRights());

            ret = builder.build();
            // skipping styles (only used in WMS)
        }

        return new WPSAccessInfo(ret, areaRet, clipRet);
    }

    public static String intersectCQL(String c1, String c2) {
        if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }

        return "(" + c1 + ") AND (" + c2 + ")";
    }

    public static Set<LayerAttribute> intersectAttributes(
            Set<LayerAttribute> s1, Set<LayerAttribute> s2) {
        if (s1 == null) {
            return s2;
        }
        if (s2 == null) {
            return s1;
        }

        Map<String, LayerAttribute[]> map = new HashMap<>();
        for (LayerAttribute la : s1) {
            map.put(la.getName(), new LayerAttribute[] {la, null});
        }
        for (LayerAttribute la : s2) {
            LayerAttribute[] arr =
                    map.computeIfAbsent(la.getName(), k -> new LayerAttribute[] {null, la});
            arr[1] = la;
        }

        Set<LayerAttribute> ret = new HashSet<>();
        for (LayerAttribute[] arr : map.values()) {
            if (arr[0] == null) {
                ret.add(arr[1]);
            }
            if (arr[1] == null) {
                ret.add(arr[0]);
            }

            AccessType strictest = getStricter(arr[0].getAccess(), arr[1].getAccess());
            ret.add(arr[0].withAccess(strictest));
        }
        return ret;
    }

    public static AccessType getStricter(AccessType a1, AccessType a2) {
        if (a1 == null || a2 == null) return AccessType.NONE; // should not happen
        if (a1 == AccessType.NONE || a2 == AccessType.NONE) return AccessType.NONE;
        if (a1 == AccessType.READONLY || a2 == AccessType.READONLY) return AccessType.READONLY;
        return AccessType.READWRITE;
    }

    public static CatalogMode getStricter(CatalogMode m1, CatalogMode m2) {
        if (m1 == null) {
            return m2;
        }
        if (m2 == null) {
            return m1;
        }
        if (CatalogMode.HIDE == m1 || CatalogMode.HIDE == m2) {
            return CatalogMode.HIDE;
        }
        if (CatalogMode.MIXED == m1 || CatalogMode.MIXED == m2) {
            return CatalogMode.MIXED;
        }
        return CatalogMode.CHALLENGE;
    }

    public static CatalogMode getLarger(CatalogMode m1, CatalogMode m2) {
        if (m1 == null) {
            return m2;
        }
        if (m2 == null) {
            return m1;
        }
        if (CatalogMode.CHALLENGE == m1 || CatalogMode.CHALLENGE == m2) {
            return CatalogMode.CHALLENGE;
        }
        if (CatalogMode.MIXED == m1 || CatalogMode.MIXED == m2) {
            return CatalogMode.MIXED;
        }
        return CatalogMode.HIDE;
    }

    public static class WPSAccessInfo {
        AccessInfo accessInfo;
        Geometry area;
        Geometry clip;

        public WPSAccessInfo(AccessInfo accessInfo) {
            this.accessInfo = accessInfo;
            this.area = null;
            this.clip = null;
        }

        public WPSAccessInfo(AccessInfo accessInfo, Geometry area, Geometry clip) {
            this.accessInfo = accessInfo;
            this.area = area;
            this.clip = clip;
        }

        public AccessInfo getAccessInfo() {
            return accessInfo;
        }

        public void setAccessInfo(AccessInfo accessInfo) {
            this.accessInfo = accessInfo;
        }

        public Geometry getArea() {
            return area;
        }

        public void setArea(Geometry area) {
            this.area = area;
        }

        public Geometry getClip() {
            return clip;
        }

        public void setClip(Geometry clip) {
            this.clip = clip;
        }
    }
}
