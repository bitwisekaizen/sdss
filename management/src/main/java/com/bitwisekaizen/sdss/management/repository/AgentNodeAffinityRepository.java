package com.bitwisekaizen.sdss.management.repository;

import com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AgentNodeAffinityRepository extends CrudRepository<AgentNodeAffinityEntity, String> {

}
