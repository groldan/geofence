/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import java.util.List;
import java.util.Set;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.Rule;

/**
 * Public interface to define operations on Rule
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RuleDAO extends PrioritizableDAO<Rule> {

    long count(RuleFilter filter);

    List<Rule> findAllByService(String string);

    List<Rule> findAllByAddressRange(IPAddressRange addressRange);

    List<Rule> search(RuleFilter filter);

    List<Rule> findAllByInstanceId(long instanceId);

    List<Rule> findAllByRole(String rolename);

    List<Rule> findAllByUser(String username);

    List<Rule> findAll(RuleFilter filter, Integer page, Integer entries);

    Set<String> getAllowedStyles(Long id);

    void setAllowedStyles(Long id, Set<String> styles);
}
