/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.RuleFilter;

/** @author ETj (etj at geo-solutions.it) */
public class FilterUtils {

    private static final Logger LOGGER = LogManager.getLogger(FilterUtils.class);

    /**
     * Add criteria for <B>searching</B>.
     *
     * <p>We're dealing with IDs here, so <U>we'll suppose that the related object id field is
     * called "id"</U>.
     */
    public static void addCriteria(
            Search searchCriteria, String fieldName, RuleFilter.IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case IDVALUE:
                if (filter.isIncludeDefault()) {
                    searchCriteria.addFilterOr(
                            Filter.isNull(fieldName),
                            Filter.equal(fieldName + ".id", filter.getId()));
                } else {
                    searchCriteria.addFilter(Filter.equal(fieldName + ".id", filter.getId()));
                }
                break;

            case NAMEVALUE:
                if (filter.isIncludeDefault()) {
                    searchCriteria.addFilterOr(
                            Filter.isNull(fieldName),
                            Filter.equal(fieldName + ".name", filter.getName()));
                } else {
                    searchCriteria.addFilter(Filter.equal(fieldName + ".name", filter.getName()));
                }
                break;

            default:
                throw new AssertionError();
        }
    }

    /** @throws IllegalArgumentException */
    public static void addPagingConstraints(Search searchCriteria, Integer page, Integer entries) {
        if ((page != null && entries == null) || (page == null && entries != null)) {
            throw new IllegalArgumentException(
                    "Page and entries params should be declared together.");
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "Searching Rule list "
                            + (page == null ? "(unpaged) " : (" p:" + page + "#:" + entries)));
        }

        if (entries != null) {
            searchCriteria.setMaxResults(entries);
            searchCriteria.setPage(page);
        }
    }

    public static void addStringCriteria(
            Search searchCriteria, String fieldName, RuleFilter.TextFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                if (filter.isIncludeDefault()) {
                    searchCriteria.addFilterOr(
                            Filter.isNull(fieldName), Filter.equal(fieldName, filter.getText()));
                } else {
                    searchCriteria.addFilter(Filter.equal(fieldName, filter.getText()));
                }
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }

    /**
     * Add criteria for <B>searching</B>.
     *
     * <p>We're dealing with IDs here, so <U>we'll suppose that the related object id field is
     * called "id"</U>.
     *
     * @throws IllegalArgumentException
     */
    public static void addFixedCriteria(
            Search searchCriteria, String fieldName, RuleFilter.IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                throw new IllegalArgumentException(
                        fieldName + " should be a fixed search and can't be ANY");

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case IDVALUE:
                if (filter.isIncludeDefault()) {
                    throw new IllegalArgumentException(fieldName + " should be a fixed search");
                } else {
                    searchCriteria.addFilter(Filter.equal(fieldName + ".id", filter.getId()));
                }
                break;

            case NAMEVALUE:
                if (filter.isIncludeDefault()) {
                    throw new IllegalArgumentException(fieldName + " should be a fixed search");

                } else {
                    searchCriteria.addFilter(Filter.equal(fieldName + ".name", filter.getName()));
                }
                break;

            default:
                throw new AssertionError();
        }
    }

    /** @throws IllegalArgumentException */
    public static void addFixedStringCriteria(
            Search searchCriteria, String fieldName, RuleFilter.TextFilter filter) {
        switch (filter.getType()) {
            case ANY:
                throw new IllegalArgumentException(
                        fieldName + " should be a fixed search and can't be ANY");

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                if (filter.isIncludeDefault()) {
                    throw new IllegalArgumentException(fieldName + " should be a fixed search");
                } else {
                    searchCriteria.addFilter(Filter.equal(fieldName, filter.getText()));
                }
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }
}
