/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.intel.podm.discovery.external;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

public final class DiscoveryFinishedEvent extends ApplicationEvent{
    /** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 5071088385368881798L;
	private final UUID serviceUuid;

	  public DiscoveryFinishedEvent(Object source, UUID serviceUuid) {
		  super(source);
		  this.serviceUuid = serviceUuid;
	  }
//    public DiscoveryFinishedEvent(UUID serviceUuid) {
//        this.serviceUuid = serviceUuid;
//    }

    public UUID getServiceUuid() {
        return serviceUuid;
    }
}
