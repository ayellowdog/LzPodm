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

package com.intel.podm.discovery.external.restgraph;

import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.reader.ResourceLinks;
import com.intel.podm.client.reader.ResourceSupplier;
import com.intel.podm.client.resources.ExternalServiceResource;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.intel.podm.client.WebClientExceptionUtils.isConnectionExceptionTheRootCause;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

//@Dependent
@Component
//这个类用到了@linkName注解，获取相关的json link
public class ResourceLinkExtractor {
	private static final Logger logger = LoggerFactory.getLogger(ResourceLinkExtractor.class);

    public Set<ResourceLink> extractFrom(ExternalServiceResource resource) throws WebClientRequestException {
        ResourceLinks links = resource.getLinks();
        if (links == null) {
            return emptySet();
        }
        Set<ResourceLink> result = new HashSet<>();
        for (String linkName : links.getLinkNames()) {
            Iterable<ExternalServiceResource> linkedResources = getResources(links, linkName);

            for (ExternalServiceResource linked : linkedResources) {
                result.add(new ResourceLink(resource, linked, linkName));
            }
        }
        return result;
    }

    private Set<ExternalServiceResource> getResources(ResourceLinks links, String linkName) throws WebClientRequestException {
        Set<ExternalServiceResource> result = new HashSet<>();

        for (ResourceSupplier supplier : getSuppliers(links, linkName)) {
            try {
                result.add(supplier.get());
            } catch (WebClientRequestException e) {
                rethrowIfCausedByConnectionException(e);
            }
        }

        return result;
    }

    private Iterable<ResourceSupplier> getSuppliers(ResourceLinks links, String linkName) throws WebClientRequestException {
        try {
            return links.get(linkName);
        } catch (WebClientRequestException e) {
            rethrowIfCausedByConnectionException(e);
            return emptyList();
        }
    }

    private void rethrowIfCausedByConnectionException(WebClientRequestException e) throws WebClientRequestException {
        if (isConnectionExceptionTheRootCause(e)) {
            throw e;
        }

        logger.error(format("Problem while reading: %s", e.getResourceUri()), e);
    }
}
