package org.geoserver.geofence.rest.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeoFenceApplication {

    public static void main(String... args) {
        try {
            SpringApplication.run(GeoFenceApplication.class, args);
        } catch (RuntimeException e) {
            System.exit(-1);
        }
    }
}
