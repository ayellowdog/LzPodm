/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.business.redfish.services;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.intel.podm.common.types.redfish.RedfishEventArray;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.EventsConfig;
import com.intel.podm.config.base.dto.EventsConfig.BufferedEventProcessing;
import com.intel.podm.config.base.dto.EventsConfig.SouthboundConfiguration;

/**
 * @ClassName: EventProcessorConfig
 * @Description: 此类原来属于EventReceivingServiceImpl类的一个方法，提出来了
 *
 * @author: liuchangbj
 * @date: 2019年1月3日 上午8:53:35
 */
@Component
public class EventProcessorImpl implements EventsProcessor {
	@Config
	@Resource(name="podmConfigProvider")
	private ConfigProvider configProvider;
	@Autowired
	private IncomingEventsProcessor incomingEventsProcessor;
	@Autowired
	private AutoEvictingIncomingEventsBuffer autoEvictingIncomingEventsBuffer;
	@PostConstruct
	public void init() {
		SouthboundConfiguration southboundEventingConfig = configProvider.get(EventsConfig.class).getSouthboundConfiguration();
		if (southboundEventingConfig.isBufferedEventProcessingEnabled()) {
			BufferedEventProcessing bufferedEventProcessingConfig = southboundEventingConfig.getBufferedEventProcessing();
			autoEvictingIncomingEventsBuffer.scheduleEvictionAtFixedRate(bufferedEventProcessingConfig.getProcessingWindowSizeInSeconds());
		}
	}
	@Override
	public void handle(UUID serviceUuid, RedfishEventArray events) {
      SouthboundConfiguration southboundEventingConfig = configProvider.get(EventsConfig.class).getSouthboundConfiguration();
      if (southboundEventingConfig.isBufferedEventProcessingEnabled()) {
          autoEvictingIncomingEventsBuffer.handle(serviceUuid, events);
      } else {
          incomingEventsProcessor.handle(serviceUuid, events);
      }
	}
}

