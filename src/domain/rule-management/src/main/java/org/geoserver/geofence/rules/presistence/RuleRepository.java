package org.geoserver.geofence.rules.presistence;

import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleIdentifier;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;

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

    /**
     * @throws IllegalStateException if there are multiple rules with the requested priority
     */
    Optional<Rule> findByPriority(long priority);

    int shift(long priorityStart, long offset);

    void swap(long id1, long id2);

    /**
     * @throws IllegalArgumentException if the rule does not exist or has no {@link
     *     RuleIdentifier#getLayer() layer set}
     */
    void setAllowedStyles(long ruleId, Set<String> styles);

    /**
     * @throws IllegalArgumentException if the rule does not exist or the access type is not {@link
     *     GrantType#LIMIT}
     */
    void setLimits(long ruleId, RuleLimits limits);

    /**
     * @throws IllegalArgumentException if the rule does not exist or the access type is not {@link
     *     GrantType#ALLOW}
     */
    void setLayerDetails(long ruleId, LayerDetails detailsNew);

    Optional<LayerDetails> findLayerDetailsByRuleId(long ruleId);
}
