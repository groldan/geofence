package org.geoserver.geofence.api.v2.it.support;

import org.geoserver.geofence.adminrules.service.AdminRuleAdminService;
import org.geoserver.geofence.api.v2.client.AdminRulesApi;
import org.geoserver.geofence.api.v2.client.ApiClient;
import org.geoserver.geofence.api.v2.client.RulesApi;
import org.geoserver.geofence.authorization.rules.RuleReaderServiceImpl;
import org.geoserver.geofence.config.api.v2.client.repository.ApiClientConfiguration;
import org.geoserver.geofence.config.api.v2.client.repository.RepositoryClientAdaptorsConfiguration;
import org.geoserver.geofence.config.domain.AdminRuleAdminServiceConfiguration;
import org.geoserver.geofence.config.domain.RuleAdminServiceConfiguration;
import org.geoserver.geofence.config.domain.UserAdminServiceConfiguration;
import org.geoserver.geofence.rules.service.RuleAdminService;
import org.geoserver.geofence.users.service.DefaultUserResolver;
import org.geoserver.geofence.users.service.UserAdminService;
import org.geoserver.geofence.users.service.UserGroupAdminService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;

import java.util.Set;
import java.util.function.Function;

public class ClientContextSupport {

    private AnnotationConfigApplicationContext clientContext;
    private boolean logRequests;
    private int serverPort;

    public ClientContextSupport serverPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public void close() {
        if (clientContext != null) clientContext.close();
    }

    public ClientContextSupport setUp() {

        String basePath = String.format("http://localhost:%d/geofence/rest/v2", serverPort);
        String username = "admin";
        String password = "auth-not-yet-implemented";

        ConfigurableEnvironment clientEnv =
                new MockEnvironment() //
                        .withProperty("geofence.client.basePath", basePath)
                        .withProperty("geofence.client.username", username)
                        .withProperty("geofence.client.password", password)
                        .withProperty("geofence.client.debug", String.valueOf(logRequests));

        clientContext = new AnnotationConfigApplicationContext();
        clientContext.setEnvironment(clientEnv);

        clientContext.register(
                // repositories from geofence-api-client
                ApiClientConfiguration.class,
                RepositoryClientAdaptorsConfiguration.class,
                // services from geofence-domain-spring-integration
                RuleAdminServiceConfiguration.class,
                AdminRuleAdminServiceConfiguration.class,
                UserAdminServiceConfiguration.class);
        clientContext.refresh();
        return this;
    }

    /**
     * Enables/disables http client request/response logging, but breaks exception dispatching for
     * error codes, throwing ResourceAccessException (wrapping an IOException) instead of an
     * HttpClientErrorException subclass as it tries to read the response body without checking if
     * there's one, see https://jira.spring.io/browse/SPR-8713?
     */
    public ClientContextSupport log(boolean logRequests) {
        this.logRequests = logRequests;
        if (null != clientContext && clientContext.isActive()) {
            clientContext.getBean(ApiClient.class).setDebugging(logRequests);
        }
        return this;
    }

    public RulesApi getRulesApiClient() {
        return clientContext.getBean(org.geoserver.geofence.api.v2.client.RulesApi.class);
    }

    public AdminRulesApi getAdminRulesApiClient() {
        return clientContext.getBean(org.geoserver.geofence.api.v2.client.AdminRulesApi.class);
    }

    public RuleAdminService getRuleAdminServiceClient() {
        return clientContext.getBean(RuleAdminService.class);
    }

    public AdminRuleAdminService getAdminRuleAdminServiceClient() {
        return clientContext.getBean(AdminRuleAdminService.class);
    }

    public RuleReaderServiceImpl getRuleReaderServiceImpl() {
        AdminRuleAdminService adminRuleService = getAdminRuleAdminServiceClient();
        RuleAdminService ruleService = getRuleAdminServiceClient();
        Function<String, Set<String>> userResolver =
                new DefaultUserResolver(getUserAdminServiceClient());
        return new RuleReaderServiceImpl(adminRuleService, ruleService, userResolver);
        // return clientContext.getBean(RuleReaderServiceImpl.class);
    }

    public UserAdminService getUserAdminServiceClient() {
        return clientContext.getBean(UserAdminService.class);
    }

    public UserGroupAdminService getGroupAdminServiceClient() {
        return clientContext.getBean(UserGroupAdminService.class);
    }
}
