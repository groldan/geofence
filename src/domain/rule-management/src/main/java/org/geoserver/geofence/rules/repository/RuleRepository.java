package org.geoserver.geofence.rules.repository;

import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.RuleQuery;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleIdentifier;
import org.geoserver.geofence.rules.model.RuleLimits;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface RuleRepository {

    boolean existsById(String id);

    /**
     * @throws RuleIdentifierConflictException if trying to insert a rule with the same {@link
     *     RuleIdentifier} than an existing one
     * @return the rule as created, with sanitized (converted to upper case) {@literal service} and
     *     {@literal request} identifier property values.
     */
    Rule create(Rule rule, InsertPosition position);

    Rule save(Rule rule);

    boolean delete(String id);

    int count();

    /**
     * @return all rules in natural order (priority)
     */
    Stream<Rule> findAll();

    int count(RuleFilter filter);

    /**
     * @return all rules matching the query in natural order (priority)
     */
    Stream<Rule> query(RuleQuery<RuleFilter> query);

    Optional<Rule> findById(String id);

    /**
     * @throws IllegalStateException if there are multiple rules with the requested priority
     */
    Optional<Rule> findByPriority(long priority);

    int shift(long priorityStart, long offset);

    void swap(String id1, String id2);

    /**
     * @throws IllegalArgumentException if the rule does not exist or has no {@link
     *     RuleIdentifier#getLayer() layer set}
     */
    void setAllowedStyles(String ruleId, Set<String> styles);

    /**
     * @throws IllegalArgumentException if the rule does not exist or the access type is not {@link
     *     GrantType#LIMIT}
     */
    void setLimits(String ruleId, RuleLimits limits);

    /**
     * @throws IllegalArgumentException if the rule does not exist or the access type is not {@link
     *     GrantType#ALLOW}
     */
    void setLayerDetails(String ruleId, LayerDetails detailsNew);

    Optional<LayerDetails> findLayerDetailsByRuleId(String ruleId);
}
