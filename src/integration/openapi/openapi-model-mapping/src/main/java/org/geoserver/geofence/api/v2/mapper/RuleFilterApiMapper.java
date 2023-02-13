package org.geoserver.geofence.api.v2.mapper;

import org.geoserver.geofence.adminrules.model.AdminRuleFilter;
import org.geoserver.geofence.api.v2.model.Pageable;
import org.geoserver.geofence.rules.model.RuleFilter;
import org.geoserver.geofence.rules.model.RuleQuery;

public class RuleFilterApiMapper {

    public RuleFilter map(org.geoserver.geofence.api.v2.model.RuleFilter filter) {
        return new RuleFilter();
    }

    public org.geoserver.geofence.api.v2.model.RuleFilter map(RuleFilter filter) {
        return new org.geoserver.geofence.api.v2.model.RuleFilter();
    }

    public org.geoserver.geofence.api.v2.model.AdminRuleFilter map(AdminRuleFilter filter) {
        return new org.geoserver.geofence.api.v2.model.AdminRuleFilter();
    }

    public AdminRuleFilter map(org.geoserver.geofence.api.v2.model.AdminRuleFilter filter) {
        return new AdminRuleFilter();
    }

    public Pageable extractPageable(RuleQuery<?> query) {
        Pageable pageable = null;
        if (query.getPageNumber().isPresent() && query.getPageSize().isPresent()) {
            pageable = new Pageable();
            pageable.setPage(query.getPageNumber().getAsInt());
            pageable.setSize(query.getPageSize().getAsInt());
        }
        return pageable;
    }
}
