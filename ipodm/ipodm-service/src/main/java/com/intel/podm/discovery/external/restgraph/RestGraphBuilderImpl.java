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
import com.intel.podm.client.reader.ExternalServiceReader;
import com.intel.podm.client.resources.ExternalServiceResource;
import com.intel.podm.client.resources.redfish.ServiceRootResource;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.synchronization.CancelableChecker;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static com.intel.podm.common.utils.Contracts.requires;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static javax.transaction.Transactional.TxType.MANDATORY;

//@Dependent
@Component
class RestGraphBuilderImpl implements RestGraphBuilder {
    @Autowired
    private ResourceLinkExtractor extractor;

    private CancelableChecker cancelableChecker;
    @Override
    public void setCancelableChecker(CancelableChecker cancelableChecker) {
        this.cancelableChecker = cancelableChecker;
    }

    @Override
//    @TimeMeasured(tag = "[Discovery]")
    @Transactional(MANDATORY)
    public RestGraph build(ExternalServiceReader client) throws WebClientRequestException {
        ServiceRootResource serviceRoot = client.getServiceRoot();
        requires(serviceRoot.getUuid() != null, "Service being discovered must have UUID assigned.");

        Queue<ExternalServiceResource> queue = new ArrayDeque<>();
        queue.add(serviceRoot);

        RestGraph graph = new RestGraph(serviceRoot);
        Set<URI> visited = new HashSet<>();
        Set<ResourceLink> allLinks = new HashSet<>();
        while (!queue.isEmpty()) {
            cancelableChecker.check();
            ExternalServiceResource current = queue.poll();
            visited.add(current.getUri());
            /**
             * 这个方法用到了@linkName注解，获取相关的resource的@linkname注解标注的关联resource:
             * 1.对于获取到的json resource，获取它的@linkName注解，取得相关的方法，然后用反射调用方法，返回相应的单个或iterable对象(ResourceSupplier)
             * 而这个supplier的get方法返回的就是ExternalServiceResource
             * 2.其次，存储method用到了一个谷歌的cache，能够动态添加内容，支持同步操作
             */
            Collection<ResourceLink> relatedLinks = extractor.extractFrom(current);
            Collection<? extends ExternalServiceResource> notVisited = filterNotVisited(relatedLinks, visited);

            allLinks.addAll(relatedLinks);
            queue.addAll(notVisited);
        }

        graph.addAll(allLinks);
        return graph;
    }

    private Collection<ExternalServiceResource> filterNotVisited(Iterable<ResourceLink> links, Set<URI> visited) {
        return stream(links.spliterator(), false)
            .map(ResourceLink::getTarget)
            .filter(resource -> !visited.contains(resource.getUri()))
            .collect(toSet());
    }
}
