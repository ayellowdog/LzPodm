/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.service.detection.dhcp;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.intel.podm.discovery.external.ExternalServiceMonitoringEvent;

/**
 * @ClassName: TestEventListener
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月13日 上午10:31:44
 */
@Component
public class TestEventListener {
	 @Transactional(propagation = Propagation.SUPPORTS)
	    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
//	    public void onExternalServiceMonitoringEvent(@Observes(during = AFTER_COMPLETION) ExternalServiceMonitoringEvent event) {
	    public void onExternalServiceMonitoringEvent(ExternalServiceMonitoringEvent event) {
		 System.out.println("--------------监听到事件！！---------------------");
		 System.out.println(event.getMonitoringState());
		 System.out.println(event.getExternalServiceUuid());
	 }
}

