/*
 * Copyright (c) 2017-2018 Intel Corporation
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

import javax.persistence.Column;
import javax.persistence.Table;

import com.inspur.podm.common.persistence.base.Entity;
@javax.persistence.Entity
@Table(name = "volume_metadata")
public class VolumeMetadata extends Entity {
    @Column(name = "allocated")
    private boolean allocated;

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    @Override
    public void preRemove() {
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return false;
    }
}
