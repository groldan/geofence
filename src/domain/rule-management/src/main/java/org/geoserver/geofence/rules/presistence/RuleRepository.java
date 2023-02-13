package org.geoserver.geofence.rules.presistence;

import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleIdentifier;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface RuleRepository {

    boolean existsById(long id);

    Rule create(Rule rule, InsertPosition position);

    Rule save(Rule rule);

    boolean delete(long id);

    int count();

    /**
     * @return all rules in natural order (instance/priority)
     */
    Stream<Rule> findAll();

    int count(RuleFilter filter);

    /**
     * @return all rules matching the query in natural order (instance/priority)
     */
    Stream<Rule> query(RuleQuery<RuleFilter> query);

    Optional<Rule> findById(long id);

    int shift(long priorityStart, long offset);

    void swap(long id1, long id2);

    /**
     * @param ruleId
     * @param styles
     * @throws NoSuchElementException if the rule does not exist
     * @throws IllegalArgumentException if the rule has no {@link RuleIdentifier#getLayer() layer
     *     set}
     */
    void setAllowedStyles(Long ruleId, Set<String> styles);

    /**
     * @param ruleId
     * @param styles
     * @throws NoSuchElementException if the rule does not exist
     * @throws IllegalArgumentException
     */
    void setLimits(Long ruleId, RuleLimits limits);

    /**
     * @param ruleId
     * @param styles
     * @throws NoSuchElementException if the rule does not exist
     * @throws IllegalArgumentException
     */
    void setDetails(Long ruleId, LayerDetails detailsNew);
}
