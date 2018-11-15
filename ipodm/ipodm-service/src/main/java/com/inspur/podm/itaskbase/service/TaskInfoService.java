package com.inspur.podm.itaskbase.service;

import com.inspur.podm.itaskbase.data.bean.TaskInfoModel;

/**
 * 统一任务信息service.
 * @author chenchunfeng
 * @date 2018年4月2日
 */
public interface TaskInfoService {
    
	/**
	 * 新建任务.
	 * 必须参数：id jobGroup taskFrequency className methodName 
	 * @param ITaskInfoModel
	 * @return
	 */
    boolean addITaskInfo(TaskInfoModel ITaskInfoModel); 
    
    /**
	 * 修改任务信息.
	 * 一次性任务修改 id jobGroup taskFrequency className methodName 参数必须
	 * 定时任务 id jobGroup taskFrequency 必须
	 * @param ITaskInfoModel 需要更新的信息
	 */
    boolean updateTaskInfo(TaskInfoModel ITaskInfoModel);
        
    /**
     * 根据任务id删除任务.
     * 需要传id jobGroup参数
     * @param iTaskInfoModel 任务信息
     * @return boolean
     */
    boolean deleteiTaskInfo(TaskInfoModel iTaskInfoModel);
}
