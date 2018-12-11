/*
 * Copyright (c) 2016-2018 Intel Corporation
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

package com.intel.podm.business.entities.listeners;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.Set;

//import static com.intel.podm.common.utils.IterableHelper.single;

//@ApplicationScoped
public class EntityListener {
//    @Inject
//    protected BeanManager beanManager;
//
//    /**
//     * Creates new instance of specified CDI bean class.
//     */
//    //Workaround for WFLY-2387
//    protected <T> T create(Class<T> beanClass) {
//        Set<Bean<?>> beans = beanManager.getBeans(beanClass, new AnnotationLiteral<Any>() {
//            private static final long serialVersionUID = 6274001017933547151L;
//        });
//
//        if (beans.size() == 0) {
//            throw new IllegalArgumentException(beanClass + " is not a managed bean");
//        } else if (beans.size() > 1) {
//            throw new IllegalArgumentException(beanClass + " is satisfied by more than one bean");
//        }
//
//        Bean<T> bean = (Bean<T>) single(beans);
//
//        CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
//
//        return bean.create(creationalContext);
//    }
}
