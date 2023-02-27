package org.geoserver.geofence.jpa.repository;

import org.geoserver.geofence.jpa.model.AdminRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@TransactionSupported
public interface JpaAdminRuleRepository
        extends JpaRepository<AdminRule, Long>, QuerydslPredicateExecutor<AdminRule> {

    Sort naturalOrder = Sort.by("identifier.instance.id", "priority");

    @TransactionRequired
    boolean deleteById(long id);

    @Query("SELECT r FROM AdminRule r ORDER BY identifier.instance.id, priority")
    List<AdminRule> findAllNaturalOrder();

    default List<AdminRule> findAllNaturalOrder(com.querydsl.core.types.Predicate predicate) {

        Iterable<AdminRule> matches = findAll(predicate, naturalOrder);

        if (matches instanceof List) return (List<AdminRule>) matches;

        return StreamSupport.stream(matches.spliterator(), false).collect(Collectors.toList());
    }

    @Query("SELECT r FROM AdminRule r ORDER BY identifier.instance.id, priority")
    Page<AdminRule> findAllNaturalOrder(Pageable pageable);

    default Page<AdminRule> findAllNaturalOrder(
            com.querydsl.core.types.Predicate predicate, Pageable pageable) {

        if (pageable.isPaged()) {
            PageRequest sortingPageRequest =
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), naturalOrder);
            return findAll(predicate, sortingPageRequest);
        }
        Iterable<AdminRule> matches = findAll(predicate, naturalOrder);
        List<AdminRule> contents;
        if (matches instanceof List) contents = (List<AdminRule>) matches;
        else
            contents =
                    StreamSupport.stream(matches.spliterator(), false).collect(Collectors.toList());
        return new PageImpl<>(contents);
    }

    @TransactionRequired
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE AdminRule SET priority = priority + :offset WHERE priority >= :priorityStart")
    int shiftPriority(@Param("priorityStart") long priorityStart, @Param("offset") long offset);
}
