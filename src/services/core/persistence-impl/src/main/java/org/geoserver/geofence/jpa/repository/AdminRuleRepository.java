/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.repository;

import static org.geoserver.geofence.core.dao.impl.SearchUtil.addAddressRangeSearch;
import static org.geoserver.geofence.core.dao.impl.SearchUtil.addSearchField;

import com.googlecode.genericdao.search.Search;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.DuplicateKeyException;
import org.geoserver.geofence.jpa.model.JPAAdminRule;
import org.geoserver.geofence.jpa.model.JPAInsertPosition;
import org.springframework.stereotype.Repository;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Repository
public class AdminRuleRepository extends PrioritizableRepository<JPAAdminRule> {

    private static final Logger LOGGER = LogManager.getLogger(AdminRuleRepository.class);

    @Override
    public JPAAdminRule save(JPAAdminRule rule) {
        // check there are no dups for the rules received
        Search search = getDupSearch(rule);
        List<JPAAdminRule> dups = search(search);
        for (JPAAdminRule dup : dups) {
            if (dup.getId().equals(rule.getId())) {
                // avoid check against self
                continue;
            }

            LOGGER.warn(" ORIG: " + dup);
            LOGGER.warn(" DUP : " + rule);
            throw new DuplicateKeyException("Duplicate AdminRule " + rule);
        }
        // if (count(search) > 0)
        // {
        // throw new DuplicateKeyException("Duplicate Rule " + rule);
        // }
        return super.save(rule);
    }

    @Override
    public void persist(JPAAdminRule... entities) {
        for (JPAAdminRule rule : entities) {
            save(rule);
        }
    }

    public int shift(long priorityStart, long offset) {
        return super.shift(JPAAdminRule.class, priorityStart, offset);
    }

    public JPAAdminRule persist(JPAAdminRule entity, JPAInsertPosition position) {
        return super.persist(JPAAdminRule.class, entity, position);
    }

    @Override
    public JPAAdminRule persistInternal(JPAAdminRule entity) {
        return this.save(entity);
    }

    @Override
    public JPAAdminRule merge(JPAAdminRule entity) {
        Search search = getDupSearch(entity);

        // check if we are dup'ing some other Rule.
        List<JPAAdminRule> existent = search(search);
        switch (existent.size()) {
            case 0:
                break;

            case 1:
                // We may be updating some other fields in this Rule
                if (!existent.get(0).getId().equals(entity.getId())) {
                    throw new DuplicateKeyException(
                            "Duplicating AdminRule " + existent.get(0) + " with " + entity);
                }
                break;

            default:
                throw new IllegalStateException("Too many AdminRules duplicating " + entity);
        }

        return super.merge(entity);
    }

    protected Search getDupSearch(JPAAdminRule rule) {
        Search search = new Search(JPAAdminRule.class);
        addSearchField(search, "username", rule.getUsername());
        addSearchField(search, "rolename", rule.getRolename());
        addSearchField(search, "instance", rule.getInstance());
        addSearchField(search, "workspace", rule.getWorkspace());

        addAddressRangeSearch(search, rule.getAddressRange());

        return search;
    }
}
