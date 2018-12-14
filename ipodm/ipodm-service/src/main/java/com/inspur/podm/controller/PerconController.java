package com.inspur.podm.controller;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.controller.test.CostTime;
import com.inspur.podm.service.itaskbase.data.bean.TaskInfoModel;
import com.inspur.podm.service.itaskbase.service.TaskInfoService;
import com.inspur.podm.service.service.detection.dhcp.MyDhcpServiceDetectorInterface;
import com.intel.podm.business.entities.dao.MyChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;


@RestController
@RequestMapping(value = "test")
public class PerconController {

	@Autowired
	MyChassisDao myDao;
	@Autowired
	MyDhcpServiceDetectorInterface dhcpServiceDetectorInterface;
    @PostMapping
    @Transactional
	@Retryable(value= {RuntimeException.class},maxAttempts = 3,backoff = @Backoff(delay = 100l,multiplier = 1))
    @CostTime
    public void addPerson() throws Exception {
    	dhcpServiceDetectorInterface.test("事件监听与重试测试");
    }
    @Recover
    public void recover(RuntimeException e) {
            System.out.println("重试结束");
    }

    
}

