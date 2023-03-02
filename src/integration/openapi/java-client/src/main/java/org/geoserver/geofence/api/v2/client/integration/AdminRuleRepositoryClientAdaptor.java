package org.geoserver.geofence.api.v2.client.integration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.adminrules.model.AdminRuleFilter;
import org.geoserver.geofence.adminrules.repository.AdminRuleRepository;
import org.geoserver.geofence.api.v2.client.AdminRulesApi;
import org.geoserver.geofence.api.v2.mapper.AdminRuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleFilterApiMapper;
import org.geoserver.geofence.rules.model.InsertPosition;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AdminRuleRepositoryClientAdaptor implements AdminRuleRepository {

    private final AdminRulesApi apiClient;
    private final AdminRuleApiMapper mapper;
    private final EnumsApiMapper enumsMapper;
    private final RuleFilterApiMapper filterMapper = new RuleFilterApiMapper();

    @Override
    public AdminRule create(AdminRule rule, InsertPosition position) {
        if (null != rule.getId()) throw new IllegalArgumentException("AdminRule must have no id");
        org.geoserver.geofence.api.v2.model.AdminRule result =
                apiClient.createAdminRule(map(rule), map(position));
        return mapper.map(result);
    }

    @Override
    public AdminRule save(AdminRule rule) {
        Objects.requireNonNull(rule.getId(), "AdminRule has no id");
        org.geoserver.geofence.api.v2.model.AdminRule response;
        response = apiClient.updateAdminRule(rule.getId(), mapper.map(rule));
        return mapper.map(response);
    }

    @Override
    public Optional<AdminRule> findById(@NonNull String id) {
        org.geoserver.geofence.api.v2.model.AdminRule rule;
        try {
            rule = apiClient.getAdminRuleById(id);
        } catch (HttpClientErrorException.NotFound e) {
            rule = null;
        }
        return Optional.ofNullable(rule).map(this::map);
    }

    @Override
    public Optional<AdminRule> findOne(AdminRuleFilter filter) {
        org.geoserver.geofence.api.v2.model.AdminRule rule;
        try {
            rule = apiClient.findOneAdminRule(map(filter));
        } catch (HttpClientErrorException.NotFound e) {
            rule = null;
        }
        return Optional.ofNullable(rule).map(this::map);
    }

    @Override
    public List<AdminRule> findAll() {
        Integer page = null;
        Integer size = null;
        return apiClient.findAllAdminRules(page, size).stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminRule> findAll(AdminRuleFilter filter) {
        Integer page = null;
        Integer size = null;
        Long priorityOffset = null;
        return apiClient.findAdminRules(page, size, priorityOffset, map(filter)).stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminRule> findAll(RuleQuery<AdminRuleFilter> query) {
        org.geoserver.geofence.api.v2.model.AdminRuleFilter filter;

        Integer page = query.getPageNumber();
        Integer size = query.getPageSize();
        Long priorityOffset;

        filter = query.getFilter().map(filterMapper::map).orElse(null);
        priorityOffset =
                query.getPriorityOffset().isPresent()
                        ? query.getPriorityOffset().getAsLong()
                        : null;

        List<org.geoserver.geofence.api.v2.model.AdminRule> rules;

        rules = apiClient.findAdminRules(page, size, priorityOffset, filter);

        return rules.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public Optional<AdminRule> findFirst(AdminRuleFilter adminRuleFilter) {

        org.geoserver.geofence.api.v2.model.AdminRule found;
        try {
            found = apiClient.findFirstAdminRule(map(adminRuleFilter));
        } catch (HttpClientErrorException.NotFound e) {
            found = null;
        }

        return Optional.ofNullable(found).map(this::map);
    }

    @Override
    public int count() {
        return apiClient.countAllAdminRules();
    }

    @Override
    public int count(AdminRuleFilter filter) {
        return apiClient.countAdminRules(map(filter));
    }

    @Override
    public int shiftPriority(long priorityStart, long offset) {
        return apiClient.shiftAdminRulesByPiority(priorityStart, offset);
    }

    @Override
    public void swap(@NonNull String id1, @NonNull String id2) {
        apiClient.swapAdminRules(id1, id2);
    }

    @Override
    public boolean deleteById(@NonNull String id) {
        try {
            apiClient.deleteAdminRuleById(id);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    @Override
    public int delete(AdminRuleFilter filter) {
        return apiClient.deleteAdminRules(map(filter));
    }

    @Override
    public Optional<AdminRule> findOneByPriority(long priority) {
        org.geoserver.geofence.api.v2.model.AdminRule found;
        try {
            found = apiClient.findOneAdminRuleByPriority(priority);
        } catch (HttpClientErrorException.NotFound e) {
            found = null;
        }
        return Optional.ofNullable(found).map(this::map);
    }

    private org.geoserver.geofence.api.v2.model.AdminRuleFilter map(AdminRuleFilter filter) {
        return filterMapper.map(filter);
    }

    private org.geoserver.geofence.api.v2.model.AdminRule map(AdminRule rule) {
        return mapper.map(rule);
    }

    private AdminRule map(org.geoserver.geofence.api.v2.model.AdminRule rule) {
        return mapper.map(rule);
    }

    private org.geoserver.geofence.api.v2.model.InsertPosition map(InsertPosition position) {
        return enumsMapper.map(position);
    }
}
