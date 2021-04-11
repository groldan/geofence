/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.repository;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base DAO furnish a set of methods usually used
 *
 * @author Tobia Di Pisa (tobia.dipisa@geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public abstract class BaseRepository<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

    /**
     * EntityManager setting
     *
     * @param entityManager the entity manager to set
     */
    @Override
    @PersistenceContext(unitName = "geofenceEntityManagerFactory")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    /**
     * JPASearchProcessor setting
     *
     * @param searchProcessor the search processor to set
     */
    @Override
    @Autowired
    public void setSearchProcessor(
            @Qualifier("geofenceSearchProcessor") JPASearchProcessor searchProcessor) {
        super.setSearchProcessor(searchProcessor);
    }

    public int countAll() {
        return count((ISearch) null);
    }
}
