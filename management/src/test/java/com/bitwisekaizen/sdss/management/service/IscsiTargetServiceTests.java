package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import com.bitwisekaizen.sdss.management.repository.UniqueIscsiTargetRepository;
import com.bitwisekaizen.sdss.management.validation.DtoValidator;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTargetBuilder.anAccessibleIscsiTarget;
import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntityBuilder.aUniqueIscsiTargetEntity;
import static com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntityBuilder.aUniqueIscsiTargetEntityFrom;
import static com.bitwisekaizen.sdss.management.util.ReflectionMatcherUtil.reflectionMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@Test
public class IscsiTargetServiceTests {
    private static final String STORAGE_HOST = "i'm a host";

    @Mock
    private UniqueIscsiTargetRepository uniqueIscsiTargetRepository;

    @Mock
    private StorageAgentClientFactory storageAgentClientFactory;

    @Mock
    private DtoValidator dtoValidator;

    private IscsiTargetService iscsiTargetService;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        iscsiTargetService = new IscsiTargetService(uniqueIscsiTargetRepository,
                storageAgentClientFactory, dtoValidator);
    }

    @Test
    public void canCreateIscsiTarget() {
        AccessibleIscsiTarget accessibleIscsiTarget = anAccessibleIscsiTarget()
                .withStorageNetworkAddress(STORAGE_HOST).build();
        IscsiTarget iscsiTarget = accessibleIscsiTarget.getIscsiTarget();
        StorageAgentClient storageAgentClient = mock(StorageAgentClient.class);
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity =
                aUniqueIscsiTargetEntityFrom(iscsiTarget).withStorageHost(STORAGE_HOST).build();
        when(uniqueIscsiTargetRepository.save(any(UniqueIscsiTargetEntity.class))).thenReturn(uniqueIscsiTargetEntity);

        when(storageAgentClientFactory.getBestStorageAgent(iscsiTarget)).thenReturn(storageAgentClient);
        when(storageAgentClient.createIscsiTarget(iscsiTarget)).thenReturn(accessibleIscsiTarget);

        UniqueIscsiTarget uniqueIscsiTarget = iscsiTargetService.createUniqueIscsiTarget(iscsiTarget);

        verify(dtoValidator).validate(iscsiTarget);
        verify(uniqueIscsiTargetRepository).save(argThat(matchesIscsiTarget(iscsiTarget)));
        verify(storageAgentClient).createIscsiTarget(iscsiTarget);
        assertThat(uniqueIscsiTarget, notNullValue());
        assertThat(uniqueIscsiTarget.getIscsiTarget(), reflectionMatching(iscsiTarget));
        assertThat(uniqueIscsiTarget.getUuid(), notNullValue());
        assertThat(uniqueIscsiTarget.getStorageIpAddress(), equalTo(STORAGE_HOST));
        assertThat(uniqueIscsiTarget.getStorageAgentUrl(), equalTo(uniqueIscsiTargetEntity.getStorageAgentUrl()));
    }

    @Test
    public void cannotCreateIscsiTargetWithDuplicateName() {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        when(uniqueIscsiTargetRepository.findByTargetName(iscsiTarget.getTargetName())).thenReturn(
                aUniqueIscsiTargetEntityFrom(iscsiTarget).withStorageHost(STORAGE_HOST).build());
        try {
            iscsiTargetService.createUniqueIscsiTarget(iscsiTarget);
            Assert.fail("Expected IscsiTargetNotFoundException but none occurred");
        } catch (DuplicateTargetNameException e) {
        }

        verify(uniqueIscsiTargetRepository, never()).save(any(UniqueIscsiTargetEntity.class));
        verify(storageAgentClientFactory, never()).getBestStorageAgent(any(IscsiTarget.class));
    }

    @Test
    public void canGetIscsiTarget() {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = aUniqueIscsiTargetEntity().build();
        when(uniqueIscsiTargetRepository.findByUuid(uniqueIscsiTargetEntity.getUuid())).thenReturn(uniqueIscsiTargetEntity);

        UniqueIscsiTarget uniqueIscsiTarget = iscsiTargetService.getUniqueIscsiTarget(uniqueIscsiTargetEntity.getUuid());

        assertThat(uniqueIscsiTarget, notNullValue());
        assertThat(uniqueIscsiTarget, matchesItself(uniqueIscsiTargetEntity));
    }

    @Test
    public void exceptionThrownIfIscsiTargetNotFound() {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = aUniqueIscsiTargetEntity().build();
        when(uniqueIscsiTargetRepository.findByUuid(uniqueIscsiTargetEntity.getUuid())).thenReturn(null);

        try {
            iscsiTargetService.getUniqueIscsiTarget(uniqueIscsiTargetEntity.getUuid());
            Assert.fail("Expected IscsiTargetNotFoundException but none occurred");
        } catch (IscsiTargetNotFoundException e) {
        }
    }

    @Test
    public void canGetAllIscsiTargets() {
        List<UniqueIscsiTargetEntity> iscsiTargetEntities = Arrays.asList(aUniqueIscsiTargetEntity().build(),
                aUniqueIscsiTargetEntity().build());
        when(uniqueIscsiTargetRepository.findAll()).thenReturn(iscsiTargetEntities);

        List<UniqueIscsiTarget> uniqueIscsiTargets = iscsiTargetService.getAllUniqueIscsiTargets();

        assertThat(uniqueIscsiTargets, notNullValue());
        assertThat(uniqueIscsiTargets, hasSize(iscsiTargetEntities.size()));
        for (UniqueIscsiTargetEntity uniqueIscsiTargetEntity : iscsiTargetEntities) {
            assertThat(uniqueIscsiTargets, hasItem(matchesItself(uniqueIscsiTargetEntity)));
        }
    }

    @Test
    public void canDeleteIscsiTarget() {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = aUniqueIscsiTargetEntity().build();
        StorageAgentClient storageAgentClient = mock(StorageAgentClient.class);
        when(uniqueIscsiTargetRepository.findByUuid(uniqueIscsiTargetEntity.getUuid())).thenReturn(uniqueIscsiTargetEntity);
        when(storageAgentClientFactory.getBestStorageAgent(argThat(matchesUniqueIscsiTarget(uniqueIscsiTargetEntity))))
                .thenReturn(storageAgentClient);

        iscsiTargetService.deleteIscsiUniqueTarget(uniqueIscsiTargetEntity.getUuid());

        verify(uniqueIscsiTargetRepository).delete(uniqueIscsiTargetEntity.getUuid());
        verify(storageAgentClient).deleteIscsiTarget(uniqueIscsiTargetEntity.getTargetName());
    }

    @Test
    public void cannotDeleteNonExistingIscsiTarget() {
        String uuid = UUID.randomUUID().toString();
        when(uniqueIscsiTargetRepository.findByUuid(uuid)).thenReturn(null);

        try {
            iscsiTargetService.deleteIscsiUniqueTarget(uuid);
            Assert.fail("Expected IscsiTargetNotFoundException but none occurred");
        } catch (IscsiTargetNotFoundException e) {
        }
    }

    private TypeSafeMatcher<UniqueIscsiTargetEntity> matchesIscsiTarget(final IscsiTarget iscsiTarget) {
        return new TypeSafeMatcher<UniqueIscsiTargetEntity>() {
            @Override
            protected boolean matchesSafely(UniqueIscsiTargetEntity uniqueIscsiTargetEntity) {
                return isEqual(uniqueIscsiTargetEntity, iscsiTarget);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private boolean isEqual(UniqueIscsiTargetEntity uniqueIscsiTargetEntity, IscsiTarget iscsiTarget) {
        if (iscsiTarget.getCapacityInMb() != uniqueIscsiTargetEntity.getCapacityInMb()) {
            return false;
        }
        if (!iscsiTarget.getTargetName().equals(uniqueIscsiTargetEntity.getTargetName())) {
            return false;
        }
        if (iscsiTarget.getHostIscsiQualifiedNames().size() !=
                uniqueIscsiTargetEntity.getInitiatorIqnEntities().size()) {
            return false;
        }

        return true;
    }

    private TypeSafeMatcher<IscsiTarget> matchesUniqueIscsiTarget(final UniqueIscsiTargetEntity iscsiTarget) {
        return new TypeSafeMatcher<IscsiTarget>() {
            @Override
            protected boolean matchesSafely(IscsiTarget uniqueIscsiTargetEntity) {
                return isEqual(iscsiTarget, uniqueIscsiTargetEntity);

            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private TypeSafeMatcher<UniqueIscsiTarget> matchesItself(final UniqueIscsiTargetEntity uniqueIscsiTargetEntity) {
        return new TypeSafeMatcher<UniqueIscsiTarget>() {
            @Override
            protected boolean matchesSafely(UniqueIscsiTarget uniqueIscsiTarget) {
                return isEqual(uniqueIscsiTargetEntity, uniqueIscsiTarget.getIscsiTarget());
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
