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

package com.inspur.podm.rest.redfish.serializers;

import static com.inspur.podm.business.services.redfish.odataid.ODataContextProvider.getContextFromODataType;
import static com.inspur.podm.business.services.redfish.odataid.ODataIdHelper.oDataIdFromUri;
import static com.inspur.podm.rest.redfish.serializers.CollectionTypeToCollectionODataMapping.getOdataForCollectionType;
import static java.util.stream.Collectors.toList;

import javax.enterprise.context.Dependent;

import com.inspur.podm.business.dto.redfish.CollectionDto;
import com.inspur.podm.business.services.redfish.odataid.ODataIdFromContextHelper;
import com.inspur.podm.rest.redfish.json.templates.CollectionJson;
import com.inspur.podm.rest.representation.json.serializers.DtoJsonSerializer;

@Dependent
public class CollectionDtoJsonSerializer extends DtoJsonSerializer<CollectionDto> {
    public CollectionDtoJsonSerializer() {
        super(CollectionDto.class);
    }

    @Override
    protected CollectionJson translate(CollectionDto dto) {
        ODataForCollection oData = getOdataForCollectionType(dto.getType());

        CollectionJson result = new CollectionJson(oData.getODataType());
        result.name = oData.getName();
        result.description = oData.getName();
        result.oDataId = oDataIdFromUri(context.getRequestPath());
        result.oDataContext = getContextFromODataType(oData.getODataType());

        result.members.addAll(dto.getMembers().stream().map(ODataIdFromContextHelper::asOdataId).collect(toList()));

        return result;
    }
}
