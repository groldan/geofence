package org.geoserver.geofence.users.repository;

import lombok.NonNull;

import org.geoserver.geofence.users.model.GeoServerUserGroup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryGeoServerUserGroupRepository implements GeoServerUserGroupRepository {

    private final AtomicLong idseq = new AtomicLong();

    private final ConcurrentMap<String, GeoServerUserGroup> groups = new ConcurrentHashMap<>();

    @Override
    public GeoServerUserGroup insert(@NonNull GeoServerUserGroup group) {
        Objects.requireNonNull(group.getName());
        group = group.withId(String.valueOf(idseq.incrementAndGet()));
        groups.put(group.getId(), group);
        return group;
    }

    @Override
    public GeoServerUserGroup save(GeoServerUserGroup group) {
        GeoServerUserGroup current = groups.get(group.getId());
        if (null == current) {
            throw new IllegalArgumentException("User does not exist");
        }
        groups.put(group.getId(), group);
        return group;
    }

    @Override
    public boolean delete(String id) {
        return groups.remove(id) != null;
    }

    @Override
    public Optional<GeoServerUserGroup> findById(String id) {
        return Optional.ofNullable(groups.get(id));
    }

    @Override
    public Optional<GeoServerUserGroup> findByName(String name) {
        return groups.values().stream().filter(u -> name.equals(u.getName())).findFirst();
    }

    @Override
    public long countByNameLike(String nameLike) {
        return streamAllByNameLike(nameLike).count();
    }

    @Override
    public List<GeoServerUserGroup> findAllByNameLike(String nameLike) {
        return streamAllByNameLike(nameLike).collect(Collectors.toList());
    }

    private Stream<GeoServerUserGroup> streamAllByNameLike(String nameLike) {
        return groups.values().stream().filter(u -> nameLike.equalsIgnoreCase(u.getName()));
    }

    @Override
    public List<GeoServerUserGroup> findAllByNameLike(
            @NonNull String nameLike, int page, int entries) {
        final int off = page * entries;
        return streamAllByNameLike(nameLike).skip(off).limit(entries).collect(Collectors.toList());
    }

    @Override
    public List<GeoServerUserGroup> findAll() {
        return List.copyOf(groups.values());
    }
}
