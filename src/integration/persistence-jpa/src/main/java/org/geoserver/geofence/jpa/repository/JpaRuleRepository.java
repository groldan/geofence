package org.geoserver.geofence.jpa.repository;

import org.geoserver.geofence.jpa.model.Rule;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@Transactional(value = TxType.SUPPORTS)
public interface JpaRuleRepository
        extends JpaRepository<Rule, Long>, QuerydslPredicateExecutor<Rule> {

    Sort naturalOrder = Sort.by("identifier.instance.id", "priority");

    boolean deleteById(long id);

    @Query("SELECT r FROM Rule r ORDER BY identifier.instance.id, priority")
    List<Rule> findAllNaturalOrder();

    default List<Rule> findAllNaturalOrder(com.querydsl.core.types.Predicate predicate) {

        Iterable<Rule> matches = findAll(predicate, naturalOrder);

        if (matches instanceof List) return (List<Rule>) matches;

        return StreamSupport.stream(matches.spliterator(), false).collect(Collectors.toList());
    }

    @Query("SELECT r FROM Rule r ORDER BY identifier.instance.id, priority")
    Page<Rule> findAllNaturalOrder(Pageable pageable);

    default Page<Rule> findAllNaturalOrder(
            com.querydsl.core.types.Predicate predicate, Pageable pageable) {

        if (pageable.isPaged()) {
            PageRequest sortingPageRequest =
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), naturalOrder);
            return findAll(predicate, sortingPageRequest);
        }
        Iterable<Rule> matches = findAll(predicate, naturalOrder);
        List<Rule> contents;
        if (matches instanceof List) contents = (List<Rule>) matches;
        else
            contents =
                    StreamSupport.stream(matches.spliterator(), false).collect(Collectors.toList());
        return new PageImpl<>(contents);
    }

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Rule SET priority = priority + :offset WHERE priority >= :priorityStart")
    int shiftPriority(@Param("priorityStart") long priorityStart, @Param("offset") long offset);
}
