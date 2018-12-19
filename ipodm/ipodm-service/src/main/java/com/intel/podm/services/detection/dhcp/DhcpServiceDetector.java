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

package com.intel.podm.services.detection.dhcp;

import com.intel.podm.common.logger.Logger;
import com.intel.podm.config.base.dto.ServiceDetectionConfig.Protocols.Dhcp;
import com.intel.podm.services.detection.dhcp.tasks.ProvideEndpointsScheduledTask;
import com.intel.podm.services.detection.dhcp.tasks.RecheckFailedUrisScheduledTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;

//@ApplicationScoped
@Component
public class DhcpServiceDetector {
//    @Resource
//	@Autowired
    private ManagedScheduledExecutorService managedExecutorService;

//    @Inject
    private Logger logger;

//    @Inject
    private ProvideEndpointsScheduledTask provideEndpointsScheduledTask;

//    @Inject
    private RecheckFailedUrisScheduledTask recheckFailedUrisScheduledTask;

    public void init(Dhcp dhcp) {
        logger.i("Initializing DHCP based service detector...");
        try {
            long checkInterval = dhcp.getFilesCheckIntervalInSeconds();
            managedExecutorService.scheduleWithFixedDelay(provideEndpointsScheduledTask, checkInterval, checkInterval, SECONDS);

            long recheckInterval = dhcp.getFailedEndpointRecheckInterval();
            managedExecutorService.scheduleWithFixedDelay(recheckFailedUrisScheduledTask, recheckInterval, recheckInterval, SECONDS);
        } catch (RejectedExecutionException e) {
            logger.e("Application failed to start properly. Service polling is disabled.", e);
            throw e;
        }
    }
}
