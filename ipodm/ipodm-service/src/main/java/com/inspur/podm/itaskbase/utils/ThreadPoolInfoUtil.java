/**
 *<P> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.itaskbase.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.inspur.podm.itaskbase.data.bean.ThreadPoolInfo;

/**
 * @author jiawei
 * @2018年6月15日 下午2:43:29
 */
public class ThreadPoolInfoUtil {
	public static ThreadPoolInfo getThreadPoolInfo(ThreadPoolExecutor exe, String name) {
    	ThreadPoolInfo info= new ThreadPoolInfo();
    	if(null != exe){
    		long queueSize = exe.getQueue().size();
    		long activeSize = exe.getActiveCount();
    		long completedTaskCount = exe.getCompletedTaskCount();
    		long totalTaskcount = exe.getTaskCount();
    		int corePoolSize = exe.getCorePoolSize();
    		long keepAliveTime = exe.getKeepAliveTime(TimeUnit.SECONDS);
    		int maximumPoolSize = exe.getMaximumPoolSize();
    		int peakValue = exe.getLargestPoolSize();
    		info.setName(name);
    		info.setActiveSize(activeSize);
    		info.setCompletedTaskCount(completedTaskCount);
    		info.setCorePoolSize(corePoolSize);
    		info.setKeepAliveTime(keepAliveTime);
    		info.setMaximumPoolSize(maximumPoolSize);
    		info.setPeakValue(peakValue);
    		info.setQueueSize(queueSize);
    		info.setTotalTaskcount(totalTaskcount);
    	}
		return info;
    }
}

