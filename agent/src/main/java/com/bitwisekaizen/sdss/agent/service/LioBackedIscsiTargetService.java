package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that manages ISCSI targets using LIO.
 */
@Service
public class LioBackedIscsiTargetService {

    public void updateTargets(List<AccessibleIscsiTarget> iscsiTargets) {
    }
}
