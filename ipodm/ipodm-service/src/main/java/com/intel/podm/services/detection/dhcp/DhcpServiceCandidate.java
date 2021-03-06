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

package com.intel.podm.services.detection.dhcp;

import com.intel.podm.common.types.ServiceType;

import java.net.URI;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static java.util.Objects.hash;
//此类原来没有public修饰
public class DhcpServiceCandidate {
    private final ServiceType serviceType;
    private final URI endpointUri;
    private LocalDateTime updateDate;
    //重试次数初始为0
    private long retries;

    public DhcpServiceCandidate(ServiceType serviceType, URI endpointUri, LocalDateTime updateDate) {
        if (endpointUri == null) {
            throw new IllegalArgumentException("URI cannot be null in DhcpServiceCandidate");
        }
        this.serviceType = serviceType;
        this.endpointUri = endpointUri;
        this.updateDate = updateDate;
        this.retries = 0;
    }

    public URI getEndpointUri() {
        return endpointUri;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public long getRetries() {
        return retries;
    }

    public void increaseRetries() {
        this.retries++;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DhcpServiceCandidate candidate = (DhcpServiceCandidate) o;

        if (serviceType != candidate.serviceType) {
            return false;
        }
        return endpointUri.equals(candidate.endpointUri);
    }

    @Override
    public int hashCode() {
        return hash(serviceType, endpointUri);
    }

    @Override
    public String toString() {
        return format("{URI=%s, type=%s, updateDate=%s}", getEndpointUri(), getServiceType(), updateDate);
    }
}
