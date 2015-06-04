package com.bitwisekaizen.sdss.agent.service;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

/**
 * Chef solo runner that runs chef solo.  The documentation for chef-solo is here:
 * https://docs.chef.io/chef_solo.html
 */
public class ChefSoloRunnerTest {
    private ChefSoloRunner chefSoloRunner;
    private File nodeFile;
    private String nodeFileContent = "content-" + UUID.randomUUID();

    @BeforeMethod
    private void setupMethod() throws Exception {
        String NO_SUDO = null;
        chefSoloRunner = new ChefSoloRunner(false, "cat", "%s", "someurl");

        nodeFile = File.createTempFile("node", "file");
        FileUtils.write(nodeFile, nodeFileContent);
    }

    @Test
    public void chefSoloRunnerShouldReturnCorrectResultsForSuccessfulRun() throws Exception {
        ChefSoloRunnerResult result = chefSoloRunner.runUsingNodeFile(nodeFile);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getOutput(), equalTo(nodeFileContent));
    }

    @Test
    public void chefSoloRunnerShouldReturnCorrectResultsForUnsuccessfulRun() throws Exception {
        File tempFile = File.createTempFile("fake", "file");
        tempFile.delete();
        ChefSoloRunnerResult result = chefSoloRunner.runUsingNodeFile(tempFile);

        assertThat(result.isSuccess(), equalTo(false));
        assertThat(result.getOutput(), containsString("No such file"));
    }
}