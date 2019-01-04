/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static com.inspur.podm.api.business.services.context.PathParamConstants.ETHERNET_INTERFACE_ID;
import static com.inspur.podm.api.business.services.context.PathParamConstants.MANAGER_ID;
import static com.inspur.podm.api.business.services.context.PathParamConstants.ETHERNET_SWITCH_PORT_VLAN_ID;
import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.inspur.podm.service.rest.error.PodmExceptions.invalidHttpMethod;
import static com.intel.podm.common.types.redfish.ResourceNames.NETWORK_PROTOCOL_RESOURCE_NAME;

import java.util.concurrent.TimeoutException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.EthernetInterfaceDto;
import com.inspur.podm.api.business.dto.ManagerDto;
import com.inspur.podm.api.business.dto.NetworkProtocolDto;
import com.inspur.podm.api.business.dto.VlanNetworkInterfaceDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.service.rest.redfish.json.templates.CollectionJson;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.CreateVlanJson;
import com.inspur.podm.service.rest.redfish.serializers.CollectionDtoJsonSerializer;
import com.intel.podm.common.types.redfish.RedfishVlanNetworkInterface;

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
	@Resource(name = "VlanNetworkInterfaceCreationService")
	private CreationService<RedfishVlanNetworkInterface> creationService;

	@Autowired
	private CollectionDtoJsonSerializer collectionDtoJsonSerializer;

	@ApiOperation(value = "get Manager collection", notes = "Managers")
	@RequestMapping(method = RequestMethod.GET)
	public CollectionJson get() {
		CollectionDto collectionDto = getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto,
				new ODataId("/redfish/v1/Managers"));
		return collectionJson;
	}

	@ApiOperation(value = "getManager ", notes = "Managers/{managerId}")
	@RequestMapping(value = "/" + MANAGER_ID, method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getManager(@PathVariable(required = true) String managerId) {
		super.uriInfo.put("managerId", managerId.toString());
		Context context = getCurrentContext();
		ManagerDto managerDto = getOrThrow(() -> readerService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, managerDto);
	}

	@ApiOperation(value = "get NetworkProtocol", notes = "NetworkProtocol")
	@RequestMapping(value = "/" + MANAGER_ID + "/NetworkProtocol", method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getNetworkProtocol(@PathVariable(required = true) String managerId) {
		super.uriInfo.put("managerId", managerId.toString());
		Context context = getCurrentContext();
		NetworkProtocolDto networkProtocolDto = getOrThrow(() -> readerNetworkProtocolService.getResource(context));
		return new RedfishResourceAmazingWrapper(singletonContextOf(context, NETWORK_PROTOCOL_RESOURCE_NAME),
				networkProtocolDto);
	}

	@ApiOperation(value = "get EthernetInterface collection", notes = "EthernetInterfaces")
	@RequestMapping(value = "/" + MANAGER_ID + "/EthernetInterfaces", method = RequestMethod.GET)
	public CollectionJson getEthernetInterfaces(@PathVariable(required = true) String managerId) {
		super.uriInfo.put("managerId", managerId.toString());
		Context context = getCurrentContext();
		CollectionDto collectionDto = getOrThrow(() -> readerEthernetInterfacesService.getCollection(context));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto,
				new ODataId("/redfish/v1/Managers" + managerId + "/EthernetInterfaces"));
		return collectionJson;
	}

	@ApiOperation(value = "get ethernetInterface  by ethernetInterfaceId", notes = "{ethernetInterfaceId}")
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

	@ApiOperation(value = "get vlan collection", notes = "VLANs")
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

	@ApiOperation(value = "get vlan by id", notes = "ethernetSwitchPortVlanId")
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

	@ApiOperation(value = "此方法目前无法创建Vlan，只有交换机才能划分vlan", notes = "此方法目前无法创建Vlan，只有交换机才能划分vlan")
	@RequestMapping(value = "/" + MANAGER_ID + "/EthernetInterfaces/" + ETHERNET_INTERFACE_ID
			+ "/VLANs", method = RequestMethod.POST)
	public Response createVlan(@PathVariable(required = true) String managerId,
			@PathVariable(required = true) String ethernetInterfaceId,
			@RequestBody(required = true) CreateVlanJson representation) throws TimeoutException, BusinessApiException {
		super.uriInfo.put("managerId", managerId.toString());
		super.uriInfo.put("ethernetInterfaceId", ethernetInterfaceId.toString());
		Context currentContext = getCurrentContext();

		if (!isPostEnabled(currentContext)) {
			throw invalidHttpMethod("Vlan cannot be created in specified resource");
		}

		Context createdContext = creationService.create(currentContext, representation);
		return Response.created(createdContext.asOdataId().toUri()).build();
	}

	private boolean isPostEnabled(Context currentContext) {
		return ETHERNET_SWITCH_PORT.equals(currentContext.getType());
	}

}
