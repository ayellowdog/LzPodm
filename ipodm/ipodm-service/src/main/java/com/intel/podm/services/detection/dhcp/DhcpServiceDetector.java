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

import com.intel.podm.config.base.dto.ServiceDetectionConfig.Protocols.Dhcp;
import com.intel.podm.services.detection.dhcp.tasks.ProvideEndpointsScheduledTask;
import com.intel.podm.services.detection.dhcp.tasks.RecheckFailedUrisScheduledTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

//@ApplicationScoped
@Component
public class DhcpServiceDetector {
    @Resource(name = "managedExecutorService")
    private ScheduledExecutorService managedExecutorService;

	private static final Logger logger = LoggerFactory.getLogger(DhcpServiceDetector.class);

	@Autowired
	private ProvideEndpointsScheduledTask provideEndpointsScheduledTask;

	@Autowired
    private RecheckFailedUrisScheduledTask recheckFailedUrisScheduledTask;

    public void init(Dhcp dhcp) {
        logger.info("Initializing DHCP based service detector...");
        try {
            long checkInterval = dhcp.getFilesCheckIntervalInSeconds();
            /**
             * 1.从checker的failedMap里拿出未达到重试上限的元素重试，如果还是失败，则fail次数++
             * 2.
             * **/
            managedExecutorService.scheduleWithFixedDelay(provideEndpointsScheduledTask, checkInterval, checkInterval, SECONDS);

            long recheckInterval = dhcp.getFailedEndpointRecheckInterval();
            //这个任务仅仅将failedMap中的那些超过重试次数的元素，统统从knownMap和failedMap中删除。这样为啥能够触发元素被recheck？
            managedExecutorService.scheduleWithFixedDelay(recheckFailedUrisScheduledTask, recheckInterval, recheckInterval, SECONDS);
        } catch (RejectedExecutionException e) {
            logger.error("Application failed to start properly. Service polling is disabled.", e);
            throw e;
        }
    }
}
