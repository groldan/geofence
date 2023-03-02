package org.geoserver.geofence.rules.repository;

public class RuleIdentifierConflictException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public RuleIdentifierConflictException(String msg) {
        super(msg);
    }

    public RuleIdentifierConflictException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
