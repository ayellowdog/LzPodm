package com.intel.podm.business.redfish.services.assembly;

import static com.intel.podm.business.redfish.services.assembly.VlanRemoveStatus.FAILED;
import static com.intel.podm.business.redfish.services.assembly.VlanRemoveStatus.SUCCESSFUL;
import static com.intel.podm.business.redfish.services.assembly.VlanRemoveStatus.UNSUPPORTED;
import static com.intel.podm.common.enterprise.utils.exceptions.RootCauseInvestigator.tryGetExternalServiceErrorInExceptionStack;
import static com.intel.podm.common.types.net.HttpStatusCode.METHOD_NOT_ALLOWED;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode.EthernetInterface.Vlan;
import com.intel.podm.business.entities.redfish.EthernetSwitchPort;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.redfish.services.actions.EthernetSwitchPortActionsInvoker;
import com.intel.podm.business.redfish.services.actions.EthernetSwitchPortVlanActionsInvoker;
import com.intel.podm.client.actions.UpdateVlanRequest;
import com.intel.podm.common.types.actions.EthernetSwitchPortRedefinition;
import com.intel.podm.common.types.actions.VlanCreationRequest;

@Component
public class VlanAllocator {
    @Autowired
    private VlanSelector vlanSelector;

    @Autowired
    private VlanTerminator vlanTerminator;

    @Autowired
    private EthernetSwitchPortVlanActionsInvoker ethernetSwitchPortVlanActionsInvoker;

    @Autowired
    private EthernetSwitchPortActionsInvoker switchPortActionsInvoker;

    private static final Logger logger = LoggerFactory.getLogger(VlanAllocator.class);

    @Transactional(propagation = Propagation.MANDATORY)
    public void createNecessaryVlans(EthernetSwitchPort associatedSwitchPort, List<Vlan> requestedVlans) throws EntityOperationException {
        List<VlanCreationRequest> vlansToCreate = vlanSelector.getVlansToCreate(associatedSwitchPort.getEthernetSwitchPortVlans(), requestedVlans);
        for (VlanCreationRequest vlanCreationRequest : vlansToCreate) {
            createVlan(associatedSwitchPort, vlanCreationRequest);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public EthernetSwitchPortVlan createVlan(EthernetSwitchPort associatedSwitchPort, VlanCreationRequest request) throws EntityOperationException {
        EthernetSwitchPortVlan vlan = ethernetSwitchPortVlanActionsInvoker.create(associatedSwitchPort, request);
        associatedSwitchPort.addEthernetSwitchPortVlan(vlan);
        return vlan;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void removeUnnecessaryTaggedVlans(EthernetSwitchPort associatedSwitchPort, List<Vlan> vlansToPreserve) throws EntityOperationException {
        List<EthernetSwitchPortVlan> vlansToDelete = vlanSelector.getTaggedVlansToDelete(associatedSwitchPort, vlansToPreserve);
        vlanTerminator.deleteVlans(vlansToDelete);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void changePrimaryVlan(EthernetSwitchPort associatedSwitchPort, Integer primaryVlanId) throws EntityOperationException {
        URI primaryVlanUri = findAssociatedVlanUri(associatedSwitchPort, primaryVlanId);
        EthernetSwitchPortRedefinition switchPortRedefinition = EthernetSwitchPortRedefinition.newBuilder()
            .primaryVlan(primaryVlanUri)
            .build();
        switchPortActionsInvoker.updateSwitchPort(associatedSwitchPort, switchPortRedefinition);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public VlanRemoveStatus tryRemoveUntaggedVlans(EthernetSwitchPort associatedSwitchPort, Vlan untaggedVlanToPreserve) {
        List<EthernetSwitchPortVlan> vlansToDelete = vlanSelector.getUntaggedVlansToDelete(associatedSwitchPort, untaggedVlanToPreserve);
        try {
            vlanTerminator.deleteVlans(vlansToDelete);
        } catch (EntityOperationException e) {
            if (isResponseMethodNotAllowed(e)) {
                logger.info("Ethernet switch does not allow to remove untagged VLANs. Untagged VLANS will not be removed!");
                return UNSUPPORTED;
            } else {
                logger.error("Cannot remove untagged VLANs, error: {}", e);
                return FAILED;
            }
        }
        return SUCCESSFUL;
    }

    private boolean isResponseMethodNotAllowed(EntityOperationException e) {
        return tryGetExternalServiceErrorInExceptionStack(e)
            .map(error -> Objects.equals(error.getResponse().getHttpStatusCode(), METHOD_NOT_ALLOWED))
            .orElse(false);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateUntaggedVlan(EthernetSwitchPortVlan vlanToChange, Integer newVlanId) {
        try {
            ethernetSwitchPortVlanActionsInvoker.update(vlanToChange, new UpdateVlanRequest(newVlanId));
        } catch (EntityOperationException e) {
            logger.warn("Could not update VLAN: {} with VLAN ID: {}", vlanToChange.getSourceUri(), newVlanId);
        }
    }

    private URI findAssociatedVlanUri(EthernetSwitchPort associatedSwitchPort, Integer primaryVlan) throws EntityOperationException {
        List<EthernetSwitchPortVlan> vlans = associatedSwitchPort.getEthernetSwitchPortVlans().stream()
            .filter(vlan -> vlan.getVlanId().equals(primaryVlan))
            .collect(toList());

        if (vlans.size() != 1) {
            throw new EntityOperationException("There should be exactly one VLAN with vlan id: " + primaryVlan
                + " associated with single switch port, vlans found: " + vlans.size());
        }

        return vlans.iterator().next().getSourceUri();
    }
}
