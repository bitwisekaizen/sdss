package com.bitwisekaizen.sdss.agentclient;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Health check of the agent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthCheck {

    @JsonProperty("diskSpace")
    private DiskspaceHealthyCheck diskspaceHealthyCheck;

    // Json serialization
    private HealthCheck() {
    }

    public HealthCheck(DiskspaceHealthyCheck diskspaceHealthyCheck) {
        this.diskspaceHealthyCheck = diskspaceHealthyCheck;
    }

    public DiskspaceHealthyCheck getDiskspaceHealthyCheck() {
        return diskspaceHealthyCheck;
    }

    public static class DiskspaceHealthyCheck {
        private String status;
        private int totalDiskInGb;
        private int freeDiskspaceInGb;
        private int provisionedDiskspaceInGb;

        // Json serialization
        private DiskspaceHealthyCheck() {
        }

        public String getStatus() {
            return status;
        }

        public int getTotalDiskInGb() {
            return totalDiskInGb;
        }

        public int getFreeDiskspaceInGb() {
            return freeDiskspaceInGb;
        }

        public int getProvisionedDiskspaceInGb() {
            return provisionedDiskspaceInGb;
        }
    }
}
