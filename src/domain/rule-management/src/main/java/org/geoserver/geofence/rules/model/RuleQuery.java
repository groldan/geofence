package org.geoserver.geofence.rules.model;

import static java.util.Optional.ofNullable;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

@Data
@Accessors(chain = true)
public class RuleQuery<F extends RuleFilter> {

    private F filter;

    private Integer pageNumber;
    private Integer pageSize;

    /** If present, return the rules with priority greater or equal to this value */
    private Long priorityOffset;

    public static <RF extends RuleFilter> RuleQuery<RF> of(RF filter) {
        return new RuleQuery<RF>().setFilter(filter);
    }

    public static <RF extends RuleFilter> RuleQuery<RF> of() {
        return new RuleQuery<RF>();
    }

    public Optional<F> getFilter() {
        return ofNullable(filter);
    }

    public OptionalInt pageNumber() {
        return pageNumber == null ? OptionalInt.empty() : OptionalInt.of(pageNumber);
    }

    public OptionalInt pageSize() {
        return pageSize == null ? OptionalInt.empty() : OptionalInt.of(pageSize);
    }

    public OptionalLong getPriorityOffset() {
        return priorityOffset == null ? OptionalLong.empty() : OptionalLong.of(priorityOffset);
    }
}
