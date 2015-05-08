package com.bitwisekaizen.sdss.management;

import com.bitwisekaizen.sdss.management.agent.StorageAgentClient;
import com.bitwisekaizen.sdss.management.dto.IscsiTarget;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import com.bitwisekaizen.sdss.management.repository.UniqueIscsiTargetRepository;
import com.bitwisekaizen.sdss.management.service.IscsiTargetNotFoundException;
import com.bitwisekaizen.sdss.management.service.IscsiTargetService;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.bitwisekaizen.sdss.management.dto.IscsiTargetBuilder.anIscsiTarget;
import static com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntityBuilder.aUniqueIscsiTargetEntity;
import static com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntityBuilder.aUniqueIscsiTargetEntityFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Test
public class IscsiTargetServiceTests {
    private static final String STORAGE_IP = "IP";

    @Mock
    private UniqueIscsiTargetRepository uniqueIscsiTargetRepository;

    @Mock
    private StorageAgentClient storageAgentClient;

    @InjectMocks
    private IscsiTargetService iscsiTargetService;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        when(storageAgentClient.getStorageIpAddress()).thenReturn(STORAGE_IP);
    }

    @Test
    public void canCreateIscsiTarget() {
        IscsiTarget iscsiTarget = anIscsiTarget().build();
        when(uniqueIscsiTargetRepository.save(any(UniqueIscsiTargetEntity.class))).thenReturn(
                aUniqueIscsiTargetEntityFrom(iscsiTarget).withStorageHost(STORAGE_IP).build());

        UniqueIscsiTarget uniqueIscsiTarget = iscsiTargetService.createUniqueIscsiTarget(iscsiTarget);

        verify(uniqueIscsiTargetRepository).save(argThat(entityThatMatches(iscsiTarget)));
        verify(storageAgentClient).createIscsiTarget(iscsiTarget);
        assertThat(uniqueIscsiTarget, notNullValue());
        assertThat(uniqueIscsiTarget.getIscsiTarget(), equalTo(iscsiTarget));
        assertThat(uniqueIscsiTarget.getUuid(), notNullValue());
        assertThat(uniqueIscsiTarget.getStorageIpAddress(), equalTo(STORAGE_IP));
    }

    @Test
    public void canGetIscsiTarget() {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = aUniqueIscsiTargetEntity().build();
        when(uniqueIscsiTargetRepository.findByUuid(uniqueIscsiTargetEntity.getUuid())).thenReturn(uniqueIscsiTargetEntity);

        UniqueIscsiTarget uniqueIscsiTarget = iscsiTargetService.getUniqueIscsiTarget(uniqueIscsiTargetEntity.getUuid());

        assertThat(uniqueIscsiTarget, notNullValue());
        assertThat(uniqueIscsiTarget, entityThatMatches(uniqueIscsiTargetEntity));
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
            assertThat(uniqueIscsiTargets, hasItem(entityThatMatches(uniqueIscsiTargetEntity)));
        }
    }

    @Test
    public void canDeleteIscsiTarget() {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = aUniqueIscsiTargetEntity().build();
        when(uniqueIscsiTargetRepository.findByUuid(uniqueIscsiTargetEntity.getUuid())).thenReturn(uniqueIscsiTargetEntity);

        iscsiTargetService.deleteIscsiUniqueTarget(uniqueIscsiTargetEntity.getUuid());

        verify(uniqueIscsiTargetRepository).delete(uniqueIscsiTargetEntity.getUuid());
        verify(storageAgentClient).deleteIscsiTarget(uniqueIscsiTargetEntity.getUuid());
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

    private TypeSafeMatcher<UniqueIscsiTargetEntity> entityThatMatches(final IscsiTarget iscsiTarget) {
        return new TypeSafeMatcher<UniqueIscsiTargetEntity>() {
            @Override protected boolean matchesSafely(UniqueIscsiTargetEntity uniqueIscsiTargetEntity) {
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

            @Override public void describeTo(Description description) {
            }
        };
    }

    private TypeSafeMatcher<UniqueIscsiTarget> entityThatMatches(final UniqueIscsiTargetEntity uniqueIscsiTargetEntity) {
        return new TypeSafeMatcher<UniqueIscsiTarget>() {
            @Override protected boolean matchesSafely(UniqueIscsiTarget uniqueIscsiTarget) {
                if (uniqueIscsiTarget.getIscsiTarget().getCapacityInMb() != uniqueIscsiTargetEntity.getCapacityInMb()) {
                    return false;
                }
                if (!uniqueIscsiTarget.getIscsiTarget().getTargetName().equals(uniqueIscsiTargetEntity.getTargetName())) {
                    return false;
                }
                if (uniqueIscsiTarget.getIscsiTarget().getHostIscsiQualifiedNames().size() !=
                        uniqueIscsiTargetEntity.getInitiatorIqnEntities().size()) {
                    return false;
                }

                return true;
            }

            @Override public void describeTo(Description description) {
            }
        };
    }
}