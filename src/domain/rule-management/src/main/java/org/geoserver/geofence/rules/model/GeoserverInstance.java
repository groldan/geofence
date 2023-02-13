/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.model;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.util.Date;

/**
 * A GeoServer instance.
 *
 * <p><B>TODO</B>: how does a GeoServer instance identify itself?
 */
@Value
@With
@Builder
@ToString(exclude = {"password"})
public class GeoserverInstance {

    private Long id;

    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The date creation. */
    private Date dateCreation;

    /** The host. */
    private String baseURL;

    private String username;

    private String password;
}
