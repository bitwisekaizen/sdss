package com.bitwisekaizen.sdss.management.repository;

import com.bitwisekaizen.sdss.management.entity.InitiatorIqnEntity;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.data.repository.CrudRepository;

@VisibleForTesting
public interface InitiatorIqnEntityRepository extends CrudRepository<InitiatorIqnEntity, String> {
}
