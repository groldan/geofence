package org.geoserver.geofence.adminrules.repository;

public class AdminRuleIdentifierConflictException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public AdminRuleIdentifierConflictException(String msg) {
        super(msg);
    }

    public AdminRuleIdentifierConflictException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
