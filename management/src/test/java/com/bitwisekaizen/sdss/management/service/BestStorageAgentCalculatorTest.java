package com.bitwisekaizen.sdss.management.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static com.bitwisekaizen.sdss.management.service.StorageAgentBuilder.aStorageAgent;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class BestStorageAgentCalculatorTest {

    private BestStorageAgentCalculator bestStorageAgentCalculator;

    @Mock
    private StorageAgentFactory storageAgentFactory;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        bestStorageAgentCalculator = new BestStorageAgentCalculator(false, storageAgentFactory);
    }

    @Test
    public void factoryShouldReturnFakeAgentWhenIntegrationTestIsActive() throws Exception {
        bestStorageAgentCalculator = new BestStorageAgentCalculator(true, storageAgentFactory);

        StorageAgent bestStorageAgent = bestStorageAgentCalculator.getBestStorageAgent(anIscsiTarget().build());
        assertThat(bestStorageAgent.getServerUrl(), is("http://fake.example.com"));
    }

    @Test
    public void factoryShouldReturnAnyStorageAgent() throws Exception {
        List<StorageAgent> storageAgents = Arrays.asList(aStorageAgent().build());
        when(storageAgentFactory.getHealthyStorageAgents()).thenReturn(storageAgents);

        StorageAgent bestStorageAgent = bestStorageAgentCalculator.getBestStorageAgent(anIscsiTarget().build());

        assertThat(storageAgents, hasItem(bestStorageAgent));
    }
}