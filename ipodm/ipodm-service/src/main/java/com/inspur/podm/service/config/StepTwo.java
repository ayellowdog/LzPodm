/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @ClassName: StepTwo
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月3日 上午10:19:56
 */
@Component("step2")
@DependsOn("step1")
//@Lazy
//@Scope("prototype")
public class StepTwo {
	@Autowired
	StepBean stepBean;
	@PostConstruct
	public void init() {
		stepBean.hehe();
		System.out.println("Step 2 init finish");
	}
}

