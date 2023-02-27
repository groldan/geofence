package org.geoserver.geofence.authorization.users;

public class InvalidCredentialsException extends AuthorizationException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}
