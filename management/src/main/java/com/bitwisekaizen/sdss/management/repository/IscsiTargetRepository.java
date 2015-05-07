package com.bitwisekaizen.sdss.management.repository;

import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import org.springframework.data.repository.CrudRepository;

public interface IscsiTargetRepository extends CrudRepository<UniqueIscsiTargetEntity, Long> {
    UniqueIscsiTargetEntity findByUuid(String uuid);
}
