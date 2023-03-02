package org.geoserver.geofence.autoconfigure.api;

import org.geoserver.geofence.config.api.UserManagementApiConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(UserManagementApiConfiguration.class)
public class UserManagementApiAutoConfiguration {}
