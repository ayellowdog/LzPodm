/*
 * Copyright (c) 2015-2018 inspur Corporation
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

package com.inspur.podm.common.config.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.springframework.beans.factory.annotation.Autowired;

import com.inspur.podm.common.config.base.dto.BaseConfig;

//@ApplicationScoped
public class ConfigHolderFactory {
    @Autowired
    private static ConfigProvider configProvider;

//    public static <T extends BaseConfig> Holder<T> createStaticHolder2(Class<T> clz) {
//    	 System.out.println("mmmmmmmmmmmmmmmmmmmmm" + configProvider);
//    	 return new StaticHolder<>(configProvider.get(clz));
//    }
    @Produces
    @Config(refreshable = true)
    public <T extends BaseConfig> Holder<T> createDynamicHolder(InjectionPoint injectionPoint) {
        Class<T> configClass = getConfigClass(injectionPoint);
        return new DynamicHolder<>();
    }

    @Produces
    @Config(refreshable = false)
    public <T extends BaseConfig> Holder<T> createStaticHolder(InjectionPoint injectionPoint) {
        Class<T> configClass = getConfigClass(injectionPoint);
        return null;//new StaticHolder<>(configProvider.get(configClass));
    }

    Class getConfigClass(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getType();
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new UnsupportedOperationException("Given injection point is not supported");
    }
}

