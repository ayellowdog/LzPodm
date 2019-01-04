/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @ClassName: TestNetowrk
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2019年1月2日 上午10:55:53
 */
public class TestNetowrk {
public static void main(String[] args) throws SocketException {
//	Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
//	while(networkInterfaces.hasMoreElements()) {
//		NetworkInterface nextElement = networkInterfaces.nextElement();
//		Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
//		while(inetAddresses.hasMoreElements()) {
//			System.out.println(inetAddresses.nextElement().getHostAddress());
//		}
//		System.out.println(nextElement.getName());
//	}
	NetworkInterface networkInterface = NetworkInterface.getByName("eth2");
	Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
	while(inetAddresses.hasMoreElements()) {
	System.out.println(inetAddresses.nextElement().getHostAddress());
}
	
}
}

