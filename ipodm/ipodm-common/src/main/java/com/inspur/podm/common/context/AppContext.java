package com.inspur.podm.common.context;


import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;

import com.inspur.podm.common.utils.ThreadUtil;

/**
 * AppContext.
 *
 * @author hewanxian
 * Copyright © 2016 hewanxian. All rights reserved.
 */
public class AppContext {
    public static final String HTTP_SESSION = "HTTP_SESSION";

    public static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";
    public static final String HTTP_SERVLET_RESPONSE = "HTTP_SERVLET_RESPONSE";
    

    private static ApplicationContext context;

    public static void init(ApplicationContext context) {
        AppContext.context = context;
    }

    public static ApplicationContext context() {
        return context;
    }
    
    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return context().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return context().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return context().getBean(name, clazz);
    }

    /**
     * 获取指定接口的所有Class.
     * @param clazz 接口名称
     * @param <T> 获取Class的接口
     * @return Map
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        Map<String, T> clazzMap = context().getBeansOfType(clazz);
        return clazzMap;
    }

    /**
     * 放入数据到线程变量，在当前线程中传递数据，线程结束时(request返回时)线程变量中数据会被清空
     *
     * @param key 标示
     * @param variable 对应的value值
     */
    public static void putThreadVariable(String key, Object variable) {
        ThreadUtil.putThreadVariable(key, variable);
    }

    /**
     * 从线程中获取相应对象
     *
     * @param key 对象对应的KEY值
     * @return variable 对象
     */
    public static Object getThreadVariable(String key) {
        return ThreadUtil.getThreadVariable(key);
    }

    /**
     * 从线程变量中删除对象
     *
     * @param key 对象对应的KEY
     * @return 对象
     */
    public static Object removeThreadVariable(String key) {
        return ThreadUtil.removeThreadVariable(key);
    }

    /**
     * 清空线程变量中的数据。
     */
    public static void clearThreadVariable() {
        ThreadUtil.clearThreadVariable();
    }

    /**
     * 把Object存入session对象中。
     *
     * @param key 变量对应的KEY值
     * @param variable 变量值
     */
    public static void putSessionVariable(String key, Object variable) {
        HttpSession session = getHttpSession();
        session.setAttribute(key, variable);
    }

    /**
     * 从SESSION中获取变量
     *
     * @param key 对应获取变量的KEY值
     * @return 变量值
     */
    public static Object getSessionVariable(String key) {
        HttpSession session = getHttpSession();
        if (session == null) {
            return null;
        }
        Object object= session.getAttribute(key);
        return object;
    }

    /**
     *
     * 获取HTTPSESSION
     *
     * @return httpSession session
     */
    public static HttpSession getHttpSession() {
        return (HttpSession) getThreadVariable(HTTP_SESSION);
    }

    /**
     * Remove variable from session
     *
     * @param key object KEY
     * @return remove Object
     */
    public static Object removeSessionVariable(String key) {
        Object obj = getSessionVariable(key);
        getHttpSession().removeAttribute(key);
        return obj;
    }
}
