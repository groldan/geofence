package org.geoserver.geofence.api.v2.rules;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.geoserver.geofence.api.v2.mapper.AdminRuleApiMapper;
import org.geoserver.geofence.api.v2.mapper.EnumsApiMapper;
import org.geoserver.geofence.api.v2.mapper.RuleFilterApiMapper;
import org.geoserver.geofence.api.v2.model.AdminRule;
import org.geoserver.geofence.api.v2.model.AdminRuleFilter;
import org.geoserver.geofence.api.v2.model.InsertPosition;
import org.geoserver.geofence.api.v2.server.AdminRulesApiDelegate;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AdminRulesApiImpl implements AdminRulesApiDelegate {

    private final @NonNull AdminRuleAdminService service;
    private final @NonNull AdminRuleApiMapper mapper;
    private final @NonNull EnumsApiMapper enumsMapper;
    private final NativeWebRequest request;

    private final RuleFilterApiMapper filterMapper = new RuleFilterApiMapper();

    public @Override Optional<NativeWebRequest> getRequest() {
        return Optional.of(request);
    }

    public @Override ResponseEntity<Integer> countAllAdminRules() {
        return ResponseEntity.ok(service.getCountAll());
    }

    public @Override ResponseEntity<Integer> countAdminRules(AdminRuleFilter adminRuleFilter) {
        return ResponseEntity.ok(service.count(map(adminRuleFilter)));
    }

    public @Override ResponseEntity<AdminRule> createAdminRule(
            InsertPosition position, AdminRule adminRule) {

        org.geoserver.geofence.adminrules.model.AdminRule rule;
        if (position == null) {
            rule = service.insert(map(adminRule));
        } else {
            rule = service.insert(map(adminRule), map(position));
        }
        return ResponseEntity.ok(map(rule));
    }

    public @Override ResponseEntity<Integer> deleteAdminRules(AdminRuleFilter filter) {
        return ResponseEntity.ok(service.deleteRules(map(filter)));
    }

    public @Override ResponseEntity<Void> deleteAdminRuleById(@NonNull String id) {
        boolean deleted = service.delete(id);
        return ResponseEntity.status(deleted ? OK : NOT_FOUND).build();
    }

    public @Override ResponseEntity<Boolean> adminRuleExistsById(@NonNull String id) {
        return ResponseEntity.ok(service.exists(id));
    }

    public @Override ResponseEntity<List<AdminRule>> findAllAdminRules(Pageable pageable) {

        org.geoserver.geofence.adminrules.model.AdminRuleFilter filter = null;
        Integer page = pageable.isPaged() ? pageable.getPageNumber() : null;
        Integer entries = pageable.isPaged() ? pageable.getPageSize() : null;
        List<org.geoserver.geofence.adminrules.model.AdminRule> matches =
                service.getList(filter, page, entries);
        List<AdminRule> body = matches.stream().map(this::map).collect(Collectors.toList());
        return ResponseEntity.ok(body);
    }

    public @Override ResponseEntity<AdminRule> findFirstAdminRule(AdminRuleFilter adminRuleFilter) {

        List<org.geoserver.geofence.adminrules.model.AdminRule> first;
        first = service.getList(map(adminRuleFilter), 0, 1);

        return ResponseEntity.status(first.isEmpty() ? NOT_FOUND : OK)
                .body(first.isEmpty() ? null : map(first.get(0)));
    }

    public @Override ResponseEntity<AdminRule> findOneAdminRule(AdminRuleFilter adminRuleFilter) {
        Optional<org.geoserver.geofence.adminrules.model.AdminRule> found =
                service.getFirstMatch(map(adminRuleFilter));

        return ResponseEntity.status(found.isPresent() ? OK : NOT_FOUND)
                .body(found.map(this::map).orElse(null));
    }

    public @Override ResponseEntity<AdminRule> findOneAdminRuleByPriority(Long priority) {
        Optional<org.geoserver.geofence.adminrules.model.AdminRule> found =
                service.getRuleByPriority(priority);

        return ResponseEntity.status(found.isPresent() ? OK : NOT_FOUND)
                .body(found.map(this::map).orElse(null));
    }

    public @Override ResponseEntity<List<AdminRule>> findAdminRules(
            Pageable pageable, Long priorityOffset, AdminRuleFilter adminRuleFilter) {

        org.geoserver.geofence.adminrules.model.AdminRuleFilter filter = map(adminRuleFilter);

        Integer page = pageable.isPaged() ? pageable.getPageNumber() : null;
        Integer pageSize = pageable.isPaged() ? pageable.getPageSize() : null;
        List<org.geoserver.geofence.adminrules.model.AdminRule> matches =
                service.getList(filter, page, pageSize);
        return ResponseEntity.ok(matches.stream().map(this::map).collect(Collectors.toList()));
    }

    public @Override ResponseEntity<AdminRule> getAdminRuleById(@NonNull String id) {

        Optional<org.geoserver.geofence.adminrules.model.AdminRule> found = service.get(id);

        return ResponseEntity.status(found.isPresent() ? OK : NOT_FOUND)
                .body(found.map(this::map).orElse(null));
    }

    public @Override ResponseEntity<Integer> shiftAdminRulesByPiority(
            @NonNull Long priorityStart, @NonNull Long offset) {
        return ResponseEntity.ok(service.shift(priorityStart, offset));
    }

    public @Override ResponseEntity<Void> swapAdminRulesById(
            @NonNull String id, @NonNull String id2) {
        service.swap(id, id2);
        return ResponseEntity.status(OK).build();
    }

    public @Override ResponseEntity<AdminRule> updateAdminRule(
            @NonNull String id, AdminRule patchBody) {
        org.geoserver.geofence.adminrules.model.AdminRule rule =
                service.get(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        // this applies only the values actually sent in the request body, using JsonNullable to
        // discern
        org.geoserver.geofence.adminrules.model.AdminRule patched = mapper.patch(rule, patchBody);
        org.geoserver.geofence.adminrules.model.AdminRule updated = service.update(patched);
        return ResponseEntity.status(OK).body(map(updated));
    }

    private org.geoserver.geofence.adminrules.model.AdminRuleFilter map(
            AdminRuleFilter adminRuleFilter) {
        return filterMapper.map(adminRuleFilter);
    }

    private org.geoserver.geofence.adminrules.model.AdminRule map(AdminRule adminRule) {
        return mapper.map(adminRule);
    }

    private AdminRule map(org.geoserver.geofence.adminrules.model.AdminRule rule) {
        return mapper.map(rule);
    }

    private org.geoserver.geofence.rules.model.InsertPosition map(InsertPosition position) {
        return enumsMapper.map(position);
    }
}
