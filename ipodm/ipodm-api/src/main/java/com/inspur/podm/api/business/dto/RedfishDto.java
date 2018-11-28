
package com.inspur.podm.api.business.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.inspur.podm.api.business.dto.redfish.attributes.UnknownOemDto;
import com.inspur.podm.common.intel.logger.Logger;
import com.inspur.podm.common.intel.types.redfish.OemType;
import com.inspur.podm.common.intel.types.redfish.RedfishResource;

import org.atteo.classindex.IndexSubclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.inspur.podm.api.business.services.OemSerializeHelper.oemDtosToUnknownOems;
import static com.inspur.podm.common.intel.logger.LoggerFactory.getLogger;
import static com.inspur.podm.common.intel.types.redfish.OemType.OEM_ELEMENT_SEPARATOR;
import static com.inspur.podm.common.intel.types.redfish.OemType.Type.OEM_IN_ACTIONS;
import static com.inspur.podm.common.intel.types.redfish.OemType.Type.OEM_IN_LINKS;
import static com.inspur.podm.common.intel.types.redfish.OemType.Type.TOP_LEVEL_OEM;
import static com.inspur.podm.common.intel.utils.StringRepresentation.fromIterable;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

public abstract class RedfishDto implements RedfishResource {
    private static final Logger LOGGER = getLogger(RedfishDto.class);
    @JsonProperty("@odata.type")
    private final String oDataType;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Oem")
    private DefaultTopLevelOemDto oem = new DefaultTopLevelOemDto();
    @JsonIgnore
    private List<UnknownOemDto> unknownOems = new ArrayList<>();

    protected RedfishDto(String oDataType) {
        this.oDataType = oDataType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getoDataType() {
        return oDataType;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public List<UnknownOemDto> getUnknownOems() {
        return unknownOems;
    }

    public void setUnknownOems(List<UnknownOemDto> unknownOems) {
        this.unknownOems = unknownOems;
    }

    @IndexSubclasses
    public abstract class RedfishOemDto {
        @JsonAnyGetter
        public Map<String, Object> handleUnknownOems() throws JsonProcessingException {
            OemType oemType = getClass().getAnnotation(OemType.class);
            if (oemType == null) {
                LOGGER.e("Mandatory OemType annotation is not defined for: " + getClass().getName());
                return emptyMap();
            }

            return createUnknownsOemsMapToAddFromOemPathAndAllUnknownOems(oemType.value().getPath(), oemDtosToUnknownOems(unknownOems));
        }

        private Map<String, Object> createUnknownsOemsMapToAddFromOemPathAndAllUnknownOems(List<String> oemNodePath, Map<String, JsonNode> unknownOems) {
            String oemNodePathBeginning = fromIterable(oemNodePath, OEM_ELEMENT_SEPARATOR);
            String prefix = oemNodePathBeginning + OEM_ELEMENT_SEPARATOR;

            return unknownOems.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(toMap(e -> e.getKey().replaceFirst(prefix, ""), Map.Entry::getValue));
        }
    }

    public abstract class RedfishLinksDto {
        @JsonProperty("Oem")
        private DefaultLinksOemDto oem = new DefaultLinksOemDto();
    }

    public abstract class RedfishActionsDto {
        @JsonProperty("Oem")
        private DefaultActionsOemDto oem = new DefaultActionsOemDto();
    }

    @OemType(TOP_LEVEL_OEM)
    public class DefaultTopLevelOemDto extends RedfishOemDto {
    }

    @OemType(OEM_IN_LINKS)
    public class DefaultLinksOemDto extends RedfishOemDto {
    }

    @OemType(OEM_IN_ACTIONS)
    public class DefaultActionsOemDto extends RedfishOemDto {
    }
}
