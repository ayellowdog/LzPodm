package com.inspur.podm.service.itaskbase.utils;

import org.springframework.context.ApplicationContext;

/**
 * 任务AppContext.
 * @author chenchunfeng
 * @date 2018年4月10日
 */
public class TaskAppContext {

    private static ApplicationContext context;

    public static void init(ApplicationContext context) {
        TaskAppContext.context = context;
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
}
