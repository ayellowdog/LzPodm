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

package com.intel.podm.config.base;

import static com.intel.podm.common.utils.Contracts.requires;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static com.intel.podm.config.base.ConfigPaths.READONLY_CONFIG_DIR;
import static com.intel.podm.config.base.ConfigPaths.WRITABLE_CONFIG_DIR;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ejb.LockType.WRITE;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.AccessTimeout;
import javax.ejb.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import com.intel.podm.config.base.dto.BaseConfig;

@Component("podmConfigProvider")
public class ConfigProvider {
	private static final Logger logger = LoggerFactory.getLogger(ConfigProvider.class);


    @Autowired
    private ConfigMap configMap;

    @Autowired
    private ConfigAccessor configAccessor;

    private static <T extends BaseConfig> boolean configCanBeGeneratedByApplication(Class<T> configClass) {
        return getConfigFileAnnotation(configClass).isGeneratedByApplication();
    }

    private static String getConfigFilePath(Class configClass) {
        ConfigFile annotation = getConfigFileAnnotation(configClass);

        String baseDirectory = annotation.isGeneratedByApplication() ? WRITABLE_CONFIG_DIR : READONLY_CONFIG_DIR;
        return baseDirectory + annotation.filename();
    }

    private static <T extends BaseConfig> ConfigFile getConfigFileAnnotation(Class<T> configClass) {
        ConfigFile annotation = configClass.getDeclaredAnnotation(ConfigFile.class);
        requires(annotation != null, format("%s should be annotated with ConfigFile", configClass));
        return annotation;
    }

    /**
     * LockType.WRITE used due to possible simultaneous read / write operations on configuration files.
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public <T extends BaseConfig> T get(Class<T> configClass) {
        requiresNonNull(configClass, "configClass");

        String filePath = getConfigFilePath(configClass);
        try {
            readFileAndUpdateMap(filePath, configClass);
        } catch (FileNotFoundException e) {
            T config = configMap.get(filePath, configClass);
            if (configCanBeGeneratedByApplication(configClass)) {
                write(filePath, config);
            } else {
                logger.warn("Problem while reading config file '{}'. PODM will use defaults for this file.", filePath);
            }
        } catch (IOException e) {
            logger.error("Problem while reading config file '{}'. Please check if file has correct permissions and valid content.", filePath);
        }

        return configMap.get(filePath, configClass);
    }

    private <T extends BaseConfig> void readFileAndUpdateMap(String fileName, Class<T> configClass) throws IOException {
        T config = configAccessor.read(fileName, configClass);
        if (!configMap.update(fileName, config)) {
            logger.error("Config file '{}' has not been updated correctly.", fileName);
        }
    }

    private void write(String fileName, Object config) {
        try {
            configAccessor.write(fileName, config);
        } catch (IOException e) {
            logger.error("Problem while writing default config to '{}'", fileName);
        }
    }
}
