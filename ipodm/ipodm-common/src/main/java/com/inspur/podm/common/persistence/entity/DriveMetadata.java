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

import com.inspur.podm.common.persistence.BaseEntity;

//import javax.persistence.Column;
//import javax.persistence.Table;
//
//@javax.persistence.Entity
//@Table(name = "drive_metadata")
public class DriveMetadata extends BaseEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 6333348711437980980L;
	//    @Column(name = "allocated")
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
    public boolean containedBy(BaseEntity possibleParent) {
        return false;
    }
}
