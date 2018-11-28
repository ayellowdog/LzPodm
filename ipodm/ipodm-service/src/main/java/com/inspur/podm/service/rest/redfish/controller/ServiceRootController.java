package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.inspur.podm.common.intel.types.ServiceKind.SINGLETON;
import static com.inspur.podm.common.intel.types.redfish.ODataServices.ODATA_ROOT_SERVICES;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.CHASSIS_RESOURCE_NAME;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.api.business.dto.ChassisDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.dto.redfish.ODataServiceDocumentDto;
import com.inspur.podm.api.business.dto.redfish.ServiceRootContext;
import com.inspur.podm.api.business.dto.redfish.ServiceRootDto;
import com.inspur.podm.api.business.dto.redfish.attributes.ODataServiceDto;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.ServiceRootService;
import com.inspur.podm.common.intel.types.ServiceKind;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: ServiceRootController
 * @Description: ServiceRootController
 *
 * @author: zhangdian
 * @date: 2018年11月28日 上午10:23:08
 */
@RestController
@RequestMapping("/redfish/v1")
@Api(value = "RedfishController", description = "/redfish/v1控制器")
public class ServiceRootController extends BaseController {

	/** @Fields readerService: readerService */
	@Autowired
	private ServiceRootService serviceRootReaderService;
	@Autowired
	private ReaderService<ChassisDto> readerService;
	private static final String SERVICE_ROOT = "/redfish/v1/";
	private static final String SERVICE = "Service";

	@ApiOperation(value = "查看redfish目录/redfish/v1", notes = "/redfish/v1")
	@RequestMapping(method = RequestMethod.GET)
	public RedfishResourceAmazingWrapper get() {
		ServiceRootDto serviceRootDto = serviceRootReaderService.getServiceRoot();
		return new RedfishResourceAmazingWrapper(new ServiceRootContext(), serviceRootDto);
	}

	// @Path(COMPOSED_NODES_RESOURCE_NAME)
	// public ComposedNodeCollectionResource getComposedNodes() {
	// return getResource(ComposedNodeCollectionResource.class);
	// }
	//
	// @Path(COMPUTER_SYSTEM_RESOURCE_NAME)
	// public ComputerSystemCollectionResource getComputerSystems() {
	// return getResource(ComputerSystemCollectionResource.class);
	// }
	//
	/**
	 * <p> TODO 功能描述 </p>
	 * 
	 * @author: zhangdian
	 * @date: 2018年11月28日 上午11:23:44
	 * @return
	 */
	@ApiOperation(value = "查看redfish目录/redfish/v1/chassis", notes = "Chassis")
	@RequestMapping(value = "/" + CHASSIS_RESOURCE_NAME, method = RequestMethod.GET)
	public CollectionDto getChassis() {
		CollectionDto collectionDto = getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
		return collectionDto;
//		return new RedfishResourceAmazingWrapper(new ServiceRootContext(), collectionDto);
	}
	//
	// @Path(MANAGERS_RESOURCE_NAME)
	// public ManagerCollectionResource getManagers() {
	// return getResource(ManagerCollectionResource.class);
	// }
	//
	// @Path(FABRIC_RESOURCE_NAME)
	// public FabricCollectionResource getFabrics() {
	// return getResource(FabricCollectionResource.class);
	// }
	//
	// @Path(STORAGE_SERVICES_RESOURCE_NAME)
	// public StorageServiceCollectionResource getStorageServices() {
	// return getResource(StorageServiceCollectionResource.class);
	// }
	//
	// @Path(EVENT_SERVICE_RESOURCE_NAME)
	// public EventServiceResource getEventService() {
	// return getResource(EventServiceResource.class);
	// }
	//
	// @Path(ETHERNET_SWITCHES_RESOURCE_NAME)
	// public EthernetSwitchCollectionResource getEthernetSwitches() {
	// return getResource(EthernetSwitchCollectionResource.class);
	// }
	//
	// @Path(TELEMETRY_SERVICE_RESOURCE_NAME)
	// public TelemetryServiceResource getTelemetryService() {
	// return getResource(TelemetryServiceResource.class);
	// }

	/**
	 * <p>
	 * /odata
	 * </p>
	 * 
	 * @author: zhangdian
	 * @date: 2018年11月28日 上午10:49:35
	 * @return
	 */
	@ApiOperation(value = "查看redfish目录/redfish/v1/odata", notes = "/redfish/v1/odata")
	@RequestMapping(value = "/odata", method = RequestMethod.GET)
	public ODataServiceDocumentDto getOData() {
		return ODataServiceDocumentDto.newBuilder().values(getODataRootServices()).build();
	}

	/**
	 * <p>
	 * getODataRootServices
	 * </p>
	 * 
	 * @author: zhangdian
	 * @date: 2018年11月28日 上午10:48:54
	 * @return
	 */
	private List<ODataServiceDto> getODataRootServices() {
		List<ODataServiceDto> services = new ArrayList<>();
		services.add(map(SERVICE, SINGLETON, URI.create(SERVICE_ROOT)));

		ODATA_ROOT_SERVICES
				.forEach(service -> services.add(map(service, SINGLETON, URI.create(SERVICE_ROOT + service))));
		return services;
	}

	/**
	 * <p>
	 * map
	 * </p>
	 * 
	 * @author: zhangdian
	 * @date: 2018年11月28日 上午10:49:20
	 * @param name
	 * @param serviceKind
	 * @param url
	 * @return
	 */
	private ODataServiceDto map(String name, ServiceKind serviceKind, URI url) {
		return ODataServiceDto.newBuilder().name(name).kind(serviceKind).url(url).build();
	}

	// @ApiOperation(value = "查看redfish目录/redfish/v1/$metadata", notes =
	// "/redfish/v1/$metadata")
	// @RequestMapping(value = "/$metadata", method = RequestMethod.GET)
	// public MetadataResourceProvider getRootMetadata() {
	// return new MetadataResourceProvider("$metadata.xml");
	// }
	//

	// @ApiOperation(value = "查看redfish目录/redfish/v1/metadata", notes =
	// "/redfish/v1/metadata")
	// @RequestMapping(value = "/metadata", method = RequestMethod.GET)
	// public MetadataResource getMetadata() {
	// throw invalidHttpMethod();
	// }

}
