/**
 *<P> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.itaskbase.data.bean;
/**
 * @author jiawei
 * @2018年6月15日 下午2:43:34
 */
public class ThreadPoolInfo {
	/**
	 * 当前排队线程数.
	 */
	private long queueSize;
	/**
	 * 当前活动线程数.
	 */
	private long activeSize;
	/**
	 * 当前执行完毕线程数.
	 */
	/**
	 * 总线程数.
	 */
	private long completedTaskCount;
	private long totalTaskcount;
	/**
	 * 线程最大的峰值.
	 */
	private long peakValue;
	private int corePoolSize;
	private int  maximumPoolSize;
	private long keepAliveTime;
	private String name;
	public long getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(long queueSize) {
		this.queueSize = queueSize;
	}
	public long getActiveSize() {
		return activeSize;
	}
	public void setActiveSize(long activeSize) {
		this.activeSize = activeSize;
	}
	public long getCompletedTaskCount() {
		return completedTaskCount;
	}
	public void setCompletedTaskCount(long completedTaskCount) {
		this.completedTaskCount = completedTaskCount;
	}
	public long getTotalTaskcount() {
		return totalTaskcount;
	}
	public void setTotalTaskcount(long totalTaskcount) {
		this.totalTaskcount = totalTaskcount;
	}
	public long getPeakValue() {
		return peakValue;
	}
	public void setPeakValue(long peakValue) {
		this.peakValue = peakValue;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ThreadPoolInfo [当前排队线程数=" + queueSize + ", 当前活动线程数=" + activeSize + ", 当前执行完毕线程数="
				+ completedTaskCount + ", 总线程数=" + totalTaskcount + "/n" + " 线程最大的峰值=" + peakValue
				+ ", 核心线程数=" + corePoolSize + ", 最大线程数=" + maximumPoolSize + ", 存活时间="
				+ keepAliveTime + ", 线程池名字=" + name + "]";
	}	
}

