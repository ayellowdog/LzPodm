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

package com.intel.podm.business.entities.resolvers;

import com.intel.podm.business.entities.dao.SimpleStorageDao;
import com.intel.podm.business.entities.redfish.SimpleStorage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Dependent
@Component
public class SimpleStorageMultiSourceResolver extends MultiSourceEntityResolver<SimpleStorage> {
    private final SimpleStorageDao simpleStorageDao;

    @Autowired
    SimpleStorageMultiSourceResolver(SimpleStorageDao simpleStorageDao) {
        super(SimpleStorage.class);
        this.simpleStorageDao = simpleStorageDao;
    }

    @Override
    protected Optional<SimpleStorage> findPrimaryEntity(SimpleStorage complementarySimpleStorage) {
        return simpleStorageDao.findPrimarySimpleStorage(complementarySimpleStorage);
    }

    @Override
    public String createMultiSourceDiscriminator(SimpleStorage simpleStorage) {
        return simpleStorage.getComputerSystem().getMultiSourceDiscriminator() + simpleStorage.getUefiDevicePath();
    }
}
