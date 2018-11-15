/**
 *<P> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.itaskbase.data.bean;

/**
 * @author jiawei
 * @2018年5月23日 上午9:25:16
 * Creates a new {@code ThreadPoolExecutor} with the given initial
 * parameters or default parameters.
 *
 * @param corePoolSize the number of threads to keep in the pool, even
 *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
 * @param maximumPoolSize the maximum number of threads to allow in the
 *        pool
 * @param keepAliveTime when the number of threads is greater than
 *        the core, this is the maximum time that excess idle threads
 *        will wait for new tasks before terminating.
 * @param unit the time unit for the {@code keepAliveTime} argument
 **/
public class ThreadPoolProperty {
	private int corePoolSize;
	private int maximumPoolSize;
	private long keepAliveTime;
	public int getCorePoolSize() {
		return corePoolSize;
	}
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}
	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}
	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}
	public long getKeepAliveTime() {
		return keepAliveTime;
	}
	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}
	@Override
	public String toString() {
		return "ThreadPoolProperty [corePoolSize=" + corePoolSize + ", maximumPoolSize=" + maximumPoolSize
				+ ", keepAliveTime=" + keepAliveTime + "]";
	}
	
}

