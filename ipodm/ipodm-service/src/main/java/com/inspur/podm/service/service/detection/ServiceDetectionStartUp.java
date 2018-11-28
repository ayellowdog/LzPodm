package com.inspur.podm.service.service.detection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.inspur.podm.service.itaskbase.data.bean.TaskInfoModel;
import com.inspur.podm.service.itaskbase.service.TaskInfoService;
/**
 * 
 * @ClassName: ServiceDetectionStartUp
 * @Description: 设备发现采用定时任务的方式，项目启动后开始运行
 *
 * @author: liuchangbj
 * @date: 2018年11月14日 上午9:40:34
 */
//@Component
//@Order(1)
public class ServiceDetectionStartUp implements ApplicationRunner {
	@Autowired
	TaskInfoService taskInfoService;
	private static final Logger logger = LoggerFactory.getLogger(ServiceDetectionStartUp.class);
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
//		logger.info("Service dection is starting");
//		TaskInfoModel taskModel = new TaskInfoModel("11111", "ttttt", "0/30 * * * * ? *", null, "dhcp",
//				"dhcpServiceDetector", "test", new Object[] {"一条大黄狗"}, "0");
//		taskInfoService.addITaskInfo(taskModel);
//		
	}

}
