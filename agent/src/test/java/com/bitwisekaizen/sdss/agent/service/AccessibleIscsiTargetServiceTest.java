package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntity;
import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import junit.framework.AssertionFailedError;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntityBuilder.anIscsiTargetEntity;
import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;


public class AccessibleIscsiTargetServiceTest {
    @Mock
    private AccessibleIscsiTargetRepository accessibleIscsiTargetRepository;

    @Mock
    private LioBackedIscsiTargetService lioBackedStorageService;

    private AccessibleIscsiTargetService accessibleIscsiTargetService;

    @BeforeMethod
    public void setupMethod() throws Exception {
        MockitoAnnotations.initMocks(this);

        accessibleIscsiTargetService = new AccessibleIscsiTargetService(
                accessibleIscsiTargetRepository, lioBackedStorageService);
    }

    @Test
    public void canCreateAccessibleIscsiTarget() throws Exception {
        IscsiTarget targetToCreate = anIscsiTarget().build();
        IscsiTargetEntity existingTarget01 = anIscsiTargetEntity().build();
        IscsiTargetEntity existingTarget02 = anIscsiTargetEntity().build();
        when(accessibleIscsiTargetRepository.findByTargetName(targetToCreate.getTargetName())).thenReturn(null);
        when(accessibleIscsiTargetRepository.findAll()).thenReturn(newArrayList(existingTarget01, existingTarget02));

        AccessibleIscsiTarget targetCreated = accessibleIscsiTargetService.createAccessbileIscsiTarget(targetToCreate);

        assertThat(targetCreated, notNullValue());
        assertThat(targetCreated.getIscsiTarget(), reflectionMatching(targetToCreate));
        ArgumentCaptor<List<AccessibleIscsiTarget>> argument = ArgumentCaptor.forClass(((Class) List.class));
        verify(lioBackedStorageService).updateTargets(argument.capture());
        List<AccessibleIscsiTarget> targetsToUpdate = argument.getValue();
        assertThat(targetsToUpdate, hasSize(3));
        assertThat(targetsToUpdate, hasItem(reflectionMatching(targetCreated)));
        assertThat(targetsToUpdate, hasItem(matching(existingTarget01)));
        assertThat(targetsToUpdate, hasItem(matching(existingTarget02)));
        verify(accessibleIscsiTargetRepository).save(argThat(matching(targetCreated)));
    }

    @Test
    public void throwExceptionIfTargetNameAlreadyExist() throws Exception {
        IscsiTarget targetToCreate = anIscsiTarget().build();
        when(accessibleIscsiTargetRepository.findByTargetName(targetToCreate.getTargetName())).thenReturn(
                anIscsiTargetEntity().build());
        try {
            accessibleIscsiTargetService.createAccessbileIscsiTarget(targetToCreate);
            Assert.fail("Expected DuplicateTargetNameException but didn't get any");
        } catch (DuplicateTargetNameException e) {
        }

        verify(lioBackedStorageService, never()).updateTargets(argThat(any(List.class)));
        verify(accessibleIscsiTargetRepository, never()).save(argThat(any(IscsiTargetEntity.class)));
    }

    @Test
    public void canDeleteAccessibleIscsiTarget() throws Exception {
        IscsiTargetEntity targetToDelete = anIscsiTargetEntity().build();
        IscsiTargetEntity targetToKeep01 = anIscsiTargetEntity().build();
        IscsiTargetEntity targetToKeep02 = anIscsiTargetEntity().build();
        when(accessibleIscsiTargetRepository.findByTargetName(
                targetToDelete.getTargetName())).thenReturn(targetToDelete);
        when(accessibleIscsiTargetRepository.findAll()).thenReturn(
                newArrayList(targetToDelete, targetToKeep01, targetToKeep02));

        accessibleIscsiTargetService.deleteAccessibleIscsiTarget(targetToDelete.getTargetName());

        ArgumentCaptor<List<AccessibleIscsiTarget>> argument = ArgumentCaptor.forClass(((Class) List.class));
        verify(lioBackedStorageService).updateTargets(argument.capture());
        List<AccessibleIscsiTarget> targetsToUpdate = argument.getValue();
        assertThat(targetsToUpdate, hasSize(2));
        assertThat(targetsToUpdate, hasItem(matching(targetToKeep01)));
        assertThat(targetsToUpdate, hasItem(matching(targetToKeep02)));
        verify(accessibleIscsiTargetRepository).delete(targetToDelete.getTargetName());
    }

