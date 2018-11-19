package com.inspur.podm.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.inspur.podm.common.context.AppContext;

/**
 * 资源文件key到value翻译.
 *
 * @author Wanxian.He
 */
public class PropertiesUtil {
    /**
     * .
     */
	public static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	
	private static final String PROPERTY_FILE = "application.properties";

	/**
	 * 获取${PROPERTY_FILE}里面的key对应的内容.
	 *
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		return getValue(PROPERTY_FILE, key, null);
	}
	
	/**
	 * 获取资源文件里面的key对应的内容.
	 *
	 * @param propertiesFilePath
	 * @param key
	 * @return
	 */
	public static String getValue(String propertiesFilePath, String key) {
		return getValue(propertiesFilePath, key, null);
	}
	
	public static Map<String, String> getValues() {
		return getValues(PROPERTY_FILE, null);
	}
	
	/**
	 * @param propertiesFilePath
	 * @return
	 */
	public static Map<String, String> getValues(String propertiesFilePath) {
		return getValues(propertiesFilePath, null);
	}

	/**
	 * 获取资源文件里面的key对应的内容
	 *
	 * @param propertiesFilePath
	 * @param key
	 * @return
	 */
	public static String getValue(String propertiesFilePath, String key, String encode) {
		String value = null;
		try {
			Properties pros = getProperties(propertiesFilePath, encode);
			value = pros.getProperty(key);
		} catch (IOException e) {
			logger.error("PropertyUtil.getValue(String propertiesFilePath:" + propertiesFilePath + ",String key:" + key
					+ ") exception ,return null", e);
		}
		return value;
	}

	/**
	 * 获取资源文件里面的key对应的内容
	 *
	 * @param propertiesFilePath
	 * @param propertiesFilePath
	 * @return
	 */
	public static Map<String, String> getValues(String propertiesFilePath, String encode) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Properties pros = getProperties(propertiesFilePath, encode);
			Enumeration<?> enumeration = pros.propertyNames();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				String value = pros.getProperty(key);
				map.put(key, value);
			}
		} catch (IOException e) {
			logger.error("PropertyUtil.getValue(String propertiesFilePath:" + propertiesFilePath
					+ ") exception ,return null", e);
		}
		return map;
	}

    public static Properties getProperties(String propertiesFilePath)
            throws UnsupportedEncodingException, IOException {
        return getProperties(propertiesFilePath, "UTF-8");
    }

    public static Properties getProperties(String propertiesFilePath, String encode)
			throws UnsupportedEncodingException, IOException {
		Properties pros = new Properties();
		InputStream in = PropertiesUtil.class.getResourceAsStream("/" + propertiesFilePath);
		if (StringUtils.isNotEmpty(encode)) {
			pros.load(new InputStreamReader(in, encode));
		} else {
			pros.load(in);
		}
		in.close();
		return pros;
	}
    public static OrderProperties getOrderProperties(String propertiesFilePath)
            throws IOException {
        return getOrderProperties(propertiesFilePath, "UTF-8");
    }

    public static OrderProperties getOrderProperties(String propertiesFilePath, String encode)
			throws IOException {
		OrderProperties pros = new OrderProperties();
		InputStream in = PropertiesUtil.class.getResourceAsStream("/" + propertiesFilePath);
		if (StringUtils.isNotEmpty(encode)) {
			pros.load(new InputStreamReader(in, encode));
		} else {
			pros.load(in);
		}
		in.close();
		return pros;
	}

	/**
	 * 获取资源文件里面的key对应的内容
	 *
	 * @param propertiesFilePath
	 * @param encode
	 * @return
	 */
	public static Map<String, String> getOrderValues(String propertiesFilePath, String encode) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			OrderProperties pros = getOrderProperties(propertiesFilePath, encode);
			Set<String> set = pros.stringPropertyNames();
			for (String key : set) {
				String value = pros.getProperty(key);
				map.put(key, value);
			}
		} catch (IOException e) {
			logger.error("PropertyUtil.getValue(String propertiesFilePath:" + propertiesFilePath
					+ ") exception ,return null", e);
		}
		return map;
	}

	/**
	 * 获取资源文件里面的key对应的内容
	 *
	 * @param propertiesFilePath
	 * @return
	 */
	public static Map<String, String> getOrderValues(String propertiesFilePath) {
		return getOrderValues(propertiesFilePath, null);
	}
	/**
	 * 获取文件里面的key对应的内容
	 *
	 * @param language
	 * @param language
	 * @param fileName
	 * @return key
	 */
	public static String InterBundle(String language, String fileName, String key) {
		Locale locale = LocaleUtils.toLocale(language);
		ResourceBundle bundle = ResourceBundle.getBundle(fileName, locale);
		String perfName = null;
		if (bundle.containsKey(key)) {
			perfName = bundle.getString(key);
		} else {
			perfName = key;
		}
		return perfName;

	}
	
	public static String getDecryptValue(String key) {
		return getDecryptValue(PROPERTY_FILE, key, "UTF-8");
	}
	
	public static String getDecryptValue(String propertiesFilePath, String key) {
		return getDecryptValue(propertiesFilePath, key, "UTF-8");
	}
	
	public static String getDecryptValue(String propertiesFilePath, String key, String encode) {
		String value = getValue(propertiesFilePath, key, encode);
		StringEncryptor se = AppContext.getBean(StringEncryptor.class);
		return se.decrypt(value.substring(4, value.length() - 1));
	}
}
