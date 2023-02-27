package org.geoserver.geofence.authorization.users;

public class UserNotFoundException extends AuthorizationException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
