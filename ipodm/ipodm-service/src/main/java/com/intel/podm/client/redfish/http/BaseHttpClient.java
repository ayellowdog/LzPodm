/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.client.redfish.http;

import java.net.URI;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intel.podm.common.types.net.HttpMethod;

/**
 * @ClassName: BaseHttpClient
 * @Description: 这个类是对Apache HttpClient的最基础封装，作为所有http请求的最底层实现
 *
 * @author: liuchangbj
 * @date: 2018年12月19日 上午8:40:06
 */
public class BaseHttpClient implements AutoCloseable{
	private static final Logger logger = LoggerFactory.getLogger(BaseHttpClient.class);
	private CloseableHttpClient httpClient;
	/**
	 * 请求的一些参数，如超时时间等
	 */
	private RequestConfig config;
	/**
	 * 用来做jackson映射的mapper
	 */
	private ObjectMapper objMapper;
	
	public BaseHttpClient(CloseableHttpClient httpClient, RequestConfig config, ObjectMapper objMapper) {
		super();
		this.httpClient = httpClient;
		this.config = config;
		this.objMapper = objMapper;
	}
	public HttpResponse call(HttpMethod method, URI uri, Object requestEntity, Class responseEntityClass) {
		HttpRequestBase request =  buildRequest(method, uri, requestEntity);
		request.setConfig(config);
		try(CloseableHttpResponse response = this.httpClient.execute(request)) {
			int statusCode = response.getStatusLine().getStatusCode();
			JavaType javaType = objMapper.constructType(responseEntityClass);
			String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
			Object readEntity = objMapper.readValue(entity, javaType);
			return new HttpResponse(statusCode, readEntity, uri);
		} catch (Exception e) {
			logger.error("Http error on '{}' : " + e.getMessage(), uri.toString());
		}
		return null;
	}
	private HttpRequestBase buildRequest(HttpMethod method, URI uri, Object requestEntity){
		switch (method) {
		case GET:
			HttpGet get = new HttpGet(uri);
			return get;
		case POST:
			HttpPost post = new HttpPost(uri);
			String entity;
			try {
				entity = objMapper.writeValueAsString(requestEntity);
				post.setEntity(new StringEntity(entity, "utf-8"));
			} catch (JsonProcessingException e) {
				logger.error("Json Convert error on '{}' : " + e.getMessage(), requestEntity);
			}
			post.setHeader("Content-Type", "application/json");
			return post;
		default:
			break;
		}
		return null;
	}

	@Override
	public void close() throws Exception {
		httpClient.close();
	}
	
}

