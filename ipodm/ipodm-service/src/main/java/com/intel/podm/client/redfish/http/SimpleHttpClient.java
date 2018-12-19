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

package com.intel.podm.client.redfish.http;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;

import java.net.URI;

import com.intel.podm.common.types.net.HttpMethod;

public class SimpleHttpClient implements AutoCloseable {
    private final BaseHttpClient client;

    public SimpleHttpClient(BaseHttpClient client) {
        this.client = requiresNonNull(client, "client");
    }

    /**
     * @throws javax.ws.rs.ProcessingException on javax.ws.rs.client.Invocation.invoke()
     */
    public HttpResponse call(HttpMethod method, URI uri, Object requestEntity, Class responseEntityClass) {
        requiresNonNull(method, "method");
        requiresNonNull(uri, "uri");
        requiresNonNull(responseEntityClass, "responseEntityClass");
        return client.call(method, uri, requestEntity, responseEntityClass);
    }

    @Override
    public void close() {
        try {
			this.client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
