/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.PathParamConstants.ETHERNET_INTERFACE_ID;
import static com.inspur.podm.api.business.services.context.PathParamConstants.MANAGER_ID;
import static com.inspur.podm.api.business.services.context.PathParamConstants.ETHERNET_SWITCH_PORT_VLAN_ID;
import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.intel.podm.common.types.redfish.ResourceNames.NETWORK_PROTOCOL_RESOURCE_NAME;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.dto.EthernetInterfaceDto;
import com.inspur.podm.api.business.dto.ManagerDto;
import com.inspur.podm.api.business.dto.NetworkProtocolDto;
import com.inspur.podm.api.business.dto.VlanNetworkInterfaceDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.service.rest.redfish.json.templates.CollectionJson;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.serializers.CollectionDtoJsonSerializer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: ManagerController
 * @Description: ManagerController
 *
 * @author: zhangdian
 * @date: 2019年1月4日 上午10:02:33
 */
@RestController
@RequestMapping("/redfish/v1/Managers")
@Api(value = "SystemController", description = "/redfish/v1/System控制器")
public class ManagerController extends BaseController {

	@Resource(name = "ManagerReaderService")
	private ReaderService<ManagerDto> readerService;
	@Resource(name = "NetworkProtocolReaderService")
	private ReaderService<NetworkProtocolDto> readerNetworkProtocolService;
	@Resource(name = "EthernetInterfaceService")
	private ReaderService<EthernetInterfaceDto> readerEthernetInterfacesService;
	@Resource(name = "VlanNetworkInterfaceReaderService")
	private ReaderService<VlanNetworkInterfaceDto> readerVlanNetworkInterfaceService;

	@Autowired
	private CollectionDtoJsonSerializer collectionDtoJsonSerializer;

