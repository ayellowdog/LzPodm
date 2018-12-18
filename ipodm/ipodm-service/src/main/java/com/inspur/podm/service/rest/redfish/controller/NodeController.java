/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.PathParamConstants.COMPOSED_NODE_ID;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.inspur.podm.service.rest.error.PodmExceptions.resourcesStateMismatch;
import static com.inspur.podm.service.rest.error.PodmExceptions.unsupportedCreationRequest;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.dto.ComposedNodeDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.AllocationService;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.service.rest.redfish.json.templates.CollectionJson;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.assembly.RequestedNodeJson;
import com.inspur.podm.service.rest.redfish.serializers.CollectionDtoJsonSerializer;

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
@RequestMapping("/redfish/v1/Nodes")
@Api(value = "NodesController", description = "/redfish/v1/Nodes控制器")
public class NodeController extends BaseController {
	@Resource(name = "composedNodeReader")
	private ReaderService<ComposedNodeDto> readerService;
	@Autowired
	private CollectionDtoJsonSerializer collectionDtoJsonSerializer;
	@Autowired
	private AllocationService service;

	@ApiOperation(value = "查看redfish目录/redfish/v1/Nodes", notes = "Nodes")
	@RequestMapping(method = RequestMethod.GET)
	public CollectionJson get() {
		CollectionDto collectionDto = getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto,
				new ODataId("/redfish/v1/Nodes"));
		return collectionJson;
	}

	@ApiOperation(value = "/redfish/v1/Nodes/{NodeID}", notes = "/redfish/v1/Nodes/{NodeID}")
	@RequestMapping(value = "/" + "{" + COMPOSED_NODE_ID + "}", method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getNode(@PathVariable(required = true) Long composedNodeId) {
		super.uriInfo.put("composedNodeId", composedNodeId.toString());
		Context context = getCurrentContext();
		ComposedNodeDto composedNodeDto = getOrThrow(() -> readerService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, composedNodeDto);
	}

	@ApiOperation(value = "/redfish/v1/Nodes/Actions/Allocate", notes = "/redfish/v1/Nodes/Actions/Allocate")
	@RequestMapping(value = "/Actions/Allocate", method = RequestMethod.POST)
	public Response allocate(RequestedNodeJson requestedNode) {
		try {
			Context context = service.allocate(requestedNode);
			return Response.created(context.asOdataId().toUri()).build();
		} catch (RequestValidationException e) {
			throw unsupportedCreationRequest(e.getViolations());
		} catch (EntityOperationException e) {
			throw resourcesStateMismatch("Conflict during allocation", e);
		}
	}

}
