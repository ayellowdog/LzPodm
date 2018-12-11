/*
 * Copyright (c) 2015-2018 inspur Corporation
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

package com.inspur.podm.service.rest.redfish.json.templates.assembly;

import static com.inspur.podm.api.business.services.context.ContextType.CHASSIS;
import static com.inspur.podm.api.business.services.context.ContextType.PROCESSOR;
import static com.inspur.podm.api.business.services.context.UriToContextConverter.getContextFromUri;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.common.types.InstructionSet;
import com.intel.podm.common.types.ProcessorBrand;
import com.intel.podm.common.types.ProcessorType;
import com.intel.podm.common.types.deserialization.PositiveIntegerDeserializer;

public final class RequestedProcessorImpl implements RequestedNode.Processor {
    @JsonProperty
    private String model;

    @JsonProperty
    @JsonDeserialize(using = PositiveIntegerDeserializer.class)
    private Integer totalCores;

    @JsonProperty("AchievableSpeedMHz")
    @JsonDeserialize(using = PositiveIntegerDeserializer.class)
    private Integer achievableSpeedMhz;

    @JsonProperty
    private InstructionSet instructionSet;

    @JsonProperty
    private ProcessorOem oem = new ProcessorOem();

    @JsonProperty
    private ProcessorType processorType;

    private Context resourceContext;

    private Context chassisContext;

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public ProcessorBrand getBrand() {
        return oem == null ? null : oem.brand;
    }

    @Override
    public Integer getTotalCores() {
        return totalCores;
    }

    @Override
    public Integer getAchievableSpeedMhz() {
        return achievableSpeedMhz;
    }

    @Override
    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    @Override
    public ProcessorType getProcessorType() {
        return processorType;
    }

    @Override
    public Context getResourceContext() {
        return resourceContext;
    }

    @JsonProperty("Resource")
    public void setResourceContext(ODataId resource) {
        if (resource == null) {
            return;
        }

        resourceContext = getContextFromUri(resource.toUri(), PROCESSOR);
    }

    @Override
    public Context getChassisContext() {
        return chassisContext;
    }

    @JsonProperty("Chassis")
    public void setChassisContext(ODataId chassis) {
        if (chassis == null) {
            return;
        }

        chassisContext = getContextFromUri(chassis.toUri(), CHASSIS);
    }

    @Override
    public List<String> getCapabilities() {
        return oem == null ? null : oem.capabilities;
    }

    private static final class ProcessorOem {
        @JsonProperty
        private ProcessorBrand brand;

        @JsonProperty
        private List<String> capabilities;
    }
}
