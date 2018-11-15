package com.inspur.podm.itaskbase.data.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 统一任务bean.
 * @author chenchunfeng
 * @date 2018年4月10日
 */
public class TaskInfoModel implements Serializable{

	/**
	 * 序列化.
	 */
	private static final long serialVersionUID = 4440700069203938772L;

    /**
     * id.
     */
	private String id;
	
	/**
	 * 任务名称.
	 */
	private String taskName;
	
	/**
	 * 巡检时间频次.
	 */
	private String taskFrequency;
	
	/**
	 * 任务开始时间.
	 */
	private Date startTime;
	
	/**
	 * 任务组.
	 */
	private String jobGroup;
	
	/**
	 * 回调类（beanId）.
	 */
	private String className;
	
	/**
	 * 回调类.
	 */
	private Object invokeClass;
	
	/**
	 * 执行方法名.
	 */
	private String methodName;
	
	/**
	 * 参数.
	 */
	private Object[] params;
	
	/**
	 * 任务类型(0定时任务 1一次性任务).
	 */
	private String taskType;
	
	/**
	 * 任务启停状态(0启动 1停止).
	 */
	private String runFlag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskFrequency() {
		return taskFrequency;
	}

	public void setTaskFrequency(String taskFrequency) {
		this.taskFrequency = taskFrequency;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}
	
	public String getRunFlag() {
		return runFlag;
	}

	public void setRunFlag(String runFlag) {
		this.runFlag = runFlag;
	}
	
	public Object getInvokeClass() {
		return invokeClass;
	}

	public void setInvokeClass(Object invokeClass) {
		this.invokeClass = invokeClass;
	}

	/**
	 * 可以设置任务启动停止参构造器.
	 * @param id 任务id （根据具体业务需要，不填会默认生产UUID）
	 * @param taskName 任务名称
	 * @param taskFrequency 频率（一次性任务不用填）
	 * @param startTime 任务开始时间（可填可不填）
	 * @param jobGroup  任务组
	 * @param className 回调类（beanId）
	 * @param methodName 方法名称
	 * @param params 参数
	 * @param taskType 任务类型(0定时任务 1一次性任务)
	 * @param runFlag 任务启停状态（0启动 1停止 默认0）
	 */
	public TaskInfoModel(String id, String taskName, String taskFrequency,
			Date startTime, String jobGroup, String className, String methodName,
			Object[] params, String taskType, String runFlag) {
		this.id = id;
		this.taskName = taskName;
		this.taskFrequency = taskFrequency;
		this.startTime = startTime;
		this.jobGroup = jobGroup;
		this.className = className;
		this.methodName = methodName;
		this.params = params;
		this.taskType = taskType;
		this.runFlag = runFlag;
	}
	
	/**
	 * 默认任务为启动参构造器.
	 * @param id 任务id （根据具体业务需要，不填会默认生产UUID）
	 * @param taskName 任务名称
	 * @param taskFrequency 频率（一次性任务不用填）
	 * @param startTime 任务开始时间（可填可不填）
	 * @param jobGroup  任务组
	 * @param className 回调类（beanId）
	 * @param methodName 方法名称
	 * @param params 参数
	 * @param taskType 任务类型(0定时任务 1一次性任务)
	 */
	public TaskInfoModel(String id, String taskName, String taskFrequency,
			Date startTime, String jobGroup, String className, String methodName,
			Object[] params, String taskType) {
		this.id = id;
		this.taskName = taskName;
		this.taskFrequency = taskFrequency;
		this.startTime = startTime;
		this.jobGroup = jobGroup;
		this.className = className;
		this.methodName = methodName;
		this.params = params;
		this.taskType = taskType;
	}
	
	/**
	 * 默认任务为启动参构造器.
	 * @param id 任务id （根据具体业务需要，不填会默认生产UUID）
	 * @param taskName 任务名称
	 * @param taskFrequency 频率（一次性任务不用填）
	 * @param startTime 任务开始时间（可填可不填）
	 * @param jobGroup  任务组
	 * @param invokeClass 回调函数
	 * @param methodName 方法名称
	 * @param params 参数
	 * @param taskType 任务类型(0定时任务 1一次性任务)
	 */
	public TaskInfoModel(String id, String taskName, String taskFrequency,
			Date startTime, String jobGroup, Object invokeClass, String methodName,
			Object[] params, String taskType) {
		this.id = id;
		this.taskName = taskName;
		this.taskFrequency = taskFrequency;
		this.startTime = startTime;
		this.jobGroup = jobGroup;
		this.methodName = methodName;
		this.params = params;
		this.taskType = taskType;
		this.invokeClass = invokeClass;
	}
	
	/**
	 * 无参构造函数.
	 */
	public TaskInfoModel() {
		
	}
}
