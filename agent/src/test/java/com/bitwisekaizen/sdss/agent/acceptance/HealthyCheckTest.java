package com.bitwisekaizen.sdss.agent.acceptance;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.HealthCheck;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

@Test
public class HealthyCheckTest extends AbstractAcceptanceTest {

    @BeforeMethod(alwaysRun = true)
    @AfterMethod(alwaysRun = true)
    public void setupMethod() {
        deleteAllTargets();
    }

    @Test
    public void canGetHealthCheck() {
        IscsiTarget iscsiTargetToCreate = anIscsiTarget().build();
        storageAgentClient.createIscsiTarget(iscsiTargetToCreate);

        HealthCheck healthCheck = storageAgentClient.getHealthCheck();

        assertThat(healthCheck.getDiskspaceHealthyCheck().getStatus(), is("UP"));
        assertThat(healthCheck.getDiskspaceHealthyCheck().getFreeDiskspaceInGb(), is(greaterThan(1)));
        assertThat(healthCheck.getDiskspaceHealthyCheck().getTotalDiskInGb(), is(greaterThan(1)));
    }

    private void deleteAllTargets() {
        for (AccessibleIscsiTarget accessibleIscsiTarget : storageAgentClient.getAllIscsiTargets()) {
            storageAgentClient.deleteIscsiTarget(accessibleIscsiTarget.getTargetName());
        }
    }
}