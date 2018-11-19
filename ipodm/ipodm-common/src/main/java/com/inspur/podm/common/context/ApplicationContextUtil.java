package com.inspur.podm.common.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring加载完成后执行，目的是能够在代码中通过AppContext这个类获取并使用Spring的ApplicationContext
 * Created by Wanxian.He on 17/3/23.
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.init(applicationContext);
    }
}
