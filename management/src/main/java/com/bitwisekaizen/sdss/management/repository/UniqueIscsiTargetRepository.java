package com.bitwisekaizen.sdss.management.repository;

import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import org.springframework.data.repository.CrudRepository;

public interface UniqueIscsiTargetRepository extends CrudRepository<UniqueIscsiTargetEntity, String> {
    UniqueIscsiTargetEntity findByUuid(String uuid);
    UniqueIscsiTargetEntity findByTargetName(String targetName);
}
