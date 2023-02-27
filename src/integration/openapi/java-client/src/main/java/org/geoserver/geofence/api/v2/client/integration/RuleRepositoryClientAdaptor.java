package org.geoserver.geofence.api.v2.client.integration;

import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.client.RulesApi;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleFilterApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapper;
import org.geoserver.geofence.api.v2.model.Pageable;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.geoserver.geofence.rules.presistence.RuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class RuleRepositoryClientAdaptor implements RuleRepository {

    private final RulesApi apiClient;
    private final RuleApiMapper mapper;
    private final EnumsApiMapper enumsMapper;
    private final RuleLimitsApiMapper limitsMapper;
    private final LayerDetailsApiMapper detailsMapper;

    private final RuleFilterApiMapper filterMapper = new RuleFilterApiMapper();

    @Override
    public boolean existsById(long id) {
        return apiClient.ruleExistsById(id);
    }

    @Override
    public Rule save(Rule rule) {
        Objects.requireNonNull(rule.getId(), "Rule has no id");
        org.geoserver.geofence.api.v2.model.Rule response;
        response = apiClient.updateRuleById(rule.getId(), map(rule));
        return map(response);
    }

    @Override
    public Rule create(Rule rule, InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("Rule must have no id");
        org.geoserver.geofence.api.v2.model.Rule response;
        response = apiClient.createRule(enumsMapper.map(position), map(rule));
        return map(response);
    }

    @Override
    public boolean delete(long id) {
        try {
            apiClient.deleteRuleById(id);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
        return true;
    }

    @Override
    public int count() {
        return apiClient.countAllRules();
    }

    @Override
    public int count(RuleFilter filter) {
        return apiClient.countRules(map(filter));
    }

    @Override
    public Stream<Rule> findAll() {
        Pageable pageable = new Pageable();
        List<org.geoserver.geofence.api.v2.model.Rule> rules = apiClient.getRules(pageable);
        return rules.stream().map(this::map);
    }

    @Override
    public Stream<Rule> query(RuleQuery<RuleFilter> query) {
        org.geoserver.geofence.api.v2.model.Pageable pageable;
        org.geoserver.geofence.api.v2.model.RuleFilter filter;
        Long priorityOffset;

        pageable = this.filterMapper.extractPageable(query);
        filter = query.getFilter().map(filterMapper::map).orElse(null);
        priorityOffset =
                query.getPriorityOffset().isPresent()
                        ? query.getPriorityOffset().getAsLong()
                        : null;

        List<org.geoserver.geofence.api.v2.model.Rule> rules;

        rules = apiClient.queryRules(pageable, priorityOffset, filter);

        return rules.stream().map(this::map);
    }

    @Override
    public Optional<Rule> findById(long id) {
        org.geoserver.geofence.api.v2.model.Rule rule;
        try {
            rule = apiClient.getRuleById(id);
        } catch (HttpClientErrorException.NotFound e) {
            rule = null;
        }
        return Optional.ofNullable(rule).map(this::map);
    }

    @Override
    public Optional<Rule> findByPriority(long priority) {
        org.geoserver.geofence.api.v2.model.Rule rule;
        try {
            rule = apiClient.findOneRuleByPriority(priority);
        } catch (HttpClientErrorException.NotFound e) {
            rule = null;
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException("Found multiple rules with priority " + priority);
        }
        return Optional.ofNullable(rule).map(this::map);
    }

    private org.geoserver.geofence.api.v2.model.RuleFilter map(RuleFilter filter) {
        return filterMapper.map(filter);
    }

    private org.geoserver.geofence.api.v2.model.Rule map(Rule rule) {
        return mapper.toApi(rule);
    }

    private Rule map(org.geoserver.geofence.api.v2.model.Rule rule) {
        return mapper.toModel(rule);
    }

    @Override
    public int shift(long priorityStart, long offset) {
        return apiClient.shiftRulesByPriority(priorityStart, offset);
    }

    @Override
    public void swap(long id1, long id2) {
        apiClient.swapRulesById(id1, id2);
    }

    @Override
    public void setAllowedStyles(long ruleId, Set<String> styles) {
        apiClient.setRuleAllowedStyles(ruleId, styles);
    }

    @Override
    public void setLimits(long ruleId, RuleLimits limits) {
        apiClient.setRuleLimits(ruleId, limitsMapper.toApi(limits));
    }

    @Override
    public void setLayerDetails(long ruleId, LayerDetails detailsNew) {
        apiClient.setRuleLayerDetails(ruleId, detailsMapper.map(detailsNew));
    }

    @Override
    public Optional<LayerDetails> findLayerDetailsByRuleId(long ruleId) {
        ResponseEntity<org.geoserver.geofence.api.v2.model.LayerDetails> response;
        try {
            response = apiClient.getLayerDetailsByRuleIdWithHttpInfo(ruleId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Rule does not exist", e);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Rule has no layer set", e);
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        HttpStatus statusCode = response.getStatusCode();
        if (OK.equals(statusCode)) {
            return Optional.of(detailsMapper.map(response.getBody()));
        } else if (NO_CONTENT.equals(statusCode)) {
            return Optional.empty();
        }
        throw new IllegalStateException("Unexpected response status code: " + statusCode);
    }
}
