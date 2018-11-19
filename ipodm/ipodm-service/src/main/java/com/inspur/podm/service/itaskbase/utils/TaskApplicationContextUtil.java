package com.inspur.podm.service.itaskbase.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 加载applicationContext.
 * @author chenchunfeng
 * @date 2018年4月11日
 */
@Component
public class TaskApplicationContextUtil implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TaskAppContext.init(applicationContext);
    }
}
