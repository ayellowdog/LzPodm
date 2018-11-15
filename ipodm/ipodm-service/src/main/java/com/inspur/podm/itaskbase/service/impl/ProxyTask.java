/**
 *<P> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.itaskbase.service.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.inspur.podm.itaskbase.data.bean.TaskInfoModel;
import com.inspur.podm.itaskbase.service.TaskInfoService;
import com.inspur.podm.itaskbase.utils.TaskAppContext;

/**
 * 任务动态代理.2018-5-22增加定时任务
 * 
 * @author jiawei
 * @2018年5月21日 下午2:05:07
 */
@Component
public class ProxyTask implements InvocationHandler, Serializable {
	/**
	 * 序列化.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ProxyTask.class);
	/**
	 * 代理对象.
	 */
	private static Object object;
	private static ProxyTask proxyTask = new ProxyTask();
	private static String cron;
	private static String taskId;

	public static ProxyTask getInstance(String taskId, String cron, Object object) {
		ProxyTask.object = object;
		ProxyTask.cron = cron;
		ProxyTask.taskId = taskId;
		return proxyTask;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		String name = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] params = new Object[] { name, parameterTypes, args };
		TaskInfoModel taskInfoModel = new TaskInfoModel(taskId, "DEFAULT", cron, new Date(),"DEFAULT",
				"proxyTask", "timerInvoke", params, "0");
		TaskInfoService taskInfoService = (TaskInfoService) TaskAppContext.getBean("taskInfoService");
		taskInfoService.addITaskInfo(taskInfoModel);
		return null;
	}

	public void timerInvoke(String name, Class<?>[] parameterTypes, Object[] args) {
		try {
			Method method = null;
			try {
				method = object.getClass().getMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				LOG.error("反射" + name + "方法错误:" + e);
			} catch (SecurityException e) {
				LOG.error("反射" + name + "方法没有权限:" + e);
			}
			method.invoke(object, args);
		} catch (IllegalAccessException e) {
			LOG.error("反射" + name + "方法没有权限:" + e);
		} catch (IllegalArgumentException e) {
			LOG.error("反射" + name + "方法参数错误:" + e);
		} catch (InvocationTargetException e) {
			LOG.error("回调方法：" + name + "内部发生异常，请检查回调方法:" + e);
		}
	}

	public Object getCallBackObject() {
		Class<? extends Object> clazz = object.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), proxyTask);
	}
}
