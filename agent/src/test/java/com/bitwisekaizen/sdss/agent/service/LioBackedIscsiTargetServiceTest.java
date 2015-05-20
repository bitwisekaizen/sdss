package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTargetBuilder.anAccessibleIscsiTarget;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LioBackedIscsiTargetServiceTest {

    private LioBackedIscsiTargetService lioBackedIscsiTargetService;

    @Mock
    private ChefSoloRunner chefSoloRunner;

    @Mock
    private IscsiChefNodeFileGenerator nodeFileGenerator;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);

        lioBackedIscsiTargetService = new LioBackedIscsiTargetService(nodeFileGenerator, chefSoloRunner);
    }

    @Test
    public void canUpdateTargets() throws Exception {
        List<AccessibleIscsiTarget> iscsiTargets = asList(anAccessibleIscsiTarget().build(),
                anAccessibleIscsiTarget().build(), anAccessibleIscsiTarget().build());
        File nodeFile = getTempFile();

        when(nodeFileGenerator.generateFile(iscsiTargets)).thenReturn(nodeFile);

        lioBackedIscsiTargetService.updateTargets(iscsiTargets);
        verify(chefSoloRunner).runUsingNodeFile(nodeFile);
    }

    private File getTempFile() throws IOException {
        File nodeFile = File.createTempFile("temp", "temp");
        nodeFile.deleteOnExit();
        return nodeFile;
    }
}