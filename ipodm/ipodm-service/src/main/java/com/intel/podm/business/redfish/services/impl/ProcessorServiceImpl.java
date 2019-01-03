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

package com.intel.podm.business.redfish.services.impl;

import static com.inspur.podm.api.business.dto.redfish.CollectionDto.Type.PROCESSOR;
import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.intel.podm.common.types.redfish.ResourceNames.PROCESSOR_METRICS_RESOURCE_NAME;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.ProcessorDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.Processor;
import com.intel.podm.business.redfish.services.Contexts;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.aggregation.ComputerSystemSubResourcesFinder;
import com.intel.podm.business.redfish.services.aggregation.MultiSourceEntityTreeTraverser;
import com.intel.podm.business.redfish.services.aggregation.ProcessorMerger;

@Service("ProcessorService")
class ProcessorServiceImpl implements ReaderService<ProcessorDto> {
    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private MultiSourceEntityTreeTraverser multiTraverser;

    @Autowired
    private ComputerSystemSubResourcesFinder computerSystemSubResourcesFinder;

    @Autowired
    private ProcessorMerger processorMerger;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CollectionDto getCollection(Context context) throws ContextResolvingException {
        ComputerSystem system = (ComputerSystem) traverser.traverse(context);

        // Multi-source resources sanity check
        if (system.isComplementary()) {
            throw new ContextResolvingException("Specified resource is not a primary resource representation!", context, null);
        }

        List<Context> contexts = computerSystemSubResourcesFinder.getUniqueSubResourcesOfClass(system, Processor.class).stream()
            .map(Contexts::toContext).sorted().collect(toList());
        return new CollectionDto(PROCESSOR, contexts);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ProcessorDto getResource(Context context) throws ContextResolvingException {
        Processor processor = (Processor) multiTraverser.traverse(context);
        ProcessorDto processorDto = processorMerger.toDto(processor);
        if (processor.getProcessorMetrics() != null) {
            processorDto.getOem().getRackScaleOem().setProcessorMetrics(singletonContextOf(context, PROCESSOR_METRICS_RESOURCE_NAME));
        }
        return processorDto;
    }
}
