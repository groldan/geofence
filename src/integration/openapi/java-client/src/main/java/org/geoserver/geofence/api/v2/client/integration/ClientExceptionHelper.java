package org.geoserver.geofence.api.v2.client.integration;

import org.springframework.web.client.HttpClientErrorException;

class ClientExceptionHelper {

    static String reason(HttpClientErrorException e) {
        return reason(e, e.getMessage());
    }

    static String reason(HttpClientErrorException e, String defaultValue) {
        String reason = e.getResponseHeaders().getFirst("X-Reason");
        return reason == null ? defaultValue : reason;
    }
}
