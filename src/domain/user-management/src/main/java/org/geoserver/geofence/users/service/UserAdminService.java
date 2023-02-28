/*
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.users.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Operations on {@link GeoServerUser GSUser}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@RequiredArgsConstructor
public class UserAdminService {

    private final @NonNull GeoServerUserRepository repository;

    // ==========================================================================
    // Basic operations

    public GeoServerUser insert(@NonNull GeoServerUser user) {
        return repository.insert(pwdEncode(user).withCreatedDate(null));
    }

    public GeoServerUser update(GeoServerUser user) {
        return repository.save(pwdEncode(user));
    }

    public boolean delete(@NonNull String id) {
        return repository.delete(id);
    }

    /**
     * Retrieves basic info on Users. <br>
     * If you need structured info (such as Groups), use the {@link #getFull(long)} method.
     *
     * @return Basic GSUser, with some info left unreferenced.
     */
    public Optional<GeoServerUser> get(@NonNull String id) {
        return repository.findById(id).map(this::pwdDecode);
    }

    public Optional<GeoServerUser> getByName(@NonNull String name) {
        return repository.findByName(name).map(this::pwdDecode);
    }

    public long getCount(String nameLike) {
        return repository.countByNameLike(nameLike);
    }

    public List<GeoServerUser> getList(String nameLike, Integer page, Integer entries) {
        return repository.findAllByNameLike(nameLike, page, entries).stream()
                .map(this::pwdDecode)
                .collect(Collectors.toList());
    }

    private GeoServerUser pwdEncode(GeoServerUser user) {
        if (null != user.getPassword()) {
            return user.withPassword(PwEncoder.encode(user.getPassword()));
        }
        return user;
    }

    private GeoServerUser pwdDecode(GeoServerUser user) {
        if (null != user.getPassword()) {
            return user.withPassword(PwEncoder.decode(user.getPassword()));
        }
        return user;
    }
}
