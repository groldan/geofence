package org.geoserver.geofence.users.repository;

import lombok.NonNull;

import org.geoserver.geofence.users.model.GeoServerUser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryGeoServerUserRepository implements GeoServerUserRepository {

    private final AtomicLong idseq = new AtomicLong();

    private final ConcurrentMap<String, GeoServerUser> users = new ConcurrentHashMap<>();

    @Override
    public GeoServerUser insert(@NonNull GeoServerUser user) {
        Objects.requireNonNull(user.getName());
        user = user.withId(String.valueOf(idseq.incrementAndGet()));
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public GeoServerUser save(GeoServerUser user) {
        GeoServerUser current = users.get(user.getId());
        if (null == current) {
            throw new IllegalArgumentException("User does not exist");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean delete(String id) {
        return users.remove(id) != null;
    }

    @Override
    public Optional<GeoServerUser> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<GeoServerUser> findByName(String name) {
        return users.values().stream().filter(u -> name.equals(u.getName())).findFirst();
    }

    @Override
    public long countByNameLike(String nameLike) {
        return streamAllByNameLike(nameLike).count();
    }

    @Override
    public List<GeoServerUser> findAllByNameLike(String nameLike) {
        return streamAllByNameLike(nameLike).collect(Collectors.toList());
    }

    private Stream<GeoServerUser> streamAllByNameLike(String nameLike) {
        return users.values().stream().filter(u -> nameLike.equalsIgnoreCase(u.getName()));
    }

    @Override
    public List<GeoServerUser> findAllByNameLike(@NonNull String nameLike, int page, int entries) {
        final int off = page * entries;
        return streamAllByNameLike(nameLike).skip(off).limit(entries).collect(Collectors.toList());
    }

    @Override
    public List<GeoServerUser> findAll() {
        return List.copyOf(users.values());
    }
}
