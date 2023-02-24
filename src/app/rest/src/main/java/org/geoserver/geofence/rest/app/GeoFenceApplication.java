package org.geoserver.geofence.rest.app;

import org.geoserver.geofence.springdoc.SpringDocConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SpringDocConfiguration.class)
public class GeoFenceApplication {

    public static void main(String... args) {
        try {
            SpringApplication.run(GeoFenceApplication.class, args);
        } catch (RuntimeException e) {
            System.exit(-1);
        }
    }
}
