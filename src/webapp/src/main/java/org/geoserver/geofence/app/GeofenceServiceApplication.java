package org.geoserver.geofence.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.bus.BusRefreshAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {BusAutoConfiguration.class, BusRefreshAutoConfiguration.class})
public class GeofenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeofenceServiceApplication.class, args);
    }
}
