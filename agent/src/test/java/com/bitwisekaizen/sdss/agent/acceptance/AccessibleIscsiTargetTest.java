package com.bitwisekaizen.sdss.agent.acceptance;

import com.bitwisekaizen.sdss.agent.config.ApplicationConfig;
import com.bitwisekaizen.sdss.agent.util.ReflectionMatcherUtil;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import junit.framework.AssertionFailedError;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.bitwisekaizen.sdss.agent.util.ReflectionMatcherUtil.reflectionMatching;
import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebAppConfiguration
@IntegrationTest
// See http://stackoverflow.com/questions/25537436/acceptance-testing-a-spring-boot-web-app-with-testng
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@Test
@TestPropertySource(locations = {"classpath:test.properties"})
public class AccessibleIscsiTargetTest extends AbstractAcceptanceTest {

    @BeforeMethod(alwaysRun = true)
    @AfterMethod(alwaysRun = true)
    public void setupMethod() {
        deleteAllTargets();
    }

    @Test
    public void canCreateIscsiTargetOnAgent() {
        IscsiTarget iscsiTargetToCreate = anIscsiTarget().build();
        AccessibleIscsiTarget iscsiTargetCreated = storageAgentClient.createIscsiTarget(iscsiTargetToCreate);

        assertThat(iscsiTargetCreated, notNullValue());
        assertThat(iscsiTargetCreated.getStorageNetworkAddresses(), notNullValue());
        assertThat(iscsiTargetCreated.getStorageNetworkAddresses(), hasSize(2));
        assertThat(iscsiTargetCreated.getIscsiTarget(), reflectionMatching(iscsiTargetToCreate));

        assertThat(allIscsiTargetsContains(iscsiTargetCreated), is(true));
    }

    @Test
    public void canDeleteIscsiTargetOnAgent() {
        AccessibleIscsiTarget iscsiTargetCreated01 = storageAgentClient.createIscsiTarget(anIscsiTarget().build());
        AccessibleIscsiTarget iscsiTargetCreated02 = storageAgentClient.createIscsiTarget(anIscsiTarget().build());
        assertThat(allIscsiTargetsContains(iscsiTargetCreated01), is(true));
        assertThat(allIscsiTargetsContains(iscsiTargetCreated01), is(true));

        storageAgentClient.deleteIscsiTarget(iscsiTargetCreated01.getTargetName());

        assertThat(allIscsiTargetsContains(iscsiTargetCreated01), is(false));
        assertThat(allIscsiTargetsContains(iscsiTargetCreated02), is(true));
    }

    @Test
    public void canConcurrentlyCreateIscsiTargets() {
        List<Callable<AccessibleIscsiTarget>> callables = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            callables.add(createCallableToCreateTarget(anIscsiTarget().build()));
        }

        List<AccessibleIscsiTarget> targetsCreated = TaskHelper.submitAllTasksToExecutor(callables);

        List<AccessibleIscsiTarget> targetsRetrieved = storageAgentClient.getAllIscsiTargets();
        for (AccessibleIscsiTarget targetCreated : targetsCreated) {
            assertThat(targetsRetrieved, hasItem(reflectionMatching(targetCreated)));
        }
    }

    @Test
    public void canConcurrentlyDeleteIscsiTargets() {
        List<AccessibleIscsiTarget> targetsCreated = new ArrayList<>();
        for (int createCount = 0; createCount < 5; createCount++) {
            targetsCreated.add(storageAgentClient.createIscsiTarget(anIscsiTarget().build()));
        }

        List<Callable<Void>> callables = new ArrayList<>();
        for (AccessibleIscsiTarget targetToDelete : targetsCreated) {
            callables.add(createCallableToDeleteTarget(targetToDelete));
        }

        TaskHelper.submitAllTasksToExecutor(callables);

        List<AccessibleIscsiTarget> targetsRetrieved = storageAgentClient.getAllIscsiTargets();
        for (AccessibleIscsiTarget targetToDelete : targetsCreated) {
            assertThat(targetsRetrieved, not(hasItem(reflectionMatching(targetToDelete))));
        }
    }

    private Callable<AccessibleIscsiTarget> createCallableToCreateTarget(final IscsiTarget iscsiTarget) {
        return new Callable<AccessibleIscsiTarget>() {
            @Override
            public AccessibleIscsiTarget call() throws Exception {
                return new StorageAgentClient(createClient()).createIscsiTarget(iscsiTarget);
            }
        };
    }

    private Callable<Void> createCallableToDeleteTarget(final AccessibleIscsiTarget uniqueIscsiTarget) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                new StorageAgentClient(createClient()).deleteIscsiTarget(uniqueIscsiTarget.getTargetName());
                return null;
            }
        };
    }
    private boolean allIscsiTargetsContains(AccessibleIscsiTarget targetToCheck) {
        List<AccessibleIscsiTarget> targetsOnServer = storageAgentClient.getAllIscsiTargets();
        for (AccessibleIscsiTarget targetOnServer : targetsOnServer) {
            try {
                ReflectionAssert.assertReflectionEquals(targetToCheck, targetOnServer);
                return true;
            } catch (AssertionFailedError assertionFailedError) {
            }
        }

        return false;
    }

    private void deleteAllTargets() {
        for (AccessibleIscsiTarget accessibleIscsiTarget : storageAgentClient.getAllIscsiTargets()) {
            storageAgentClient.deleteIscsiTarget(accessibleIscsiTarget.getTargetName());
        }
    }
}