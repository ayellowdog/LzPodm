package com.inspur.podm.service.itaskbase.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.service.itaskbase.data.bean.TaskInfoModel;
import com.inspur.podm.service.itaskbase.data.consts.TaskConst;
import com.inspur.podm.service.itaskbase.data.task.QuartzManager;
import com.inspur.podm.service.itaskbase.data.thread.TaskObjectThread;
import com.inspur.podm.service.itaskbase.data.thread.TaskThread;
import com.inspur.podm.service.itaskbase.data.thread.TaskThreadPool;
import com.inspur.podm.service.itaskbase.service.TaskInfoService;
import com.inspur.podm.service.itaskbase.utils.TaskUtil;
import com.inspur.podm.service.itaskbase.utils.TimeUtil;

/**
 * 统一任务管理实现类.
 * @author chenchunfeng
 * @date 2018年4月10日
 */
@Service("taskInfoService")
public class TaskInfoServiceImpl implements TaskInfoService {
	
	/**
     * 日志对象.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskInfoServiceImpl.class);
	
	@Autowired
	private QuartzManager manager;

	@Override
	public boolean addITaskInfo(TaskInfoModel taskInfoModel) {
		try {
			//任务添加
			if (taskInfoModel.getTaskType().equals(TaskConst.TASK_TYPE_ONE)) {
				if (null != taskInfoModel.getStartTime() && (taskInfoModel.getStartTime().getTime() - 60000 > new Date().getTime())
						&& TimeUtil.isValidExpression(taskInfoModel.getTaskFrequency())) {
					if (null != taskInfoModel.getParams()) {
						String params = TaskUtil.serializeToString(taskInfoModel.getParams());
						if (null != taskInfoModel.getInvokeClass()) {
							manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
									taskInfoModel.getInvokeClass(), taskInfoModel.getMethodName(), params);
						} else {
							manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
									taskInfoModel.getClassName(), taskInfoModel.getMethodName(), params);
						}
					} else {
						if (null != taskInfoModel.getInvokeClass()) {
							manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
									taskInfoModel.getInvokeClass(), taskInfoModel.getMethodName(), null);
						} else {
							manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
									taskInfoModel.getClassName(), taskInfoModel.getMethodName(), null);
						}
					}
				} else {
					TaskThreadPool.getInstance();
					if (null != taskInfoModel.getInvokeClass()) {
						TaskThreadPool.ThreadPoolExecutor().execute(
								new TaskObjectThread(taskInfoModel.getInvokeClass(), taskInfoModel.getMethodName(), taskInfoModel.getParams()));
					} else {
						TaskThreadPool.ThreadPoolExecutor().execute(
								new TaskThread(taskInfoModel.getClassName(), taskInfoModel.getMethodName(), taskInfoModel.getParams()));
					}
				}
			} else {
				if (null != taskInfoModel.getParams()) {
					String params = TaskUtil.serializeToString(taskInfoModel.getParams());
					if (null != taskInfoModel.getInvokeClass()) {
						manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
								taskInfoModel.getInvokeClass(), taskInfoModel.getMethodName(), params);
					} else {
						manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
								taskInfoModel.getClassName(), taskInfoModel.getMethodName(), params);
					}
				} else {
					if (null != taskInfoModel.getInvokeClass()) {
						manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
								taskInfoModel.getInvokeClass(), taskInfoModel.getMethodName(), null);
					} else {
						manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
								taskInfoModel.getClassName(), taskInfoModel.getMethodName(), null);
					}
				}
			}
			return true;
		} catch (Exception e) {
			LOG.error("任务添加失败：" + e);
			return false;
		}
	}

	@Override
	public boolean updateTaskInfo(TaskInfoModel taskInfoModel) {
		try {
			if (taskInfoModel.getTaskType().equals(TaskConst.TASK_TYPE_ONE)) {
				manager.removeJob(taskInfoModel.getId(), taskInfoModel.getJobGroup());
				if (null != taskInfoModel.getStartTime() && (taskInfoModel.getStartTime().getTime() - 60000 > new Date().getTime())
						&& TimeUtil.isValidExpression(taskInfoModel.getTaskFrequency())) {
					if (null != taskInfoModel.getParams()) {
						String params = TaskUtil.serializeToString(taskInfoModel.getParams());
						manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
								taskInfoModel.getClassName(), taskInfoModel.getMethodName(), params);
					} else {
						manager.addJob(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency(), taskInfoModel.getStartTime(),
								taskInfoModel.getClassName(), taskInfoModel.getMethodName(), null);
					}
				} else {
					TaskThreadPool.getInstance();
					TaskThreadPool.ThreadPoolExecutor().execute(
							new TaskThread(taskInfoModel.getClassName(), taskInfoModel.getMethodName(), taskInfoModel.getParams()));
				}
			} else {
				manager.modifyJobTime(taskInfoModel.getId(), taskInfoModel.getJobGroup(), taskInfoModel.getTaskFrequency());
			}
			return true;
		} catch (Exception e) {
			LOG.error("任务更新失败：" + e);
			return false;
		}
	}

	@Override
	public boolean deleteiTaskInfo(TaskInfoModel taskInfoModel) {
		try {
             //移除定时器中的任务
             manager.removeJob(taskInfoModel.getId(), taskInfoModel.getJobGroup());
             return true;
        } catch (Exception e) {
        	LOG.error("任务删除失败：" + e);
            return false;
        }
	}

}
