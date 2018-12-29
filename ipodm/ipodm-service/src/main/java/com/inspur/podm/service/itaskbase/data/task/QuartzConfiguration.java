package com.inspur.podm.service.itaskbase.data.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz配置.
 * @author chenchunfeng
 * @date 2018年4月5日
 */
//@Configuration
public class QuartzConfiguration {
   /**
    * Scheduler调度.
    */
   @Bean(name = "scheduler")
   public SchedulerFactoryBean scheduer() {
	   SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
	   //启动时更新已存在的job
	   schedulerFactoryBean.setOverwriteExistingJobs(true);
	   schedulerFactoryBean.setStartupDelay(1);
	   return schedulerFactoryBean;
   }
   
   
}
