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

package com.inspur.podm.config.base;

import com.inspur.podm.config.base.dto.BaseConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@ApplicationScoped
public class ConfigHolderFactory {
    @Inject
    private ConfigProvider configProvider;

    @Produces
    @Config(refreshable = true)
    public <T extends BaseConfig> Holder<T> createDynamicHolder(InjectionPoint injectionPoint) {
        Class<T> configClass = getConfigClass(injectionPoint);
        return new DynamicHolder<>(configProvider, configClass);
    }

    @Produces
    @Config(refreshable = false)
    public <T extends BaseConfig> Holder<T> createStaticHolder(InjectionPoint injectionPoint) {
        Class<T> configClass = getConfigClass(injectionPoint);
        return new StaticHolder<>(configProvider.get(configClass));
    }

    Class getConfigClass(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getType();
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new UnsupportedOperationException("Given injection point is not supported");
    }
}

