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

package com.inspur.podm.common.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.inspur.podm.common.persistence.BaseEntity;

//@javax.persistence.Entity
//@Table(name = "metric_definition_wildcard")
public class MetricDefinitionWildcard extends BaseEntity {

/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 4387494604817166762L;

//    @Column(name = "name")
    private String name;

//    @ElementCollection
//    @CollectionTable(name = "metric_definition_wildcard_value", joinColumns = @JoinColumn(name = "metric_definition_wildcard_id"))
//    @Column(name = "metric_definition_wildcard_value")
//    @OrderColumn(name = "metric_definition_wildcard_value_order")
    private List<String> values = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void addValues(String value) {
        this.values.add(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MetricDefinitionWildcard)) {
            return false;
        }

        MetricDefinitionWildcard wildcards = (MetricDefinitionWildcard) o;

        return new EqualsBuilder()
            .append(getName(), wildcards.getName())
            .append(getValues(), wildcards.getValues())
            .isEquals();
    }

    @Override
    public void preRemove() {

    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, values);
    }
}
