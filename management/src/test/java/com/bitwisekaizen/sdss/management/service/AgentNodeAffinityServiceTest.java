package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntity;
import com.bitwisekaizen.sdss.management.repository.AgentNodeAffinityRepository;
import com.bitwisekaizen.sdss.management.validation.DtoValidator;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.bitwisekaizen.sdss.management.dto.AgentNodeAffinityBuilder.anAgentNodeAffinity;
import static com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntityBuilder.anAgentNodeAffinityEntity;
import static com.bitwisekaizen.sdss.management.util.ReflectionMatcherUtil.reflectionMatching;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Test
public class AgentNodeAffinityServiceTest {
    @Mock
    private AgentNodeAffinityRepository agentNodeAffinityRepository;

    @Mock
    private DtoValidator dtoValidator;

    private AgentNodeAffinityService agentNodeAffinityService;


    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        agentNodeAffinityService = new AgentNodeAffinityService(agentNodeAffinityRepository, dtoValidator);
    }

    @Test
    public void canCreateAgentAffinity() {
        AgentNodeAffinity affinityToCreate = anAgentNodeAffinity().build();
        when(agentNodeAffinityRepository.findOne(affinityToCreate.getAffinityKey())).thenReturn(null);

        AgentNodeAffinity affinityCreated = agentNodeAffinityService.createOrUpdateAgentAffinity(affinityToCreate);

        verify(agentNodeAffinityRepository).save(argThat(matching(affinityToCreate)));
        verify(dtoValidator).validate(affinityToCreate);
        assertThat(affinityCreated, reflectionMatching(affinityToCreate));
        verify(agentNodeAffinityRepository).save(argThat(matching(affinityCreated)));
    }

    @Test
    public void canUpdateAgentAffinity() throws Exception {
        AgentNodeAffinity affinityToUpdate = anAgentNodeAffinity().build();
        AgentNodeAffinityEntity affinityEntity = anAgentNodeAffinityEntity().
                withAffinityKey(affinityToUpdate.getAffinityKey()).build();
        when(agentNodeAffinityRepository.findOne(affinityToUpdate.getAffinityKey())).thenReturn(affinityEntity);
        when(agentNodeAffinityRepository.save(affinityEntity)).thenReturn(affinityEntity);

        AgentNodeAffinity affinityUpdated = agentNodeAffinityService.createOrUpdateAgentAffinity(affinityToUpdate);

        verify(dtoValidator).validate(affinityToUpdate);
        assertThat(affinityUpdated, reflectionMatching(affinityToUpdate));
        verify(agentNodeAffinityRepository).save(affinityEntity);
    }

    @Test
    public void canGetAgentAffinity() throws Exception {
        AgentNodeAffinityEntity affinityEntity = anAgentNodeAffinityEntity().build();
        when(agentNodeAffinityRepository.findOne(affinityEntity.getAffinityKey())).thenReturn(affinityEntity);

        AgentNodeAffinity affinityFound = agentNodeAffinityService.getAgentAffinity(affinityEntity.getAffinityKey());
        assertThat(affinityFound, matching(affinityEntity));
    }


    @Test
    public void throwExceptionIfCannotFindAgentAffinity() throws Exception {
        when(agentNodeAffinityRepository.findOne(anyString())).thenReturn(null);

        String affinityKey = UUID.randomUUID().toString();
        try {
            agentNodeAffinityService.getAgentAffinity(affinityKey);
            fail("Expected AgentNodeAffinityNotFoundException");
        } catch (AgentNodeAffinityNotFoundException e) {
        }
    }

    @Test
    public void canDeleteAgentAffinity() throws Exception {
        AgentNodeAffinityEntity affinityEntity = anAgentNodeAffinityEntity().build();
        when(agentNodeAffinityRepository.findOne(affinityEntity.getAffinityKey())).thenReturn(affinityEntity);

        agentNodeAffinityService.deleteAgentAffinity(affinityEntity.getAffinityKey());

        verify(agentNodeAffinityRepository).delete(affinityEntity);
    }

    @Test
    public void throwExceptionIfCannotFindAgentAffinityDuringDeletion() throws Exception {
        when(agentNodeAffinityRepository.findOne(anyString())).thenReturn(null);

        String affinityKey = UUID.randomUUID().toString();
        try {
            agentNodeAffinityService.deleteAgentAffinity(affinityKey);
            fail("Expected AgentNodeAffinityNotFoundException");
        } catch (AgentNodeAffinityNotFoundException e) {
        }
    }


    @Test
    public void canGetAllAgentAffinities() throws Exception {
        List<AgentNodeAffinityEntity> entities = Arrays.asList(anAgentNodeAffinityEntity().build(),
                anAgentNodeAffinityEntity().build(), anAgentNodeAffinityEntity().build());
        when(agentNodeAffinityRepository.findAll()).thenReturn(entities);

        List<AgentNodeAffinity> allAgentAffinities = agentNodeAffinityService.getAllAgentAffinities();

        for (AgentNodeAffinityEntity entity : entities) {
            assertThat(allAgentAffinities, hasItem(matching(entity)));
        }
    }

    private TypeSafeMatcher<AgentNodeAffinityEntity> matching(final AgentNodeAffinity expectedAffinity) {
        return new TypeSafeMatcher<AgentNodeAffinityEntity>() {
            @Override
            protected boolean matchesSafely(AgentNodeAffinityEntity actualAffinity) {
                if (!expectedAffinity.getAffinityKey().equals(actualAffinity.getAffinityKey())) {
                    return false;
                }

                if (!expectedAffinity.getAgentNode().equals(actualAffinity.getAgentNode())) {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    private TypeSafeMatcher<AgentNodeAffinity> matching(final AgentNodeAffinityEntity expectedAffinity) {
        return new TypeSafeMatcher<AgentNodeAffinity>() {
            @Override
            protected boolean matchesSafely(AgentNodeAffinity actualAffinity) {
                if (!expectedAffinity.getAffinityKey().equals(actualAffinity.getAffinityKey())) {
                    return false;
                }

                if (!expectedAffinity.getAgentNode().equals(actualAffinity.getAgentNode())) {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}