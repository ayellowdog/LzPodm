/*
 * Copyright (c) 2016-2018 Intel Corporation
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

package com.intel.podm.business.entities.redfish.embeddables;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.intel.podm.common.types.PowerInputType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

@Embeddable
@SuppressWarnings({"checkstyle:MethodCount"})
public class InputRange {
    @Column(name = "input_type")
    @Enumerated(STRING)
    private PowerInputType inputType;

    @Column(name = "minimum_voltage")
    private BigDecimal minimumVoltage;

    @Column(name = "maximum_voltage")
    private BigDecimal maximumVoltage;

    @Column(name = "output_wattage")
    private BigDecimal outputWattage;

    @Column(name = "minimum_frequency_hz")
    private BigDecimal minimumFrequencyHz;

    @Column(name = "maximum_frequency_hz")
    private BigDecimal maximumFrequencyHz;

    @Column(name = "oem")
    private String oem;

    public PowerInputType getInputType() {
        return inputType;
    }

    public void setInputType(PowerInputType inputType) {
        this.inputType = inputType;
    }

    public BigDecimal getMinimumVoltage() {
        return minimumVoltage;
    }

    public void setMinimumVoltage(BigDecimal minimumVoltage) {
        this.minimumVoltage = minimumVoltage;
    }

    public BigDecimal getMaximumVoltage() {
        return maximumVoltage;
    }

    public void setMaximumVoltage(BigDecimal maximumVoltage) {
        this.maximumVoltage = maximumVoltage;
    }

    public BigDecimal getOutputWattage() {
        return outputWattage;
    }

    public void setOutputWattage(BigDecimal outputWattage) {
        this.outputWattage = outputWattage;
    }

    public BigDecimal getMinimumFrequencyHz() {
        return minimumFrequencyHz;
    }

    public void setMinimumFrequencyHz(BigDecimal minimumFrequencyHz) {
        this.minimumFrequencyHz = minimumFrequencyHz;
    }

    public BigDecimal getMaximumFrequencyHz() {
        return maximumFrequencyHz;
    }

    public void setMaximumFrequencyHz(BigDecimal maximumFrequencyHz) {
        this.maximumFrequencyHz = maximumFrequencyHz;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InputRange that = (InputRange) o;

        return new EqualsBuilder()
            .append(inputType, that.inputType)
            .append(minimumVoltage, that.minimumVoltage)
            .append(maximumVoltage, that.maximumVoltage)
            .append(outputWattage, that.outputWattage)
            .append(minimumFrequencyHz, that.minimumFrequencyHz)
            .append(maximumFrequencyHz, that.maximumFrequencyHz)
            .append(oem, that.oem)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(inputType)
            .append(minimumVoltage)
            .append(maximumVoltage)
            .append(outputWattage)
            .append(minimumFrequencyHz)
            .append(maximumFrequencyHz)
            .append(oem)
            .toHashCode();
    }
}
