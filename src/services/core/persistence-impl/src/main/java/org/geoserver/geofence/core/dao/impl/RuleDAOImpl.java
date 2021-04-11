/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import static org.geoserver.geofence.core.dao.impl.FilterUtils.addCriteria;
import static org.geoserver.geofence.core.dao.impl.FilterUtils.addPagingConstraints;
import static org.geoserver.geofence.core.dao.impl.FilterUtils.addStringCriteria;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.geoserver.geofence.core.dao.RuleDAO;
import org.geoserver.geofence.core.dao.RuleFilter;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.jpa.model.JPAIPAddressRange;
import org.geoserver.geofence.jpa.model.JPALayerDetails;
import org.geoserver.geofence.jpa.model.JPARule;
import org.geoserver.geofence.jpa.repository.LayerDetailsRepository;
import org.geoserver.geofence.jpa.repository.RuleLimitsRepository;
import org.geoserver.geofence.jpa.repository.RuleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class RuleDAOImpl extends PrioritizableDAOImpl<Rule, JPARule> implements RuleDAO {

    private final @Getter Class<JPARule> persistenceType = JPARule.class;

    private @Autowired @Getter RuleRepository repository;

    private @Autowired LayerDetailsRepository layerDetails;

    private @Autowired RuleLimitsRepository ruleLimits;

    protected @Override Function<JPARule, Rule> persistenceToModel() {
        return MAPPER::map;
    }

    protected @Override Function<Rule, JPARule> modelToPersistence() {
        return MAPPER::map;
    }

    /**
     * Override to check business rules:
     *
     * <ul>
     *   <li>All {@link LayerAttribute}s in {@link Rule#getLayerDetails()
     *       entity.getLayerDetails().getAttributes()} have {@link LayerAttribute#getAccess()
     *       access} set.
     * </ul>
     */
    @Override
    public Rule persist(Rule entity) {
        checkBusinessRules(entity);
        return super.persist(entity);
    }

    @Override
    public Rule merge(Rule entity) {
        checkBusinessRules(entity);
        JPARule curr = repository.find(entity.getId());

        if (curr.getLayerDetails() == null) {
            curr.setLayerDetails(MAPPER.map(entity.getLayerDetails()));
        } else if (entity.getLayerDetails() == null) {
            curr.setLayerDetails(null);
            layerDetails.removeById(entity.getId());
        } else { // neither is null, map using the attached jpa entity
            MAPPER.map(entity.getLayerDetails(), curr.getLayerDetails());
        }
        if (curr.getRuleLimits() == null) {
            curr.setRuleLimits(MAPPER.map(entity.getRuleLimits()));
        } else if (entity.getRuleLimits() == null) {
            curr.setRuleLimits(null);
            ruleLimits.removeById(entity.getId());
        } else { // neither is null, map using the attached jpa entity
            MAPPER.map(entity.getRuleLimits(), curr.getRuleLimits());
        }
        // not map the rule using the attached jpa entity, this mapper method is
        // configured not to deal with ruleLimits and layerDetails, handled above.
        MAPPER.map(entity, curr);
        JPARule merged = repository.merge(curr);
        return toEntity(merged);
    }

    private void checkBusinessRules(Rule entity) {
        LayerDetails details = entity.getLayerDetails();
        if (details != null) {
            Set<LayerAttribute> attributes = details.getAttributes();
            Set<String> uniqueNames = new HashSet<>();
            String duplicateNames =
                    attributes
                            .stream()
                            .map(LayerAttribute::getName)
                            .filter(n -> !uniqueNames.add(n))
                            .collect(Collectors.joining(","));
            if (!duplicateNames.isEmpty()) {
                throw new IllegalArgumentException("Duplicate attribute names: " + duplicateNames);
            }
            for (LayerAttribute attr : attributes) {
                if (attr.getAccess() == null) {
                    throw new NullPointerException(
                            "Null access type for attribute " + attr.getName() + " in " + details);
                }
            }
        }
    }

    @Override
    public List<Rule> search(RuleFilter filter) {
        List<JPARule> jpaRules = getRepository().search(filter);
        return toEntity(jpaRules);
    }

    @Override
    public List<Rule> findAllByService(String service) {
        List<JPARule> rules = getRepository().findAllByService(service);
        return toEntity(rules);
    }

    @Override
    public List<Rule> findAllByAddressRange(IPAddressRange addressRange) {
        JPAIPAddressRange jpaAddressRange = MAPPER.map(addressRange);
        List<JPARule> found = getRepository().findAllByAddressRange(jpaAddressRange);
        return toEntity(found);
    }

    @Override
    public long count(RuleFilter filter) {
        return getRepository().count(filter);
    }

    @Override
    public List<Rule> findAllByInstanceId(long instanceId) {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("instance.id", instanceId));
        return search(searchCriteria);
    }

    @Override
    public List<Rule> findAllByRole(String rolename) {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("rolename", rolename));
        return search(searchCriteria);
    }

    @Override
    public List<Rule> findAllByUser(String username) {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("username", username));
        return search(searchCriteria);
    }

    private List<Rule> search(Search searchCriteria) {
        RuleRepository repo = getRepository();
        List<JPARule> list = repo.search(searchCriteria);
        return toEntity(list);
    }

    @Override
    public List<Rule> findAll(RuleFilter filter, Integer page, Integer entries) {
        Search searchCriteria = buildSearch(page, entries, filter);
        return search(searchCriteria);
    }

    @Override
    public Set<String> getAllowedStyles(Long id) {
        JPALayerDetails found = layerDetails.find(id);
        if (found != null) {
            Set<String> styles = found.getAllowedStyles();

            if ((styles != null) && !Hibernate.isInitialized(styles)) {
                Hibernate.initialize(styles); // fetch the props
            }

            return styles;
        } else {
            throw new IllegalArgumentException("LayerDetails not found");
        }
    }

    @Override
    public void setAllowedStyles(Long id, Set<String> styles) {
        // TODO: (groldan) question: why this method in a DAO class sets a value and
        // does not save
        // it?
        JPALayerDetails found = layerDetails.find(id);
        if (found != null) {
            found.setAllowedStyles(styles);
        } else {
            throw new IllegalArgumentException("LayerDetails not found");
        }
    }

    protected Search buildSearch(Integer page, Integer entries, RuleFilter filter) {
        Search searchCriteria = buildRuleSearch(filter);
        addPagingConstraints(searchCriteria, page, entries);
        searchCriteria.addSortAsc("priority");
        return searchCriteria;
    }

    private Search buildRuleSearch(RuleFilter filter) {
        Search searchCriteria = new Search(Rule.class);

        if (filter != null) {
            addStringCriteria(searchCriteria, "username", filter.getUser());
            addStringCriteria(searchCriteria, "rolename", filter.getRole());
            addCriteria(searchCriteria, "instance", filter.getInstance());

            addStringCriteria(searchCriteria, "service", filter.getService()); // see class' javadoc
            addStringCriteria(searchCriteria, "request", filter.getRequest()); // see class' javadoc
            addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
            addStringCriteria(searchCriteria, "layer", filter.getLayer());
        }

        return searchCriteria;
    }
}
