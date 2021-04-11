/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

/** @author ETj (etj at geo-solutions.it) */
public interface Prioritizable extends Identifiable {

    long getPriority();

    void setPriority(long priority);
}
