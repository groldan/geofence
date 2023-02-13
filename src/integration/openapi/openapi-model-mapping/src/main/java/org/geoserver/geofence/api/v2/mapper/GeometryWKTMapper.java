package org.geoserver.geofence.api.v2.mapper;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.mapstruct.Mapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring")
interface GeometryWKTMapper {

    Pattern pattern = Pattern.compile("((SRID=(\\d+))\\s*;)?\\s*(MULTIPOLYGON.*)");

    default String multiPolygonToWKT(MultiPolygon geom) {
        if (null == geom) return null;
        int srid = geom.getSRID();
        String wkt = geom.toText();
        if (0 == srid) srid = 4326;

        return String.format("SRID=%d;%s", srid, wkt);
    }

    default MultiPolygon wktToMultiPolygon(String sridDelimitedWKT) {
        if (null == sridDelimitedWKT) return null;

        Matcher matcher = pattern.matcher(sridDelimitedWKT);
        if (!matcher.matches()) {
            // TODO: log and/or throw
            return null;
        }
        String ssrid = matcher.group(3);
        int srid = ssrid == null ? 4326 : Integer.valueOf(ssrid);
        String wkt = matcher.group(4);
        MultiPolygon geom;
        try {
            geom = (MultiPolygon) new WKTReader().read(wkt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid geometry", e);
        }
        geom.setSRID(srid);
        return geom;
    }
}
