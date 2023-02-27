package org.geoserver.geofence.adminrules.repository;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.adminrules.model.AdminRuleFilter;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.RuleQuery;

import java.util.List;
import java.util.Optional;

public interface AdminRuleRepository {

    AdminRule create(AdminRule rule, InsertPosition position);

    /**
     * @param rule
     * @return
     * @throws IllegalArgumentException if the rule does not exist
     */
    AdminRule save(AdminRule rule);

    Optional<AdminRule> findById(long id);

    /**
     * Returns a single entity matching the given {@link AdminRuleFilter} or {@link
     * Optional#empty()} if none was found.
     *
     * @param filter must not be {@literal null}.
     * @return a single entity matching the given {@link AdminRuleFilter} or {@link
     *     Optional#empty()} if none was found.
     * @throws IllegalArgumentException if the filter produces more than one result
     */
    Optional<AdminRule> findOne(AdminRuleFilter filter);

    List<AdminRule> findAll();

    List<AdminRule> findAll(AdminRuleFilter filter);

    List<AdminRule> findAll(RuleQuery<AdminRuleFilter> query);

    Optional<AdminRule> findFirst(AdminRuleFilter adminRuleFilter);

    int count();

    int count(AdminRuleFilter filter);

    int shiftPriority(long priorityStart, long offset);

    void swap(long id1, long id2);

    boolean deleteById(long id);

    int delete(AdminRuleFilter filter);

    Optional<AdminRule> findOneByPriority(long priority);
}
