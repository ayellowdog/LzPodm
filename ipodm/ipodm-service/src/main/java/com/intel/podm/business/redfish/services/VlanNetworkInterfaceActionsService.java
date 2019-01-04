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

package com.intel.podm.business.redfish.services;

import static com.intel.podm.business.redfish.services.Contexts.toContext;

import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.ResourceStateMismatchException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.redfish.EthernetSwitchPort;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.redfish.services.actions.EthernetSwitchPortVlanActionsInvoker;
import com.intel.podm.business.redfish.services.assembly.VlanAllocator;
import com.intel.podm.business.redfish.services.assembly.VlanTerminator;
import com.intel.podm.client.actions.UpdateVlanRequest;
import com.intel.podm.common.types.actions.VlanCreationRequest;
import com.intel.podm.common.types.redfish.RedfishVlanNetworkInterface;

/**
 * @ClassName: VlanNetworkInterfaceActionsService
 * @Description: VlanNetworkInterfaceActionsService
 *
 * @author: zhangdian
 * @date: 2019年1月4日 下午2:13:17
 */
@Component
@SuppressWarnings({ "checkstyle:ClassFanOutComplexity" })
public class VlanNetworkInterfaceActionsService {
	@Autowired
	private EntityTreeTraverser traverser;

	@Autowired
	private VlanAllocator vlanAllocator;

	@Autowired
	private VlanTerminator vlanTerminator;

	@Autowired
	private EthernetSwitchPortVlanActionsInvoker invoker;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Context createVlan(Context creationalContext, RedfishVlanNetworkInterface representation)
			throws BusinessApiException {
		EthernetSwitchPort ethernetSwitchPort = (EthernetSwitchPort) traverser.traverse(creationalContext);
		validateVlanUniqueness(ethernetSwitchPort, representation);

		EthernetSwitchPortVlan createdVlan = vlanAllocator.createVlan(ethernetSwitchPort, buildRequest(representation));
		return toContext(createdVlan);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateVlan(Context context, RedfishVlanNetworkInterface representation) throws BusinessApiException {
		EthernetSwitchPortVlan vlan = (EthernetSwitchPortVlan) traverser.traverse(context);
		validateVlanIdsUniqueness(vlan, representation.getVlanId());

		invoker.update(vlan, new UpdateVlanRequest(representation.getVlanId()));
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteVlan(Context vlanContext) throws ContextResolvingException, EntityOperationException {
		EthernetSwitchPortVlan ethernetSwitchPortVlan = (EthernetSwitchPortVlan) traverser.traverse(vlanContext);
		vlanTerminator.deleteVlan(ethernetSwitchPortVlan);
	}

	private void validateVlanUniqueness(EthernetSwitchPort ethernetSwitchPort,
			RedfishVlanNetworkInterface representation) throws ResourceStateMismatchException {

		boolean isVlanUnique = ethernetSwitchPort.getEthernetSwitchPortVlans().stream()
				.noneMatch(vlanWithSameVlanIdAndTaggedStateAlreadyExistsPredicate(representation.getVlanId(),
						representation.getTagged()));

		if (!isVlanUnique) {
			throw new ResourceStateMismatchException("VLAN already exists");
		}
	}

	private void validateVlanIdsUniqueness(EthernetSwitchPortVlan vlan, Integer targetVlanId)
			throws ResourceStateMismatchException {

		boolean isVlanUnique = vlan.getEthernetSwitchPort().getEthernetSwitchPortVlans().stream()
				.filter(excludeSourceVlanPredicate(vlan.getVlanId()))
				.noneMatch(vlanWithSameVlanIdAndTaggedStateAlreadyExistsPredicate(targetVlanId, vlan.getTagged()));

		if (!isVlanUnique) {
			throw new ResourceStateMismatchException("VLAN with that VLANId already exists");
		}
	}

	private Predicate<EthernetSwitchPortVlan> excludeSourceVlanPredicate(Integer sourceVlanId) {
		return vlan -> !Objects.equals(vlan.getVlanId(), sourceVlanId);
	}

	private Predicate<EthernetSwitchPortVlan> vlanWithSameVlanIdAndTaggedStateAlreadyExistsPredicate(Integer vlanId,
			Boolean tagged) {
		return vlan -> Objects.equals(vlan.getVlanId(), vlanId) && Objects.equals(vlan.getTagged(), tagged);
	}

	private VlanCreationRequest buildRequest(RedfishVlanNetworkInterface representation) {
		return new VlanCreationRequest(representation.getVlanId(), representation.getTagged(),
				representation.getVlanEnable());
	}
}
