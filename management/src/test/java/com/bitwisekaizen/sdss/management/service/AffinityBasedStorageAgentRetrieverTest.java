package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntity;
import com.bitwisekaizen.sdss.management.repository.AgentNodeAffinityRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntityBuilder.anAgentNodeAffinityEntity;
import static com.bitwisekaizen.sdss.management.service.StorageAgentBuilder.aStorageAgent;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.object.HasToString.hasToString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class AffinityBasedStorageAgentRetrieverTest {

    private AffinityBasedStorageAgentRetriever storageAgentRetriever;

    @Mock
    private StorageAgentFactory storageAgentFactory;

    @Mock
    private AgentNodeAffinityRepository agentNodeAffinityRepository;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        storageAgentRetriever = new AffinityBasedStorageAgentRetriever(false, storageAgentFactory,
                agentNodeAffinityRepository);
    }

    @Test
    public void factoryShouldReturnFakeAgentWhenIntegrationTestIsActive() throws Exception {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        AgentNodeAffinityEntity affinityEntity =
                anAgentNodeAffinityEntity().withAffinityKey(iscsiTarget.getAffinityKey()).build();
        when(agentNodeAffinityRepository.findOne(iscsiTarget.getAffinityKey())).thenReturn(affinityEntity);
        storageAgentRetriever = new AffinityBasedStorageAgentRetriever(true, storageAgentFactory, agentNodeAffinityRepository);

        StorageAgent bestStorageAgent = storageAgentRetriever.getStorageAgent(iscsiTarget);

        assertThat(bestStorageAgent.getAgentNode(), containsString(affinityEntity.getAgentNode()));
    }

    @Test
    public void factoryShouldReturnPreferredStorageAgent() throws Exception {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        AgentNodeAffinityEntity affinityEntity =
                anAgentNodeAffinityEntity().withAffinityKey(iscsiTarget.getAffinityKey()).build();
        when(agentNodeAffinityRepository.findOne(iscsiTarget.getAffinityKey())).thenReturn(affinityEntity);
        List<StorageAgent> storageAgents = Arrays.asList(aStorageAgent().build(), aStorageAgent().build(),
                aStorageAgent().withAgentNode(affinityEntity.getAgentNode()).build());
        when(storageAgentFactory.getHealthyStorageAgents()).thenReturn(storageAgents);

        StorageAgent bestStorageAgent = storageAgentRetriever.getStorageAgent(iscsiTarget);

        assertThat(bestStorageAgent.getAgentNode(), containsString(affinityEntity.getAgentNode()));
    }

    @Test
    public void factoryShouldThrowExceptionIfAffinityKeyIsNotFound() throws Exception {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        when(agentNodeAffinityRepository.findOne(iscsiTarget.getAffinityKey())).thenReturn(null);

        try {
            storageAgentRetriever.getStorageAgent(iscsiTarget);
            fail("Expected AgentNodeAffinityNotFoundException");
        } catch (AgentNodeAffinityNotFoundException e) {
        }
    }

    @Test
    public void factoryShouldThrowExceptionIfStorageAgentIsHealthyForAffinityKey() throws Exception {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        AgentNodeAffinityEntity affinityEntity =
                anAgentNodeAffinityEntity().withAffinityKey(iscsiTarget.getAffinityKey()).build();
        when(agentNodeAffinityRepository.findOne(iscsiTarget.getAffinityKey())).thenReturn(affinityEntity);
        List<StorageAgent> storageAgents = Arrays.asList(aStorageAgent().build(), aStorageAgent().build(),
                aStorageAgent().build());
        when(storageAgentFactory.getHealthyStorageAgents()).thenReturn(storageAgents);

        try {
            storageAgentRetriever.getStorageAgent(iscsiTarget);
            fail("Expected AgentNodeNotFoundException");
        } catch (AgentNodeNotFoundException e) {
        }
    }
}