package com.inspur.podm.controller;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.UPPER_CAMEL_CASE;

import java.net.URI;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.json.Json;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inspur.podm.common.client.HttpApiService;
import com.inspur.podm.common.context.AppContext;
import com.inspur.podm.controller.test.CostTime;
import com.inspur.podm.service.itaskbase.data.bean.TaskInfoModel;
import com.inspur.podm.service.itaskbase.service.TaskInfoService;
import com.inspur.podm.service.service.detection.dhcp.MyDhcpServiceDetectorInterface;
import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.dao.MyChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.client.SerializersProvider;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.reader.ResourceSupplier;
import com.intel.podm.client.redfish.response.RedfishResponseBodyImpl;
import com.intel.podm.client.resources.ExternalServiceResource;
import com.intel.podm.client.resources.redfish.ChassisResource;
import com.intel.podm.client.resources.redfish.PowerResource;
import com.intel.podm.common.enterprise.utils.beans.BeanFactory;
import com.intel.podm.discovery.external.DiscoveryRunner;


@RestController
@RequestMapping(value = "test")
public class PerconController {

//	@Autowired
//	MyChassisDao myDao;
//	@Autowired
//	MyDhcpServiceDetectorInterface dhcpServiceDetectorInterface;
//    @PostMapping
//    @Transactional
//	@Retryable(value= {RuntimeException.class},maxAttempts = 3,backoff = @Backoff(delay = 100l,multiplier = 1))
//    @CostTime
//    public void addPerson() throws Exception {
//    	dhcpServiceDetectorInterface.test("事件监听与重试测试");
//    }
//    @Recover
//    public void recover(RuntimeException e) {
//            System.out.println("重试结束");
//    }

	@Autowired
	private WebClientBuilder webClientBuilder;
	@Autowired 
	private BeanFactory bf;
	@Autowired
	ChassisDao myDao;
	@Resource(name="managedExecutorService")
	private ScheduledExecutorService executor;
//    private CloseableHttpClient httpClient;
	@PostMapping
	public void testClient () throws WebClientRequestException {
//		URI uri = URI.create("http://10.180.201.7:9443/redfish/v1/Chassis/drawer1/Power");
//		WebClient client = webClientBuilder.newInstance(uri).build();
//		PowerResource c = (PowerResource) client.get(uri);
//		Iterable<ResourceSupplier> powerControls = c.getPowerControls();
//		Iterator<ResourceSupplier> iterator = powerControls.iterator();
//		while(iterator.hasNext()){
//			ResourceSupplier next = iterator.next();
//			System.out.println(next.getUri());
//			ExternalServiceResource resource = next.get();
//			URI uri2 = next.getUri();
//			System.out.println(uri2);
//		}
//		try {
//			String str = service.doGet("http://10.180.201.7:9443/redfish/v1/Chassis/module2");
//			
//			ObjectMapper mapper = new ObjectMapper()
//		            .setPropertyNamingStrategy(UPPER_CAMEL_CASE)
//		            .registerModule(new JavaTimeModule())
//		            .registerModule(new SerializersProvider().getSerializersModule())
//		            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
//		            .enable(FAIL_ON_NULL_FOR_PRIMITIVES);
////			ChassisResource c4 = mapper.readValue(str,ChassisResource.class);
//			
//			JavaType baseType = mapper.constructType(RedfishResponseBodyImpl.class);
//			RedfishResponseBodyImpl c = mapper.readValue(str,baseType);
//			ExternalServiceResource c2 = (ExternalServiceResource) c;
//			ChassisResource c3 = (ChassisResource) c2;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		DiscoveryRunner r = bf.create(DiscoveryRunner.class);
		System.out.println(r);
		DiscoveryRunner r2 = bf.create(DiscoveryRunner.class);
		System.out.println(r2);
		System.out.println(r==r2);
		
	}

@GetMapping
public void testTrans() throws InterruptedException, ExecutionException {
//	System.out.println("最外层Thread：" + Thread.currentThread());
//	TestRunner runner = bf.create(TestRunner.class);
//	ScheduledFuture<?> schedule = executor.scheduleAtFixedRate(runner, 1, 10, TimeUnit.SECONDS);
//	System.out.println("已经提交了1");
//	TestRunner runner2 = new TestRunner();
//	runner2.setDao(myDao);
//	ScheduledFuture<?> schedul2 = executor.scheduleAtFixedRate(runner2, 1, 5, TimeUnit.SECONDS);
//	System.out.println("已经提交了2");
////	dao.create();
	TestRunner r1 = AppContext.getBean(TestRunner.class);
	r1.setId("1");
	System.out.println(r1.getId());
	System.out.println(r1.getDao());
	TestRunner r2 = AppContext.getBean(TestRunner.class);
	r2.setId("2");
	System.out.println(r2.getId());
	System.out.println(r2.getDao());
//	@SuppressWarnings("unused")
//	ScheduledFuture<?> schedule = executor.scheduleAtFixedRate(r2, 1, 300, TimeUnit.SECONDS);
}
    
}

