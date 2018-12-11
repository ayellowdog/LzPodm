package com.inspur.podm.controller;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.service.itaskbase.data.bean.TaskInfoModel;
import com.inspur.podm.service.itaskbase.service.TaskInfoService;
import com.inspur.podm.service.service.detection.dhcp.DhcpServiceDetectorInterface;
import com.intel.podm.business.entities.dao.MyChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;


@RestController
@RequestMapping(value = "test")
public class PerconController {

	@Autowired
	MyChassisDao myDao;
	@Autowired
	DhcpServiceDetectorInterface dhcpServiceDetectorInterface;
    @PostMapping
    public void addPerson() {
    	dhcpServiceDetectorInterface.test("wryueihreuihf");
    }

    
}

