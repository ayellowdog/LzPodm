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

package com.intel.podm.discovery.external.event;

import static com.intel.podm.client.WebClientExceptionUtils.isConnectionExceptionTheRootCause;
import static java.lang.String.format;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.events.EventServiceDefinition;
import com.intel.podm.client.events.EventSubscriptionManager;
import com.intel.podm.client.resources.EventSubscriptionResource;
import com.intel.podm.common.enterprise.utils.tasks.DefaultManagedTask;
import com.intel.podm.discovery.ServiceExplorer;

//@RequestScoped
@Component
@Lazy
class EventSubscriptionTask extends DefaultManagedTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(EventSubscriptionMonitor.class);
    @Autowired
    private ServiceExplorer serviceExplorer;

    @Autowired
    private EventServiceDefinitionFactory eventServiceDefinitionFactory;

    @Autowired
    private WebClientBuilder webClientBuilder;

    private UUID serviceUuid;

    void setServiceUuid(UUID serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    @Override
    public void run() {
        EventServiceDefinition eventServiceDefinition = eventServiceDefinitionFactory.create(serviceUuid);
        URI serviceBaseUri = eventServiceDefinition.getServiceBaseUri();
        try (WebClient webClient = webClientBuilder.newInstance(serviceBaseUri).retryable().build()) {
            EventSubscriptionManager eventSubscriptionManager = new EventSubscriptionManager(webClient, eventServiceDefinition);
            Optional<EventSubscriptionResource> eventSubscription = eventSubscriptionManager.fetchPodmSubscription();

            if (eventSubscription.isPresent()) {
                EventSubscriptionResource subscription = eventSubscription.get();
                if (!subscriptionHaveSameDestinationAsThisPodm(subscription.getDestination(), eventServiceDefinition.getPodmEventServiceDestinationUri())) {
                    eventSubscriptionManager.deleteSubscription(subscription);
                    subscribe(eventSubscriptionManager, serviceBaseUri);
                } else {
                    logger.trace(format("Subscription is already present for %s @ %s", serviceUuid, serviceBaseUri));
                }
            } else {
                subscribe(eventSubscriptionManager, serviceBaseUri);
            }
        } catch (WebClientRequestException e) {
            handleRequestException(e, serviceBaseUri);
        }
    }

    private boolean subscriptionHaveSameDestinationAsThisPodm(URI destination, URI podmEventServiceDestinationUri) {
        return Objects.equals(destination, podmEventServiceDestinationUri);
    }

    private void subscribe(EventSubscriptionManager eventSubscriptionManager, URI serviceBaseUri) throws WebClientRequestException {
        eventSubscriptionManager.subscribeToAllKnownEvents();
        logger.trace(format("Successfully subscribed to event service for %s @ %s", serviceUuid, serviceBaseUri));
    }

    private void handleRequestException(WebClientRequestException e, URI serviceBaseUri) {
        if (isConnectionExceptionTheRootCause(e)) {
            logger.warn(format("Connection error while checking subscriptions for %s @ %s, error: %s", serviceUuid, serviceBaseUri, e.getMessage()));
            serviceExplorer.enqueueVerification(serviceUuid);
        } else {
            logger.error(format("Error while subscribing to event service for %s @ %s", serviceUuid, serviceBaseUri), e);
        }
    }
}
