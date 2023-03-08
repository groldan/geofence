package org.geoserver.geofence.users.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.users.model.GeoServerUser;

import java.util.Set;

@RequiredArgsConstructor
public class DefaultUserRolesResolver implements UserRolesResolver {

    private final @NonNull UserAdminService userAdminService;

    @Override
    public Set<String> apply(String name) {
        return userAdminService
                .getByName(name)
                .map(GeoServerUser::getUserGroups)
                .orElseGet(Set::of);
    }
}
