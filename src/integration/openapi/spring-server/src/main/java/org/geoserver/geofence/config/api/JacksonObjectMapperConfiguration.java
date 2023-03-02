package org.geoserver.geofence.config.api;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class JacksonObjectMapperConfiguration {

    @Bean
    JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Bean
    JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }
}
