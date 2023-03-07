/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.filter.RuleFilter;
import org.geoserver.geofence.filter.RuleQuery;
import org.geoserver.geofence.rules.model.GrantType;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleIdentifier;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.repository.RuleIdentifierConflictException;
import org.geoserver.geofence.rules.repository.RuleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Operations on {@link Rule Rule}s.
 *
 * <p><B>Note:</B> <TT>service</TT> and <TT>request</TT> params are usually set by the client, and
 * by OGC specs they are not case sensitive, so we're going to turn all of them uppercase. See also
 * {@link RuleReaderServiceImpl}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@RequiredArgsConstructor
public class RuleAdminService {

    private final @NonNull RuleRepository ruleRepository;

    // =========================================================================
    // Basic operations
    // =========================================================================

    /**
     * @throws RuleIdentifierConflictException if trying to insert a rule with the same {@link
     *     RuleIdentifier} than an existing one
     * @return the rule as created, with sanitized (converted to upper case) {@literal service} and
     *     {@literal request} identifier property values.
     */
    public Rule insert(@NonNull Rule rule) {
        return insert(rule, InsertPosition.FIXED);
    }

    /**
     * @throws IllegalArgumentException if the rule has an {@link Rule#getId() id} set
     * @throws RuleIdentifierConflictException if trying to insert a rule with the same {@link
     *     RuleIdentifier} than an existing one
     * @return the rule as created, with sanitized (converted to upper case) {@literal service} and
     *     {@literal request} identifier property values.
     */
    public Rule insert(@NonNull Rule rule, @NonNull InsertPosition position) {
        if (null != rule.getId())
            throw new IllegalArgumentException("a new Rule must not have id, got " + rule.getId());

        rule = sanitizeFields(rule);
        return ruleRepository.create(rule, position);
    }

    /**
     * @throws IllegalArgumentException if the rule has {@code null} {@link Rule#getId() id} or does
     *     not exist
     * @throws RuleIdentifierConflictException if trying to update a rule with the same {@link
     *     RuleIdentifier} than an existing one
     */
    public Rule update(@NonNull Rule rule) {
        if (null == rule.getId()) {
            throw new IllegalArgumentException("Rule has no id");
        }

        rule = sanitizeFields(rule);
        return ruleRepository.save(rule);
    }

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT> down by
     * <TT>offset</TT>.
     *
     * <p>The shift will not be performed if there are no Rules with priority: <br>
     * <tt> startPriority &lt;= priority &lt; startPriority + offset </TT>
     *
     * @return the number of rules updated, or -1 if no need to shift.
     */
    public int shift(long priorityStart, long offset) {
        if (offset <= 0) {
            throw new IllegalArgumentException("Positive offset required");
        }
        return ruleRepository.shift(priorityStart, offset);
    }

    /**
     * Swaps the priorities of two rules.
     *
     * @throws IllegalArgumentException if either rules does not exist
     */
    public void swapPriority(String id1, String id2) {
        ruleRepository.swap(id1, id2);
    }

    /**
     * <TT>service</TT> and <TT>request</TT> params are usually set by the client, and by OGC specs
     * they are not case sensitive, so we're going to turn all of them uppercase. See also {@link
     * RuleReaderServiceImpl}.
     */
    protected Rule sanitizeFields(Rule rule) {
        if (rule.getPriority() < 0)
            throw new IllegalArgumentException(
                    "Negative priority is not allowed: " + rule.getPriority());

        // read class' javadoc
        RuleIdentifier identifier = rule.getIdentifier();
        if (identifier.getService() != null) {
            identifier = identifier.withService(identifier.getService().toUpperCase());
        }
        if (identifier.getRequest() != null) {
            identifier = identifier.withRequest(identifier.getRequest().toUpperCase());
        }
        return rule.withIdentifier(identifier);
    }

    public Optional<Rule> get(@NonNull String id) {
        return ruleRepository.findById(id);
    }

    // gr: used to return boolean but threw a NotFoundServiceEx, changed to return false instead for
    // consistency
    public boolean delete(@NonNull String id) {
        return ruleRepository.delete(id);
    }

    // gr: is it a good idea to delete by user without forcing a geoserver instance?
    public List<String> deleteRulesByUser(@NonNull String username) {
        return delete(new RuleFilter().setUser(username));
    }

    // gr: is it a good idea to delete by role without forcing a geoserver instance?
    public List<String> deleteRulesByRole(@NonNull String rolename) {
        return delete(new RuleFilter().setRole(rolename));
    }

    public List<String> deleteRulesByInstance(long instanceId) {
        return delete(new RuleFilter().setInstance(instanceId));
    }

