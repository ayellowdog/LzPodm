/*
 * Copyright (c) 2016-2018 inspur Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inspur.podm.api.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.intel.podm.common.types.Status;

import java.util.HashSet;
import java.util.Set;

@JsonPropertyOrder({"@odata.id", "memberId", "name", "redundancyEnabled", "redundancySet", "mode", "status", "minNumNeeded", "maxNumSupported"})
@SuppressWarnings({"checkstyle:MethodCount"})
public final class RedundancyDto implements Comparable<RedundancyDto> {
    @JsonUnwrapped
    @JsonProperty("@odata.id")
    private ODataId oDataId;
    private String memberId;
    private String name;
    private Boolean redundancyEnabled;
    private Set<Context> redundancySet = new HashSet<>();
    private String mode;
    private Status status;
    private Integer minNumNeeded;
    private Integer maxNumSupported;

    public ODataId getoDataId() {
        return oDataId;
    }

    public void setoDataId(ODataId oDataId) {
        this.oDataId = oDataId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRedundancyEnabled() {
        return redundancyEnabled;
    }

    public void setRedundancyEnabled(Boolean redundancyEnabled) {
        this.redundancyEnabled = redundancyEnabled;
    }

    public Set<Context> getRedundancySet() {
        return redundancySet;
    }

    public void setRedundancySet(Set<Context> redundancySet) {
        this.redundancySet = redundancySet;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getMinNumNeeded() {
        return minNumNeeded;
    }

    public void setMinNumNeeded(Integer minNumNeeded) {
        this.minNumNeeded = minNumNeeded;
    }

    public Integer getMaxNumSupported() {
        return maxNumSupported;
    }

    public void setMaxNumSupported(Integer maxNumSupported) {
        this.maxNumSupported = maxNumSupported;
    }

    @Override
    public int compareTo(RedundancyDto other) {
        return this.getMemberId().compareTo(other.getMemberId());
    }
}
