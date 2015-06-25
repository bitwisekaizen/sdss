package com.bitwisekaizen.sdss.agentclient;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Spec that describes the ISCSI target
 */
@ApiModel(description = "Spec that describes an ISCSI target")
public class IscsiTarget {

    private List<String> hostIscsiQualifiedNames;

    @Min(value = 500, message = "capacity.size.too.small")
    @Max(value = 1000000, message = "capacity.size.too.large")
    private int capacityInMb;

    @NotEmpty(message = "target.name.empty")
    private String targetName;

    @NotEmpty(message = "affinity.key.empty")
    private String affinityKey;

    // Json serialization
    private IscsiTarget() {
    }

    public IscsiTarget(List<String> hostIscsiQualifiedNames, int capacityInMb, String targetName, String affinityKey) {
        this.hostIscsiQualifiedNames = hostIscsiQualifiedNames;
        this.capacityInMb = capacityInMb;
        this.targetName = targetName;
        this.affinityKey = affinityKey;
    }

    /**
     * Get the LUN target capacity in MB.
     *
     * @return the LUN target capacity in MB.
     */
    @ApiModelProperty(value = "LUN size in MM", required = true)
    public int getCapacityInMb() {
        return capacityInMb;
    }

    /**
     * Get the list of ISCSI qualified names of the hosts, permissible to access this target.
     *
     * @return the list of ISCSI qualified names of the hosts, permissible to access this target.
     */
    @ApiModelProperty(value = "List of ISCSI qualified names (IQNs) of he host, permissible to " +
            "access the target", required = true)
    public List<String> getHostIscsiQualifiedNames() {
        return hostIscsiQualifiedNames;
    }

    /**
     * Get the target name.
     *
     * @return target name.
     */
    @ApiModelProperty(value = "Name of the target", required = true)
    public String getTargetName() {
        return targetName;
    }

    /**
     * Get the affinity key (i.e. user name) that determines where to create target on.
     *
     * @return affinity key.
     */
    @ApiModelProperty(value = "Affinity key (i.e. user name) that determines where to create target on.", required = true)
    public String getAffinityKey() {
        return affinityKey;
    }
}
