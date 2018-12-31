/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.common.utils;

import java.util.List;

import com.intel.podm.common.types.EnumeratedType;

/**
 * @ClassName: Test
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月18日 上午11:11:04
 */
public class Test {
	public static void main(String[] args) {
		List<Class<EnumeratedType>> list = ClassUtil.myGetAllClassByParent(EnumeratedType.class);
		System.out.println(list.size());
	}
}

