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


import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.service.service.detection.ServiceDetectionStartUp;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.reader.ExternalServiceReader;
import com.intel.podm.client.reader.ExternalServiceReaderFactory;
import com.intel.podm.common.synchronization.CancelableRunnable;
import com.intel.podm.common.synchronization.TaskCanceledException;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.DiscoveryConfig;
import com.intel.podm.discovery.external.restgraph.RestGraph;
import com.intel.podm.discovery.external.restgraph.RestGraphBuilderFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.intel.podm.client.WebClientExceptionUtils.isConnectionExceptionTheRootCause;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

//@Dependent
//@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:IllegalCatch"})
//@Component
public class DiscoveryRunner extends CancelableRunnable {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryRunner.class);

    @Autowired
    private ExternalServiceAvailabilityChecker availabilityChecker;

    @Autowired
    private ExternalServiceReaderFactory readerFactory;

    @Autowired
    private ExternalServiceRepository externalServiceRepository;

    @Autowired
    private RestGraphBuilderFactory restGraphBuilderFactory;

    @Autowired
    private EntityGraphMapper mapper;

//    @Autowired
    @Config
//    private Holder<DiscoveryConfig> discoveryConfig;
    @Resource(name="podmConfigProvider")
    private ConfigProvider discoveryConfig;

    private UUID serviceUuid;

    @Autowired
    private DiscoveryRunnerHooks discoveryRunnerHooks;

//    @Autowired
    private Event<DiscoveryFinishedEvent> discoveryFinishedEvent;

    @Override
    @Transactional(REQUIRES_NEW)
//    @TimeMeasured(tag = "[DiscoveryTask]")
    public void run() {
        try {
            requiresNonNull(serviceUuid, "Service UUID cannot be null, discovery action has not been configured correctly");
            findService(serviceUuid).ifPresent(this::discover);
        } finally {
            clearCancellationFlag();
        }
    }

    private Optional<ExternalService> findService(UUID serviceUuid) {
        requiresNonNull(serviceUuid, "Service UUID cannot be null, discovery action has not been configured correctly");
        ExternalService externalService = externalServiceRepository.findOrNull(serviceUuid);
        if (externalService == null) {
            logger.error("Service with UUID: {} does not exist", serviceUuid);
        }
        return ofNullable(externalService);
    }

    private void discover(ExternalService service) {
        logger.info("Polling data from {} started", service);
        try (ExternalServiceReader reader = readerFactory.createExternalServiceReaderWithCacheAndRetries(service.getBaseUri())) {
            UUID obtainedUuid = reader.getServiceRoot().getUuid();
            if (!Objects.equals(obtainedUuid, serviceUuid)) {
                logger.error("Service UUID change detected, terminating discovery! Expected UUID: {} obtained UUID: {}", serviceUuid, obtainedUuid);
                availabilityChecker.verifyServiceAvailabilityByUuid(serviceUuid);
                return;
            }
            RestGraph graph = restGraphBuilderFactory.createWithCancelableChecker(this::throwIfEligibleForCancellation).build(reader);
            discoveryRunnerHooks.onRestGraphCreated(graph, service);
            mapper.map(graph);
            logger.info("Polling data from {} finished", service);
        } catch (WebClientRequestException e) {
            triggerAvailabilityCheckOnConnectionException(service, e);
            logger.warn(format("Unable to process data from %s service", service), e);
        } catch (TaskCanceledException e) {
            logger.info("Discovery was canceled for {} due to: {}", service, e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error while polling data from " + service, e);
        }

        discoveryFinishedEvent.fire(new DiscoveryFinishedEvent(serviceUuid));
    }

    private void triggerAvailabilityCheckOnConnectionException(ExternalService service, WebClientRequestException e) {
        if (isConnectionExceptionTheRootCause(e)) {
            logger.warn("Connection error while getting data from {} service - performing check on this service", service);
            availabilityChecker.verifyServiceAvailabilityByUuid(serviceUuid);
        }
    }

    private void throwIfEligibleForCancellation() {
        if (discoveryConfig.get(DiscoveryConfig.class).isDiscoveryCancelable()) {
            throwWithMessageIfEligibleForCancellation("Discovery was canceled.");
        }
    }

    public void setServiceUuid(UUID serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiscoveryRunner that = (DiscoveryRunner) o;
        return serviceUuid.equals(that.serviceUuid);
    }

    @Override
    public int hashCode() {
        return serviceUuid.hashCode();
    }

    @Override
    public String toString() {
        return format("DiscoveryRunner(%s)", serviceUuid);
    }
}
