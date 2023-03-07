package org.geoserver.geofence.filter.predicate;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public abstract class RulePredicate<T> implements Serializable, Cloneable {

    private static final long serialVersionUID = 6565336016075974626L;

    protected FilterType type;
    /** Only used in TYPE_NAME, tells if also default Rules should be matched. */
    protected boolean includeDefault = true;

    public RulePredicate(FilterType type) {
        this.type = type;
    }

    public RulePredicate(FilterType type, boolean includeDefault) {
        this.type = type;
        this.includeDefault = includeDefault;
    }

    public void setType(SpecialFilterType type) {
        this.type = type.getRelatedType();
    }

    public FilterType getType() {
        return type;
    }

    public boolean isIncludeDefault() {
        return includeDefault;
    }

    public void setIncludeDefault(boolean includeDefault) {
        this.includeDefault = includeDefault;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RulePredicate<T> clone() throws CloneNotSupportedException {
        return (RulePredicate<T>) super.clone();
    }

    public abstract boolean matches(T value);
}
