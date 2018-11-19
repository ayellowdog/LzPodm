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

import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.UPPER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS;
import static com.fasterxml.jackson.jaxrs.cfg.JaxRSFeature.ALLOW_EMPTY_INPUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.inspur.podm.service.rest.representation.json.serializers.ContextAwareSerializer;

@ApplicationScoped
@Provider
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:ParameterNumber"})
public class JsonProvider extends JacksonJsonProvider {
    @Context
    UriInfo uriInfo;

    @Inject
    public void initialize(Instance<ContextAwareSerializer> serializers) {
        NorthboundObjectMapperModuleProvider mapperModuleProvider = new NorthboundObjectMapperModuleProvider();
        SerializerContext context = new SerializerContext(this);

        ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(UPPER_CAMEL_CASE)
            .registerModule(mapperModuleProvider.getSerializerModule(context, serializers))
            .registerModule(mapperModuleProvider.getDeserializerModule())
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .disable(WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(SORT_PROPERTIES_ALPHABETICALLY)
            .disable(FAIL_ON_EMPTY_BEANS)
            .enable(INDENT_OUTPUT);

        disable(ALLOW_EMPTY_INPUT);

        setMapper(mapper);
    }

    @Override
    public Object readFrom(Class<Object> type,
                           Type genericType,
                           Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException {
        try {
            return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
        } catch (JsonProcessingException | NoContentException e) {
            throw new JsonProviderException(e);
        }
    }
}
