package com.bitwisekaizen.sdss.agent.config;

import com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntity;
import com.bitwisekaizen.sdss.agent.repository.IscsiTargetEntityRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.DiskSpaceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntityBuilder.anIscsiTargetEntity;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class AgentDiskSpaceHealthIndicatorTest {

    @Mock
    private IscsiTargetEntityRepository iscsiTargetEntityRepository;

    private AgentDiskSpaceHealthIndicator agentDiskSpaceHealthIndicator;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        DiskSpaceHealthIndicatorProperties diskSpaceHealthIndicatorProperties =
                new DiskSpaceHealthIndicatorProperties();

        agentDiskSpaceHealthIndicator = new AgentDiskSpaceHealthIndicator(diskSpaceHealthIndicatorProperties,
                iscsiTargetEntityRepository);
    }

    @Test
    public void canUpdateProvisionedDiskspaceValue() {
        List<IscsiTargetEntity> iscsiTargets =
                asList(anIscsiTargetEntity().build(), anIscsiTargetEntity().build(), anIscsiTargetEntity().build());
        when(iscsiTargetEntityRepository.findAll()).thenReturn(iscsiTargets);

        agentDiskSpaceHealthIndicator.updateProvisionedDiskSpaceValue();

        Health health = agentDiskSpaceHealthIndicator.health();

        assertThat((long) health.getDetails().get("provisionedDiskspaceInGb"), is(greaterThan(10l)));
        assertThat((long) health.getDetails().get("provisionedDiskspaceInGb"), is(lessThan(60l)));
    }

    @Test
    public void canGetHealth() {
        List<IscsiTargetEntity> iscsiTargets =
                asList(anIscsiTargetEntity().build(), anIscsiTargetEntity().build(), anIscsiTargetEntity().build());
        when(iscsiTargetEntityRepository.findAll()).thenReturn(iscsiTargets);

        Health health = agentDiskSpaceHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.UP));
        assertThat((long) health.getDetails().get("provisionedDiskspaceInGb"), is(0l));
        assertThat((long) health.getDetails().get("totalDiskInGb"), is(greaterThan(1l)));
        assertThat((long) health.getDetails().get("freeDiskspaceInGb"), is(greaterThan(1l)));
    }
}