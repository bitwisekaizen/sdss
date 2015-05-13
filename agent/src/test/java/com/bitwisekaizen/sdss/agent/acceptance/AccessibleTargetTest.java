package com.bitwisekaizen.sdss.agent.acceptance;

import com.bitwisekaizen.sdss.agent.config.ApplicationConfig;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder;
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

import java.util.List;

import static com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTargetBuilder.anAccessibleIscsiTarget;
import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebAppConfiguration
@IntegrationTest
// See http://stackoverflow.com/questions/25537436/acceptance-testing-a-spring-boot-web-app-with-testng
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@Test(groups = "unimplemented")
@TestPropertySource(locations = {"classpath:test.properties"})
public class AccessibleTargetTest extends AbstractAcceptanceTest {

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
        assertThat(iscsiTargetCreated.getIscsiTarget(), reflectionEqualsTo(iscsiTargetToCreate));

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

    private boolean allIscsiTargetsContains(AccessibleIscsiTarget targetToCheck) {
        List<AccessibleIscsiTarget> targetsOnServer = storageAgentClient.getAllIscsiTargets();
        for (AccessibleIscsiTarget targetOnServer : targetsOnServer) {
            if (EqualsBuilder.reflectionEquals(targetToCheck, targetOnServer)) {
                return true;
            }
        }

        return false;
    }

    private TypeSafeMatcher<Object> reflectionEqualsTo(final Object expectedObject) {
        return new TypeSafeMatcher<Object>() {
            public Object actualObject;

            @Override
            protected boolean matchesSafely(Object actualObject) {
                this.actualObject = actualObject;
                return EqualsBuilder.reflectionEquals(actualObject, expectedObject);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected " + expectedObject.toString() + " but got " + actualObject);
            }
        };
    }

    private void deleteAllTargets() {
        for (AccessibleIscsiTarget accessibleIscsiTarget : storageAgentClient.getAllIscsiTargets()) {
            storageAgentClient.deleteIscsiTarget(accessibleIscsiTarget.getTargetName());
        }
    }
}