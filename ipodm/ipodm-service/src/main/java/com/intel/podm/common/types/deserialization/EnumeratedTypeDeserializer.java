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

package com.intel.podm.common.types.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.intel.podm.common.types.EnumeratedType;

import static com.intel.podm.common.types.EnumeratedType.stringToEnum;

import java.io.IOException;

public class EnumeratedTypeDeserializer<T extends EnumeratedType> extends JsonDeserializer<T> {
    private final Class<T> enumType;

    public EnumeratedTypeDeserializer(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String text = parser.getValueAsString();
        // Allow null as value
        if (text == null) {
            return null;
        }

        return stringToEnum(enumType, text);
    }
}
