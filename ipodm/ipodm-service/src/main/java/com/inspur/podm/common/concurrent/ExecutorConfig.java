/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.common.concurrent;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import static com.intel.podm.common.enterprise.utils.beans.JndiNames.EVENT_SUBSCRIPTION_TASK_EXECUTOR;
import static com.intel.podm.common.enterprise.utils.beans.JndiNames.SYNCHRONIZED_TASK_EXECUTOR;
import static com.intel.podm.common.enterprise.utils.beans.JndiNames.DEFAULT_SCHEDULED_EXECUTOR_SERVICE;

/**
 * @ClassName: ExecutorConfig
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月21日 上午9:52:11
 */
@Configuration
@EnableAsync
public class ExecutorConfig {
	@Bean(name = "managedExecutorService")
	public ScheduledExecutorService getManagedExecutorService() {
		ScheduledExecutorService managedExecutorService = Executors.newScheduledThreadPool(5);
		return managedExecutorService;
	}
	@Bean(name = SYNCHRONIZED_TASK_EXECUTOR)
	public ScheduledExecutorService getTaskExecutorService() {
		ScheduledExecutorService taskExecutorService = Executors.newScheduledThreadPool(10);
		return taskExecutorService;
	}
	@Bean(name = EVENT_SUBSCRIPTION_TASK_EXECUTOR)
	public ScheduledExecutorService getEventsExecutorService() {
		ScheduledExecutorService eventsExecutorService = Executors.newScheduledThreadPool(3);
		return eventsExecutorService;
	}
	//这个好像只需要单线程，可以优化Executors.newSingleThreadScheduledExecutor()
	@Bean(name = DEFAULT_SCHEDULED_EXECUTOR_SERVICE)
	public ScheduledExecutorService getDefaultScheduledExecutorService() {
		ScheduledExecutorService eventsExecutorService = Executors.newScheduledThreadPool(1);
		return eventsExecutorService;
	}
}

