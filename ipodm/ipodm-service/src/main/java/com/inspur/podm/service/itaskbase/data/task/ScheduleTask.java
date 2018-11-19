package com.inspur.podm.service.itaskbase.data.task;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inspur.podm.service.itaskbase.data.thread.TaskObjectThread;
import com.inspur.podm.service.itaskbase.data.thread.TaskThread;
import com.inspur.podm.service.itaskbase.data.thread.TaskThreadPool;
import com.inspur.podm.service.itaskbase.utils.TaskUtil;


/**
 * 定时任务调度.
 * @author chenchunfeng
 * @date 2018年4月4日
 */
public class ScheduleTask implements Job {
	/**
     * 日志对象.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTask.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// String taskId = context.getJobDetail().getJobDataMap().getString("taskId");
		 String beanId = context.getJobDetail().getJobDataMap().getString("beanId");
		 String methodName = context.getJobDetail().getJobDataMap().getString("methodName");
		 String params = context.getJobDetail().getJobDataMap().getString("params");
		 String invokeClass = context.getJobDetail().getJobDataMap().getString("class");
		 Object[] obj = null;
		 if (null != params && !params.isEmpty()) {
			 obj = (Object[]) TaskUtil.deSerializeToObject(params);
		 }
		// LOG.info("任务id:" + taskId + "运行开始...");
		 TaskThreadPool.getInstance();
		 if (StringUtils.isNotEmpty(beanId)) {
			 TaskThreadPool.ThreadPoolExecutor().execute(
					 new TaskThread(beanId, methodName, obj));
		 } else {
			 //反序列化回调类
			 Object invokeObj = TaskUtil.deSerializeToObject(invokeClass);
			 TaskThreadPool.ThreadPoolExecutor().execute(
					 new TaskObjectThread(invokeObj, methodName, obj));
		 }
	}

}
