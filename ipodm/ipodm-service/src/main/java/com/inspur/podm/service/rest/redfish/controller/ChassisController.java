/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.PathParamConstants.CHASSIS_ID;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.dto.ChassisDto;
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
 * @ClassName: ChassisController
 * @Description: ChassisController
 *
 * @author: zhangdian
 * @date: 2018年11月29日 下午2:16:44
 */
@RestController
@RequestMapping("/redfish/v1/Chassis")
@Api(value = "ChassisController", description = "/redfish/v1/Chassis控制器")
public class ChassisController extends BaseController {
	
	@Autowired
	private ReaderService<ChassisDto> readerService;
	@Autowired
	private CollectionDtoJsonSerializer collectionDtoJsonSerializer;
	
	@ApiOperation(value = "查看redfish目录/redfish/v1/Chassis", notes = "Chassis")
	@RequestMapping(method = RequestMethod.GET)
    public CollectionJson get() {
		CollectionDto collectionDto = getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
		CollectionJson collectionJson = collectionDtoJsonSerializer.translate(collectionDto, new ODataId("/redfish/v1/Chassis"));
		return collectionJson;
    }
	
	@ApiOperation(value = "/redfish/v1/Chassis/{Chassis}", notes = "/redfish/v1/Chassis/{Chassis}")
	@RequestMapping(value = "/" + CHASSIS_ID, method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper getChassis(@PathVariable(required = true) String chassisId) {
		super.uriInfo.put("chassisId", chassisId);
        Context context = getCurrentContext();
        ChassisDto chassisDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, chassisDto);
    }
	
	

}

