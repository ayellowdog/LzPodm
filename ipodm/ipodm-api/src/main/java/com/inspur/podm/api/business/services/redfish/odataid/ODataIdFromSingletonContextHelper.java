/*
 * Copyright (c) 2015-2018 inspur Corporation
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

package com.inspur.podm.api.business.services.redfish.odataid;

import com.inspur.podm.api.business.services.context.SingletonContext;
import com.intel.podm.common.types.redfish.OdataIdProvider;

import static java.lang.String.format;

/**
 * Helper class for SingletonContext to ODataId conversions.
 */
public final class ODataIdFromSingletonContextHelper {
    private ODataIdFromSingletonContextHelper() {
    }

    public static ODataId asOdataId(SingletonContext singletonContext) {
        if (singletonContext == null) {
            return null;
        }
        OdataIdProvider.ODataId ownerContextODataId = singletonContext.getOwnerODataId().asOdataId();
        return new ODataId(format("%s/%s", ownerContextODataId, singletonContext.getSingletonName()));
    }
}
