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

package com.inspur.podm.service.service.redfish.mappers;

import static com.inspur.podm.common.intel.logger.LoggerFactory.getLogger;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import com.inspur.podm.api.business.dto.RedfishDto;
import com.inspur.podm.api.business.dto.redfish.attributes.UnknownOemDto;
import com.inspur.podm.common.intel.logger.Logger;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.common.persistence.entity.DiscoverableEntity;
import com.inspur.podm.service.service.redfish.helpers.UnknownOemTranslator;
import com.inspur.podm.service.service.redfish.mappers.BrilliantMapper;

public abstract class DtoMapper<S extends Entity, T extends RedfishDto> extends BrilliantMapper<S, T> {
    private static final Logger LOGGER = getLogger(DtoMapper.class);
    protected S source;
    protected T target;

    private UnknownOemTranslator unknownOemTranslator;

    protected DtoMapper(Class<S> sourceClass, Class<T> targetClass) {
        super(sourceClass, targetClass);
    }

    boolean canMap(Class clazz) {
        return getSourceClass().isAssignableFrom(clazz);
    }

    @Override
    protected void performNotAutomatedMapping(S source, T target) {
        super.performNotAutomatedMapping(source, target);
        //todo: verify this implementation
        if (source instanceof DiscoverableEntity) {
            DiscoverableEntity discoverableEntity = (DiscoverableEntity) source;
            List<UnknownOemDto> unknownOemDtos = discoverableEntity.getExternalServices().stream()
                .map(service -> unknownOemTranslator.translateUnknownOemToDtos(service, discoverableEntity.getUnknownOems()))
                .flatMap(Collection::stream)
                .collect(toList());

            target.setUnknownOems(unknownOemDtos);
        }
    }

    @Override
    public void map(S source, T target) {
        this.source = source;
        this.target = target;
        super.map(source, target);
    }

    public void setUnknownOemTranslator(UnknownOemTranslator unknownOemTranslator) {
        this.unknownOemTranslator = unknownOemTranslator;
    }

    @SuppressWarnings({"unchecked"})
    public T createDto() {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.e("Could not create DTO for {}", clazz.getSimpleName());
            throw new RuntimeException(e);
        }
    }
}
