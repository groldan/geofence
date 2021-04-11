/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.geoserver.geofence.core.dao.RestrictedGenericDAO;
import org.geoserver.geofence.jpa.mapper.ModelMapper;
import org.geoserver.geofence.jpa.repository.BaseRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base DAO furnish a set of methods usually used
 *
 * @author Tobia Di Pisa (tobia.dipisa@geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public abstract class BaseDAO<T, JPA> implements RestrictedGenericDAO<T> {

    protected static final ModelMapper MAPPER = Mappers.getMapper(ModelMapper.class);

    protected abstract Class<JPA> getPersistenceType();

    protected abstract BaseRepository<JPA, Long> getRepository();

    protected abstract Function<JPA, T> persistenceToModel();

    protected abstract Function<T, JPA> modelToPersistence();

    protected JPA toJPA(T entity) {
        return modelToPersistence().apply(entity);
    }

    protected T toEntity(JPA jpa) {
        return persistenceToModel().apply(jpa);
    }

    protected List<T> toEntity(List<JPA> jpa) {
        return jpa.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public int countAll() {
        return getRepository().countAll();
    }

    @Override
    public List<T> findAll() {
        return toEntity(getRepository().findAll());
    }

    @Override
    public T merge(T entity) {
        JPA jpa = toJPA(entity);
        JPA merged = getRepository().merge(jpa);
        return toEntity(merged);
    }

    @Override
    public T persist(T entity) {
        JPA jpa = toJPA(entity);
        jpa = getRepository().save(jpa);
        return toEntity(jpa);
    }

    @Override
    public T find(Long id) {
        JPA found = getRepository().find(id);
        return toEntity(found);
    }

    @Override
    public boolean remove(T entity) {
        return getRepository().remove(toJPA(entity));
    }

    @Override
    public boolean removeById(Long id) {
        return getRepository().removeById(id);
    }

    @Override
    public void removeAllById(Long... ids) {
        getRepository().removeByIds(ids);
    }
}
