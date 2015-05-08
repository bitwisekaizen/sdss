package com.bitwisekaizen.sdss.management.repository;

import com.bitwisekaizen.sdss.management.config.ApplicationConfig;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.bitwisekaizen.sdss.management.entity.InitiatorIqnEntityBuilder.anUnsavedInitiatorIqnEntity;
import static com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntityBuilder.anUnsavedUniqueIscsiTargetEntity;
import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@TestPropertySource(locations = {"classpath:test.properties"})
public class UniqueIscsiTargetRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UniqueIscsiTargetRepository uniqueIscsiTargetRepository;

    @Autowired
    private InitiatorIqnEntityRepository initiatorIqnEntityRepository;

    @BeforeMethod
    public void beforeMethod() {
        uniqueIscsiTargetRepository.deleteAll();
    }

    @Test
    public void canSaveUniqueIscsiTarget() throws Exception {
        UniqueIscsiTargetEntity entityToSave = anUnsavedUniqueIscsiTargetEntity().build();

        UniqueIscsiTargetEntity entitySaved = uniqueIscsiTargetRepository.save(entityToSave);

        assertThat(entitySaved, notNullValue());
        assertThat(entitySaved.getUuid(), notNullValue());
        UniqueIscsiTargetEntity entityRetrieved = uniqueIscsiTargetRepository.findOne(entitySaved.getUuid());
        assertThat(entityRetrieved, notNullValue());
        assertThat(entityRetrieved, not(is(entitySaved)));
        assertThat(reflectionEquals(entityRetrieved, entitySaved, "initiatorIqnEntities"), is(true));
    }

    @Test
    public void saveUniqueIscsiTargetShouldAlsoSaveItsInitiatorIqns() throws Exception {
        UniqueIscsiTargetEntity entityToSave =
                anUnsavedUniqueIscsiTargetEntity().withInitiatorIqnEntity(anUnsavedInitiatorIqnEntity())
                        .withInitiatorIqnEntity(anUnsavedInitiatorIqnEntity()).build();

        UniqueIscsiTargetEntity entitySaved = uniqueIscsiTargetRepository.save(entityToSave);

        UniqueIscsiTargetEntity entityRetrieved = uniqueIscsiTargetRepository.findOne(entityToSave.getUuid());
        assertThat(entityRetrieved, not(is(entitySaved)));
        assertThat(entityRetrieved.getInitiatorIqnEntities(), hasSize(2));
        assertThat(entityRetrieved.getInitiatorIqnEntities(), hasItem(entitySaved.getInitiatorIqnEntities().get(0)));
        assertThat(entityRetrieved.getInitiatorIqnEntities(), hasItem(entitySaved.getInitiatorIqnEntities().get(1)));
    }

    @Test
    public void deletingUniqueIscsiTargetShouldAlsoDeleteItsInitiatorIqn() throws Exception {
        UniqueIscsiTargetEntity entitySaved01 = uniqueIscsiTargetRepository.save(
                anUnsavedUniqueIscsiTargetEntity().withInitiatorIqnEntity(anUnsavedInitiatorIqnEntity()).build());
        UniqueIscsiTargetEntity entitySaved02 = uniqueIscsiTargetRepository.save(
                anUnsavedUniqueIscsiTargetEntity().withInitiatorIqnEntity(anUnsavedInitiatorIqnEntity()).build());
        assertThat(newLinkedList(initiatorIqnEntityRepository.findAll()), hasSize(2));

        uniqueIscsiTargetRepository.delete(entitySaved01);

        assertThat(newLinkedList(uniqueIscsiTargetRepository.findAll()), hasSize(1));
        assertThat(newLinkedList(initiatorIqnEntityRepository.findAll()), hasSize(1));
    }
}