package com.intel.podm.business.redfish.services.actions;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.redfish.EthernetSwitchPort;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.actions.CreateVlanRequest;
import com.intel.podm.client.actions.UpdateVlanRequest;
import com.intel.podm.client.resources.redfish.EthernetSwitchPortVlanResource;
import com.intel.podm.common.types.actions.VlanCreationRequest;
import com.intel.podm.discovery.external.partial.EthernetSwitchPortVlanObtainer;
import com.intel.podm.mappers.redfish.EthernetSwitchPortVlanMapper;

@Component
public class EthernetSwitchPortVlanActionsInvoker {
    @Autowired	
    private WebClientBuilder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(EthernetSwitchPortVlanActionsInvoker.class);

    @Autowired
    private EthernetSwitchPortVlanObtainer vlanObtainer;

    @Autowired
    private EthernetSwitchPortVlanMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public EthernetSwitchPortVlan create(EthernetSwitchPort port, VlanCreationRequest request) throws EntityOperationException {
        ExternalService service = port.getService();
        requiresNonNull(service, "service", "There is no Service associated with selected switch port");

        URI serviceBaseUri = service.getBaseUri();
        URI switchPortUri = port.getSourceUri();

        try (WebClient webClient = webClientBuilder.newInstance(serviceBaseUri).retryable().build()) {
            URI vlanCollectionUri = URI.create(switchPortUri + "/VLANs");
            URI vlanUri = webClient.post(vlanCollectionUri, new CreateVlanRequest(request.getId(), request.isTagged(), request.isEnabled()));
            return vlanObtainer.discoverEthernetSwitchPortVlan(service, vlanUri);
        } catch (WebClientRequestException e) {
            String errorMessage = "Vlan creation failed on selected switch port";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", serviceBaseUri, switchPortUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(EthernetSwitchPortVlan vlan, UpdateVlanRequest request) throws EntityOperationException {
        ExternalService service = vlan.getService();
        requiresNonNull(service, "service", "There is no Service associated with selected vlan");

        URI serviceBaseUri = service.getBaseUri();
        URI vlanUri = vlan.getSourceUri();

        try (WebClient webClient = webClientBuilder.newInstance(serviceBaseUri).retryable().build()) {
            EthernetSwitchPortVlanResource ethernetSwitchPortVlanResource = webClient.patchAndRetrieve(vlanUri, request);
            mapper.map(ethernetSwitchPortVlanResource, vlan);
        } catch (WebClientRequestException e) {
            String errorMessage = "Vlan update failed";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", serviceBaseUri, vlanUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }
}
