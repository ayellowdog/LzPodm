package com.intel.podm.business.redfish.services.actions;

import static com.intel.podm.common.utils.Contracts.requires;
import static java.util.stream.Collectors.toSet;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business	.EntityOperationException;
import com.intel.podm.business.entities.redfish.ConnectedEntity;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.Zone;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.actions.ZoneActionJson;

@Component
public class PcieZoneActionsInvoker {
	
    private static final Logger logger = LoggerFactory.getLogger(PcieZoneActionsInvoker.class);

    @Autowired
    private WebClientBuilder webClientBuilder;

    @Autowired
    private PcieDriveActionsInvoker pcieDriveActionsInvoker;

    @Transactional(propagation = Propagation.MANDATORY)
    public void attachEndpoint(Zone zone, Endpoint endpoint) throws EntityOperationException {
        ExternalService service = checkPreconditions(zone, endpoint);

        zone.addEndpoint(endpoint);
        updateZone(service, zone);
        updateDriveErased(endpoint);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void detachEndpoint(Zone zone, Endpoint endpoint) throws EntityOperationException {
        ExternalService service = checkPreconditions(zone, endpoint);

        zone.unlinkEndpoint(endpoint);
        updateZone(service, zone);
    }

    private ExternalService checkPreconditions(Zone zone, Endpoint endpoint) throws EntityOperationException {
        requires(zone != null, "Cannot perform action if zone is unspecified(null)");
        ExternalService zoneService = zone.getService();
        requires(zoneService != null, "There is no Service associated with selected Zone");
        confirmSameService(zoneService, endpoint.getService());
        return zoneService;
    }

    private void updateDriveErased(Endpoint endpoint) throws EntityOperationException {
        for (ConnectedEntity connectedEntity : endpoint.getConnectedEntities()) {
            DiscoverableEntity entity = connectedEntity.getEntityLink();
            if (entity instanceof Drive) {
                pcieDriveActionsInvoker.updateDriveErased((Drive) entity, false);
            }
        }
    }

    private void updateZone(ExternalService service, Zone zone) throws EntityOperationException {
        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            webClient.patch(zone.getSourceUri(), new ZoneActionJson(zone.getEndpoints().stream()
                .map(DiscoverableEntity::getSourceUri)
                .collect(toSet())));
        } catch (WebClientRequestException e) {
            String errorMessage = "Patch Zone failed: " + e.getMessage();
            logger.warn("{} on [ service: {}, Zone: {}, Endpoints: {}]",
                errorMessage,
                service.getBaseUri(),
                zone.getSourceUri(),
                zone.getEndpoints());
            throw new EntityOperationException(errorMessage, e);
        }
    }

    private void confirmSameService(ExternalService firstService, ExternalService secondService) throws EntityOperationException {
        boolean allDevicesBelongToTheSameService = Objects.equals(firstService, secondService);
        if (!allDevicesBelongToTheSameService) {
            throw new EntityOperationException("Not all devices belong to the same service");
        }
    }
}
