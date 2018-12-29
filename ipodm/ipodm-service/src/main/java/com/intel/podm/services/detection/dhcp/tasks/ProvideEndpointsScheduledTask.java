/*
 * Copyright (c) 2015-2018 Intel Corporation
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

package com.intel.podm.services.detection.dhcp.tasks;

import com.intel.podm.common.enterprise.utils.tasks.DefaultManagedTask;
import com.intel.podm.services.detection.dhcp.ServiceChecker;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Dependent
@Component
public class ProvideEndpointsScheduledTask extends DefaultManagedTask implements Runnable {

	@Autowired
	private ServiceChecker serviceChecker;

    @Override
    public void run() {
    	//从checker的failedMap里拿出未达到重试上限的元素重试，如果还是失败，则fail次数++
        serviceChecker.retryFailedEndpointCandidates();
        
        serviceChecker.triggerEndpointCandidatesCheck();
    }
}