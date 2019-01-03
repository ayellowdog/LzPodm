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

import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.SecurityConfig;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static javax.net.ssl.SSLContext.getInstance;

//@ApplicationScoped
@Component
public class SslContextProvider {
    private static final String PROTOCOL = "TLSv1.2";

    private static final Logger logger = LoggerFactory.getLogger(SslContextProvider.class);

    @Autowired
    private SslConnectionManagersProvider keyManagersProvider;

    @Autowired
//    @Config
//    private Holder<SecurityConfig> configHolder;
	ConfigProvider configHolder;

    private SSLContext context;

    public SSLContext getContext() {
        return context;
    }

    @PostConstruct
    private void init() {
//        try {
//            if (context == null) {
//                KeyManager[] keyManagers = keyManagersProvider.getKeyManagersArray();
//                TrustManager[] trustManagers = configHolder.get(SecurityConfig.class).isServerCertificateVerificationEnabled()
//                    ? keyManagersProvider.getTrustManagersArray() : keyManagersProvider.getTrustManagersWhichTrustAllIssuers();
//                context = getInstance(PROTOCOL);
//                context.init(keyManagers, trustManagers, null);
//            }
//        } catch (NoSuchAlgorithmException | KeyManagementException e) {
//            logger.error("Keystore has not been initialized {}", e.getMessage());
//            throw new RuntimeException("Keystore has not been initialized", e);
//        }
    }
}
