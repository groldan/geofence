/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.model;

/**
 * The Enum AccessType.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public enum JPAAccessType {

    /** No access to the resource. */
    NONE,

    /** Read only access. */
    READONLY,

    /** Full access. */
    READWRITE
}
