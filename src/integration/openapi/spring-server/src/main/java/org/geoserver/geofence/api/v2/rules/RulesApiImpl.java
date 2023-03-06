package org.geoserver.geofence.api.v2.rules;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
import org.geoserver.geofence.filter.RuleQuery;
import org.geoserver.geofence.rules.repository.RuleIdentifierConflictException;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

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

    private final RuleFilterApiMapper filterMapper = new RuleFilterApiMapper();

    @Override
    public ResponseEntity<Rule> createRule(@NonNull Rule rule, InsertPosition position) {
        org.geoserver.geofence.rules.model.Rule model = map(rule);
        org.geoserver.geofence.rules.model.Rule created;
        try {
            if (null == position) {
                created = service.insert(model);
            } else {
                created = service.insert(model, enumsMapper.map(position));
            }
        } catch (RuleIdentifierConflictException conflict) {
            return error(CONFLICT, conflict.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toApi(created));
    }

    private <T> ResponseEntity<T> error(HttpStatus code, String reason) {
        return ResponseEntity.status(code).header("X-Reason", reason).build();
    }

    @Override
    public ResponseEntity<Void> deleteRuleById(@NonNull String id) {

        boolean deleted = service.delete(id);
        HttpStatus status = deleted ? OK : NOT_FOUND;
        return ResponseEntity.status(status).build();
    }

    @Override
    public ResponseEntity<List<Rule>> getRules(Integer page, Integer size) {
        return query(RuleQuery.of(page, size));
    }

    @Override
    public ResponseEntity<List<Rule>> queryRules( //
            @Nullable Integer page,
            @Nullable Integer size,
            @Nullable Long priorityOffset,
            @Nullable RuleFilter ruleFilter) {

        org.geoserver.geofence.filter.RuleFilter filter = map(ruleFilter);

        RuleQuery<org.geoserver.geofence.filter.RuleFilter> query;
        query = RuleQuery.of(filter).setPriorityOffset(priorityOffset);

        query.setPageNumber(page).setPageSize(size);

        return query(query);
    }

    private ResponseEntity<List<Rule>> query(
            RuleQuery<org.geoserver.geofence.filter.RuleFilter> query) {
        List<org.geoserver.geofence.rules.model.Rule> list;
        try {
            list = service.getList(query);
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }

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
        try {
            service.setAllowedStyles(id, requestBody);
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.status(OK).build();
    }

    @Override
    public ResponseEntity<LayerDetails> getLayerDetailsByRuleId(@NonNull String id) {
        try {
            LayerDetails details =
                    service.getLayerDetails(id).map(layerDetailsMapper::map).orElse(null);
            return ResponseEntity.status(details == null ? NO_CONTENT : OK).body(details);
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> setRuleLayerDetails(@NonNull String id, LayerDetails layerDetails) {
        try {
            org.geoserver.geofence.rules.model.LayerDetails ld =
                    layerDetailsMapper.map(layerDetails);
            service.setLayerDetails(id, ld);
            return ResponseEntity.status(OK).build();
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> setRuleLimits(@NonNull String id, RuleLimits ruleLimits) {
        try {
            service.setLimits(id, limitsMapper.toModel(ruleLimits));
            return ResponseEntity.status(NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Integer> shiftRulesByPriority(Long priorityStart, Long offset) {
        try {
            int affectedCount = service.shift(priorityStart, offset);
            return ResponseEntity.ok(affectedCount);
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> swapRules(@NonNull String id, @NonNull String id2) {
        try {
            service.swapPriority(id, id2);
            return ResponseEntity.status(NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Rule> updateRuleById(@NonNull String id, Rule patchBody) {
        org.geoserver.geofence.rules.model.Rule rule = service.get(id).orElse(null);
        if (null == rule) {
            return error(NOT_FOUND, "Rule " + id + " does not exist");
        }
        if (patchBody.getId().isPresent() && !id.equals(patchBody.getId().get())) {
            return error(
                    BAD_REQUEST,
                    "Request body supplied a different id ("
                            + patchBody.getId().get()
                            + ") than the requested rule id: "
                            + id);
        }

        // this applies only the values actually sent in the request body, using JsonNullable to
        // discern
        org.geoserver.geofence.rules.model.Rule patched = mapper.patch(rule, patchBody);
        try {
            org.geoserver.geofence.rules.model.Rule updated = service.update(patched);
            return ResponseEntity.status(OK).body(mapper.toApi(updated));
        } catch (RuleIdentifierConflictException e) {
            return error(CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            return error(BAD_REQUEST, e.getMessage());
        }
    }

    private org.geoserver.geofence.filter.RuleFilter map(RuleFilter ruleFilter) {
        return filterMapper.toModel(ruleFilter);
    }

    private org.geoserver.geofence.rules.model.Rule map(Rule apiModel) {
        return mapper.toModel(apiModel);
    }
}
