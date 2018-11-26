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

package com.inspur.podm.common.persistence.converter;



import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.inspur.podm.common.intel.types.Id;

import static com.inspur.podm.common.intel.types.Id.id;

@Converter(autoApply = true)
public class IdToStringConverter implements AttributeConverter<Id, String> {
    @Override
    public String convertToDatabaseColumn(Id value) {
        return value == null ? null : value.getValue();
    }

    @Override
    public Id convertToEntityAttribute(String value) {
        return value == null ? null : id(value);
    }
}