    @Test
    public void throwExceptionIfDeletingTargetThatDoesNotExist() throws Exception {
        when(accessibleIscsiTargetRepository.findByTargetName(anyString())).thenReturn(null);

        try {
            accessibleIscsiTargetService.deleteAccessibleIscsiTarget("target-name");
            Assert.fail("Expected IscsiTargetNotFoundException but didn't get any");
        } catch (IscsiTargetNotFoundException e) {
        }
    }

    @Test
    public void canGetAllAccessibleIscsiTargets() throws Exception {
        List<IscsiTargetEntity> storedTargets = asList(anIscsiTargetEntity().build(),
                anIscsiTargetEntity().build(), anIscsiTargetEntity().build());
        when(accessibleIscsiTargetRepository.findAll()).thenReturn(storedTargets);

        List<AccessibleIscsiTarget> retrievedTargets = accessibleIscsiTargetService.getAllAccessibleIscsiTargets();

        assertThat(retrievedTargets, hasSize(storedTargets.size()));
        assertThat(retrievedTargets, hasItem(matching(storedTargets.get(0))));
        assertThat(retrievedTargets, hasItem(matching(storedTargets.get(1))));
        assertThat(retrievedTargets, hasItem(matching(storedTargets.get(2))));
    }

    private TypeSafeMatcher<AccessibleIscsiTarget> matching(final IscsiTargetEntity expectedObject) {
        return new TypeSafeMatcher<AccessibleIscsiTarget>() {
            public Object actualObject;

            @Override
            protected boolean matchesSafely(AccessibleIscsiTarget actualObject) {
                this.actualObject = actualObject;
                return isEqual(actualObject, expectedObject);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected " + expectedObject.toString() + " but got " + actualObject);
            }
        };
    }

    private boolean isEqual(AccessibleIscsiTarget actualObject, IscsiTargetEntity expectedObject) {
        if (!actualObject.getIscsiTarget().getTargetName().equals(expectedObject.getTargetName())) {
            return false;
        }
        if (actualObject.getIscsiTarget().getCapacityInMb() != expectedObject.getCapacityInMb()) {
            return false;
        }
        if (!actualObject.getIscsiTarget().getHostIscsiQualifiedNames()
                .equals(expectedObject.getHostIscsiQualifiedNames())) {
            return false;
        }

        return true;
    }

    private TypeSafeMatcher<IscsiTargetEntity> matching(final AccessibleIscsiTarget expectedObject) {
        return new TypeSafeMatcher<IscsiTargetEntity>() {
            public Object actualObject;

            @Override
            protected boolean matchesSafely(IscsiTargetEntity actualObject) {
                this.actualObject = actualObject;
                return isEqual(expectedObject, actualObject);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected " + expectedObject.toString() + " but got " + actualObject);
            }
        };
    }

    private <T> TypeSafeMatcher<T> reflectionMatching(final T expectedObject) {
        return new TypeSafeMatcher<T>() {
            public T actualObject;

            @Override
            protected boolean matchesSafely(T actualObject) {
                this.actualObject = actualObject;
                try {
                    assertReflectionEquals(expectedObject, actualObject);
                } catch (AssertionFailedError assertionFailedError) {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected " + expectedObject.toString() + " but got " + actualObject);
            }
        };
    }
}