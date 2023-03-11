package org.geoserver.geofence.filter;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.geoserver.geofence.adminrules.model.AdminGrantType;
import org.geoserver.geofence.adminrules.model.AdminRule;
import org.geoserver.geofence.adminrules.model.AdminRuleIdentifier;
import org.geoserver.geofence.filter.predicate.FilterType;
import org.geoserver.geofence.filter.predicate.IPAddressRangeFilter;
import org.geoserver.geofence.filter.predicate.IdNameFilter;
import org.geoserver.geofence.filter.predicate.InSetPredicate;
import org.geoserver.geofence.filter.predicate.SpecialFilterType;
import org.geoserver.geofence.filter.predicate.TextFilter;

import java.util.Set;

// REVISIT: shouldn't extend RuleFilter, it has only a subset of its properties
public class AdminRuleFilter extends Filter implements Cloneable {

    private @Getter @Setter AdminGrantType grantType;

    private final @Getter TextFilter user;
    private final @Getter InSetPredicate<String> role;
    private final @Getter IdNameFilter instance;
    private final @Getter IPAddressRangeFilter sourceAddress;
    private final @Getter TextFilter workspace;

    public AdminRuleFilter() {
        this(SpecialFilterType.DEFAULT);
    }

    /**
     * Creates a RuleFilter by setting all fields filtering either to ANY or DEFAULT. <br>
     * If no other field is set, you will get
     *
     * <UL>
     *   <LI>with <B>ANY</B>, all Rules will be returned
     *   <LI>with <B>DEFAULT</B>, only the default Rule will be returned
     * </UL>
     */
    public AdminRuleFilter(SpecialFilterType type) {
        FilterType ft = type.getRelatedType();

        user = new TextFilter(ft);
        role = new InSetPredicate<String>(ft);
        instance = new IdNameFilter(ft);
        sourceAddress = new IPAddressRangeFilter(ft);
        workspace = new TextFilter(ft);
    }

    public AdminRuleFilter setIncludeDefault(boolean includeDefault) {
        user.setIncludeDefault(includeDefault);
        role.setIncludeDefault(includeDefault);
        instance.setIncludeDefault(includeDefault);
        sourceAddress.setIncludeDefault(includeDefault);
        workspace.setIncludeDefault(includeDefault);
        return this;
    }

    public AdminRuleFilter(AdminRuleFilter source) {
        grantType = source.getGrantType();
        try {
            user = source.user.clone();
            role = source.role.clone();
            instance = source.instance.clone();
            sourceAddress = source.sourceAddress.clone();
            workspace = source.workspace.clone();
        } catch (CloneNotSupportedException ex) {
            // Should not happen
            throw new UnknownError("Clone error - should not happen");
        }
    }

    private AdminRuleFilter(RuleFilter source) {
        try {
            user = source.getUser().clone();
            role = source.getRole().clone();
            instance = source.getInstance().clone();
            sourceAddress = source.getSourceAddress().clone();
            workspace = source.getWorkspace().clone();
        } catch (CloneNotSupportedException ex) {
            // Should not happen
            throw new UnknownError("Clone error - should not happen");
        }
    }

    @Override
    public AdminRuleFilter clone() {
        return new AdminRuleFilter(this);
    }

    public static AdminRuleFilter of(RuleFilter ruleFilter) {
        return new AdminRuleFilter(ruleFilter);
    }

    public static AdminRuleFilter any() {
        return AdminRuleFilter.of(RuleFilter.any());
    }

    public AdminRuleFilter setUser(String name) {
        if (name == null) throw new NullPointerException();
        user.setText(name);
        return this;
    }

    public AdminRuleFilter setUser(SpecialFilterType type) {
        user.setType(type);
        return this;
    }

    public AdminRuleFilter setRole(Set<String> roles) {
        if (roles == null) throw new NullPointerException();
        role.setValues(roles);
        return this;
    }

    public AdminRuleFilter setRole(String name) {
        if (name == null) throw new NullPointerException();
        role.setText(name);
        return this;
    }

    public AdminRuleFilter setRole(SpecialFilterType type) {
        role.setType(type);
        return this;
    }

    public AdminRuleFilter setInstance(Long id) {
        instance.setId(id);
        return this;
    }

    public AdminRuleFilter setInstance(String name) {
        instance.setName(name);
        return this;
    }

    public AdminRuleFilter setInstance(SpecialFilterType type) {
        instance.setType(type);
        return this;
    }

    public AdminRuleFilter setSourceAddress(String dotted) {
        sourceAddress.setText(dotted);
        return this;
    }

    public AdminRuleFilter setSourceAddress(SpecialFilterType type) {
        sourceAddress.setType(type);
        return this;
    }

    public AdminRuleFilter setWorkspace(String name) {
        workspace.setText(name);
        return this;
    }

    public AdminRuleFilter setWorkspace(SpecialFilterType type) {
        workspace.setType(type);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdminRuleFilter other = (AdminRuleFilter) obj;
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        if (this.role != other.role && (this.role == null || !this.role.equals(other.role))) {
            return false;
        }
        if (this.instance != other.instance
                && (this.instance == null || !this.instance.equals(other.instance))) {
            return false;
        }
        if (this.workspace != other.workspace
                && (this.workspace == null || !this.workspace.equals(other.workspace))) {
            return false;
        }
        // NOTE: ipaddress not in equals() bc it is not used for caching
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 37 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 37 * hash + (this.instance != null ? this.instance.hashCode() : 0);
        hash = 37 * hash + (this.sourceAddress != null ? this.sourceAddress.hashCode() : 0);
        hash = 37 * hash + (this.workspace != null ? this.workspace.hashCode() : 0);
        // NOTE: ipaddress not in hashcode bc it is not used for caching
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        sb.append("user:").append(user);
        sb.append(" role:").append(role);
        sb.append(" inst:").append(instance);
        sb.append(" ip:").append(sourceAddress);
        sb.append(" ws:").append(workspace);
        sb.append(']');

        return sb.toString();
    }

    public boolean matches(@NonNull AdminRule rule) {
        AdminRuleIdentifier idf = rule.getIdentifier();
        return getInstance().matches(idf.getInstanceName())
                        && getRole().matches(idf.getRolename())
                        && getSourceAddress().matches(idf.getAddressRange())
                        && getUser().matches(idf.getUsername())
                        && getWorkspace().matches(idf.getWorkspace())
                        && grantType == null
                ? true
                : grantType.equals(rule.getAccess());
    }
}
