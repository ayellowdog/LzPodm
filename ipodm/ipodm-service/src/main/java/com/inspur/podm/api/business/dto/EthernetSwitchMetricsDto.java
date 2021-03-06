/*
 * Copyright (c) 2017-2018 inspur Corporation
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"@odata.context", "@odata.id", "@odata.type", "id", "name", "description", "health", "actions", "oem"})
@JsonIgnoreProperties({"Oem"})
public final class EthernetSwitchMetricsDto extends RedfishDto {
    private final Actions actions = new Actions();
    private String health;

    public EthernetSwitchMetricsDto() {
        super("#EthernetSwitchMetrics.v1_0_0.EthernetSwitchMetrics");
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public Actions getActions() {
        return actions;
    }

    public final class Actions extends RedfishActionsDto {
    }
}
