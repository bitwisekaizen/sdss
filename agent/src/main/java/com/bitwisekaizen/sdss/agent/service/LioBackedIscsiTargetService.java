package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Service that manages ISCSI targets using LIO.
 */
@Service
public class LioBackedIscsiTargetService {

    private IscsiChefNodeFileGenerator nodeFileGenerator;
    private ChefSoloRunner chefSoloRunner;

    @Autowired
    public LioBackedIscsiTargetService(IscsiChefNodeFileGenerator nodeFileGenerator, ChefSoloRunner chefSoloRunner) {
        this.nodeFileGenerator = nodeFileGenerator;
        this.chefSoloRunner = chefSoloRunner;
    }

    /**
     * Update LIO-backed storage with the specified ISCSI targets.
     *
     * @param iscsiTargets targets to update.
     */
    public void updateTargets(List<AccessibleIscsiTarget> iscsiTargets) {
        File nodeFile = nodeFileGenerator.generateFile(iscsiTargets);
        chefSoloRunner.runUsingNodeFile(nodeFile);
    }
}
