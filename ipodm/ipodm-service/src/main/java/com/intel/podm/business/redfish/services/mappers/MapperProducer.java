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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.dto.ChassisDto;
import com.inspur.podm.api.business.dto.ComputerSystemDto;
import com.inspur.podm.api.business.dto.EthernetInterfaceDto;
import com.inspur.podm.api.business.dto.ManagerDto;
import com.inspur.podm.api.business.dto.ProcessorDto;
import com.inspur.podm.api.business.dto.RedfishDto;
import com.inspur.podm.api.business.dto.VlanNetworkInterfaceDto;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.EthernetInterface;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.entities.redfish.Manager;
import com.intel.podm.business.entities.redfish.Processor;
import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.business.redfish.services.helpers.UnknownOemTranslator;

@Component
public class MapperProducer {
	@Autowired
	private UnknownOemTranslator unknownOemTranslator;

	private Collection<DtoMapper<? extends Entity, ? extends RedfishDto>> mapperCache;

	/**
	 * <p> TODO 不依靠注入，根据日后代码手动维护mapperPool。 </p>
	 * 
	 * @author: zhangdian
	 * @date: 2018年12月4日 下午4:56:59
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		mapperCache = new ArrayList<DtoMapper<? extends Entity, ? extends RedfishDto>>();
		mapperCache.add(new DtoMapper(Chassis.class, ChassisDto.class));
		mapperCache.add(new DtoMapper(ComputerSystem.class, ComputerSystemDto.class));
		mapperCache.add(new DtoMapper(Processor.class, ProcessorDto.class));
		mapperCache.add(new DtoMapper(Manager.class, ManagerDto.class));
		mapperCache.add(new DtoMapper(EthernetInterface.class, EthernetInterfaceDto.class));
		mapperCache.add(new DtoMapper(EthernetSwitchPortVlan.class, VlanNetworkInterfaceDto.class));
	}

	public Optional<DtoMapper<? extends Entity, ? extends RedfishDto>> tryFindDtoMapperForEntity(
			Class<? extends Entity> entityClass) {
		Optional<DtoMapper<? extends Entity, ? extends RedfishDto>> possibleDtoMapper = tryGetDtoMapperFromPool(
				entityClass);
		if (possibleDtoMapper.isPresent()) {
			DtoMapper<? extends Entity, ? extends RedfishDto> dtoMapper = possibleDtoMapper.get();
			dtoMapper.setUnknownOemTranslator(unknownOemTranslator);
			return of(dtoMapper);
		} else {
			return empty();
		}
	}

	private Optional<DtoMapper<? extends Entity, ? extends RedfishDto>> tryGetDtoMapperFromPool(
			Class<? extends Entity> entityClass) {
		return stream(mapperCache.spliterator(), false).filter(mapper -> mapper.canMap(entityClass)).findFirst();
	}
	

}
