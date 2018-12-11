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

package com.intel.podm.discovery.internal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@DependsOn({"DatabaseSchemaUpdateFinalizer"})
public class PodStartupDiscovery {
    @Autowired
    private PodStartUpDiscoveryProxy podStartUpDiscoveryProxy;

    /**
     * This method needs to be executed within a transaction context thus it must be public and by default LockType.WRITE is used implicitly
     * for public methods in singleton bean.
     * In real-life scenario it is called only once and does not need any locking mechanism.
     */
    @PostConstruct
    public void initInitialPod() {
    	podStartUpDiscoveryProxy.initInitialPod();
    }
}
