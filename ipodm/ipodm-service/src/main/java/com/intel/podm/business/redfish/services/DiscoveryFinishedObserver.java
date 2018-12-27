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

package com.intel.podm.business.redfish.services;

import com.intel.podm.discovery.external.DiscoveryFinishedEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.TimeoutException;

import static javax.enterprise.event.TransactionPhase.AFTER_SUCCESS;

//@Dependent
@Component
public class DiscoveryFinishedObserver {

    @Autowired
    private IscsiOobChapAuthenticationUpdater iscsiOobChapAuthenticationUpdater;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
//    public void onDiscoveryFinished(@Observes(during = AFTER_SUCCESS) DiscoveryFinishedEvent event) throws TimeoutException {
    public void onDiscoveryFinished(DiscoveryFinishedEvent event) throws TimeoutException {
        iscsiOobChapAuthenticationUpdater.checkIscsiOobAuthentication(event.getServiceUuid());
    }
}
