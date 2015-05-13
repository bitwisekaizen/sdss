package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTargetBuilder.anAccessibleIscsiTarget;
import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;


public class AccessibleIscsiTargetServiceTest {
    @Mock
    private AccessibleIscsiTargetRepository accessibleIscsiTargetRepository;

    @Mock
    private LioBackedIscsiTargetService lioBackedStorageService;

    @InjectMocks
    private AccessibleIscsiTargetService accessibleIscsiTargetService;

    @BeforeMethod
    public void setupMethod() throws Exception {
        accessibleIscsiTargetService = new AccessibleIscsiTargetService(
                accessibleIscsiTargetRepository, lioBackedStorageService);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void canCreateAccessbileIscsiTarget() throws Exception {
        IscsiTarget targetToCreate = anIscsiTarget().build();
        AccessibleIscsiTarget lioCreatedTarget = anAccessibleIscsiTarget().build();
        when(accessibleIscsiTargetRepository.findByTargetName(targetToCreate.getTargetName())).thenReturn(null);
        when(lioBackedStorageService.createIscsiTarget(targetToCreate)).thenReturn(lioCreatedTarget);

        AccessibleIscsiTarget targetCreated = accessibleIscsiTargetService.createAccessbileIscsiTarget(targetToCreate);

        assertThat(targetCreated, notNullValue());
        assertThat(targetCreated, new ReflectionEquals(lioCreatedTarget));

        verify(lioBackedStorageService).createIscsiTarget(targetToCreate);
        verify(accessibleIscsiTargetRepository).save(lioCreatedTarget);
    }

    @Test
    public void throwExceptionIfTargetNameAlreadyExist() throws Exception {
        IscsiTarget targetToCreate = anIscsiTarget().build();
        when(accessibleIscsiTargetRepository.findByTargetName(targetToCreate.getTargetName())).thenReturn(
                anAccessibleIscsiTarget().build());
        try {
            accessibleIscsiTargetService.createAccessbileIscsiTarget(targetToCreate);
            Assert.fail("Expected DuplicateTargetNameException but didn't get any");
        } catch (DuplicateTargetNameException e) {
        }

        verify(lioBackedStorageService, never()).createIscsiTarget(argThat(any(IscsiTarget.class)));
        verify(accessibleIscsiTargetRepository, never()).save(argThat(any(AccessibleIscsiTarget.class)));
    }

    @Test
    public void canDeleteAccessibleIscsiTarget() throws Exception {
        AccessibleIscsiTarget storedTarget = anAccessibleIscsiTarget().build();
        when(accessibleIscsiTargetRepository.findByTargetName(storedTarget.getTargetName())).thenReturn(storedTarget);

        accessibleIscsiTargetService.deleteAccessibleIscsiTarget(storedTarget.getTargetName());

        verify(lioBackedStorageService).delete(argThat(is(storedTarget.getTargetName())));
        verify(accessibleIscsiTargetRepository).delete(argThat(is(storedTarget.getTargetName())));
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
        List<AccessibleIscsiTarget> storedTargets = Arrays.asList(anAccessibleIscsiTarget().build(),
                anAccessibleIscsiTarget().build(), anAccessibleIscsiTarget().build());
        when(accessibleIscsiTargetRepository.findAll()).thenReturn(storedTargets);

        List<AccessibleIscsiTarget> retrievedTargets = accessibleIscsiTargetService.getAllAccessibleIscsiTargets();

        assertThat(retrievedTargets, equalTo(storedTargets));
    }
}