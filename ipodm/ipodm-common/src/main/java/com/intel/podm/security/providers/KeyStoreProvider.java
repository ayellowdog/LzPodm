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

package com.intel.podm.security.providers;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.SecurityConfig;
import com.intel.podm.config.base.dto.SecurityConfig.CertificateType;

//@ApplicationScoped
@Component
//@DependsOn("dynamicHolder")
class KeyStoreProvider {
	private static final Logger logger = LoggerFactory.getLogger(KeyStoreProvider.class);

//    @Inject
//    @Autowired
//    @Config
//    private DynamicHolder<SecurityConfig> configHolder;
	@Autowired
	ConfigProvider configHolder;

    KeyStore loadCertificate(String password, CertificateType type) {
        try (FileInputStream keystoreStream = new FileInputStream(configHolder.get(SecurityConfig.class).getKeystorePath(type))) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keystoreStream, password.toCharArray());
            return keyStore;
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            logger.error("Keystore has not been initialized due to error {}", e.getMessage());
            throw new RuntimeException("Keystore load error, SSL connection can not be obtained", e);
        }
    }
}
