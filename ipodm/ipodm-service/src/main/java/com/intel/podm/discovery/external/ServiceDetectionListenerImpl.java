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

package com.intel.podm.discovery.external;

import com.intel.podm.common.enterprise.utils.beans.BeanFactory;
import com.intel.podm.common.logger.Logger;
import com.intel.podm.common.logger.LoggerFactory;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.types.discovery.ServiceEndpoint;
import com.intel.podm.discovery.ServiceExplorer;
//import com.intel.podm.services.configuration.DiscoveryServiceDetectionHandler;
import com.intel.podm.services.configuration.DiscoveryServiceDetectionHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@ApplicationScoped
@Component
public class ServiceDetectionListenerImpl implements ServiceDetectionListener {
	private static final Logger logger = LoggerFactory.getLogger(ServiceDetectionListenerImpl.class);

	@Autowired
    private ServiceExplorer serviceExplorer;

	@Autowired
    private ExternalServiceUpdater externalServiceUpdater;

	@Autowired
    private TaskCoordinator taskCoordinator;

	@Autowired
    private DiscoveryServiceDetectionHandler discoveryServiceDetectionHandler;

	@Autowired
	private BeanFactory beanFactory;
    @Override
    public void onServiceDetected(ServiceEndpoint serviceEndpoint) {
        UUID serviceUuid = serviceEndpoint.getServiceUuid();
//        taskCoordinator.registerAsync(serviceUuid, () -> {
//            // Do not change form of this log, as this is being used in developer tools!
//            logger.i("Service {} detected", serviceEndpoint);
//
//            switch (serviceEndpoint.getServiceType()) {
//                case DISCOVERY_SERVICE:
//                    discoveryServiceDetectionHandler.onServiceDetected(serviceEndpoint);
//                    break;
//                default:
//                	/*
//                	 * 根据UUID判断是否数据库里存在这个数据，如果没有则插入，有了的话，更新一下baseUrl
//                	 * 同时，还会根据类型判断这个数据源会不会event，是不是ComplementaryDataSource
//                	 * */
//                    externalServiceUpdater.updateExternalService(serviceEndpoint);
//                    serviceExplorer.startMonitoringOfService(serviceUuid);
//                    break;
//            }
//        });
        OnServiceDetectRunner runner = beanFactory.create(OnServiceDetectRunner.class);
        runner.setServiceEndpoint(serviceEndpoint);
        taskCoordinator.registerAsync(serviceUuid,runner);
    }

    @Override
    public void onServiceRemoved(ServiceEndpoint serviceEndpoint) {
        switch (serviceEndpoint.getServiceType()) {
            case DISCOVERY_SERVICE:
                discoveryServiceDetectionHandler.onServiceRemoved(serviceEndpoint);
                break;
            default:
                serviceExplorer.enqueueVerification(serviceEndpoint.getServiceUuid());
                break;
        }
    }
}
