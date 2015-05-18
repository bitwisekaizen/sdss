package com.bitwisekaizen.sdss.agent.acceptance;

import com.bitwisekaizen.sdss.agent.config.ApplicationConfig;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static java.util.concurrent.TimeUnit.MINUTES;


@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebAppConfiguration
@IntegrationTest({"server.port:0"})
// See http://stackoverflow.com/questions/25537436/acceptance-testing-a-spring-boot-web-app-with-testng
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@TestPropertySource(locations = {"classpath:test.properties"})
public class AbstractAcceptanceTest extends AbstractTestNGSpringContextTests {

    private static final boolean TRACE_HTTP = false;
    private static final boolean PRINT_ENTITY = false;

    @Value("${local.server.port}")
    private int serverPort;

    protected StorageAgentClient storageAgentClient;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        storageAgentClient = new StorageAgentClient(createClient());
    }

    protected WebTarget createClient() {
        String serverBaseUrl = "http://127.0.0.1:" + serverPort;

        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        // https://java.net/jira/browse/JERSEY-2373
        clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, (int) MINUTES.toMillis(5));
        clientConfig.property(ClientProperties.READ_TIMEOUT, (int) MINUTES.toMillis(30));
        Client client = ClientBuilder.newBuilder().newClient(clientConfig).register(JacksonFeature.class);

        if (TRACE_HTTP) {
            LoggingFilter loggingFilter = new LoggingFilter(
                    java.util.logging.Logger.getLogger(AbstractAcceptanceTest.class.getName()), PRINT_ENTITY);
            client = client.register(loggingFilter);
        }

        return client.target(serverBaseUrl);
    }
}