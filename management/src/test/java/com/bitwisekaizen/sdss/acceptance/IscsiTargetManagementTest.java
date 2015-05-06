package com.bitwisekaizen.sdss.acceptance;

import com.bitwisekaizen.sdss.dto.IscsiTarget;
import com.bitwisekaizen.sdss.dto.IscsiTargetSpec;
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

import static com.bitwisekaizen.sdss.acceptance.IscsiTargetManagementTest.UNIMPLEMENTED;
import static com.bitwisekaizen.sdss.dto.IscsiTargetBuilder.aIscsiTarget;
import static com.bitwisekaizen.sdss.dto.IscsiTargetSpecBuilder.aIscsiTargetSpec;
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
        IscsiTargetSpec targetToCreate = aIscsiTargetSpec().build();

        IscsiTarget targetCreated = createIscsiTarget(targetToCreate);

        IscsiTarget iscsiTargetRetrieved = getIscsiTarget(targetCreated.getUuid());
        assertThat(iscsiTargetRetrieved, notNullValue());
        assertThat(targetCreated.getIscsiTargetSpec(), equalTo(targetToCreate));
        assertThat(targetCreated.getUuid(), notNullValue());
        assertThat(targetCreated.getStorageIpAddress(), notNullValue());
    }

    @Test
    public void cannotCreateIscsiTargetWithDuplicateTargetName() {
        IscsiTargetSpec targetToCreate = aIscsiTargetSpec().build();

        createIscsiTarget(targetToCreate);

        try {
            createIscsiTarget(aIscsiTargetSpec().withTargetName(targetToCreate.getTargetName()).build());
            fail("Expected exception as duplicated target name is not allowed.");
        } catch (ForbiddenException e) {
        }
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void cannotCreateIscsiTargetIfValidationFails() {
        createIscsiTarget(aIscsiTargetSpec().withTargetName("").build());
    }

    @Test
    public void canDeleteIscsiTargets() {
        IscsiTarget iscsiTargetCreated = createIscsiTarget(aIscsiTargetSpec().build());

        deleteIscsiTarget(iscsiTargetCreated);

        IscsiTarget iscsiTarget = getIscsiTarget(iscsiTargetCreated.getUuid());
        assertThat(iscsiTarget, notNullValue());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotDeleteAnIscsiTargetIfItDoesNotExist() {
        deleteIscsiTarget(aIscsiTarget().build());
    }

    @Test
    public void canConcurrentlyCreateIscsiTargets() {
        List<Callable<IscsiTarget>> callables = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            callables.add(createCallableToCreateTarget(createClient(), aIscsiTargetSpec().build()));
        }

        List<IscsiTarget> iscsiTargetsCreated = TaskHelper.submitAllTasksToExecutor(callables);

        List<IscsiTarget> iscsiTargets = getAllIscsiTargets();
        for (IscsiTarget iscsiTargetSpecCreated : iscsiTargetsCreated) {
            assertThat(iscsiTargets, hasItem(iscsiTargetSpecCreated));
        }
    }

    @Test
    public void canConcurrentlyDeleteIscsiTargets() {
        List<IscsiTarget> iscsiTargetsCreated = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            iscsiTargetsCreated.add(createIscsiTarget(aIscsiTargetSpec().build()));
        }

        List<Callable<Void>> callables = new ArrayList<>();
        for (IscsiTarget iscsiTargetSpecToDelete : iscsiTargetsCreated) {
            callables.add(createCallableToDeleteTarget(createClient(), iscsiTargetSpecToDelete));
        }

        TaskHelper.submitAllTasksToExecutor(callables);

        List<IscsiTarget> iscsiTargetsReceived = getAllIscsiTargets();
        for (IscsiTarget iscsiTargetSpecToDelete : iscsiTargetsCreated) {
            assertThat(iscsiTargetsReceived, not(hasItem(iscsiTargetSpecToDelete)));
        }
    }

    private Callable<IscsiTarget> createCallableToCreateTarget(final WebTarget webTarget, final IscsiTargetSpec iscsiTargetSpec) {
        return new Callable<IscsiTarget>() {
            @Override
            public IscsiTarget call() throws Exception {
                return createIscsiTarget(webTarget, iscsiTargetSpec);
            }
        };
    }

    private Callable<Void> createCallableToDeleteTarget(final WebTarget webTarget, final IscsiTarget iscsiTarget) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                deleteIscsiTarget(webTarget, iscsiTarget);

                return null;
            }
        };
    }
    
    private WebTarget createClient() {
        return createClient(null);
    }

    private void deleteAllIscsiTargets() {

    }

    private IscsiTarget getIscsiTarget(String uuid) {
        return null;
    }

    private IscsiTarget createIscsiTarget(IscsiTargetSpec targetToCreate) {
        return null;
    }

    public List<IscsiTarget> getAllIscsiTargets() {
        return null;
    }

    private IscsiTarget createIscsiTarget(WebTarget webTarget, IscsiTargetSpec targetToCreate) {
        return null;
    }


    private void deleteIscsiTarget(IscsiTarget iscsiTarget) {

    }

    private void deleteIscsiTarget(WebTarget webTarget, IscsiTarget iscsiTarget) {

    }
}