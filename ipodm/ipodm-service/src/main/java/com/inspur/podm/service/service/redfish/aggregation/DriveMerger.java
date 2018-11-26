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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.inspur.podm.api.business.dto.DriveDto;
import com.inspur.podm.common.persistence.entity.Drive;
import com.inspur.podm.service.dao.DriveDao;

@ApplicationScoped
public class DriveMerger extends DiscoverableEntityDataMerger<Drive, DriveDto> {
    @Inject
    private DriveDao driveDao;

    @Override
    protected List<Drive> getMultiSourceRepresentations(Drive entity) {
        return driveDao.findComplementaryDrives(entity);
    }
}
