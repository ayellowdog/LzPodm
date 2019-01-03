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

package com.inspur.podm.api.business.services.redfish;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.common.types.redfish.RedfishResource;

import java.util.concurrent.TimeoutException;

public interface UpdateService<T extends RedfishResource> {
    void perform(Context target, T representation) throws BusinessApiException, TimeoutException;
}
