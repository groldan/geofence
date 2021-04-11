/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.PrioritizableDAO;
import org.geoserver.geofence.core.model.InsertPosition;
import org.geoserver.geofence.core.model.Prioritizable;
import org.geoserver.geofence.jpa.model.JPAInsertPosition;
import org.geoserver.geofence.jpa.model.JPAPrioritizable;
import org.geoserver.geofence.jpa.repository.PrioritizableRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Component
@Transactional(value = "geofenceTransactionManager")
public abstract class PrioritizableDAOImpl<T extends Prioritizable, JPA extends JPAPrioritizable>
        extends BaseDAO<T, JPA> implements PrioritizableDAO<T> {

    @Override
    protected abstract PrioritizableRepository<JPA> getRepository();

    @Override
    public T persist(T entity, InsertPosition position) {
        final PrioritizableRepository<JPA> repo = getRepository();
        final Class<JPA> clazz = getPersistenceType();
        JPA jpa = toJPA(entity);
        JPAInsertPosition jpaposition = MAPPER.map(position);
        jpa = repo.persist(clazz, jpa, jpaposition);
        return toEntity(jpa);
    }

    @Override
    public int shift(long priorityStart, long offset) {
        final Class<JPA> clazz = getPersistenceType();
        final PrioritizableRepository<JPA> repo = getRepository();
        return repo.shift(clazz, priorityStart, offset);
    }

    @Override
    public void swap(long id1, long id2) {
        final PrioritizableRepository<JPA> repo = getRepository();
        repo.swap(id1, id2);
    }
}
