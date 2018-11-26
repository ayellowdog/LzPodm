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

package com.inspur.podm.service.service.redfish.aggregation;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.inspur.podm.api.business.dto.StorageControllerDto;
import com.inspur.podm.common.persistence.entity.Storage;
import com.inspur.podm.common.persistence.entity.StorageController;
import com.inspur.podm.service.dao.StorageDao;

@Dependent
public class StorageControllerCollectionMerger {
    @Inject
    private StorageControllerMerger storageControllerMerger;

    @Inject
    private StorageDao storageDao;

    public List<StorageControllerDto> getMergedStorageControllerCollection(Storage baseStorage) {
        List<Storage> complementaryStorages = storageDao.findComplementaryStorages(baseStorage);
        Map<String, StorageController> uniqueControllers = new LinkedHashMap<>();

        concat(
            concat(baseStorage.getStorageControllers().stream(), baseStorage.getAdapters().stream()),
            concat(
                complementaryStorages.stream().flatMap(storage -> storage.getStorageControllers().stream()),
                complementaryStorages.stream().flatMap(storage -> storage.getAdapters().stream())
            )
        ).forEach(controller -> uniqueControllers.put(controller.getMultiSourceDiscriminator(), controller));

        return uniqueControllers.values().stream()
            .map(storageController -> storageControllerMerger.toDto(storageController))
            .sorted()
            .collect(toList());
    }
}
