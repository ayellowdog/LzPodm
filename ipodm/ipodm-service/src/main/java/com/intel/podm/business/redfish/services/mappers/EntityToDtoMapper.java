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

package com.intel.podm.business.redfish.services.mappers;

import static java.lang.String.format;
import static java.util.Optional.of;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.dto.RedfishDto;
import com.intel.podm.business.entities.redfish.base.Entity;

@Component
public class EntityToDtoMapper {
    private final MapperProducer mapperProducer;

    @Autowired
    public EntityToDtoMapper(MapperProducer mapperProducer) {
        this.mapperProducer = mapperProducer;
    }

    public RedfishDto map(Entity entity) {
        return mapperProducer.tryFindDtoMapperForEntity(entity.getClass())
            .flatMap(mapper -> mapWithMapper(entity, mapper))
            .orElseThrow(() -> new RuntimeException(format("Could not find proper mapper for: %s", entity.getClass())));
    }

    @SuppressWarnings({"unchecked"})
    private Optional<RedfishDto> mapWithMapper(Entity mappedEntity, DtoMapper dtoMapper) {
        RedfishDto dto = dtoMapper.createDto();
        dtoMapper.map(mappedEntity, dto);
        return of(dto);
    }
}
