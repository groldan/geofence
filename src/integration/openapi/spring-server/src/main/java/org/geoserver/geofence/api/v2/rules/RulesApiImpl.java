package org.geoserver.geofence.api.v2.rules;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.LayerDetailsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleFilterApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleLimitsApiMapper;
import org.geoserver.geofence.api.v2.model.InsertPosition;
import org.geoserver.geofence.api.v2.model.LayerDetails;
import org.geoserver.geofence.api.v2.model.Rule;
import org.geoserver.geofence.api.v2.model.RuleFilter;
import org.geoserver.geofence.api.v2.model.RuleLimits;
import org.geoserver.geofence.api.v2.server.RulesApiDelegate;
import org.geoserver.geofence.rules.model.RuleQuery;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RulesApiImpl implements RulesApiDelegate {

    private final @NonNull RuleAdminService service;
    private final @NonNull RuleApiMapper mapper;
    private final @NonNull LayerDetailsApiMapper layerDetailsMapper;
    private final @NonNull RuleLimitsApiMapper limitsMapper;
    private final @NonNull EnumsApiMapper enumsMapper;
    private final @NonNull NativeWebRequest request;

    private final RuleFilterApiMapper filterMapper = new RuleFilterApiMapper();

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Rule> createRule(InsertPosition position, @NonNull Rule rule) {
        org.geoserver.geofence.rules.model.Rule model = map(rule);
        org.geoserver.geofence.rules.model.Rule created;
        if (null == position) {
            created = service.insert(model);
        } else {
            created = service.insert(model, enumsMapper.map(position));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toApi(created));
    }

    @Override
    public ResponseEntity<Void> deleteRuleById(@NonNull String id) {

        boolean deleted = service.delete(id);
        HttpStatus status = deleted ? OK : NOT_FOUND;
        return ResponseEntity.status(status).build();
    }

    @Override
    public ResponseEntity<List<Rule>> getRules(@NonNull Pageable pageable) {

        return queryRules(pageable, null, null);
    }

    @Override
    public ResponseEntity<List<Rule>> queryRules( //
            @Nullable Pageable pageable,
            @Nullable Long priorityOffset,
            @Nullable RuleFilter ruleFilter) {

        org.geoserver.geofence.rules.model.RuleFilter filter = map(ruleFilter);

        RuleQuery<org.geoserver.geofence.rules.model.RuleFilter> query;
        query = RuleQuery.of(filter).setPriorityOffset(priorityOffset);

        if (pageable != null && pageable.isPaged()) {
            query.setPageNumber(pageable.getPageNumber()).setPageSize(pageable.getPageSize());
        }

        List<org.geoserver.geofence.rules.model.Rule> list;
        list = service.getList(query);

        List<Rule> body = list.stream().map(mapper::toApi).collect(Collectors.toList());

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<Rule> getRuleById(@NonNull String id) {
        Optional<org.geoserver.geofence.rules.model.Rule> found = service.get(id);

        return ResponseEntity.status(found.isPresent() ? OK : NOT_FOUND)
                .body(found.map(mapper::toApi).orElse(null));
    }

    @Override
    public ResponseEntity<Rule> findOneRuleByPriority(Long priority) {
        Optional<org.geoserver.geofence.rules.model.Rule> found;
        try {
            found = service.getRuleByPriority(priority);
        } catch (IllegalStateException multipleResults) {
            return ResponseEntity.status(CONFLICT).build();
        }
        return ResponseEntity.status(found.isPresent() ? OK : NOT_FOUND)
                .body(found.map(mapper::toApi).orElse(null));
    }

    @Override
    public ResponseEntity<Integer> countAllRules() {
        return ResponseEntity.ok(service.getCountAll());
    }

    @Override
    public ResponseEntity<Integer> countRules(RuleFilter ruleFilter) {
        return ResponseEntity.ok(service.count(map(ruleFilter)));
    }

    @Override
    public ResponseEntity<Boolean> ruleExistsById(@NonNull String id) {

        return ResponseEntity.ok(service.get(id).isPresent());
    }

    @Override
    public ResponseEntity<Void> setRuleAllowedStyles(@NonNull String id, Set<String> requestBody) {
        service.setAllowedStyles(id, requestBody);
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<LayerDetails> getLayerDetailsByRuleId(@NonNull String id) {
        LayerDetails details =
                service.getLayerDetails(id).map(layerDetailsMapper::map).orElse(null);
        return ResponseEntity.status(details == null ? NO_CONTENT : OK).body(details);
    }

    @Override
    public ResponseEntity<Void> setRuleLayerDetails(@NonNull String id, LayerDetails layerDetails) {
        org.geoserver.geofence.rules.model.LayerDetails ld = layerDetailsMapper.map(layerDetails);
        service.setLayerDetails(id, ld);
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<Void> unsetRuleLayerDetails(@NonNull String id) {
        service.setLayerDetails(id, null);
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<Void> setRuleLimits(@NonNull String id, RuleLimits ruleLimits) {
        service.setLimits(id, limitsMapper.toModel(ruleLimits));
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<Integer> shiftRulesByPriority(Long priorityStart, Long offset) {
        service.shift(priorityStart, offset);
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<Void> swapRulesById(@NonNull String id, @NonNull String id2) {
        service.swapPriority(id, id2);
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<Rule> updateRuleById(@NonNull String id, Rule patchBody) {
        org.geoserver.geofence.rules.model.Rule rule =
                service.get(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        // this applies only the values actually sent in the request body, using JsonNullable to
        // discern
        org.geoserver.geofence.rules.model.Rule patched = mapper.patch(rule, patchBody);
        org.geoserver.geofence.rules.model.Rule updated = service.update(patched);
        return ResponseEntity.status(OK).body(mapper.toApi(updated));
    }

    private org.geoserver.geofence.rules.model.RuleFilter map(RuleFilter ruleFilter) {
        return filterMapper.map(ruleFilter);
    }

    private org.geoserver.geofence.rules.model.Rule map(Rule apiModel) {
        return mapper.toModel(apiModel);
    }
}
