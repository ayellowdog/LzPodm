package com.intel.podm.business.redfish.services.assembly.tasks;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Zone;
import com.intel.podm.business.redfish.services.actions.PcieZoneActionsInvoker;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.types.Id;

@Component
public class DetachPcieDriveTask extends NodeTask {

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private PcieZoneActionsInvoker pcieZoneActionsInvoker;
	
    private Id zoneId;
    private Id endpointId;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return genericDao.find(Zone.class, zoneId).getService().getUuid();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TimeMeasured(tag = "[AssemblyTask]")
    public void run() {
        Zone zone = genericDao.find(Zone.class, zoneId);
        Endpoint endpoint = genericDao.find(Endpoint.class, endpointId);

        try {
            pcieZoneActionsInvoker.detachEndpoint(zone, endpoint);
        } catch (EntityOperationException e) {
            throw new RuntimeException("Detaching PCIe device functions from PCIe zone failed", e);
        }

    }

    public void setZoneId(Id zoneId) {
        this.zoneId = zoneId;
    }

    public void setEndpointId(Id endpointId) {
        this.endpointId = endpointId;
    }
}
