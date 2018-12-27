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

package com.intel.podm.services.detection.dhcp;

import com.intel.podm.discovery.external.ServiceDescriptor;
import com.intel.podm.discovery.external.ServiceDetectionListener;
import com.inspur.podm.demoUtil.DemoDataUtil;
import com.intel.podm.common.types.ServiceType;
import com.intel.podm.common.types.discovery.ServiceEndpoint;
import com.intel.podm.discovery.external.UnrecognizedServiceTypeException;

import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ejb.LockType.WRITE;
import static javax.transaction.Transactional.TxType.SUPPORTS;

/**
 * Class responsible for detecting service presence and notifying
 * discovery service (providing it with service endpoints) if a valid
 * service was detected at given URI. Notifications about known,
 * discovered services are not repeated.
 */
//@Singleton
@Component
public class ServiceChecker {

	private static final Logger logger = LoggerFactory.getLogger(ServiceChecker.class);

	@Autowired
    private ServiceDetectionListener serviceDetectionListener;

	@Autowired
    private ServiceDescriptor serviceDescriptor;

	@Autowired
    private ServiceEndpointsProcessor serviceEndpointsProcessor;

	@Autowired
    private CandidatesProvider candidatesProvider;

    /**
     * Method initiates service detection for all provided Service Endpoint Candidates.
     *
     * LockType.WRITE used due to concurrent access to service endpoints processor.
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
	@Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public void triggerEndpointCandidatesCheck() {
    	//得到新鲜的候选者freshSet，如何定义新鲜？
//        Set<DhcpServiceCandidate> candidateSet = candidatesProvider.getEndpointCandidates();
		/*start 修改为假数据*/
		Set<DhcpServiceCandidate> candidateSet = DemoDataUtil.getEndpointCandidates();
		/*end*/
        //从knownServiceEndpointsMap中取出那些不在freshSet里的uri，这些uri是陈旧的stale，直接将其从系统抹除
        for (URI staleCandidateUri : serviceEndpointsProcessor.getKnownUrisNotPresentInFreshCandidateSet(candidateSet)) {
            ServiceEndpoint endpointToCheck = serviceEndpointsProcessor.getKnownServiceByUri(staleCandidateUri);
            if (endpointToCheck != null) {
                removeServiceEndpointIfItBecameUnavailable(endpointToCheck);
            } else {
                logger.trace("Service with URI {} might already disappeared during ongoing recheck", staleCandidateUri);
            }
        }
        //从failedServiceEndpointsMap中，删除不在freshSet里的元素，个人感觉这些候选者已经没用了
        serviceEndpointsProcessor.removeFailedServicesNotPresentInFreshCandidateSet(candidateSet);
        //failedServiceEndpointsMap中有一些元素，在freshSet里而且被更新了，将其从failedServiceEndpointsMap删除
        serviceEndpointsProcessor.removeUpdatedCandidatesFromFailedServices(candidateSet);
        //真正需要去detect的是仅包含在freshSet，不包含在knownServiceEndpointsMap和failedServiceEndpointsMap里的元素
        List<DhcpServiceCandidate> candidatesForPoll = serviceEndpointsProcessor.getFreshCandidates(candidateSet);
        candidatesForPoll.forEach(this::detectServiceUsingServiceEndpointCandidate);
    }

    /**
     * LockType.WRITE used due to concurrent access to service endpoints processor.
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
	@Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public void reCheckForFailedUris() {
        serviceEndpointsProcessor.updateServicesListForReCheck();
    }

    /**
     * Method initiates retry of service detection for previously failed URIs
     *
     * LockType.WRITE used due to concurrent access to service endpoints processor.
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
	@Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public void retryFailedEndpointCandidates() {
    	//从faildMap里取出还没有达到重试次数的元素，进行detect
        serviceEndpointsProcessor.getCandidatesForRetry().forEach(this::detectServiceUsingServiceEndpointCandidate);
    }

    private void removeServiceEndpointIfItBecameUnavailable(ServiceEndpoint serviceEndpoint) {
        if (!isServiceAvailable(serviceEndpoint.getEndpointUri())) {
            serviceDetectionListener.onServiceRemoved(serviceEndpoint);
            serviceEndpointsProcessor.removeKnownService(serviceEndpoint.getEndpointUri());
        }
    }

    private boolean isServiceAvailable(URI serviceUri) {
        try {
            serviceDescriptor.describe(serviceUri);
            return true;
        } catch (UnrecognizedServiceTypeException e) {
            logger.info("Service {} is not available: {}", serviceUri, e.getMessage());
            return false;
        }
    }

    /**
     * Method tries to detect a single service using service endpoint candidate
     * and notify discovery service upon success.
     * <p>
     * Detected service is added to knownLocations so it won't be unnecessarily polled later.
     *
     * @param candidate
     */
    private void detectServiceUsingServiceEndpointCandidate(DhcpServiceCandidate candidate) {
        try {
        	//这个方法去请去目标uri，会抛出UnrecognizedServiceTypeException异常，否则这个设备被识别
            ServiceEndpoint serviceEndpoint = serviceDescriptor.describe(candidate.getEndpointUri(), candidate.getServiceType());
            
            serviceDetectionListener.onServiceDetected(serviceEndpoint);
            //把这个节点添加到knownMap里
            serviceEndpointsProcessor.addKnownEndpointService(serviceEndpoint);
            //把这个节点从failMap里删除，如果存在的话
            serviceEndpointsProcessor.removeCandidateFromFailedServices(candidate);
        } catch (UnrecognizedServiceTypeException e) {
        	//放入failedMap，准备下次重试
            serviceEndpointsProcessor.failServiceEndpointCandidate(candidate);
        }
    }
}
