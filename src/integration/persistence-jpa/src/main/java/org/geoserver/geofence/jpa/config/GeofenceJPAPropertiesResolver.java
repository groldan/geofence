package org.geoserver.geofence.jpa.config;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

class GeofenceJPAPropertiesResolver {

    private Map<String, String> geofenceProperties;

    public GeofenceJPAPropertiesResolver(ConfigurableEnvironment env) {
        this.geofenceProperties = properties(env, "geofence.");
    }

    public Map<String, String> getMap(String propName) {
        final String prefix = propName.endsWith(".") ? propName : propName + ".";

        return geofenceProperties.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(
                        Collectors.toMap(
                                e -> e.getKey().substring(prefix.length()), e -> e.getValue()));
    }

    public String get(String key) {
        return geofenceProperties.get(key);
    }

    public String get(String key, String defaultValue) {
        return geofenceProperties.getOrDefault(key, defaultValue);
    }

    /**
     * Used to retrieve all {@code geofence.*} config properties, without using any spring-boot
     * utility such as automatic value conversion, so this config can be used both with and without
     * boot
     */
    private Map<String, String> properties(ConfigurableEnvironment env, String prefix) {

        return env.getPropertySources().stream()
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .map(EnumerablePropertySource::getPropertyNames)
                .flatMap(Arrays::stream)
                .filter(propName -> propName.startsWith(prefix))
                // use env to get the value property so it's its final value in case there are
                // multiple property sources with overriding property names
                .sorted()
                .distinct()
                .collect(Collectors.toMap(p -> p.substring(prefix.length()), env::getProperty));
    }
}
