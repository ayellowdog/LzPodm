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

package com.intel.podm.config.network;

import static com.intel.podm.common.types.EnumeratedType.SUB_TYPES;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.intel.podm.common.types.deserialization.EnumeratedTypeDeserializer;
import com.intel.podm.common.types.deserialization.MacAddressDeserializer;
import com.intel.podm.common.types.net.MacAddress;

/**
 * Reads preconfigured content for the given type from config json file
 */
//@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
//@Dependent
@Component
//！！！！！！！！！！！！！！！！！！已经改写，目前全都是读取的一样的假配置
public class NetworkConfigurationReader {

//    public static final String DEFAULT_PATH_TO_CONFIGURATION_FILES = "/tmp/pod-manager/config";
    public static final String DEFAULT_PATH_TO_CONFIGURATION_FILES = "C:\\pod-manager\\config\\";

    private static final Logger logger = LoggerFactory.getLogger(NetworkConfigurationReader.class);

    public <T> T readConfiguration(String configurationName, Class<T> type) throws NetworkConfigurationIOException {
//        String path = DEFAULT_PATH_TO_CONFIGURATION_FILES + "/" + configurationName + ".json";
    	String path = DEFAULT_PATH_TO_CONFIGURATION_FILES + "interface-ens33.json";
        try (InputStream is = new FileInputStream(path)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(getDeserializersModule());

            return mapper.readValue(is, type);
        } catch (IOException e) {
            throw new NetworkConfigurationIOException("Could not read configuration : " + path + ". ", e);
        }
    }

    public <T> T readConfigurationOrDefault(String configurationName, Class<T> type, Supplier<T> defaultConfigSupplier) {
        try {
            return readConfiguration(configurationName, type);
        } catch (NetworkConfigurationIOException e) {
            logger.error("Pod manager network service configuration is unavailable, using defaults");
            return defaultConfigSupplier.get();
        }
    }

    private Module getDeserializersModule() {
        SimpleModule deserializersModule = new SimpleModule();
        for (Class subType : SUB_TYPES) {
            deserializersModule.addDeserializer(subType, new EnumeratedTypeDeserializer<>(subType));
        }

        deserializersModule.addDeserializer(MacAddress.class, new MacAddressDeserializer());
        return deserializersModule;
    }
}
