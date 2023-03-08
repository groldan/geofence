package org.geoserver.geofence.users.service;

import java.util.Set;
import java.util.function.Function;

@FunctionalInterface
public interface UserRolesResolver extends Function<String, Set<String>> {}
