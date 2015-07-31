package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import com.bitwisekaizen.sdss.management.config.InMemoryStorageAgentClient;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Factory to create the storage agent client.
 */
@Component
public class StorageAgentClientFactory {

    private static Map<String, StorageAgentClient> storageAgentClients = new HashMap<>();
    private final boolean runningIntegrationTest;
    private final AffinityBasedStorageAgentRetriever storageAgentRetriever;

    @Autowired
    public StorageAgentClientFactory(
            @Value("${app.running.integration.test}") boolean runningIntegrationTest,
            AffinityBasedStorageAgentRetriever storageAgentRetriever) {

        this.runningIntegrationTest = runningIntegrationTest;
        this.storageAgentRetriever = storageAgentRetriever;
    }

    /**
     * Get the "best" storage agent that can be used to create the iscsi target.
     *
     * @param iscsiTarget iscsi target to create.
     * @return "best" storage agent that can be used to create the iscsi target.
     */
    public synchronized StorageAgentClient getBestStorageAgent(IscsiTarget iscsiTarget) {
        StorageAgent bestStorageAgent = storageAgentRetriever.getStorageAgent(iscsiTarget);

        String serverUrl = bestStorageAgent.getServerUrl();

        StorageAgentClient storageAgentClient = storageAgentClients.get(serverUrl);
        if (storageAgentClient != null) {
            return storageAgentClient;
        }

        WebTarget client = createClient(serverUrl);
        if (runningIntegrationTest) {
            storageAgentClient = new InMemoryStorageAgentClient(client);
        } else {
            storageAgentClient = new StorageAgentClient(client);
        }

        storageAgentClients.put(storageAgentClient.getStorageAgentUrl(), storageAgentClient);

        return storageAgentClient;
    }

    private WebTarget createClient(String serverUrl) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        // https://java.net/jira/browse/JERSEY-2373
        clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, (int) MINUTES.toMillis(5));
        clientConfig.property(ClientProperties.READ_TIMEOUT, (int) MINUTES.toMillis(30));
        Client client = ClientBuilder.newBuilder().newClient(clientConfig).register(JacksonFeature.class);

        return client.target(serverUrl);
    }
}
