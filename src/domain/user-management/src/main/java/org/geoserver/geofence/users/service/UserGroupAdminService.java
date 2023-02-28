/*
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.users.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.users.model.GeoServerUserGroup;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Operations on {@link GeoServerUserGroup UserGroups}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@RequiredArgsConstructor
public class UserGroupAdminService {

    private final @NonNull GeoServerUserGroupRepository userGroupRepository;

    // ==========================================================================

    public GeoServerUserGroup insert(@NonNull GeoServerUserGroup group) {
        Objects.requireNonNull(group.getName());
        return userGroupRepository.insert(group.withCreatedDate(null));
    }

    /**
     * @throws IllegalArgumentException if {@code group} has no id, does not exist, or its name
     *     changed to one that already exists
     */
    public GeoServerUserGroup update(@NonNull GeoServerUserGroup group) {
        if (null == group.getId()) throw new IllegalArgumentException("no group id provided");
        return userGroupRepository.save(group);
    }

    public Optional<GeoServerUserGroup> get(@NonNull String id) {
        return userGroupRepository.findById(id);
    }

    public Optional<GeoServerUserGroup> getByName(@NonNull String name) {
        return userGroupRepository.findByName(name);
    }

    public boolean delete(@NonNull String id) {
        return userGroupRepository.delete(id);
    }

    public List<GeoServerUserGroup> getList(String nameLike, Integer page, Integer entries) {
        return userGroupRepository.findAllByNameLike(nameLike, page, entries);
    }

    public long getCount(@NonNull String nameLike) {
        return userGroupRepository.countByNameLike(nameLike);
    }
}
