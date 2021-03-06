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

package com.intel.podm.mappers.redfish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.SimpleStorage;
import com.intel.podm.client.resources.redfish.SimpleStorageResource;
import com.intel.podm.mappers.EntityMapper;
import com.intel.podm.mappers.subresources.SimpleStorageDeviceMapper;

//@Dependent
@Component
public class SimpleStorageMapper extends EntityMapper<SimpleStorageResource, SimpleStorage> {
	@Autowired
    private SimpleStorageDeviceMapper simpleStorageDeviceMapper;

    public SimpleStorageMapper() {
        super(SimpleStorageResource.class, SimpleStorage.class);
    }

    @Override
    protected void performNotAutomatedMapping(SimpleStorageResource sourceStorage, SimpleStorage targetStorage) {
        super.performNotAutomatedMapping(source, target);
        simpleStorageDeviceMapper.map(sourceStorage.getDevices(), targetStorage.getDevices(), targetStorage::addDevice);
    }
}
