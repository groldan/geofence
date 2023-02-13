package org.geoserver.geofence.adminrules.model;

import lombok.Getter;
import lombok.Setter;

import org.geoserver.geofence.rules.model.RuleFilter;

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
}
