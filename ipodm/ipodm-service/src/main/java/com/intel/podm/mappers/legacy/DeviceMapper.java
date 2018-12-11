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

package com.intel.podm.mappers.legacy;

import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.client.resources.legacy.DeviceResource;
import com.intel.podm.client.resources.redfish.RedfishLocationObject;
import com.intel.podm.mappers.EntityMapper;
import com.intel.podm.mappers.subresources.RedfishLocationMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static com.intel.podm.common.utils.Converters.convertGibToBytes;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

@Dependent
public class DeviceMapper extends EntityMapper<DeviceResource, Drive> {
    private static final String INFO_FORMAT = "BusInfo";

    @Inject
    RedfishLocationMapper redfishLocationMapper;

    public DeviceMapper() {
        super(DeviceResource.class, Drive.class);
    }

    @Override
    protected void performNotAutomatedMapping(DeviceResource source, Drive target) {
        super.performNotAutomatedMapping(source, target);
        target.setProtocol(source.getInterface());
        target.setCapacityBytes(ofNullable(convertGibToBytes(source.getCapacityGib())).map(BigDecimal::longValue).orElse(null));
        target.setMediaType(source.getType());
        target.setRotationSpeedRpm(new BigDecimal(source.getRpm()));

        redfishLocationMapper.map(createRedfishLocationObjectList(source), target.getLocation(), target::addLocation);
    }

    private List<RedfishLocationObject> createRedfishLocationObjectList(DeviceResource source) {
        return singletonList(new RedfishLocationObject() {
            public String getInfo() {
                return source.getBusInfo();
            }

            public String getInfoFormat() {
                return INFO_FORMAT;
            }
        });
    }
}
