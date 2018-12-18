package com.intel.podm.business.redfish.services.impl;

import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.intel.podm.business.redfish.services.ContextCollections.asDriveContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asEthernetInterfaceContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asManagerContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asMemoryContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asProcessorContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asSimpleStorageContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asVolumeContexts;
import static com.intel.podm.business.redfish.services.Contexts.toContext;
import static com.intel.podm.common.types.ChassisType.POD;
import static com.intel.podm.common.types.actions.ActionInfoNames.ATTACH_RESOURCE_ACTION_INFO;
import static com.intel.podm.common.types.actions.ActionInfoNames.DETACH_RESOURCE_ACTION_INFO;
import static com.intel.podm.common.utils.IterableHelper.single;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static com.inspur.podm.api.business.services.context.Context.contextOf;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.ComposedNodeDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.dto.redfish.ComposedNodeCollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.dao.ComposedNodeDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.embeddables.Boot;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;

@Service("composedNodeReader")
class ComposedNodeReaderImpl implements ReaderService<ComposedNodeDto> {
    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private ComposedNodeDao composedNodeDao;

    @Autowired
    private ChassisDao chassisDao;

    @Transactional(REQUIRED)
    @Override
    public CollectionDto getCollection(Context serviceRootContext) {
        List<Context> nodeContexts = composedNodeDao.getAllComposedNodeIds().stream().map(id -> contextOf(id, ContextType.COMPOSED_NODE)).sorted()
            .collect(toList());
        return new ComposedNodeCollectionDto(CollectionDto.Type.COMPOSED_NODE, nodeContexts);
    }

    @Transactional(REQUIRED)
    @Override
    public ComposedNodeDto getResource(Context composedNodeContext) throws ContextResolvingException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(composedNodeContext);

        ComposedNodeDto composedNodeDto = new ComposedNodeDto();
        fillFromNode(composedNodeDto, composedNode);

        ComputerSystem computerSystem = composedNode.getComputerSystem();
        if (computerSystem != null) {
            fillFromComputerSystem(composedNodeDto, computerSystem);
        }

        composedNodeDto.setClearTpmOnDelete(composedNode.getClearTpmOnDelete());

        return composedNodeDto;
    }

    private void fillFromNode(ComposedNodeDto composedNodeDto, ComposedNode composedNode) {
        composedNodeDto.setId(composedNode.getTheId().toString());
        composedNodeDto.setName(composedNode.getName());
        composedNodeDto.setDescription(composedNode.getDescription());
        composedNodeDto.setStatus(composedNode.getStatus());
        composedNodeDto.setComposedNodeState(composedNode.getComposedNodeState());

        fillNodeLinks(composedNodeDto, composedNode);
        fillNodeActions(composedNodeDto, composedNode);
    }

    private void fillNodeLinks(ComposedNodeDto composedNodeDto, ComposedNode composedNode) {
        ComposedNodeDto.Links links = composedNodeDto.getLinks();
        links.getEthernetInterfaces().addAll(asEthernetInterfaceContexts(composedNode.getEthernetInterfaces()));
        links.getManagedBy().addAll(asManagerContexts(single(chassisDao.getAllByChassisType(POD)).getManagers()));

        links.getStorage().addAll(asVolumeContexts(composedNode.getVolumes()));
        links.getStorage().addAll(asDriveContexts(composedNode.getLocalDrives()));
        links.getStorage().addAll(asSimpleStorageContexts(composedNode.getSimpleStorages()));
        links.getStorage().addAll(asDriveContexts(composedNode.getDrives()));
    }

    private void fillNodeActions(ComposedNodeDto composedNodeDto, ComposedNode composedNode) {
        Context context = contextOf(composedNode.getTheId(), ContextType.COMPOSED_NODE);
        ComposedNodeDto.Actions actions = composedNodeDto.getActions();
        actions.getResetAction().setTarget(singletonContextOf(context, "Actions/ComposedNode.Reset"));
        actions.getAssembleAction().setTarget(singletonContextOf(context, "Actions/ComposedNode.Assemble"));
        actions.getAttachResourceAction().setTarget(singletonContextOf(context, "Actions/ComposedNode.AttachResource"));
        actions.getAttachResourceAction().setActionInfo(singletonContextOf(context, format("Actions/%s", ATTACH_RESOURCE_ACTION_INFO)));
        actions.getDetachResourceAction().setTarget(singletonContextOf(context, "Actions/ComposedNode.DetachResource"));
        actions.getDetachResourceAction().setActionInfo(singletonContextOf(context, format("Actions/%s", DETACH_RESOURCE_ACTION_INFO)));

    }

    private void fillFromComputerSystem(ComposedNodeDto composedNodeDto, ComputerSystem computerSystem) {
        fillBoot(composedNodeDto, computerSystem);

        composedNodeDto.getActions().getResetAction().setAllowableResetTypes(computerSystem.getAllowableResetTypes());
        composedNodeDto.setUuid(computerSystem.getUuid());
        composedNodeDto.setPowerState(computerSystem.getPowerState());
        composedNodeDto.getLinks().setProcessors(asProcessorContexts(computerSystem.getProcessors()));
        composedNodeDto.getLinks().setMemory(asMemoryContexts(computerSystem.getMemoryModules()));
        composedNodeDto.getLinks().setComputerSystem(toContext(computerSystem));
    }

    private void fillBoot(ComposedNodeDto composedNodeDto, ComputerSystem computerSystem) {
        ComposedNodeDto.BootDto bootDto = composedNodeDto.getBoot();
        Boot boot = computerSystem.getBoot();
        if (boot == null) {
            return;
        }

        bootDto.setBootSourceOverrideEnabled(boot.getBootSourceOverrideEnabled());
        bootDto.setBootSourceOverrideTarget(boot.getBootSourceOverrideTarget());
        bootDto.setBootSourceOverrideTargetAllowableValues(boot.getBootSourceOverrideTargetAllowableValues());
        bootDto.setBootSourceOverrideMode(boot.getBootSourceOverrideMode());
        bootDto.setBootSourceOverrideModeAllowableValues(boot.getBootSourceOverrideModeAllowableValues());
    }
}
