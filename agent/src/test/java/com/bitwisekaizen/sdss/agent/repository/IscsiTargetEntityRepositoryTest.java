package com.bitwisekaizen.sdss.agent.repository;

import com.bitwisekaizen.sdss.agent.config.ApplicationConfig;
import com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntity;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntityBuilder.anIscsiTargetEntity;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@TestPropertySource(locations = {"classpath:test.properties"})
public class IscsiTargetEntityRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private IscsiTargetEntityRepository iscsiTargetEntityRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @AfterMethod(alwaysRun = true)
    public void cleanup() throws Exception {
        deleteAll();
    }

    @Test
    public void canSave() throws Exception {
        IscsiTargetEntity targetToSave = anIscsiTargetEntity().build();
        iscsiTargetEntityRepository.save(targetToSave);
        entityManager.flush();
        entityManager.clear();

        IscsiTargetEntity targetFound =
                iscsiTargetEntityRepository.findOne(targetToSave.getTargetName());
        assertThat(targetFound, notNullValue());
        assertThat(targetFound, not(is(targetToSave)));
        assertThat(targetFound, new ReflectionEquals(targetToSave));
    }

    @Test
    public void canDelete() throws Exception {
        IscsiTargetEntity targetToSave = anIscsiTargetEntity().build();
        iscsiTargetEntityRepository.save(targetToSave);
        entityManager.flush();
        entityManager.clear();

        iscsiTargetEntityRepository.delete(targetToSave.getTargetName());
        entityManager.flush();
        entityManager.clear();

        IscsiTargetEntity targetFound = iscsiTargetEntityRepository.findOne(targetToSave.getTargetName());
        assertThat(targetFound, nullValue());
    }

    private void deleteAll() {
        for (IscsiTargetEntity accessibleIscsiTarget : iscsiTargetEntityRepository.findAll()) {
            iscsiTargetEntityRepository.delete(accessibleIscsiTarget.getTargetName());
        }
    }
}