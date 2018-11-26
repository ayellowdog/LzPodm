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

package com.inspur.podm.service.service.redfish.helpers;

import static com.inspur.podm.service.service.redfish.Contexts.toContext;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inspur.podm.api.business.dto.redfish.attributes.UnknownOemDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.common.intel.logger.Logger;
import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.entity.DiscoverableEntity;
import com.inspur.podm.common.persistence.entity.ExternalService;
import com.inspur.podm.common.persistence.entity.embeddables.UnknownOem;
import com.inspur.podm.common.utils.IdFromUriGenerator;
import com.inspur.podm.service.dao.DiscoverableEntityDao;

@Dependent
@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
public class UnknownOemTranslator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ODATA_ID = "@odata.id";

    @Inject
    private Logger logger;

    @Inject
    private DiscoverableEntityDao discoverableEntityDao;

    public List<UnknownOemDto> translateUnknownOemToDtos(ExternalService externalService, Collection<UnknownOem> unknownOems) {
        return unknownOems.stream()
            .map(unknownOem -> buildUnknownOemDto(externalService, unknownOem))
            .collect(toList());
    }

    public UnknownOemDto translateStringOemToDto(ExternalService externalService, String oem) {
        ObjectNode objectNode = convertStringToObjectNode(oem);
        Map<String, Context> foundContexts = getExistingContexts(externalService, objectNode);

        UnknownOemDto dto = new UnknownOemDto();
        dto.setOemValue(objectNode);
        dto.setFoundContexts(foundContexts);
        return dto;
    }

    private UnknownOemDto buildUnknownOemDto(ExternalService externalService, UnknownOem unknownOem) {
        ObjectNode objectNode = convertStringToObjectNode(unknownOem.getOemValue());
        Map<String, Context> foundContexts = getExistingContexts(externalService, objectNode);

        UnknownOemDto dto = new UnknownOemDto();
        dto.setOemPath(unknownOem.getOemPath());
        dto.setOemValue(objectNode);
        dto.setFoundContexts(foundContexts);
        return dto;
    }

    private ObjectNode convertStringToObjectNode(String value) {
        if (value != null) {
            try {
                return (ObjectNode) OBJECT_MAPPER.readTree(value);
            } catch (IOException | ClassCastException e) {
                logger.e("Couldn't convert: [" + value + "] to ObjectNode", e);
            }
        }

        return OBJECT_MAPPER.createObjectNode();
    }

    private Map<String, Context> getExistingContexts(ExternalService externalService, JsonNode jsonNode) {
        Map<String, Context> foundContexts = new HashMap<>();
        JsonNode oDataIdJsonNode = jsonNode.get(ODATA_ID);
        if (oDataIdJsonNode != null && oDataIdJsonNode.isTextual()) {
            String sourceUriString = oDataIdJsonNode.asText();
            Id globalId = IdFromUriGenerator.instance().getIdFromUri(URI.create(sourceUriString), externalService.getId().getValue());
            DiscoverableEntity discoverableEntity = discoverableEntityDao.findByGlobalId(globalId, DiscoverableEntity.class);

            if (discoverableEntity != null) {
                Context context = toContext(discoverableEntity);
                foundContexts.put(sourceUriString, context);
            }
        }

        for (JsonNode childNode : jsonNode) {
            foundContexts.putAll(getExistingContexts(externalService, childNode));
        }

        return foundContexts;
    }
}
