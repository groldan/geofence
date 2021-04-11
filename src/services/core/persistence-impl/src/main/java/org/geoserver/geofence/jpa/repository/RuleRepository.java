/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.repository;

import static org.geoserver.geofence.core.dao.impl.SearchUtil.addAddressRangeSearch;
import static org.geoserver.geofence.core.dao.impl.SearchUtil.addSearchField;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.DuplicateKeyException;
import org.geoserver.geofence.core.dao.RuleFilter;
import org.geoserver.geofence.core.dao.RuleFilter.IdNameFilter;
import org.geoserver.geofence.core.dao.RuleFilter.TextFilter;
import org.geoserver.geofence.core.dao.impl.FilterUtils;
import org.geoserver.geofence.core.dao.impl.SearchUtil;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.jpa.model.JPAGrantType;
import org.geoserver.geofence.jpa.model.JPAIPAddressRange;
import org.geoserver.geofence.jpa.model.JPARule;
import org.springframework.stereotype.Repository;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Repository
public class RuleRepository extends PrioritizableRepository<JPARule> {

    private static final Logger LOGGER = LogManager.getLogger(RuleRepository.class);

    @Override
    public JPARule persistInternal(JPARule entity) {
        return this.save(entity);
    }

    @Override
    public JPARule save(JPARule rule) {
        // check there are no dups for the rules received
        if (rule.getAccess() != JPAGrantType.LIMIT) { // there may be as many LIMIT rules as desired
            Search search = getDupSearch(rule);
            List<JPARule> dups = search(search);
            for (JPARule dup : dups) {
                if (dup.getAccess() != JPAGrantType.LIMIT) {
                    if (dup.getId().equals(rule.getId())) {
                        // avoid check against self
                        continue;
                    }

                    LOGGER.warn(" ORIG: " + dup);
                    LOGGER.warn(" DUP : " + rule);
                    throw new DuplicateKeyException("Duplicate Rule " + rule);
                }
            }
            // if (count(search) > 0)
            // {
            // throw new DuplicateKeyException("Duplicate Rule " + rule);
            // }
        }
        return super.save(rule);
    }

    @Override
    public void persist(JPARule... entities) throws DuplicateKeyException {
        for (JPARule rule : entities) {
            this.save(rule);
        }
    }

    protected Search getDupSearch(JPARule rule) {
        Search search = new Search(JPARule.class);
        addSearchField(search, "username", rule.getUsername());
        addSearchField(search, "rolename", rule.getRolename());
        addSearchField(search, "instance", rule.getInstance());
        addSearchField(search, "service", rule.getService());
        addSearchField(search, "request", rule.getRequest());
        addSearchField(search, "workspace", rule.getWorkspace());
        addSearchField(search, "layer", rule.getLayer());

        addAddressRangeSearch(search, rule.getAddressRange());

        return search;
    }

    public List<JPARule> search(RuleFilter filter) {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addSortAsc("priority");
        addStringCriteria(searchCriteria, "username", filter.getUser());
        addStringCriteria(searchCriteria, "rolename", filter.getRole());
        addCriteria(searchCriteria, "instance", filter.getInstance());
        addStringCriteria(searchCriteria, "service", filter.getService()); // see class' javadoc
        addStringCriteria(searchCriteria, "request", filter.getRequest()); // see class' javadoc
        addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
        addStringCriteria(searchCriteria, "layer", filter.getLayer());
        Integer limit = filter.getLimit();
        if (limit != null) {
            searchCriteria.setMaxResults(limit);
        }
        return search(searchCriteria);
    }

    // TODO: (groldan) REVISIT! shouldn't it be the same as
    // FitlerUtils.addCriteria()? taken from RuleReaderServiceImpl
    private void addCriteria(Search searchCriteria, String fieldName, IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case IDVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName), Filter.equal(fieldName + ".id", filter.getId()));
                break;

            case NAMEVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName + ".name", filter.getName()));
                break;

            default:
                throw new AssertionError();
        }
    }

    // TODO: (groldan) REVISIT! shouldn't it be the same as
    // FitlerUtils.addStringCriteria()? taken from RuleReaderServiceImpl
    private void addStringCriteria(Search searchCriteria, String fieldName, TextFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName), Filter.equal(fieldName, filter.getText()));
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }

    @Override
    public JPARule merge(JPARule entity) {
        Search search = getDupSearch(entity);

        // check if we are dup'ing some other Rule.
        List<JPARule> existent = search(search);
        switch (existent.size()) {
            case 0:
                break;

            case 1:
                // We may be updating some other fields in this Rule
                if (!existent.get(0).getId().equals(entity.getId())) {
                    throw new DuplicateKeyException(
                            "Duplicating Rule " + existent.get(0) + " with " + entity);
                }
                break;

            default:
                throw new IllegalStateException("Too many rules duplicating " + entity);
        }

        return super.merge(entity);
    }

    @Override
    public boolean remove(JPARule entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    public List<JPARule> findAllByService(String service) {
        Search s = new Search(JPARule.class);
        s.addFilterEqual("service", service);
        return search(s);
    }

    public List<JPARule> findAllByAddressRange(JPAIPAddressRange addressRange) {
        Search s = new Search(JPARule.class);
        SearchUtil.addAddressRangeSearch(s, addressRange);
        return search(s);
    }

    public long count(RuleFilter filter) {
        Search searchCriteria = buildRuleSearch(filter);
        return count(searchCriteria);
    }

    // =========================================================================
    // Search stuff

    private Search buildRuleSearch(RuleFilter filter) {
        Search searchCriteria = new Search(Rule.class);

        if (filter != null) {
            addStringCriteria(searchCriteria, "username", filter.getUser());
            addStringCriteria(searchCriteria, "rolename", filter.getRole());
            addCriteria(searchCriteria, "instance", filter.getInstance());

            addStringCriteria(searchCriteria, "service", filter.getService()); // see class' javadoc
            addStringCriteria(searchCriteria, "request", filter.getRequest()); // see class' javadoc
            addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
            addStringCriteria(searchCriteria, "layer", filter.getLayer());
        }

        return searchCriteria;
    }

    // =========================================================================

    private Search buildFixedRuleSearch(RuleFilter filter) {
        Search searchCriteria = new Search(Rule.class);

        if (filter != null) {
            FilterUtils.addFixedStringCriteria(searchCriteria, "username", filter.getUser());
            FilterUtils.addFixedStringCriteria(searchCriteria, "rolename", filter.getRole());
            FilterUtils.addFixedCriteria(searchCriteria, "instance", filter.getInstance());

            FilterUtils.addFixedStringCriteria(
                    searchCriteria, "service", filter.getService()); // see class' javadoc
            FilterUtils.addFixedStringCriteria(
                    searchCriteria, "request", filter.getRequest()); // see class' javadoc
            FilterUtils.addFixedStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
            FilterUtils.addFixedStringCriteria(searchCriteria, "layer", filter.getLayer());
        }

        return searchCriteria;
    }
}
