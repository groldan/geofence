package org.geoserver.geofence.jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

@NoRepositoryBean
public interface PriorityRepository<T> extends PagingAndSortingRepository<T, Long> {

    Optional<T> findOneByPriority(long priority);

    int shiftPriority(long priorityStart, long offset);

    Optional<Long> findMaxPriority();

    Optional<Long> findMinPriority();

    void shiftPrioritiesBetween(long min, long max, long offset);
}
