package com.inspur.podm.itaskbase.data.thread;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inspur.podm.itaskbase.utils.TaskAppContext;


/**
 * 统一任务调度线程.
 * @author chenchunfeng
 * @date 2018年4月11日
 */
public class TaskThread implements Runnable {
	
	/**
     * 日志对象.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskThread.class);
    
    /**
     * spring容器beanid.
     */
    private String beanId;
    
    /**
     * 方法名.
     */
    private String methodName;
    
    /**
     * 参数.
     */
    private Object[] obj;
    
    /**
     * 有参构造函数.
     * @param beanId spring容器beanid
     * @param methodName 方法名称 
     * @param obj 
     */
	public TaskThread(String beanId, String methodName, Object[] obj){
		this.beanId = beanId;
		this.methodName = methodName;
		this.obj = obj;
	}

	@Override
	public void run() {
	    		 Object object = TaskAppContext.getBean(beanId);
	    		 if (null != object) {
	    			 Class<? extends Object> clazz = object.getClass();
					 Method[] methods = clazz.getMethods();
	    			 for (Method method : methods) {
	    				 if (method.getName().equals(methodName)) {
	    					 try {
	    						 if (null != obj) {
	    							 method.invoke(object, obj);
	    						 } else {
	    							 method.invoke(object);
	    						 }
	    					 } catch (Exception e) {
	    						 LOG.error("任务id:taskid" + "运行失败："+ e);
	    					 }
	    					 break;
	    				 }
	    			 } 
	    		 }
	    }

}
