package org.geoserver.geofence.config.api.v2.client.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.geoserver.geofence.api.v2.client.AdminRulesApi;
import org.geoserver.geofence.api.v2.client.ApiClient;
import org.geoserver.geofence.api.v2.client.RulesApi;
import org.geoserver.geofence.api.v2.client.UserGroupsApi;
import org.geoserver.geofence.api.v2.client.UsersApi;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Include this configuration to contribute an {@link
 * org.geoserver.geofence.api.v2.client.ApiClient}
 *
 * @since 4.0
 */
@Configuration(proxyBeanMethods = false)
public class ApiClientConfiguration {

    @Bean
    ApiClient geofenceApiClient(
            PropertyResolver env,
            @Qualifier("geofenceClientRestTemplate") RestTemplate restTemplate) {

        String basePath = env.getProperty("geofence.client.basePath");
        String username = env.getProperty("geofence.client.username");
        String password = env.getProperty("geofence.client.password");
        boolean debugging = Boolean.valueOf(env.getProperty("geofence.client.debug", "false"));

        ApiClient apiClient = new ApiClient(restTemplate);
        if (null == basePath) {
            throw new IllegalStateException(
                    "GeoFence target URL not provided through config property geofence.client.basePath");
        }
        apiClient.setBasePath(basePath);

        apiClient.setDebugging(debugging);
        apiClient.setUsername(username);
        apiClient.setPassword(password);
        return apiClient;
    }

    @Bean
    RulesApi geofenceRulesApiClient(ApiClient client) {
        return new RulesApi(client);
    }

    @Bean
    AdminRulesApi geofenceAdminRulesApiClient(ApiClient client) {
        return new AdminRulesApi(client);
    }

    @Bean
    UsersApi geofenceUsersApiClient(ApiClient client) {
        return new UsersApi(client);
    }

    @Bean
    UserGroupsApi geofenceUserGroupsApiClient(ApiClient client) {
        return new UserGroupsApi(client);
    }

    @Bean
    RestTemplate geofenceClientRestTemplate(
            @Qualifier("geofenceClientObjectMapper") ObjectMapper objectMapper) {

        // Use Apache HttpComponents HttpClient, otherwise SimpleClientHttpRequestFactory fails on
        // PATCH requests
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        // This allows us to read the response more than once - Necessary for debugging
        restTemplate.setRequestFactory(
                new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));

        // disable default URL encoding
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        List<HttpMessageConverter<?>> messageConverters =
                restTemplate.getMessageConverters().stream()
                        .filter(m -> !(MappingJackson2HttpMessageConverter.class.isInstance(m)))
                        .collect(Collectors.toCollection(ArrayList::new));

        messageConverters.add(0, new MappingJackson2HttpMessageConverter(objectMapper));
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

    @Bean
    ObjectMapper geofenceClientObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JsonNullableModule());
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
