package com.inspur.podm.service.itaskbase.data.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inspur.podm.service.itaskbase.data.bean.ThreadPoolInfo;
import com.inspur.podm.service.itaskbase.data.bean.ThreadPoolProperty;
import com.inspur.podm.service.itaskbase.utils.StrategyUtils;
import com.inspur.podm.service.itaskbase.utils.ThreadPoolInfoUtil;

/**
 * 任务管理线程池.
 * 
 * @author chenchunfeng
 * @date 2018年4月10日
 */
public class TaskThreadPool {
	/**
	 * 核心线程数.
	 */
	private static int corePoolSize = 10;
	/**
	 * 最大线程数.
	 */
	private static int maxPoolSize = 20;
	/**
	 * 线程存活时间.
	 */
	private static long keepAlive = 30;

	/**
	 * log.
	 */
	static Logger log = LoggerFactory.getLogger(TaskThreadPool.class);

	/** The instance. */
	private static class ThreadPoolHolder {
		private static final TaskThreadPool instance = new TaskThreadPool();
	}

	/** The exec. */
	private static Executor exec;

	private TaskThreadPool() {

	}

	/**
	 * 创建线程池类.
	 * 
	 * @return
	 */
	public static TaskThreadPool getInstance() {
		return ThreadPoolHolder.instance;
	}

	/**
	 * Gets the executor .
	 *
	 * @return the executor
	 */
	public static synchronized Executor ThreadPoolExecutor() {
		if (exec == null) {
			ThreadPoolProperty thp = new StrategyUtils().getThreadPoolParamter();
			if (null != thp) {
				corePoolSize = thp.getCorePoolSize();
				maxPoolSize = thp.getMaximumPoolSize();
				keepAlive = thp.getKeepAliveTime();
			}
			exec = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAlive, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>());
			return exec;
		}
		return exec;
	}

	public ThreadPoolInfo getThreadPoolInfo() {
		java.util.concurrent.ThreadPoolExecutor exe = (ThreadPoolExecutor) exec;
		ThreadPoolInfo info = ThreadPoolInfoUtil.getThreadPoolInfo(exe, "监控线程池:");
		return info;
	}
}
