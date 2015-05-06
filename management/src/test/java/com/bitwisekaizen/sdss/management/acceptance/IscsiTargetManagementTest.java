package com.bitwisekaizen.sdss.management.acceptance;

import com.bitwisekaizen.sdss.management.dto.IscsiTargetBuilder;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.dto.IscsiTarget;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTargetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.bitwisekaizen.sdss.management.acceptance.IscsiTargetManagementTest.UNIMPLEMENTED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@Test(groups = UNIMPLEMENTED)
public class IscsiTargetManagementTest extends AbstractAcceptanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(IscsiTargetManagementTest.class);
    public static final String UNIMPLEMENTED = "unimplemented";

    @AfterMethod
    public void afterMethod() {
        deleteAllUniqueIscsiTargets();
    }

    @Test
    public void canCreateIscsiTargets() {
        IscsiTarget targetToCreate = IscsiTargetBuilder.anIscsiTarget().build();

        UniqueIscsiTarget targetCreated = createUniqueIscsiTarget(targetToCreate);

        UniqueIscsiTarget uniqueIscsiTargetRetrieved = getUniqueIscsiTarget(targetCreated.getUuid());
        assertThat(uniqueIscsiTargetRetrieved, notNullValue());
        assertThat(targetCreated.getIscsiTarget(), equalTo(targetToCreate));
        assertThat(targetCreated.getUuid(), notNullValue());
        assertThat(targetCreated.getStorageIpAddress(), notNullValue());
    }

    @Test
    public void cannotCreateIscsiTargetWithDuplicateTargetName() {
        IscsiTarget targetToCreate = IscsiTargetBuilder.anIscsiTarget().build();

        createUniqueIscsiTarget(targetToCreate);

        try {
            createUniqueIscsiTarget(IscsiTargetBuilder.anIscsiTarget().withTargetName(targetToCreate.getTargetName()).build());
            fail("Expected exception as duplicated target name is not allowed.");
        } catch (ForbiddenException e) {
        }
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void cannotCreateIscsiTargetIfValidationFails() {
        createUniqueIscsiTarget(IscsiTargetBuilder.anIscsiTarget().withTargetName("").build());
    }

    @Test
    public void canDeleteIscsiTargets() {
        UniqueIscsiTarget uniqueIscsiTargetCreated = createUniqueIscsiTarget(IscsiTargetBuilder.anIscsiTarget().build());

        deleteUniqueIscsiTarget(uniqueIscsiTargetCreated);

        UniqueIscsiTarget uniqueIscsiTarget = getUniqueIscsiTarget(uniqueIscsiTargetCreated.getUuid());
        assertThat(uniqueIscsiTarget, notNullValue());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotDeleteAnIscsiTargetIfItDoesNotExist() {
        deleteUniqueIscsiTarget(UniqueIscsiTargetBuilder.aUniqueIscsiTarget().build());
    }

    @Test
    public void canConcurrentlyCreateIscsiTargets() {
        List<Callable<UniqueIscsiTarget>> callables = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            callables.add(createCallableToCreateTarget(createClient(), IscsiTargetBuilder.anIscsiTarget().build()));
        }

        List<UniqueIscsiTarget> uniqueIscsiTargetsCreated = TaskHelper.submitAllTasksToExecutor(callables);

        List<UniqueIscsiTarget> uniqueIscsiTargets = getAllUniqueIscsiTargets();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecCreated : uniqueIscsiTargetsCreated) {
            assertThat(uniqueIscsiTargets, hasItem(uniqueIscsiTargetSpecCreated));
        }
    }

    @Test
    public void canConcurrentlyDeleteIscsiTargets() {
        List<UniqueIscsiTarget> uniqueIscsiTargetsCreated = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            uniqueIscsiTargetsCreated.add(createUniqueIscsiTarget(IscsiTargetBuilder.anIscsiTarget().build()));
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