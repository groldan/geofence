/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.exception;

/** @author ETj (etj at geo-solutions.it) */
public class BadRequestServiceEx extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    private String message;

    public BadRequestServiceEx(String message) {
        super(Response.Status.BAD_REQUEST);
        this.message = message;
    }

    public BadRequestServiceEx(String message, Throwable cause) {
        super(Response.Status.BAD_REQUEST, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
