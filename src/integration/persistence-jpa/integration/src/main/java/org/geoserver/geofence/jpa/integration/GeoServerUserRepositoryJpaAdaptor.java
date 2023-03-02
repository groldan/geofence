package org.geoserver.geofence.jpa.integration;

import static org.geoserver.geofence.jpa.integration.mapper.GeoServerUserJpaMapper.decodeId;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.NonNull;

import org.geoserver.geofence.jpa.integration.mapper.GeoServerUserJpaMapper;
import org.geoserver.geofence.jpa.model.QGeoServerUser;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserRepository;
import org.geoserver.geofence.jpa.repository.TransactionRequired;
import org.geoserver.geofence.jpa.repository.TransactionSupported;
import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.repository.GeoServerUserRepository;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@TransactionSupported
public class GeoServerUserRepositoryJpaAdaptor implements GeoServerUserRepository {

    private final JpaGeoServerUserRepository jpaRepository;
    private final GeoServerUserJpaMapper mapper;

    public GeoServerUserRepositoryJpaAdaptor(
            @NonNull JpaGeoServerUserRepository jpaRepository,
            @NonNull GeoServerUserJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @TransactionRequired
    public GeoServerUser insert(@NonNull GeoServerUser user) {
        if (null != user.getId()) throw new IllegalArgumentException("user must have no id");
        org.geoserver.geofence.jpa.model.GeoServerUser created =
                jpaRepository.save(mapper.map(user));
        return mapper.map(created);
    }

    @Override
    @TransactionRequired
    public GeoServerUser save(GeoServerUser user) {
        Objects.requireNonNull(user.getId());
        org.geoserver.geofence.jpa.model.GeoServerUser saved = jpaRepository.save(mapper.map(user));
        return mapper.map(saved);
    }

    @Override
    @TransactionRequired
    public boolean delete(@NonNull String id) {
        return jpaRepository.deleteById(decodeId(id).longValue());
    }

    @Override
    public Optional<GeoServerUser> findById(@NonNull String id) {
        return jpaRepository.findById(decodeId(id).longValue()).map(mapper::map);
    }

    @Override
    public Optional<GeoServerUser> findByName(String name) {
        return jpaRepository.findOneByName(name).map(mapper::map);
    }

    @Override
    public long countByNameLike(@NonNull String nameLike) {
        return jpaRepository.count(nameILike(nameLike));
    }

    @Override
    public List<GeoServerUser> findAllByNameLike(@NonNull String nameLike) {
        return findAll(nameILike(nameLike));
    }

    @Override
    public List<GeoServerUser> findAllByNameLike(@NonNull String nameLike, int page, int entries) {
        PageRequest pageRequest = PageRequest.of(page, entries);
        BooleanExpression predicate = nameILike(nameLike);
        return jpaRepository.findAll(predicate, pageRequest).getContent().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    private List<GeoServerUser> findAll(BooleanExpression predicate) {
        return StreamSupport.stream(jpaRepository.findAll(predicate).spliterator(), false)
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    private BooleanExpression nameILike(String nameLike) {
        BooleanExpression predicate = QGeoServerUser.geoServerUser.name.likeIgnoreCase(nameLike);
        return predicate;
    }

    @Override
    public List<GeoServerUser> findAll() {
        return jpaRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }
}
