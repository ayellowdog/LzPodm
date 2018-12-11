/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.config;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * @ClassName: StepOne
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月3日 上午10:15:47
 */
@Component("step1")
public class StepOne {
	@PostConstruct
	public void init() {
		System.out.println("Step 1 init finish");
	}
}

