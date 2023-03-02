package org.geoserver.geofence.api.v2.it.rules;

import org.geoserver.geofence.api.v2.it.support.ClientContextSupport;
import org.geoserver.geofence.api.v2.it.support.IntegrationTestsApplication;
import org.geoserver.geofence.jpa.repository.JpaRuleRepository;
import org.geoserver.geofence.rules.service.AbstractRuleAdminServiceIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
            "geofence.jpa.show-sql=false",
            "geofence.jpa.properties.hibernate.hbm2ddl.auto=create",
            "geofence.datasource.url=jdbc:h2:mem:geofence"
        },
        classes = {IntegrationTestsApplication.class})
class RuleAdminServiceApiIT extends AbstractRuleAdminServiceIT {

    private @Autowired ApplicationContext serverContext;
    private @LocalServerPort int serverPort;

    private ClientContextSupport clientContext;

    @BeforeEach
    void setUp() throws Exception {
        clientContext =
                new ClientContextSupport()
                        // logging breaks client exception handling, only enable if need to see the
                        // request/response bodies
                        .log(false)
                        .serverPort(serverPort)
                        .setUp();
        super.ruleAdminService = clientContext.getRuleAdminServiceClient();

        JpaRuleRepository jparepo = serverContext.getBean(JpaRuleRepository.class);
        jparepo.deleteAll();
    }

    @AfterEach
    void tearDown() {
        clientContext.close();
    }
}
