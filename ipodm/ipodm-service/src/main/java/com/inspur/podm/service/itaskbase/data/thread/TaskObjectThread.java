package com.inspur.podm.service.itaskbase.data.thread;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过反射对象直接回调.
 * @author chenchunfeng
 * @date 2018年5月29日
 */
public class TaskObjectThread implements Runnable {
	
	/**
     * 日志对象.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskObjectThread.class);
    
    /**
     * 回调类.
     */
    private Object invokClass;
    
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
	public TaskObjectThread(Object invokClass, String methodName, Object[] obj){
		this.invokClass = invokClass;
		this.methodName = methodName;
		this.obj = obj;
	}

	@Override
	public void run() {
	    		 if (null != invokClass) {
	    			 Class<? extends Object> clazz = invokClass.getClass();
					 Method[] methods = clazz.getMethods();
	    			 for (Method method : methods) {
	    				 if (method.getName().equals(methodName)) {
	    					 try {
	    						 if (null != obj) {
	    							 method.invoke(invokClass, obj);
	    						 } else {
	    							 method.invoke(invokClass);
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