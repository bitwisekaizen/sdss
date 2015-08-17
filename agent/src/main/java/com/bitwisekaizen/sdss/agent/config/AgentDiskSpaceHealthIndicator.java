package com.bitwisekaizen.sdss.agent.config;

import com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntity;
import com.bitwisekaizen.sdss.agent.repository.IscsiTargetEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.DiskSpaceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.Health;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;


@Service("diskSpaceHealthIndicator")
public class AgentDiskSpaceHealthIndicator extends AbstractHealthIndicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentDiskSpaceHealthIndicator.class);

    public static final double MB_TO_GB_FACTOR = Math.pow(2, 10);
    public static final double BYTE_TO_GB_FACTOR = Math.pow(2, 30);

    private DiskSpaceHealthIndicatorProperties properties;
    private IscsiTargetEntityRepository iscsiTargetEntityRepository;

    private AtomicLong provisionedDiskSpaceInGb = new AtomicLong(0l);

    @Autowired
    public AgentDiskSpaceHealthIndicator(DiskSpaceHealthIndicatorProperties properties,
                                         IscsiTargetEntityRepository iscsiTargetEntityRepository) {
        this.properties = properties;
        this.iscsiTargetEntityRepository = iscsiTargetEntityRepository;
    }

//    @Scheduled(fixedDelay=30000)
    // Every 30 minutes, update the diskspace provisioned
    @Scheduled(fixedDelay = 30*60*1000)
    @Transactional(readOnly = true)
    public void updateProvisionedDiskSpaceValue() {
        LOGGER.info("Running disk space checker");
        Iterable<IscsiTargetEntity> targetEntities = iscsiTargetEntityRepository.findAll();

        long diskSpaceInMb = 0;
        for (IscsiTargetEntity iscsiTargetEntity : targetEntities) {
            diskSpaceInMb += iscsiTargetEntity.getCapacityInMb();
        }

        long diskSpaceInGb = (long) (diskSpaceInMb / MB_TO_GB_FACTOR);
        provisionedDiskSpaceInGb.set(diskSpaceInGb);
        LOGGER.info("Setting disk space to be in GB " + diskSpaceInGb);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        File file = this.properties.getPath();

        long diskFreeInBytes = file.getFreeSpace();
        long freeDiskInGb = (long) (diskFreeInBytes / BYTE_TO_GB_FACTOR);
        long totalDiskInGb = (long) (file.getTotalSpace() / BYTE_TO_GB_FACTOR);
        builder.withDetail("totalDiskInGb", totalDiskInGb)
                .withDetail("freeDiskspaceInGb", freeDiskInGb)
                .withDetail("provisionedDiskspaceInGb", provisionedDiskSpaceInGb.get());

        if (diskFreeInBytes >= this.properties.getThreshold()) {
            builder.up();
            LOGGER.info("It's up");
        } else {
            builder.down();
            LOGGER.info("It's down");
        }
    }
}
