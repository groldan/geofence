package org.geoserver.geofence.autoconfigure.rest;

import org.geoserver.geofence.rest.config.RulesApiConfiguration;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(RulesApiConfiguration.class)
public class RestApiAutoConfiguration {

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
    //    @Bean
    //    public ObjectMapper objectMapper() {
    //        ObjectMapper mapper = new ObjectMapper();
    //        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    //        mapper.registerModule(new JsonNullableModule());
    //        return mapper;
    //    }
}
