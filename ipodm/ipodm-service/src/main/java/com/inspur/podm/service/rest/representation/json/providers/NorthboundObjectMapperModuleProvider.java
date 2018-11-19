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

package com.inspur.podm.service.rest.representation.json.providers;

import static com.inspur.podm.common.intel.types.EnumeratedType.SUB_TYPES;

import java.time.OffsetDateTime;

import javax.enterprise.inject.Instance;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.SingletonContext;
import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.NeighborInfo;
import com.inspur.podm.common.intel.types.Ref;
import com.inspur.podm.common.intel.types.Status;
import com.inspur.podm.common.intel.types.deserialization.BooleanDeserializer;
import com.inspur.podm.common.intel.types.deserialization.MacAddressDeserializer;
import com.inspur.podm.common.intel.types.deserialization.RefDeserializer;
import com.inspur.podm.common.intel.types.deserialization.StrictEnumeratedTypeDeserializer;
import com.inspur.podm.common.intel.types.net.MacAddress;
import com.inspur.podm.common.intel.types.serialization.EnumeratedTypeSerializer;
import com.inspur.podm.common.intel.types.serialization.IdSerializer;
import com.inspur.podm.common.intel.types.serialization.MacAddressSerializer;
import com.inspur.podm.common.intel.types.serialization.NeighborInfoSerializer;
import com.inspur.podm.common.intel.types.serialization.OffsetDateTimeSerializer;
import com.inspur.podm.common.intel.types.serialization.StatusSerializer;
import com.inspur.podm.service.rest.representation.json.serializers.ContextAwareSerializer;
import com.inspur.podm.service.rest.representation.json.serializers.ContextSerializer;
import com.inspur.podm.service.rest.representation.json.serializers.DtoSerializerContext;
import com.inspur.podm.service.rest.representation.json.serializers.SingletonContextSerializer;

@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
class NorthboundObjectMapperModuleProvider {
    @SuppressWarnings({"unchecked"})
    SimpleModule getSerializerModule(DtoSerializerContext context, Instance<ContextAwareSerializer> serializers) {
        SimpleModule module = new SimpleModule();

        for (ContextAwareSerializer serializer : serializers) {
            serializer.setContext(context);
            module.addSerializer((JsonSerializer<?>) serializer);
        }

        for (Class subType : SUB_TYPES) {
            module.addSerializer(subType, new EnumeratedTypeSerializer<>());
        }

        module.addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
        module.addSerializer(Id.class, IdSerializer.INSTANCE);
        module.addSerializer(MacAddress.class, MacAddressSerializer.INSTANCE);
        module.addSerializer(NeighborInfo.class, NeighborInfoSerializer.INSTANCE);
        module.addSerializer(Status.class, StatusSerializer.INSTANCE);
        module.addSerializer(Context.class, new ContextSerializer());
        module.addSerializer(SingletonContext.class, new SingletonContextSerializer());

        return module;
    }

    @SuppressWarnings({"unchecked"})
    SimpleModule getDeserializerModule() {
        SimpleModule module = new SimpleModule();

        for (Class subType : SUB_TYPES) {
            module.addDeserializer(subType, new StrictEnumeratedTypeDeserializer<>(subType));
        }
        module.addDeserializer(MacAddress.class, new MacAddressDeserializer());
        module.addDeserializer(Ref.class, new RefDeserializer());
        module.addDeserializer(Boolean.class, BooleanDeserializer.INSTANCE);

        return module;
    }
}
