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

package com.intel.podm.client;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.UPPER_CAMEL_CASE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder.HostnameVerificationPolicy.ANY;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intel.podm.client.redfish.RedfishClient;
import com.intel.podm.client.redfish.SocketErrorAwareHttpClient;
import com.intel.podm.client.redfish.http.BaseHttpClient;
import com.intel.podm.client.redfish.http.SimpleHttpClient;
import com.intel.podm.common.types.ConnectionParameters;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.ServiceConnectionConfig;
import com.intel.podm.security.providers.SslContextProvider;

//@Dependent
//@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
@Component
public class WebClientBuilder {
	@Autowired
//	Holder<ServiceConnectionConfig> configHolder;
	ConfigProvider configHolder;
	@Autowired
    private SslContextProvider sslContextProvider;
    private ConnectionParameters connectionParameters;
    private ObjectMapper objMapper = initMapper();
//    private final ResteasyJackson2Provider jackson2Provider = initializeProvider();

    @Autowired
    private PoolingHttpClientConnectionManager httpClientConnectionManager;
    @Autowired
    private  RequestConfig.Builder cfgBuilder;
    @PostConstruct
    public void init() {
    	 this.connectionParameters = configHolder.get(ServiceConnectionConfig.class).getConnectionConfiguration().getConnectionParameters();
    }

    public Builder newInstance(URI baseUri) {
        return new Builder(baseUri);
    }

    /**
     * Provides ResteasyClient with configured Jackson
     *
     * @return configured client with jackson provider
     */
//    private Client getClientWithJacksonProvider() {
//        ResteasyClientBuilder clientBuilder = ((ResteasyClientBuilder) ResteasyClientBuilder.newBuilder())
//            .connectionPoolSize(connectionParameters.getConnectionPoolSize())
//            .maxPooledPerRoute(connectionParameters.getMaxPooledPerRoute())
//            .register(jackson2Provider);
////            .sslContext(sslContextProvider.getContext())
////            .hostnameVerification(ANY);
//
//        clientBuilder
//            .establishConnectionTimeout(connectionParameters.getServiceConnectionTimeout(), SECONDS)
//            .socketTimeout(connectionParameters.getServiceSocketTimeout(), SECONDS);
//
//        return clientBuilder.build();
//    }
	  private BaseHttpClient getBaseClient() {
		  httpClientConnectionManager.setMaxTotal(connectionParameters.getConnectionPoolSize());
		  httpClientConnectionManager.setDefaultMaxPerRoute(connectionParameters.getMaxPooledPerRoute());
	      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	      httpClientBuilder.setConnectionManager(httpClientConnectionManager)
	      .setConnectionManagerShared(true);
//	      httpClientBuilder.setSSLContext(sslContextProvider.getContext());
//	      httpClientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
	      CloseableHttpClient client = httpClientBuilder.build();
	      RequestConfig requestConfig = cfgBuilder.build();
	      return new BaseHttpClient(client, requestConfig, objMapper);
	}

//    private ResteasyJackson2Provider initializeProvider() {
//        ResteasyJackson2Provider jackson2Provider = new CustomResteasyJackson2Provider();
//        ObjectMapper mapper = new ObjectMapper()
//            .setPropertyNamingStrategy(UPPER_CAMEL_CASE)
//            .registerModule(new JavaTimeModule())
//            .registerModule(new SerializersProvider().getSerializersModule())
//            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
//            .enable(FAIL_ON_NULL_FOR_PRIMITIVES);
//        jackson2Provider.setMapper(mapper);
//
//        return jackson2Provider;
//    }
	  private ObjectMapper initMapper() {
        ObjectMapper mapper = new ObjectMapper()
	        .setPropertyNamingStrategy(UPPER_CAMEL_CASE)
	        .registerModule(new JavaTimeModule())
	        .registerModule(new SerializersProvider().getSerializersModule())
	        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
	        .enable(FAIL_ON_NULL_FOR_PRIMITIVES);
        return mapper;
	  }
    public class Builder {
        private URI baseUri;
        private boolean retryable;
        private boolean cachable;

        Builder(URI baseUri) {
            this.baseUri = baseUri;
        }

        public Builder retryable() {
            retryable = true;
            return this;
        }

        public Builder cachable() {
            cachable = true;
            return this;
        }

        public WebClient build() {
        	BaseHttpClient client = getBaseClient();
            SimpleHttpClient httpClient = new SimpleHttpClient(client);
            SocketErrorAwareHttpClient socketErrorAwareHttpClient = new SocketErrorAwareHttpClient(httpClient);
            RedfishClient redfishClient = new RedfishClient(baseUri, socketErrorAwareHttpClient);
            WebClient webClient = new WebClientBasedOnRedfishClient(redfishClient);

            if (retryable) {
                webClient = new WebClientWithRetrying(webClient);
            }
            if (cachable) {
                webClient = new CachedWebClient(webClient);
            }
            return webClient;
        }
    }

//    public class CustomResteasyJackson2Provider extends ResteasyJackson2Provider {
//        /*
//         * Workaround for:
//         *
//         * RESTEASY002155: Provider class org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider is already registered.
//         *     2nd registration is being ignored.
//         */
//    }
}
