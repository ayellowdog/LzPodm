/**
 *<P> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.itaskbase.utils;

import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inspur.podm.service.itaskbase.data.bean.ThreadPoolProperty;
import com.inspur.podm.service.itaskbase.data.strategy.ThreadPoolSizeStrategy;

/**
 * @author jiawei
 * @2018年5月23日 下午2:52:22
 */
//@Configuration
public class StrategyUtils {

	private static String type;

	private static String corePoolSize;

	private static String maximumPoolSize;

	private static String keepAliveTime;

	private static String nodenum;

	@Bean
	public String initStrategyUtils(@Value("${threadpool.type}") String type,
			@Value("${threadpool.corepoolsize}") String corePoolSize,
			@Value("${threadpool.maximumPoolSize}") String maximumPoolSize,
			@Value("${threadpool.keepAliveTime}") String keepAliveTime,
			@Value("${threadpool.nodenum}") String nodenum) {
		StrategyUtils.type = type;
		StrategyUtils.corePoolSize = corePoolSize;
		StrategyUtils.maximumPoolSize = maximumPoolSize;
		StrategyUtils.keepAliveTime = keepAliveTime;
	    StrategyUtils.nodenum = nodenum;
	    return "";
	}
	
	public ThreadPoolProperty getThreadPoolParamter() {
		ThreadPoolProperty thp = null;
		switch (type) {
		case "custom":
			thp = new ThreadPoolProperty();
			thp.setCorePoolSize(Integer.parseInt(corePoolSize));
			thp.setMaximumPoolSize(Integer.parseInt(maximumPoolSize));
			thp.setKeepAliveTime(Long.parseLong(keepAliveTime));
			break;
		case "flexible":
			thp = ThreadPoolSizeStrategy.FlexibleThreadPool();
			break;
		case "hardware":
			thp = ThreadPoolSizeStrategy.HardWareThreadPool();
			break;
		case "node":
			thp = ThreadPoolSizeStrategy.NodeThreadPool(Integer.parseInt(nodenum));
			break;
		default:
			thp = ThreadPoolSizeStrategy.FlexibleThreadPool();
			break;
		}
		return thp;
	}

}
