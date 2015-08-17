package com.bitwisekaizen.sdss.agent.config;

import com.bitwisekaizen.sdss.agent.entity.IscsiTargetEntity;
import com.bitwisekaizen.sdss.agent.repository.IscsiTargetEntityRepository;
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

    // Every 30 minutes, update the diskspace provisioned
    @Scheduled(fixedDelay = 30*60*1000)
    @Transactional(readOnly = true)
    public void updateProvisionedDiskSpaceValue() {
        Iterable<IscsiTargetEntity> targetEntities = iscsiTargetEntityRepository.findAll();

        long diskSpaceInMb = 0;
        for (IscsiTargetEntity iscsiTargetEntity : targetEntities) {
            diskSpaceInMb += iscsiTargetEntity.getCapacityInMb();
        }

        provisionedDiskSpaceInGb.set((long) (diskSpaceInMb / MB_TO_GB_FACTOR));
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
        } else {
            builder.down();
        }
    }
}
