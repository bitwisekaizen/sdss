package com.bitwisekaizen.sdss.management.repository;

import com.bitwisekaizen.sdss.management.config.ApplicationConfig;
import com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntity;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntityBuilder.anAgentNodeAffinityEntity;
import static com.bitwisekaizen.sdss.management.entity.InitiatorIqnEntityBuilder.anUnsavedInitiatorIqnEntity;
import static com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntityBuilder.anUnsavedUniqueIscsiTargetEntity;
import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@TestPropertySource(locations = {"classpath:test.properties"})
public class AgentNodeAffinityRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AgentNodeAffinityRepository agentNodeAffinityRepository;

    @BeforeMethod
    @AfterMethod
    public void beforeMethod() {
        agentNodeAffinityRepository.deleteAll();
    }

    @Test
    public void canSaveAgentNodeAffinity() throws Exception {
        AgentNodeAffinityEntity entityToSave = anAgentNodeAffinityEntity().build();

        AgentNodeAffinityEntity entitySaved = agentNodeAffinityRepository.save(entityToSave);

        assertThat(entitySaved, notNullValue());
        AgentNodeAffinityEntity entityRetrieved = agentNodeAffinityRepository.findOne(entityToSave.getAffinityKey());
        assertThat(entityRetrieved, notNullValue());
        assertThat(entityRetrieved, not(is(entitySaved)));
        assertThat(reflectionEquals(entitySaved, entityToSave), is(true));
        assertThat(reflectionEquals(entityRetrieved, entityToSave), is(true));
    }

    @Test
    public void canDeleteAgentNodeAffinity() throws Exception {
        AgentNodeAffinityEntity entitySaved = agentNodeAffinityRepository.save(anAgentNodeAffinityEntity().build());

        agentNodeAffinityRepository.delete(entitySaved.getAffinityKey());

        AgentNodeAffinityEntity entityRetrieved = agentNodeAffinityRepository.findOne(entitySaved.getAffinityKey());
        assertThat(entityRetrieved, nullValue());
    }
}