    /** Deletes all rules matching the filter and returns their ids */
    private List<String> delete(RuleFilter filter) {
        return this.ruleRepository
                .query(RuleQuery.of(filter))
                .map(Rule::getId)
                .map(id -> ruleRepository.delete(id) ? id : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // REVISIT: return Stream?
    public List<Rule> getAll() {
        return ruleRepository.findAll().collect(Collectors.toList());
    }

    public List<Rule> getList(@NonNull RuleFilter filter) {
        return getList(RuleQuery.of(filter));
    }

    /**
     * Return the Rules according to the query.
     *
     * @param query provides a filter predicate, paging, and priority offset
     */
    public List<Rule> getList(@NonNull RuleQuery<RuleFilter> query) {
        return ruleRepository.query(query).collect(Collectors.toList());
    }

    /**
     * Return a single Rule according to the filter.
     *
     * <p>Search for a precise rule match. No ANY filter is allowed. Name/id specification with
     * default inclusion is not allowed.
     *
     * @return the matching rule or null if not found
     * @throws BadRequestServiceEx if a wildcard type is used in filter
     */
    public Optional<Rule> getRule(@NonNull RuleFilter filter) throws IllegalArgumentException {
        RuleQuery<RuleFilter> query = RuleQuery.of(filter).setPageSize(0).setPageSize(2);
        List<Rule> found = ruleRepository.query(query).collect(Collectors.toList());
        if (found.size() > 1) {
            // LOGGER.error("Unexpected rule count for filter " + filter + " : " + found);
            throw new IllegalArgumentException(
                    "Unexpected rule count for filter " + filter + " : " + found.size());
        }

        return Optional.ofNullable(found.isEmpty() ? null : found.get(0));
    }

    /**
     * Search a Rule by priority.
     *
     * <p>Returns the rule having the requested priority, or null if none found.
     */
    public Optional<Rule> getRuleByPriority(long priority) throws IllegalArgumentException {
        return ruleRepository.findByPriority(priority);
    }

    public int getCountAll() {
        return ruleRepository.count();
    }

    /** Return the Rules count according to the filter. */
    public int count(@NonNull RuleFilter filter) {
        return ruleRepository.count(filter);
    }

    // =========================================================================
    // Limits
    // =========================================================================

    /**
     * @param ruleId
     * @param limits
     * @throws IllegalArgumentException if Rule does not exist or is not of {@link GrantType#LIMIT
     *     LIMIT} type
     */
    public void setLimits(@NonNull String ruleId, RuleLimits limits)
            throws IllegalArgumentException {

        ruleRepository.setLimits(ruleId, limits);
    }

    // =========================================================================
    // Details
    // =========================================================================

    /**
     * @see #getLayerDetails(String)
     */
    public Optional<LayerDetails> getLayerDetails(@NonNull Rule rule) {
        Objects.requireNonNull(rule.getId());
        return getLayerDetails(rule.getId());
    }

    /**
     * @return The {@link LayerDetails} (possibly {@link Optional#empty() empty}) for the rule as
     *     long as the rule has {@link RuleIdentifier#getLayer() layer}
     * @throws IllegalArgumentException if the rule does not exist or has no {@link
     *     RuleIdentifier#getLayer() layer} set
     */
    public Optional<LayerDetails> getLayerDetails(@NonNull String ruleId) {
        return ruleRepository.findLayerDetailsByRuleId(ruleId);
    }

    /**
     * @throws IllegalArgumentException if the rule does not exist, or {@code detailsNew} is not
     *     null but the Rule's {@link RuleIdentifier#getAccess() access} is not {@link
     *     GrantType#ALLOW}
     */
    public void setLayerDetails(@NonNull String ruleId, LayerDetails detailsNew) {
        ruleRepository.setLayerDetails(ruleId, detailsNew);
    }

    /**
     * @throws IllegalArgumentException if the rule does not exist or has no {@link
     *     RuleIdentifier#getLayer() layer} set
     */
    public void setAllowedStyles(@NonNull String ruleId, Set<String> styles) {
        ruleRepository.setAllowedStyles(ruleId, styles);
    }

    /**
     * @return The {@link LayerDetails#getAllowedStyles() layer allowed styles} (possibly empty) for
     *     the rule as long as the rule has {@link RuleIdentifier#getLayer() layer}
     * @throws IllegalArgumentException if the rule does not exist or has no {@link
     *     RuleIdentifier#getLayer() layer} set
     */
    public Set<String> getAllowedStyles(@NonNull String ruleId) {
        return getLayerDetails(ruleId).map(LayerDetails::getAllowedStyles).orElse(Set.of());
    }

    // ==========================================================================

}
