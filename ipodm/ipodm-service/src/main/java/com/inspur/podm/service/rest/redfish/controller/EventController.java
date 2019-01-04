/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SERVICE;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SUBSCRIPTION;
import static com.inspur.podm.api.business.services.context.PathParamConstants.CHASSIS_ID;
import static com.inspur.podm.api.business.services.context.PathParamConstants.COMPOSED_NODE_ID;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.intel.podm.common.types.Id.id;

import java.util.concurrent.TimeoutException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.ChassisDto;
import com.inspur.podm.api.business.dto.EventServiceDto;
import com.inspur.podm.api.business.dto.EventSubscriptionDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.requests.EventSubscriptionRequest;
import com.inspur.podm.service.rest.redfish.json.templates.CollectionJson;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.EventSubscriptionRequestJson;
import com.inspur.podm.service.rest.redfish.serializers.CollectionDtoJsonSerializer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * @ClassName: EventController
 * @Description: EventController
 *
 * @author: zhangdian
 * @date: 2019年1月3日 上午8:35:06
 */
@RestController
@RequestMapping("/redfish/v1/EventService")
@Api(value = "EventServiceController", description = "/redfish/v1/EventService")
public class EventController extends BaseController {
	@Resource(name = "EventServiceReaderService")
    private ReaderService<EventServiceDto> readerService;
	
    @Autowired
	private CollectionDtoJsonSerializer collectionDtoJsonSerializer;
    
    @Resource(name = "EventSubscriptionService")
    private ReaderService<EventSubscriptionDto> readerSubscriptionService;
    
    @Resource(name = "SubscriptionActionService")
    private CreationService<EventSubscriptionRequest> creationService;
    
    @Resource(name = "SubscriptionActionService")
    private RemovalService<EventSubscriptionRequest> removalService;

    @ApiOperation(value = "EventService", notes = "EventService")
	@RequestMapping(method = RequestMethod.GET)
    public RedfishResourceAmazingWrapper get() {
        Context context = contextOf(id(""), EVENT_SERVICE);
        EventServiceDto eventServiceDto = getOrThrow(() -> readerService.getResource(context));
		return new RedfishResourceAmazingWrapper(context, eventServiceDto);
    }
    
    @ApiOperation(value = "EventService/Subscriptions", notes = "Subscriptions")
    @RequestMapping(value = "/Subscriptions", method = RequestMethod.GET)
    public CollectionJson getSubscriptions() {
        CollectionDto collectionDto = getOrThrow(() -> readerSubscriptionService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto, new ODataId("/redfish/v1/EventService/Subscriptions"));
		return collectionJson;
    }
    
    @ApiOperation(value = "EventService/Subscriptions/{id}", notes = "Subscriptions")
    @RequestMapping(value = "/Subscriptions/{id}", method = RequestMethod.GET)
    public RedfishResourceAmazingWrapper getSubscription(@PathVariable(required = true) Long id) {
        Context context = contextFromSubscriptionId(id);
        return new RedfishResourceAmazingWrapper(
            context,
            getOrThrow(() -> readerSubscriptionService.getResource(context))
        );
    }
    
    @ApiOperation(value = "EventService/Subscriptions", notes = "Subscriptions")
	@RequestMapping(value = "/Subscriptions", method = RequestMethod.POST)
    public Response createSubscription(@RequestBody(required = true)EventSubscriptionRequestJson payload) throws BusinessApiException, TimeoutException {
        Context context = creationService.create(null, payload);
        return Response.created(context.asOdataId().toUri()).build();
    }
    
	@ApiOperation(value = "EventService/Subscriptions/{id} Delete", notes = "Delete")
	@RequestMapping(value = "/Subscriptions/{id}", method = RequestMethod.DELETE)
    public Response deleteSubscription(@PathVariable(required = true) Long id) throws BusinessApiException, TimeoutException {
        removalService.perform(contextFromSubscriptionId(id));
        return noContent().build();
    }
    
    private Context contextFromSubscriptionId(Long id) {
        // TODO: RSASW-8103
        return contextOf(id(""), EVENT_SERVICE).child(id(id), EVENT_SUBSCRIPTION);
    }
    
}
