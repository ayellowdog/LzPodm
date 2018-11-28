package com.inspur.podm.service.rest.redfish.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.dto.redfish.ServiceRootContext;
import com.inspur.podm.api.business.dto.redfish.ServiceRootDto;
import com.inspur.podm.api.business.services.redfish.ServiceRootService;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * @ClassName: RedfishResourceController
 * @Description:  service root Controller
 *
 * @author: zhangdian
 * @date: 2018年11月27日 下午4:23:22
 */
@RestController
@RequestMapping("/redfish")
@Api(value = "RedfishController", description = "根目录控制器")
public class RedfishResourceController {
	
	/** @Fields readerService: readerService  */
	@Autowired
    private ServiceRootService readerService;

	@ApiOperation(value = "查看redfish根目录", notes = "查看redfish根目录")
	@RequestMapping(method = RequestMethod.GET)
	public Object get() {
        return new V1();
    }

	@ApiOperation(value = "v1目录", notes = "v1目录")
	@RequestMapping(value = "/v1", method = RequestMethod.GET)
    public RedfishResourceAmazingWrapper getServiceRoot() {
		ServiceRootDto serviceRootDto = readerService.getServiceRoot();
        return new RedfishResourceAmazingWrapper(new ServiceRootContext(), serviceRootDto);
    }

    static class V1 {
        @JsonProperty("v1")
        String v1 = "/redfish/v1";
    }
}
