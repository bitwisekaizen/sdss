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
import javax.ws.rs.client.WebTarget;
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
        deleteAllIscsiTargets();
    }

    @Test
    public void canCreateIscsiTargets() {
        IscsiTarget targetToCreate = IscsiTargetBuilder.anIscsiTarget().build();

        UniqueIscsiTarget targetCreated = createIscsiTarget(targetToCreate);

        UniqueIscsiTarget uniqueIscsiTargetRetrieved = getIscsiTarget(targetCreated.getUuid());
        assertThat(uniqueIscsiTargetRetrieved, notNullValue());
        assertThat(targetCreated.getIscsiTarget(), equalTo(targetToCreate));
        assertThat(targetCreated.getUuid(), notNullValue());
        assertThat(targetCreated.getStorageIpAddress(), notNullValue());
    }

    @Test
    public void cannotCreateIscsiTargetWithDuplicateTargetName() {
        IscsiTarget targetToCreate = IscsiTargetBuilder.anIscsiTarget().build();

        createIscsiTarget(targetToCreate);

        try {
            createIscsiTarget(IscsiTargetBuilder.anIscsiTarget().withTargetName(targetToCreate.getTargetName()).build());
            fail("Expected exception as duplicated target name is not allowed.");
        } catch (ForbiddenException e) {
        }
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void cannotCreateIscsiTargetIfValidationFails() {
        createIscsiTarget(IscsiTargetBuilder.anIscsiTarget().withTargetName("").build());
    }

    @Test
    public void canDeleteIscsiTargets() {
        UniqueIscsiTarget uniqueIscsiTargetCreated = createIscsiTarget(IscsiTargetBuilder.anIscsiTarget().build());

        deleteIscsiTarget(uniqueIscsiTargetCreated);

        UniqueIscsiTarget uniqueIscsiTarget = getIscsiTarget(uniqueIscsiTargetCreated.getUuid());
        assertThat(uniqueIscsiTarget, notNullValue());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotDeleteAnIscsiTargetIfItDoesNotExist() {
        deleteIscsiTarget(UniqueIscsiTargetBuilder.aUniqueIscsiTarget().build());
    }

    @Test
    public void canConcurrentlyCreateIscsiTargets() {
        List<Callable<UniqueIscsiTarget>> callables = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            callables.add(createCallableToCreateTarget(createClient(), IscsiTargetBuilder.anIscsiTarget().build()));
        }

        List<UniqueIscsiTarget> uniqueIscsiTargetsCreated = TaskHelper.submitAllTasksToExecutor(callables);

        List<UniqueIscsiTarget> uniqueIscsiTargets = getAllIscsiTargets();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecCreated : uniqueIscsiTargetsCreated) {
            assertThat(uniqueIscsiTargets, hasItem(uniqueIscsiTargetSpecCreated));
        }
    }

    @Test
    public void canConcurrentlyDeleteIscsiTargets() {
        List<UniqueIscsiTarget> uniqueIscsiTargetsCreated = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            uniqueIscsiTargetsCreated.add(createIscsiTarget(IscsiTargetBuilder.anIscsiTarget().build()));
        }

        List<Callable<Void>> callables = new ArrayList<>();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecToDelete : uniqueIscsiTargetsCreated) {
            callables.add(createCallableToDeleteTarget(createClient(), uniqueIscsiTargetSpecToDelete));
        }

        TaskHelper.submitAllTasksToExecutor(callables);

        List<UniqueIscsiTarget> uniqueIscsiTargetsReceived = getAllIscsiTargets();
        for (UniqueIscsiTarget uniqueIscsiTargetSpecToDelete : uniqueIscsiTargetsCreated) {
            assertThat(uniqueIscsiTargetsReceived, not(hasItem(uniqueIscsiTargetSpecToDelete)));
        }
    }

    private Callable<UniqueIscsiTarget> createCallableToCreateTarget(final WebTarget webTarget, final IscsiTarget iscsiTarget) {
        return new Callable<UniqueIscsiTarget>() {
            @Override
            public UniqueIscsiTarget call() throws Exception {
                return createIscsiTarget(webTarget, iscsiTarget);
            }
        };
    }

    private Callable<Void> createCallableToDeleteTarget(final WebTarget webTarget, final UniqueIscsiTarget uniqueIscsiTarget) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                deleteIscsiTarget(webTarget, uniqueIscsiTarget);

                return null;
            }
        };
    }
    
    private WebTarget createClient() {
        return createClient(null);
    }

    private void deleteAllIscsiTargets() {

    }

    private UniqueIscsiTarget getIscsiTarget(String uuid) {
        return null;
    }

    private UniqueIscsiTarget createIscsiTarget(IscsiTarget targetToCreate) {
        return null;
    }

    public List<UniqueIscsiTarget> getAllIscsiTargets() {
        return null;
    }

    private UniqueIscsiTarget createIscsiTarget(WebTarget webTarget, IscsiTarget targetToCreate) {
        return null;
    }


    private void deleteIscsiTarget(UniqueIscsiTarget uniqueIscsiTarget) {

    }

    private void deleteIscsiTarget(WebTarget webTarget, UniqueIscsiTarget uniqueIscsiTarget) {

    }
}