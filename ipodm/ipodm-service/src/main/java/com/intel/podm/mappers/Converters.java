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

package com.intel.podm.mappers;

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import com.intel.podm.common.types.Ref;

public final class Converters {

    private Converters() {
    }

    @SuppressWarnings("checkstyle:AnonInnerLength")
    public static ConditionalConverter<Ref<?>, ?> refConverter() {
        return new ConditionalConverter<Ref<?>, Object>() {
            @Override
            public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
                return sourceType.isAssignableFrom(Ref.class) ? MatchResult.FULL : MatchResult.NONE;
            }

            @Override
            public Object convert(MappingContext<Ref<?>, Object> context) {
                Object source = context.getSource();
                if (source == null) {
                    return null;
                }

                if (context.getSourceType().isAssignableFrom(Ref.class)) {
                    return ((Ref) source).get();
                }

                return source;
            }
        };
    }
}
