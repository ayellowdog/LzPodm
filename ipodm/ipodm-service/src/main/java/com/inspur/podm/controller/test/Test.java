/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.controller.test;

import java.util.List;

import com.inspur.podm.common.utils.ClassUtil;
import com.intel.podm.client.resources.redfish.OemVendor;
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
		List<Class<?>> list = ClassUtil.getAllClassByParent(OemVendor.class);
		System.out.println(list.size());
	}
}

