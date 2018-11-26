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

package com.inspur.podm.service.service.redfish.aggregation;

import static java.util.Collections.emptyList;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.inspur.podm.common.persistence.base.MultiSourceResource;
import com.inspur.podm.common.persistence.entity.Chassis;
import com.inspur.podm.common.persistence.entity.DiscoverableEntity;
import com.inspur.podm.common.persistence.entity.Drive;
import com.inspur.podm.service.dao.ChassisDao;

@Dependent
public class ChassisSubResourcesFinder {
    @Inject
    private ChassisDao chassisDao;

    @Transactional(MANDATORY)
    public Collection<? extends DiscoverableEntity> getUniqueSubResourcesOfClass(Chassis baseEntity, Class<? extends DiscoverableEntity> subResourceClass) {
        if (Objects.equals(subResourceClass, Drive.class)) {
            List<Chassis> complementaryChassis = chassisDao.findComplementaryChassis(baseEntity);
            return getStringMap(baseEntity, complementaryChassis, Chassis::getDrives).values();
        } else {
            return emptyList();
        }
    }

    private <T extends MultiSourceResource> Map<String, T> getStringMap(Chassis chassis, List<Chassis> complementaryChassis,
                                                                        Function<Chassis, Set<T>> func) {
        Map<String, T> map = new HashMap<>();
        for (T item : func.apply(chassis)) {
            map.put(item.getMultiSourceDiscriminator(), item);
        }

        for (Chassis complementary : complementaryChassis) {
            for (T complementaryItem : func.apply(complementary)) {
                map.putIfAbsent(complementaryItem.getMultiSourceDiscriminator(), complementaryItem);
            }
        }
        return map;
    }
}
