package com.inspur.podm.itaskbase.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskUtil {
	 /**
     * 日志对象.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskUtil.class);
    /**
     * 将对象序列化并转化为二进制数组.
     * @param obj
     * @return  byte[]
     */
	public static byte[] serializableObject(Object obj){
	   try {
		   ByteArrayOutputStream bout = new ByteArrayOutputStream();
		   ObjectOutputStream oos;
	  	   oos = new ObjectOutputStream(bout);
	  	   oos.writeObject(obj);
		   oos.close();
		   return bout.toByteArray();
	   } catch (IOException e) {
		   LOG.error("对象序列化错误:" + e);
	   }
	   return null;
    }
	
	/**
	 * 对象反序列化.
	 * @param byteArray 二进制数组
	 * @return object
	 */
	public static Object deserialize(byte[] byteArray){
		try {
			if (null != byteArray) {
				ByteArrayInputStream bin = new ByteArrayInputStream(byteArray);
				ObjectInputStream ois = new ObjectInputStream(bin);
				Object obj = ois.readObject();
				ois.close();
				return obj;
			}
		} catch (Exception e) {
			  LOG.error("对象反序列化错误:" + e);
		}
		return null;
	}
	
	/**
	 * 将对象序列化为string类型.
	 * @param obj
	 * @return string
	 */
	public static String serializeToString(Object obj) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(obj);
			String str = byteOut.toString("ISO-8859-1");
			return str;
		} catch (Exception e) {
			 LOG.error("对象序列化为字符串错误:" + e);
			 return null;
		}
	}
	
	/**
	 * 反序列化.
	 * @param str 序列化后的字符串
	 * @return object
	 */
	public static Object deSerializeToObject(String str) {
		try {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			Object obj = objIn.readObject();
			return obj;
		} catch (Exception e) {
			 LOG.error("字符串反序列化为对象错误:" + e);
			 return null;
		}
	}
	
	
	
	/**
	 * 获取UUID.
	 * @return uuID
	 */
	 public static String getUUID() {
	        while (true) {
	            long uuid = UUID.randomUUID().getMostSignificantBits();
	            if (uuid > 0) {
	                return uuid + "";
	            }
	        }
	 }
}
