/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.dao;

import java.util.UUID;

/**
 * @ClassName: Test
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月26日 上午9:27:42
 */
public class Test {
	public boolean hehe;

	public Test() {
	}
	public static void main(String[] args) {
		Test t = new Test();
		System.out.println(t.hehe);
		UUID a = UUID.randomUUID();
		System.out.println(a.toString());
	}
}

