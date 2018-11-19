/**
 *<P> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.itaskbase.data.strategy;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.ThreadPoolExecutor;

import com.inspur.podm.service.itaskbase.data.bean.ThreadPoolProperty;

/**
 * 线程池策略(待考验).
 * 
 * @author jiawei
 * @2018年5月23日 上午9:22:02
 */
public class ThreadPoolSizeStrategy {

	/**
	 * Creates a thread pool that creates new threads as needed, but will reuse
	 * previously constructed threads when they are available. These pools will
	 * typically improve the performance of programs that execute many
	 * short-lived asynchronous tasks. Calls to {@code execute} will reuse
	 * previously constructed threads if available. If no existing thread is
	 * available, a new thread will be created and added to the pool. Threads
	 * that have not been used for sixty seconds are terminated and removed from
	 * the cache. Thus, a pool that remains idle for long enough will not
	 * consume any resources. Note that pools with similar properties but
	 * different details (for example, timeout parameters) may be created using
	 * {@link ThreadPoolExecutor} constructors.
	 *
	 * @return ThreadPoolProperty
	 */
	public static ThreadPoolProperty FlexibleThreadPool() {
		ThreadPoolProperty thp = new ThreadPoolProperty();
		thp.setCorePoolSize(0);
		thp.setMaximumPoolSize(Integer.MAX_VALUE);
		thp.setKeepAliveTime(60L);
		return thp;
	}

	public static ThreadPoolProperty HardWareThreadPool() {
		Runtime rtime = Runtime.getRuntime();
		long freeM = rtime.freeMemory();
		long totalM = rtime.totalMemory();
		OperatingSystemMXBean systemMX = ManagementFactory.getOperatingSystemMXBean();
		int core = systemMX.getAvailableProcessors();
		int corefactor = 20;
		float maxfactor = 1.5f;
		int corePoolSize = (int) (corefactor * core * totalM / freeM);
		int maximumPoolSize = (int) (corePoolSize * maxfactor);
		ThreadPoolProperty thp = new ThreadPoolProperty();
		thp.setCorePoolSize(corePoolSize);
		thp.setMaximumPoolSize(maximumPoolSize);
		thp.setKeepAliveTime(60L);
		return thp;
	}

	public static ThreadPoolProperty NodeThreadPool(int nodeNum) {
		int corePoolSize = nodeNum * 6 / (120 / 5);
		ThreadPoolProperty thp = new ThreadPoolProperty();
		thp.setCorePoolSize(corePoolSize);
		thp.setMaximumPoolSize((int) (corePoolSize * 2));
		thp.setKeepAliveTime(60L);
		return thp;
	}

}
