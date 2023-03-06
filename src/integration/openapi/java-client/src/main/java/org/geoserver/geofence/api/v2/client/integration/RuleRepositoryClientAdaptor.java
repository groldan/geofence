package org.geoserver.geofence.api.v2.client.integration;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.client.RulesApi;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleFilterApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapper;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.LayerDetails;
import org.geoserver.geofence.rules.model.Rule;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleLimits;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.geoserver.geofence.rules.repository.RuleIdentifierConflictException;
import org.geoserver.geofence.rules.repository.RuleRepository;
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
    public boolean existsById(@NonNull String id) {
        return apiClient.ruleExistsById(id);
    }

    @Override
    public Rule save(Rule rule) {
        Objects.requireNonNull(rule.getId(), "Rule has no id");
        try {
            org.geoserver.geofence.api.v2.model.Rule response;
            response = apiClient.updateRuleById(rule.getId(), map(rule));
            return map(response);
        } catch (HttpClientErrorException.Conflict e) {
            throw new RuleIdentifierConflictException(reason(e), e);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException(reason(e), e);
        }
    }

    @Override
    public Rule create(Rule rule, InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("Rule must have no id");
        org.geoserver.geofence.api.v2.model.Rule response;
        try {
            response = apiClient.createRule(map(rule), enumsMapper.map(position));
        } catch (HttpClientErrorException.Conflict c) {
            throw new RuleIdentifierConflictException(reason(c), c);
        }
        return map(response);
    }

    private String reason(HttpClientErrorException e) {
        return reason(e, e.getMessage());
    }

    private String reason(HttpClientErrorException e, String defaultValue) {
        String reason = e.getResponseHeaders().getFirst("X-Reason");
        return reason == null ? defaultValue : reason;
    }

    @Override
    public boolean delete(@NonNull String id) {
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
        Integer page = null;
        Integer size = null;
        List<org.geoserver.geofence.api.v2.model.Rule> rules = apiClient.getRules(page, size);
        return rules.stream().map(this::map);
    }

    @Override
    public Stream<Rule> query(RuleQuery<RuleFilter> query) {

        org.geoserver.geofence.api.v2.model.RuleFilter filter;
        Long priorityOffset;

        filter = query.getFilter().map(filterMapper::toApi).orElse(null);
        priorityOffset =
                query.getPriorityOffset().isPresent()
                        ? query.getPriorityOffset().getAsLong()
                        : null;

        List<org.geoserver.geofence.api.v2.model.Rule> rules;

        Integer page = query.getPageNumber();
        Integer size = query.getPageSize();
        rules = apiClient.queryRules(page, size, priorityOffset, filter);

        return rules.stream().map(this::map);
    }

    @Override
    public Optional<Rule> findById(@NonNull String id) {
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

    @Override
    public int shift(long priorityStart, long offset) {
        try {
            return apiClient.shiftRulesByPriority(priorityStart, offset);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(reason(e), e);
        }
    }

    @Override
    public void swap(@NonNull String id1, @NonNull String id2) {
        try {
            apiClient.swapRules(id1, id2);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(reason(e), e);
        }
    }

    @Override
    public void setAllowedStyles(@NonNull String ruleId, Set<String> styles) {
        try {
            apiClient.setRuleAllowedStyles(ruleId, styles);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(reason(e), e);
        }
    }

    @Override
    public void setLimits(@NonNull String ruleId, RuleLimits limits) {
        try {
            apiClient.setRuleLimits(ruleId, limitsMapper.toApi(limits));
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException(reason(e), e);
        }
    }

    @Override
    public void setLayerDetails(@NonNull String ruleId, LayerDetails detailsNew) {
        try {
            apiClient.setRuleLayerDetails(ruleId, detailsMapper.map(detailsNew));
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(reason(e), e);
        }
    }

    @Override
    public Optional<LayerDetails> findLayerDetailsByRuleId(@NonNull String ruleId) {
        ResponseEntity<org.geoserver.geofence.api.v2.model.LayerDetails> response;
        try {
            response = apiClient.getLayerDetailsByRuleIdWithHttpInfo(ruleId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Rule does not exist", e);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(reason(e), e);
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

    private org.geoserver.geofence.api.v2.model.RuleFilter map(RuleFilter filter) {
        return filterMapper.toApi(filter);
    }

    private org.geoserver.geofence.api.v2.model.Rule map(Rule rule) {
        return mapper.toApi(rule);
    }

    private Rule map(org.geoserver.geofence.api.v2.model.Rule rule) {
        return mapper.toModel(rule);
    }
}