	@ApiOperation(value = "Managers", notes = "Managers")
	@RequestMapping(method = RequestMethod.GET)
	public CollectionJson get() {
		CollectionDto collectionDto = getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto,
				new ODataId("/redfish/v1/Managers"));
		return collectionJson;
	}

	@ApiOperation(value = "Managers/{managerId}", notes = "Managers/{managerId}")
	@RequestMapping(value = "/" + MANAGER_ID, method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getManager(@PathVariable(required = true) String managerId) {
		super.uriInfo.put("managerId", managerId.toString());
		Context context = getCurrentContext();
		ManagerDto managerDto = getOrThrow(() -> readerService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, managerDto);
	}

	@ApiOperation(value = "Managers/{managerId}/NetworkProtocol", notes = "NetworkProtocol")
	@RequestMapping(value = "/" + MANAGER_ID + "/NetworkProtocol", method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getNetworkProtocol(@PathVariable(required = true) String managerId) {
		super.uriInfo.put("managerId", managerId.toString());
		Context context = getCurrentContext();
		NetworkProtocolDto networkProtocolDto = getOrThrow(() -> readerNetworkProtocolService.getResource(context));
		return new RedfishResourceAmazingWrapper(singletonContextOf(context, NETWORK_PROTOCOL_RESOURCE_NAME),
				networkProtocolDto);
	}

	@ApiOperation(value = "Managers/{managerId}/EthernetInterfaces", notes = "EthernetInterfaces")
	@RequestMapping(value = "/" + MANAGER_ID + "/EthernetInterfaces", method = RequestMethod.GET)
	public CollectionJson getEthernetInterfaces(@PathVariable(required = true) String managerId) {
		super.uriInfo.put("managerId", managerId.toString());
		Context context = getCurrentContext();
		CollectionDto collectionDto = getOrThrow(() -> readerEthernetInterfacesService.getCollection(context));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto,
				new ODataId("/redfish/v1/Managers" + managerId + "/EthernetInterfaces"));
		return collectionJson;
	}

	@ApiOperation(value = "Managers/{managerId}/EthernetInterfaces/{ethernetInterfaceId}", notes = "{ethernetInterfaceId}")
	@RequestMapping(value = "/" + MANAGER_ID + "/EthernetInterfaces/"
			+ ETHERNET_INTERFACE_ID, method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getEthernetInterface(@PathVariable(required = true) String managerId,
			@PathVariable(required = true) String ethernetInterfaceId) {
		super.uriInfo.put("managerId", managerId.toString());
		super.uriInfo.put("ethernetInterfaceId", ethernetInterfaceId.toString());
		Context context = getCurrentContext();
		EthernetInterfaceDto ethernetInterfaceDto = getOrThrow(
				() -> readerEthernetInterfacesService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, ethernetInterfaceDto);
	}

	@ApiOperation(value = "Managers/{managerId}/EthernetInterfaces/{ethernetInterfaceId}/VLANs", notes = "VLANs")
	@RequestMapping(value = "/" + MANAGER_ID + "/EthernetInterfaces/" + ETHERNET_INTERFACE_ID
			+ "/VLANs", method = RequestMethod.GET)
	public CollectionJson getVlanCollection(@PathVariable(required = true) String managerId,
			@PathVariable(required = true) String ethernetInterfaceId) {
		super.uriInfo.put("managerId", managerId.toString());
		super.uriInfo.put("ethernetInterfaceId", ethernetInterfaceId.toString());
		Context context = getCurrentContext();
		CollectionDto collectionDto = getOrThrow(() -> readerVlanNetworkInterfaceService.getCollection(context));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto, new ODataId(
				"/redfish/v1/Managers" + managerId + "/EthernetInterfaces/" + ethernetInterfaceId + "/VLANs"));
		return collectionJson;
	}

	@ApiOperation(value = "Managers/{managerId}/EthernetInterfaces/{ethernetInterfaceId}/VLANs/{ethernetSwitchPortVlanId}", notes = "ethernetSwitchPortVlanId")
	@RequestMapping(value = "/" + MANAGER_ID + "/EthernetInterfaces/" + ETHERNET_INTERFACE_ID + "/VLANs/"
			+ ETHERNET_SWITCH_PORT_VLAN_ID, method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getEthernetSwitchPortVlans(@PathVariable(required = true) String managerId,
			@PathVariable(required = true) String ethernetInterfaceId,
			@PathVariable(required = true) String ethernetSwitchPortVlanId) {
		super.uriInfo.put("managerId", managerId.toString());
		super.uriInfo.put("ethernetInterfaceId", ethernetInterfaceId.toString());
		super.uriInfo.put("ethernetSwitchPortVlanId", ethernetSwitchPortVlanId.toString());
		Context context = getCurrentContext();
		VlanNetworkInterfaceDto vlanNetworkInterfaceDto = getOrThrow(
				() -> readerVlanNetworkInterfaceService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, vlanNetworkInterfaceDto);
	}

	// @ApiOperation(value = "patchComputerSystem", notes = "PATCH")
	// @RequestMapping(value = "/" + COMPUTER_SYSTEM_ID, method =
	// RequestMethod.PATCH)
	// public Response patchComputerSystem(@PathVariable(required = true) String
	// computerSystemId,
	// @RequestBody(required = false) ComputerSystemPartialRepresentation
	// representation)
	// throws TimeoutException, BusinessApiException {
	// super.uriInfo.put("computerSystemId", computerSystemId.toString());
	// computerSystemUpdateService.perform(getCurrentContext(), representation);
	// return ok(get()).build();
	// }
	//
	// @ApiOperation(value = "/redfish/v1/Systems/{computerSystemId}/Processors",
	// notes = "Processors")
	// @RequestMapping(value = "/" + COMPUTER_SYSTEM_ID + "/"
	// +PROCESSORS_RESOURCE_NAME, method = RequestMethod.GET)
	// public CollectionJson getProcessorsCollection(@PathVariable(required = true)
	// String computerSystemId) {
	// super.uriInfo.put("computerSystemId", computerSystemId.toString());
	// Context context = getCurrentContext();
	// CollectionDto collectionDto = getOrThrow(() ->
	// readerProcessorService.getCollection(context));
	// CollectionJson collectionJson =
	// collectionDtoJsonSerializer.translate(collectionDto,
	// new ODataId("/redfish/v1/Systems" + computerSystemId.toString() +
	// "/Processors"));
	// return collectionJson;
	// }
	//
	// @ApiOperation(value =
	// "/redfish/v1/Systems/{computerSystemId}/Processors/{processorId}", notes =
	// "Processors")
	// @RequestMapping(value = "/" + COMPUTER_SYSTEM_ID + "/" +
	// PROCESSORS_RESOURCE_NAME + "/" + PROCESSOR_ID, method = RequestMethod.GET)
	// public RedfishResourceAmazingWrapper getProcessor(@PathVariable(required =
	// true) String computerSystemId,
	// @PathVariable(required = true) String processorId) {
	// super.uriInfo.put("computerSystemId", computerSystemId.toString());
	// super.uriInfo.put("processorId", processorId.toString());
	// Context context = getCurrentContext();
	// ProcessorDto processorDto = getOrThrow(() ->
	// readerProcessorService.getResource(context));
	// return new RedfishResourceAmazingWrapper(context, processorDto);
	// }

}
