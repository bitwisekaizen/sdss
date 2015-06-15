package com.bitwisekaizen.sdss.management.config;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;

import javax.ws.rs.client.WebTarget;
import java.util.*;

/**
 * Stubbed out StorageAgentClient that stores the targets in memory rather than contacting the actual agent.  This is
 * used for testing.
 */
public class InMemoryStorageAgentClient extends StorageAgentClient {

    private List<AccessibleIscsiTarget> targets = new ArrayList<>();
    private List<String> storageNetworkAddress = Arrays.asList("1.1.1.1", "2.2.2.2");

    public InMemoryStorageAgentClient(WebTarget webTarget) {
        super(webTarget);
    }

    @Override
    public synchronized AccessibleIscsiTarget createIscsiTarget(IscsiTarget iscsiTarget) {
        AccessibleIscsiTarget accessibleIscsiTarget = new AccessibleIscsiTarget(iscsiTarget, storageNetworkAddress);
        targets.add(accessibleIscsiTarget);

        return accessibleIscsiTarget;
    }

    @Override
    public synchronized void deleteIscsiTarget(String targetName) {
        Iterator<AccessibleIscsiTarget> iterator = targets.iterator();
        while (iterator.hasNext()) {
            AccessibleIscsiTarget accessibleIscsiTarget = iterator.next();
            if (accessibleIscsiTarget.getIscsiTarget().getTargetName().equals(targetName)) {
                iterator.remove();
                return;
            }
        }
    }

    @Override
    public synchronized List<AccessibleIscsiTarget> getAllIscsiTargets() {
        return Collections.unmodifiableList(targets);
    }
}
