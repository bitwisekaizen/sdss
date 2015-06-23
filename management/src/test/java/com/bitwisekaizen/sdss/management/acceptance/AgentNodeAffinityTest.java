package com.bitwisekaizen.sdss.management.acceptance;

import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.UUID;

import static com.bitwisekaizen.sdss.management.dto.AgentNodeAffinityBuilder.anAgentNodeAffinity;
import static com.bitwisekaizen.sdss.management.util.ReflectionMatcherUtil.reflectionMatching;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.fail;

@Test
public class AgentNodeAffinityTest extends AbstractAcceptanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentNodeAffinityTest.class);

    @AfterMethod
    @BeforeMethod
    public void afterMethod() {
        affinityOperations.deleteAllAgentNodeAffinities();
    }

    @Test
    public void canCreateAffinity() {
        AgentNodeAffinity affinityToCreate = anAgentNodeAffinity().build();

        AgentNodeAffinity affinityCreated = affinityOperations.createOrUpdateAgentNodeAffinity(affinityToCreate);

        assertThat(affinityCreated, notNullValue());
        assertThat(affinityCreated, reflectionMatching(affinityToCreate));
        AgentNodeAffinity affinityRetrieved =
                affinityOperations.getAgentNodeAffinity(affinityToCreate.getAffinityKey());
        assertThat(affinityRetrieved, notNullValue());
        assertThat(affinityRetrieved, reflectionMatching(affinityCreated));
    }

    @Test
    public void canUpdateAffinity() {
        AgentNodeAffinity affinityToUpdate = affinityOperations.createOrUpdateAgentNodeAffinity(anAgentNodeAffinity().build());
        affinityToUpdate.setAgentNode(UUID.randomUUID().toString());

        AgentNodeAffinity affinityUpdated = affinityOperations.createOrUpdateAgentNodeAffinity(affinityToUpdate);

        assertThat(affinityUpdated, notNullValue());
        assertThat(affinityUpdated, reflectionMatching(affinityToUpdate));
        AgentNodeAffinity affinityRetrieved =
                affinityOperations.getAgentNodeAffinity(affinityToUpdate.getAffinityKey());
        assertThat(affinityRetrieved, notNullValue());
        assertThat(affinityRetrieved, reflectionMatching(affinityToUpdate));
    }

    @Test(expectedExceptions = BadRequestException.class)
    public void cannotCreateAffinityIfValidationFails() {
        affinityOperations.createOrUpdateAgentNodeAffinity(anAgentNodeAffinity().withStorageAgentNode("").build());
    }

    @Test
    public void canDeleteAgentNodeAffinity() {
        AgentNodeAffinity affinityCreated =
                affinityOperations.createOrUpdateAgentNodeAffinity(anAgentNodeAffinity().build());

        affinityOperations.deleteAgentNodeAffinity(affinityCreated);

        try {
            affinityOperations.getAgentNodeAffinity(affinityCreated.getAffinityKey());
            fail("Expected NotFoundException but no exception thrown.");
        } catch (NotFoundException e) {
        }
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void cannotDeleteAgentNodeAffinityIfItDoesNotExist() {
        affinityOperations.deleteAgentNodeAffinity(anAgentNodeAffinity().build());
    }
}