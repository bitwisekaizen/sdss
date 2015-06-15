package com.bitwisekaizen.sdss.management.service;

import com.google.common.collect.Lists;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.async.ConsulResponseCallback;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.orbitz.consul.option.QueryOptionsBuilder.builder;

/**
 * Factory to create the storage agent client.
 */
public class StorageAgentFactory {
    final static Logger logger = LoggerFactory.getLogger(StorageAgentFactory.class);
    private final static String SERVICE_NAME = "sdss-agent";

    private String consulHost;
    private List<StorageAgent> agents = new ArrayList<>();

    public StorageAgentFactory(@Value("${app.consul.host}") String consulHost) {
        this.consulHost = consulHost;
    }

    @PostConstruct
    public void start() {
        Consul consul = Consul.newClient(consulHost, Consul.DEFAULT_HTTP_PORT);
        final HealthClient healthClient = consul.healthClient();

        ConsulResponseCallback<List<ServiceHealth>> callback = new ConsulResponseCallback<List<ServiceHealth>>() {

            int index;

            @Override
            public void onComplete(ConsulResponse<List<ServiceHealth>> services) {
                logger.info("Receive updates of size: " + services.getResponse().size());
                synchronized (agents) {
                    agents.clear();
                    for (ServiceHealth health : services.getResponse()) {
                        agents.add(convertToStorageAgent(health));
                    }
                }

                index = (int) services.getIndex();

                // blocking request with new index
                healthClient.getHealthyServiceInstances(SERVICE_NAME, builder().blockMinutes(5, index).build(), this);
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.error("Unable contact consul server ", throwable);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                healthClient.getHealthyServiceInstances(SERVICE_NAME, builder().blockMinutes(5, index).build(), this);
            }
        };

        healthClient.getHealthyServiceInstances(SERVICE_NAME, builder().blockMinutes(1, 0).build(), callback);
    }

    /**
     * Get all healthy storage agent nodes.
     *
     * @return all healthy storage agent nodes.
     */
    public List<StorageAgent> getHealthyStorageAgents() {
        synchronized (agents) {
            return Lists.newArrayList(agents);
        }
    }

    private StorageAgent convertToStorageAgent(ServiceHealth serviceHealth) {
        String host = serviceHealth.getNode().getAddress();
        int port = serviceHealth.getService().getPort();
        return new StorageAgent("http://" + host + ":" + port);
    }
}
