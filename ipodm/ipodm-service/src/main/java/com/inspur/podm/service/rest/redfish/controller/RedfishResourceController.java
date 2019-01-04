package com.inspur.podm.service.rest.redfish.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@ApiOperation(value = "redfish", notes = "查看redfish根目录")
	@RequestMapping(method = RequestMethod.GET)
	public Object get() {
        return new V1();
    }

    static class V1 {
        @JsonProperty("v1")
        String v1 = "/redfish/v1";
    }
}
