package com.intel.podm.discovery.external.partial;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static java.lang.String.format;

import java.net.URI;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.DiscoverableEntityDao;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.Fabric;
import com.intel.podm.business.entities.redfish.Zone;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.reader.ResourceSupplier;
import com.intel.podm.client.resources.ExternalServiceResource;
import com.intel.podm.client.resources.redfish.ZoneResource;
import com.intel.podm.common.types.Id;
import com.intel.podm.mappers.redfish.ZoneMapper;

@Component
public class ZoneObtainer {
    @Autowired
    private WebClientBuilder webClientBuilder;

    @Autowired
    private DiscoverableEntityDao discoverableEntityDao;

    @Autowired
    private ZoneMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(ZoneObtainer.class);

    @Transactional(propagation = Propagation.MANDATORY)
    public Zone discoverZone(ExternalService service, Fabric fabric, URI zoneUri) throws WebClientRequestException {
        requiresNonNull(service, "service", "There is no Service associated with selected volume");

        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            ZoneResource zoneResource = (ZoneResource) webClient.get(zoneUri);
            Id entityId = zoneResource.getGlobalId(service.getId());
            Zone zone = discoverableEntityDao.findOrCreateEntity(service, entityId, zoneResource.getUri(), Zone.class);
            mapper.map(zoneResource, zone);
            refreshZoneEndpointsAfterUpdate(zone, zoneResource);
            fabric.addZone(zone);

            return zone;
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void refreshZoneEndpointsAfterUpdate(Zone zone, ZoneResource zoneResource) throws WebClientRequestException {
        zone.clearEndpointsCollection();

        StreamSupport.stream(zoneResource.getEndpoints().spliterator(), false)
            .map(endpointResourceSupplier -> getEndpoint(endpointResourceSupplier, zone.getService()))
            .filter(Objects::nonNull)
            .forEach(zone::addEndpoint);
    }

    private Endpoint getEndpoint(ResourceSupplier endpointResourceSupplier, ExternalService service) {
        try {
            ExternalServiceResource externalResource = endpointResourceSupplier.get();
            Id globalId = externalResource.getGlobalId(service.getId());
            Endpoint endpoint = discoverableEntityDao.findByGlobalId(globalId, Endpoint.class);

            if (endpoint == null) {
                logger.error("Entity(id:{}) doesn't exist", globalId);
                return null;
            }
            return endpoint;
        } catch (WebClientRequestException e) {
            logger.error(format("Error while retrieving endpoint resource supplier: %s", e.getMessage()), e);
            return null;
        }
    }
}
