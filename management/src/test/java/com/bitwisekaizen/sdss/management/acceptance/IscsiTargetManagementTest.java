package com.bitwisekaizen.sdss.management.acceptance;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder;
import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinityBuilder;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTargetBuilder;
import com.bitwisekaizen.sdss.management.util.ReflectionMatcherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.bitwisekaizen.sdss.management.dto.AgentNodeAffinityBuilder.anAgentNodeAffinity;
import static com.bitwisekaizen.sdss.management.util.ReflectionMatcherUtil.reflectionMatching;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.fail;

@Test
public class IscsiTargetManagementTest extends AbstractAcceptanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(IscsiTargetManagementTest.class);

    private AgentNodeAffinity affinityCreated;

    @BeforeMethod
    public void beforeMethod() {
        afterMethod();
        affinityCreated = affinityOperations.createOrUpdateAgentNodeAffinity(anAgentNodeAffinity().build());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        deleteAllUniqueIscsiTargets();
        affinityOperations.deleteAllAgentNodeAffinities();
    }

    @Test
    public void canCreateIscsiTargets() {
        IscsiTarget targetToCreate = anIscsiTarget().build();

        UniqueIscsiTarget targetCreated = createUniqueIscsiTarget(targetToCreate);

        UniqueIscsiTarget uniqueIscsiTargetRetrieved = getUniqueIscsiTarget(targetCreated.getUuid());
        assertThat(uniqueIscsiTargetRetrieved, notNullValue());
        assertThat(targetCreated.getIscsiTarget(), reflectionMatching(targetToCreate));
        assertThat(targetCreated.getUuid(), notNullValue());
        assertThat(targetCreated.getStorageIpAddress(), notNullValue());
        assertThat(targetCreated.getStorageAgentUrl(), notNullValue());
    }

    @Test
    public void cannotCreateIscsiTargetWithDuplicateTargetName() {
        IscsiTarget targetToCreate = anIscsiTarget().build();

        createUniqueIscsiTarget(targetToCreate);

        try {
            createUniqueIscsiTarget(anIscsiTarget().withTargetName(targetToCreate.getTargetName()).build());
            fail("Expected exception as duplicated target name is not allowed.");
        } catch (ClientErrorException e) {
            assertThat(e.getResponse().getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        }
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void cannotCreateIscsiTargetIfValidationFails() {
        createUniqueIscsiTarget(anIscsiTarget().withTargetName("").build());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotCreateIscsiTargetIfNoMatchingAffinityIsFound() {
        createUniqueIscsiTarget(anIscsiTarget().withAffinityKey(UUID.randomUUID().toString()).build());
    }

    @Test
    public void canDeleteIscsiTargets() {
        UniqueIscsiTarget uniqueIscsiTargetCreated = createUniqueIscsiTarget(anIscsiTarget().build());

        deleteUniqueIscsiTarget(uniqueIscsiTargetCreated);

        try {
            getUniqueIscsiTarget(uniqueIscsiTargetCreated.getUuid());
            fail("Expected NotFoundException but no exception thrown.");
        } catch (NotFoundException e) {
        }
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotDeleteAnIscsiTargetIfItDoesNotExist() {
        deleteUniqueIscsiTarget(UniqueIscsiTargetBuilder.aUniqueIscsiTarget().build());
    }

    @Test
    public void canConcurrentlyCreateIscsiTargets() {
        List<Callable<UniqueIscsiTarget>> callables = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            callables.add(createCallableToCreateTarget(createClient(), anIscsiTarget().build()));
        }

        List<UniqueIscsiTarget> uniqueIscsiTargetsCreated = TaskHelper.submitAllTasksToExecutor(callables);

        List<UniqueIscsiTarget> uniqueIscsiTargets = getAllUniqueIscsiTargets();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecCreated : uniqueIscsiTargetsCreated) {
            assertThat(uniqueIscsiTargets, hasItem(reflectionMatching(uniqueIscsiTargetSpecCreated)));
        }
    }

    @Test
    public void canConcurrentlyDeleteIscsiTargets() {
        List<UniqueIscsiTarget> uniqueIscsiTargetsCreated = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            uniqueIscsiTargetsCreated.add(createUniqueIscsiTarget(anIscsiTarget().build()));
        }

        List<Callable<Void>> callables = new ArrayList<>();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecToDelete : uniqueIscsiTargetsCreated) {
            callables.add(createCallableToDeleteTarget(createClient(), uniqueIscsiTargetSpecToDelete));
        }

        TaskHelper.submitAllTasksToExecutor(callables);

        List<UniqueIscsiTarget> uniqueIscsiTargetsReceived = getAllUniqueIscsiTargets();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecToDelete : uniqueIscsiTargetsCreated) {
            assertThat(uniqueIscsiTargetsReceived, not(hasItem(uniqueIscsiTargetSpecToDelete)));
        }
    }

    private IscsiTargetBuilder anIscsiTarget() {
        return IscsiTargetBuilder.anIscsiTarget().withAffinityKey(affinityCreated.getAffinityKey());
    }

    private Callable<UniqueIscsiTarget> createCallableToCreateTarget(final WebTarget webTarget, final IscsiTarget iscsiTarget) {
        return new Callable<UniqueIscsiTarget>() {
            @Override
            public UniqueIscsiTarget call() throws Exception {
                return createUniqueIscsiTarget(webTarget, iscsiTarget);
            }
        };
    }

    private Callable<Void> createCallableToDeleteTarget(final WebTarget webTarget, final UniqueIscsiTarget uniqueIscsiTarget) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                deleteUniqueIscsiTarget(webTarget, uniqueIscsiTarget);

                return null;
            }
        };
    }

    private void deleteAllUniqueIscsiTargets() {
        for (UniqueIscsiTarget uniqueIscsiTarget : getAllUniqueIscsiTargets()) {
            deleteUniqueIscsiTarget(uniqueIscsiTarget);
        }
    }

    private UniqueIscsiTarget getUniqueIscsiTarget(String uuid) {
        return getUniqueIscsiTarget(webTarget, uuid);
    }

    private UniqueIscsiTarget getUniqueIscsiTarget(WebTarget webTarget, String uuid) {
        return webTarget.path("api").path("uniqueiscsitargets").path(uuid).request().get(UniqueIscsiTarget.class);
    }

    private UniqueIscsiTarget createUniqueIscsiTarget(IscsiTarget targetToCreate) {
        return createUniqueIscsiTarget(webTarget, targetToCreate);
    }

    private UniqueIscsiTarget createUniqueIscsiTarget(WebTarget webTarget, IscsiTarget targetToCreate) {
        return webTarget.path("api").path("uniqueiscsitargets").request().post(Entity.json(targetToCreate), UniqueIscsiTarget.class);
    }

    private void deleteUniqueIscsiTarget(UniqueIscsiTarget iscsiTarget) {
        deleteUniqueIscsiTarget(webTarget, iscsiTarget);
    }

    private void deleteUniqueIscsiTarget(WebTarget webTarget, UniqueIscsiTarget iscsiTarget) {
        webTarget.path("api").path("uniqueiscsitargets").path(iscsiTarget.getUuid()).request().delete(byte[].class);
    }

    public List<UniqueIscsiTarget> getAllUniqueIscsiTargets() {
        return webTarget.path("api").path("uniqueiscsitargets").request().get(new GenericType<List<UniqueIscsiTarget>>() {});
    }
}