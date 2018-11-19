package com.inspur.podm.service.itaskbase.data.task;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.service.itaskbase.utils.TaskUtil;
import com.inspur.podm.service.itaskbase.utils.TimeUtil;


/**
 * 定时任务操作类.
 * @author chenchunfeng
 * @date 2018年4月5日
 */
@Component
public class QuartzManager {
	
	/**
     * 日志对象.
     */
    private static final Logger LOG = LoggerFactory.getLogger(QuartzManager.class);
    
    @Autowired
    private Scheduler sched;

    /** 
     * 添加定时任务.
     * @param taskId 任务id
     * @param groupId 任务组名
     * @param jobClass  任务
     * @param cron  时间设置
     * @param startTime 任务开始时间
     * @param beanId spring容器beanid
     * @param methodName 方法名称
     * @param params 序列化后的参数
     */  
    public void addJob(String taskId, String groupId, String cron, Date startTime,
            String beanId, String methodName, String params) {
        try {
        	if (TimeUtil.isValidExpression(cron)) {
        		//任务执行类
        		JobDetail jobDetail= JobBuilder.newJob(ScheduleTask.class).withIdentity(taskId, groupId)
        				.usingJobData("taskId", taskId).usingJobData("beanId", beanId)
        				.usingJobData("methodName", methodName).usingJobData("params", params).build();
        		// 触发器
        		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        		// 触发器组
        		triggerBuilder.withIdentity(taskId, groupId);
        		if (null != startTime && (startTime.getTime() - 60000 > new Date().getTime())) {
        			triggerBuilder.startAt(startTime);
        		} else {
        			triggerBuilder.startNow();
        		}
        		// 触发时间绑定
        		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
        		// 触发器对象
        		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
        		// 调度容器设置
        		sched.scheduleJob(jobDetail, trigger);
        		//启动调动容器
        		if (sched.isShutdown()) {  
        			sched.start();
        		}  
        	}
        } catch (Exception e) {  
        	LOG.error("定时任务" + taskId + "触发失败:"+e);
        }  
    } 
    
    /** 
     * 添加定时任务.
     * @param taskId 任务id
     * @param groupId 任务组名
     * @param jobClass  任务
     * @param cron  时间设置
     * @param startTime 任务开始时间
     * @param invokeClass 回调对象
     * @param methodName 方法名称
     * @param params 序列化后的参数
     */  
    public void addJob(String taskId, String groupId, String cron, Date startTime,
            Object invokeClass, String methodName, String params) {
        try {
        	if (TimeUtil.isValidExpression(cron)) {
        		//任务执行类
        		JobDetail jobDetail= JobBuilder.newJob(ScheduleTask.class).withIdentity(taskId, groupId)
        				.usingJobData("taskId", taskId).usingJobData("class", TaskUtil.serializeToString(invokeClass))
        				.usingJobData("methodName", methodName).usingJobData("params", params).build();
        		// 触发器
        		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        		// 触发器组
        		triggerBuilder.withIdentity(taskId, groupId);
        		if (null != startTime && (startTime.getTime() - 60000 > new Date().getTime())) {
        			triggerBuilder.startAt(startTime);
        		} else {
        			triggerBuilder.startNow();
        		}
        		// 触发时间绑定
        		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
        		// 触发器对象
        		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
        		// 调度容器设置
        		sched.scheduleJob(jobDetail, trigger);
        		//启动调动容器
        		if (sched.isShutdown()) {  
        			sched.start();
        		}  
        	}
        } catch (Exception e) {  
        	LOG.error("定时任务" + taskId + "触发失败:"+e);
        }  
    } 

    /** 
     * 修改定时任务.
     * @param taskId 任务名
     * @param groupId 任务组名
     * @param cron 时间设置
     */  
    public void modifyJobTime(String taskId, String groupId, String cron) {  
        try {
        	if (TimeUtil.isValidExpression(cron)) {
            TriggerKey triggerKey = TriggerKey.triggerKey(taskId, groupId);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);  
            if (trigger == null) {  
                return;  
            }  
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) { 
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名，触发器组
                triggerBuilder.withIdentity(taskId, groupId);
                triggerBuilder.startNow();
                // 触发器时间绑定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 触发器对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 修改任务触发时间
                sched.rescheduleJob(triggerKey, trigger);
            }  
          }
        } catch (Exception e) {  
        	LOG.error("定时任务" + taskId + "修改触发时间失败:"+e);
        }  
    }  

    /** 
     * 删除定时任务.
     * @param taskId 任务id
     * @param groupId 任务组名
     */  
    public void removeJob(String taskId, String groupId) { 
        try {  
            TriggerKey triggerKey = TriggerKey.triggerKey(taskId, groupId);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);  
            if (trigger == null) {  
                return;  
            }  
            //停止触发器
            sched.pauseTrigger(triggerKey);
            //移除触发器
            sched.unscheduleJob(triggerKey);
            //删除任务
            sched.deleteJob(JobKey.jobKey(taskId, groupId));
        } catch (Exception e) {  
        	LOG.error("定时任务" + taskId + "删除触发失败:"+e); 
        }  
    } 

    /** 
     * 开启任务
     */  
    public void startJobs() {
        try {  
            sched.start();  
        } catch (Exception e) {  
        	LOG.error("Quartz定时任务管理器启动失败:"+e); 
        }  
    }  

    /** 
     * 停止任务
     */  
    public void shutdownJobs() {  
        try {  
            if (!sched.isShutdown()) {
                sched.shutdown();  
            }  
        } catch (Exception e) {  
        	LOG.error("Quartz定时任务管理器停止失败:"+e); 
        }  
    } 
    
    /**
     * 判断任务是否存在.
     * @param jobName 任务名称
     * @param jobGroupName 任务组
     * @return boolean
     */
    public boolean checkJobExist(String jobName, String jobGroupName){
    	JobKey jobKey = new JobKey(jobName , jobGroupName);
    	try {
    		if (null != sched.getJobDetail(jobKey)){
    			return true;
    		} else {
    			return false;
    		}
    	} catch (Exception e) {
            return false;
    	}
    }
}
