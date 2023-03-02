package org.geoserver.geofence.adminrules.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.geoserver.geofence.rules.model.RuleFilter;

// REVISIT: shouldn't extend RuleFilter, it has only a subset of its properties
public class AdminRuleFilter extends RuleFilter {

    private @Getter @Setter AdminGrantType grantType;

    public AdminRuleFilter() {
        super();
    }

    public AdminRuleFilter(AdminRuleFilter source) {
        super(source);
        grantType = source.getGrantType();
    }

    private AdminRuleFilter(RuleFilter source) {
        super(source);
    }

    @Override
    public AdminRuleFilter clone() {
        return new AdminRuleFilter(this);
    }

    public static AdminRuleFilter of(RuleFilter ruleFilter) {
        return new AdminRuleFilter(ruleFilter);
    }

    public static AdminRuleFilter any() {
        return AdminRuleFilter.of(RuleFilter.any());
    }

    public boolean matches(@NonNull AdminRule rule) {
        AdminRuleIdentifier idf = rule.getIdentifier();
        return getInstance().matches(idf.getInstanceName())
                        && getRole().matches(idf.getRolename())
                        && getSourceAddress().matches(idf.getAddressRange())
                        && getUser().matches(idf.getUsername())
                        && getWorkspace().matches(idf.getWorkspace())
                        && grantType == null
                ? true
                : grantType.equals(rule.getAccess());
    }
}
