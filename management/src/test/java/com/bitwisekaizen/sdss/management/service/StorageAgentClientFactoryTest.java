package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import com.bitwisekaizen.sdss.management.config.InMemoryStorageAgentClient;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static com.bitwisekaizen.sdss.management.service.StorageAgentBuilder.aStorageAgent;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class StorageAgentClientFactoryTest {

    @Mock
    private BestStorageAgentCalculator bestStorageAgentCalculator;
    private StorageAgentClientFactory storageAgentClientFactory;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        storageAgentClientFactory = new StorageAgentClientFactory(true, bestStorageAgentCalculator);
    }

    @Test
    public void factoryShouldReturnInMemoryClientForTesting() throws Exception {
        StorageAgentClientFactory storageAgentClientFactory =
                new StorageAgentClientFactory(true, bestStorageAgentCalculator);

        IscsiTarget iscsiTarget = anIscsiTarget().build();
        when(bestStorageAgentCalculator.getBestStorageAgent(iscsiTarget)).thenReturn(aStorageAgent().build());

        StorageAgentClient bestStorageAgent = storageAgentClientFactory.getBestStorageAgent(iscsiTarget);

        assertThat(bestStorageAgent, instanceOf(InMemoryStorageAgentClient.class));
    }

    @Test
    public void factoryShouldCacheStorageClients() throws Exception {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        StorageAgent storageAgentWithFirstUrl = aStorageAgent().build();
        StorageAgent storageAgentWithSecondUrl = aStorageAgent().build();
        when(bestStorageAgentCalculator.getBestStorageAgent(iscsiTarget)).thenReturn(storageAgentWithFirstUrl,
                storageAgentWithFirstUrl, storageAgentWithSecondUrl);

        StorageAgentClient clientForFirstUrlFirstCall = storageAgentClientFactory.getBestStorageAgent(iscsiTarget);
        StorageAgentClient clientForFirstUrlSecondCall = storageAgentClientFactory.getBestStorageAgent(iscsiTarget);
        StorageAgentClient clientForSecondUrl = storageAgentClientFactory.getBestStorageAgent(iscsiTarget);

        assertThat(clientForFirstUrlFirstCall, is(clientForFirstUrlSecondCall));
        assertThat(clientForSecondUrl, is(not(clientForFirstUrlSecondCall)));
    }
}