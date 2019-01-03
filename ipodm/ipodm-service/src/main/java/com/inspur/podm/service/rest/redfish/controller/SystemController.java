/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.PathParamConstants.COMPUTER_SYSTEM_ID;
import static com.inspur.podm.api.business.services.context.PathParamConstants.PROCESSOR_ID;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.intel.podm.common.types.redfish.ResourceNames.COMPUTER_SYSTEM_METRICS_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.ETHERNET_INTERFACES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.MEMORY_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.NETWORK_INTERFACES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.PROCESSORS_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.SIMPLE_STORAGE_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.STORAGE_RESOURCE_NAME;
import static javax.ws.rs.core.Response.ok;

import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.ComputerSystemDto;
import com.inspur.podm.api.business.dto.ProcessorDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.UpdateService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.service.rest.redfish.json.templates.CollectionJson;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.ComputerSystemPartialRepresentation;
import com.inspur.podm.service.rest.redfish.resources.ComputerSystemMetricsResource;
import com.inspur.podm.service.rest.redfish.resources.EthernetInterfaceCollectionResource;
import com.inspur.podm.service.rest.redfish.resources.MemoryCollectionResource;
import com.inspur.podm.service.rest.redfish.resources.NetworkInterfaceCollectionResource;
import com.inspur.podm.service.rest.redfish.resources.ProcessorResource;
import com.inspur.podm.service.rest.redfish.resources.ProcessorsCollectionResource;
import com.inspur.podm.service.rest.redfish.resources.SimpleStorageCollectionResource;
import com.inspur.podm.service.rest.redfish.resources.StorageCollectionResource;
import com.inspur.podm.service.rest.redfish.serializers.CollectionDtoJsonSerializer;
import com.intel.podm.common.types.redfish.RedfishComputerSystem;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: NodeController
 * @Description: NodeController
 *
 * @author: zhangdian
 * @date: 2018年12月11日 下午2:11:45
 */
@RestController
@RequestMapping("/redfish/v1/Systems")
@Api(value = "SystemController", description = "/redfish/v1/System控制器")
public class SystemController extends BaseController {
	
	@Autowired
	private CollectionDtoJsonSerializer collectionDtoJsonSerializer;
	
	@Resource(name = "ComputerSystemService")
    private ReaderService<ComputerSystemDto> readerService;
	
	@Resource(name = "ProcessorService")
    private ReaderService<ProcessorDto> readerProcessorService;
	
    @Resource(name = "ComputerSystem")
    private UpdateService<RedfishComputerSystem> computerSystemUpdateService;

	@ApiOperation(value = "查看redfish目录/redfish/v1/Systems", notes = "Systems")
	@RequestMapping(method = RequestMethod.GET)
	public CollectionJson get() {
		CollectionDto collectionDto = getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto, new ODataId("/redfish/v1/Systems"));
		return collectionJson;
    }
	
	@ApiOperation(value = "/redfish/v1/Systems/{computerSystemId}", notes = "/redfish/v1/Systems/{computerSystemId}")
	@RequestMapping(value = "/" + COMPUTER_SYSTEM_ID, method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getNode(@PathVariable(required = true) String computerSystemId) {
		super.uriInfo.put("computerSystemId", computerSystemId.toString());
		Context context = getCurrentContext();
		ComputerSystemDto computerSystemDto = getOrThrow(() -> readerService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, computerSystemDto);
	}
	
	@ApiOperation(value = "patchComputerSystem", notes = "PATCH")
	@RequestMapping(value = "/" + COMPUTER_SYSTEM_ID, method = RequestMethod.PATCH)
	public Response patchComputerSystem(@PathVariable(required = true) String computerSystemId,
			@RequestBody(required = false) ComputerSystemPartialRepresentation representation)
			throws TimeoutException, BusinessApiException {
		super.uriInfo.put("computerSystemId", computerSystemId.toString());
		computerSystemUpdateService.perform(getCurrentContext(), representation);
        return ok(get()).build();
	}
	
	@ApiOperation(value = "/redfish/v1/Systems/{computerSystemId}/Processors", notes = "Processors")
	@RequestMapping(value = "/" + COMPUTER_SYSTEM_ID + "/" +PROCESSORS_RESOURCE_NAME, method = RequestMethod.GET)
    public CollectionJson getProcessorsCollection(@PathVariable(required = true) String computerSystemId) {
		CollectionDto collectionDto = getOrThrow(() -> readerProcessorService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto,
				new ODataId("/redfish/v1/Systems" + computerSystemId.toString() + "/Processors"));
		return collectionJson;
    }
	
    @ApiOperation(value = "/redfish/v1/Systems/{computerSystemId}/Processors/{processorId}", notes = "Processors")
	@RequestMapping(value = "/" + COMPUTER_SYSTEM_ID + "/" + PROCESSORS_RESOURCE_NAME + "/" + PROCESSOR_ID, method = RequestMethod.GET)
    public RedfishResourceAmazingWrapper getProcessor(@PathVariable(required = true) String computerSystemId) {
    	super.uriInfo.put("computerSystemId", computerSystemId.toString());
        Context context = getCurrentContext();
        ProcessorDto processorDto = getOrThrow(() -> readerProcessorService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, processorDto);
    }

//    @Path(SIMPLE_STORAGE_RESOURCE_NAME)
//    public SimpleStorageCollectionResource getSimpleStorage() {
//        return getResource(SimpleStorageCollectionResource.class);
//    }
//
//    @Path(STORAGE_RESOURCE_NAME)
//    public StorageCollectionResource getStorage() {
//        return getResource(StorageCollectionResource.class);
//    }
//
//    @Path(MEMORY_RESOURCE_NAME)
//    public MemoryCollectionResource getMemoryCollection() {
//        return getResource(MemoryCollectionResource.class);
//    }
//
//    @Path(ETHERNET_INTERFACES_RESOURCE_NAME)
//    public EthernetInterfaceCollectionResource getEthernetInterfacesCollection() {
//        return getResource(EthernetInterfaceCollectionResource.class);
//    }
//
//    @Path(NETWORK_INTERFACES_RESOURCE_NAME)
//    public NetworkInterfaceCollectionResource getNetworkInterfacesCollection() {
//        return getResource(NetworkInterfaceCollectionResource.class);
//    }
//
//    @Path(COMPUTER_SYSTEM_METRICS_RESOURCE_NAME)
//    public ComputerSystemMetricsResource getComputerSystemMetrics() {
//        return getResource(ComputerSystemMetricsResource.class);
//    }


}
