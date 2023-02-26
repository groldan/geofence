package org.geoserver.geofence.autoconfigure.rest;

import org.geoserver.geofence.config.rest.RulesApiConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(RulesApiConfiguration.class)
public class RestApiAutoConfiguration {}
