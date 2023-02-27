package org.geoserver.geofence.jpa.integration;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.NonNull;

import org.geoserver.geofence.jpa.integration.mapper.JPAGeoServerUserGroupMapper;
import org.geoserver.geofence.jpa.model.QGeoServerUserGroup;
import org.geoserver.geofence.jpa.repository.JpaGeoServerUserGroupRepository;
import org.geoserver.geofence.jpa.repository.TransactionRequired;
import org.geoserver.geofence.jpa.repository.TransactionSupported;
import org.geoserver.geofence.users.model.GeoServerUserGroup;
import org.geoserver.geofence.users.repository.GeoServerUserGroupRepository;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@TransactionSupported
public class GeoServerUserGroupRepositoryJpaAdaptor implements GeoServerUserGroupRepository {

    private final JpaGeoServerUserGroupRepository jpaRepository;
    private final JPAGeoServerUserGroupMapper mapper;

    public GeoServerUserGroupRepositoryJpaAdaptor(
            @NonNull JpaGeoServerUserGroupRepository jpaRepository,
            @NonNull JPAGeoServerUserGroupMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @TransactionRequired
    public GeoServerUserGroup insert(@NonNull GeoServerUserGroup user) {
        if (null != user.getId()) throw new IllegalArgumentException("user must have no id");
        org.geoserver.geofence.jpa.model.GeoServerUserGroup created =
                jpaRepository.save(mapper.map(user));
        return mapper.map(created);
    }

    @Override
    @TransactionRequired
    public GeoServerUserGroup save(GeoServerUserGroup user) {
        Objects.requireNonNull(user.getId());
        org.geoserver.geofence.jpa.model.GeoServerUserGroup saved =
                jpaRepository.save(mapper.map(user));
        return mapper.map(saved);
    }

    @Override
    @TransactionRequired
    public boolean delete(long id) {
        return jpaRepository.deleteById(id);
    }

    @Override
    public Optional<GeoServerUserGroup> findById(long id) {
        return jpaRepository.findById(id).map(mapper::map);
    }

    @Override
    public Optional<GeoServerUserGroup> findByName(String name) {
        return jpaRepository.findOneByName(name).map(mapper::map);
    }

    @Override
    public long countByNameLike(@NonNull String nameLike) {
        return jpaRepository.count(nameILike(nameLike));
    }

    @Override
    public List<GeoServerUserGroup> findAllByNameLike(@NonNull String nameLike) {
        return findAll(nameILike(nameLike));
    }

    @Override
    public List<GeoServerUserGroup> findAllByNameLike(
            @NonNull String nameLike, int page, int entries) {
        PageRequest pageRequest = PageRequest.of(page, entries);
        BooleanExpression predicate = nameILike(nameLike);
        return jpaRepository.findAll(predicate, pageRequest).getContent().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    private List<GeoServerUserGroup> findAll(BooleanExpression predicate) {
        return StreamSupport.stream(jpaRepository.findAll(predicate).spliterator(), false)
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    private BooleanExpression nameILike(String nameLike) {
        BooleanExpression predicate =
                QGeoServerUserGroup.geoServerUserGroup.name.likeIgnoreCase(nameLike);
        return predicate;
    }
}
