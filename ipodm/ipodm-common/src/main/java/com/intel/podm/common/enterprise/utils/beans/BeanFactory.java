/*
 * Copyright (c) 2015-2018 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.podm.common.enterprise.utils.beans;

import static com.intel.podm.common.utils.IterableHelper.single;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.inspur.podm.common.context.AppContext;

//@Dependent
@Component
@Lazy
public class BeanFactory implements Serializable {
    private static final long serialVersionUID = -6691592134302488258L;
    /**
     * Creates new instance of specified CDI bean class.
     */
    /**
     * CDI转Spring框架改写之后的方法，利用spring context动态创建bean,从源码看来，目的是为了完成bean属性的自动注入？.
     * <p> TODO 功能描述 </p>
     * 
     * @author: liuchangbj
     * @date: 2018年12月10日 上午10:48:56
     * @param beanClass
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> T create(Class<T> beanClass) {
    	ConfigurableApplicationContext context = (ConfigurableApplicationContext)  AppContext.context();
    	//Bean的实例工厂
        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) context.getBeanFactory();
        //Bean构建  BeanService.class 要创建的Bean的Class对象
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        //向里面的属性注入值，提供get set方法
//        dataSourceBuider.addPropertyValue("msg", "hello ");
        //dataSourceBuider.setParentName("");  同配置 parent
        //dataSourceBuider.setScope("");   同配置 scope
        //将实例注册spring容器中   bs 等同于  id配置
        AbstractBeanDefinition beanDefinition = dataSourceBuider.getBeanDefinition();
        String beanName = beanClass.getSimpleName() + UUID.randomUUID();
        dbf.registerBeanDefinition(beanName, beanDefinition);
        T existingBean = (T) context.getBean(beanName);
        //完成注入
        dbf.autowireBean(existingBean);
        return existingBean;
    }
//    @Inject
//    BeanManager beanManager;
//
//    /**
//     * Creates new instance of specified CDI bean class.
//     */
//    public <T> T create(Class<T> beanClass) {
//        Set<Bean<?>> beans = beanManager.getBeans(beanClass, new AnnotationLiteral<Any>() { });
//
//        if (beans.size() == 0) {
//            throw new IllegalArgumentException(beanClass + " is not a managed bean");
//        } else if (beans.size() > 1) {
//            throw new IllegalArgumentException(beanClass + " is satisfied by more than one bean");
//        }
//
//        @SuppressWarnings("unchecked")
//		Bean<T> bean = (Bean<T>) single(beans);
//
//        CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
//
//        return bean.create(creationalContext);
//    }
}
