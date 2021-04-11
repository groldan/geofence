/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.*;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geoserver.geofence.jpa.model.JPAUserGroup;
import org.junit.Test;

/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class UserGroupDAOLdapImplTest extends BaseDAOTest {

    @Test
    public void testFindAll() {
        List<JPAUserGroup> groups = userGroupDAO.findAll();
        assertTrue("No groups", groups.size() > 0);
        JPAUserGroup group = groups.get(0);
        assertTrue("Empty group name", group.getName().length() > 0);

        Set<String> expected =
                new HashSet<>(
                        Arrays.asList(
                                new String[] {
                                    "adminGroup", "parent", "other", "otherGroup", "destination"
                                }));
        Set<String> found = new HashSet<>();
        for (JPAUserGroup g : groups) {
            LOGGER.debug("Found group " + g);
            found.add(g.getName());
        }
        assertEquals("Bad group set found", expected, found);
    }

    @Test
    public void testFind() {
        JPAUserGroup group = userGroupDAO.find(1l);
        assertNull("Find by id should be disabled in LDAP", group);
    }

    @Test
    public void testSearch() {
        Search search = new Search();
        search.addFilter(new Filter("groupname", "adminGroup"));

        List<JPAUserGroup> groups = userGroupDAO.search(search);
        assertTrue(groups.size() == 1);
        JPAUserGroup group = groups.get(0);
        assertEquals("adminGroup", group.getName());
    }

    @Test
    public void testCount() {
        assertEquals(5, userGroupDAO.count(new Search()));
    }

    @Test
    public void testSearchPagination() {
        Search search = new Search();
        List<JPAUserGroup> groups = userGroupDAO.search(search);
        assertEquals(5, groups.size());

        search.setPage(0);
        search.setMaxResults(2);
        groups = userGroupDAO.search(search);
        assertEquals(2, groups.size());

        search.setPage(1);
        search.setMaxResults(2);
        groups = userGroupDAO.search(search);
        assertEquals(2, groups.size());

        search.setPage(2);
        search.setMaxResults(2);
        groups = userGroupDAO.search(search);
        assertEquals(1, groups.size());
    }
}
