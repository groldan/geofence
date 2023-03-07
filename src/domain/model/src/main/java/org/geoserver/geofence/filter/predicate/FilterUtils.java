/*
 * (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.filter.predicate;

import lombok.extern.slf4j.Slf4j;

import org.geoserver.geofence.rules.model.IPAddressRange;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Slf4j
class FilterUtils {

    /**
     * Filters out rules not matching with ip address filter.
     *
     * <p>IP address filtering is not performed by DAO at the moment, so we'll have to filter out
     * such results by hand.
     */
    public static <T> Predicate<T> filterByAddress(
            IPAddressRangeFilter addressFilter, Function<T, IPAddressRange> addrRangeExtractor) {

        final FilterType type = addressFilter.getType();

        switch (type) {
            case ANY:
                return r -> true;
            case DEFAULT:
                return r -> null == addrRangeExtractor.apply(r);
            case NAMEVALUE:
                return nameValueFilter(addressFilter, addrRangeExtractor);
            case IDVALUE:
            default:
                throw new IllegalArgumentException("Bad address filter type" + type);
        }
    }

    private static <T> Predicate<T> nameValueFilter(
            IPAddressRangeFilter filter, Function<T, IPAddressRange> addrRangeExtractor) {

        final String ipvalue = filter.getText();
        if (!IPUtils.isAddressValid(ipvalue)) {
            log.warn("Bad address filter " + ipvalue);
            return r -> false;
        }

        if (filter.isIncludeDefault()) {
            return r -> {
                IPAddressRange range = addrRangeExtractor.apply(r);
                return null == range || range.matches(ipvalue);
            };
        }

        return r -> {
            IPAddressRange range = addrRangeExtractor.apply(r);
            return null != range && range.matches(ipvalue);
        };
    }
}
