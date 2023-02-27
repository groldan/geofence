package org.geoserver.geofence.autoconfigure.api;

import org.geoserver.geofence.config.api.RulesApiConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(RulesApiConfiguration.class)
public class RestApiAutoConfiguration {}
