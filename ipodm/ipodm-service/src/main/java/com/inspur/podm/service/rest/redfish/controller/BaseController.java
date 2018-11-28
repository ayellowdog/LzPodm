/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.service.rest.error.PodmExceptions.notFound;

import com.inspur.podm.api.business.ContextResolvingException;

/**
 * @ClassName: BaseController
 * @Description: TODO
 *
 * @author: zhangdian
 * @date: 2018年11月27日 下午4:47:59
 */
public class BaseController {
	
	/**
     * Gets DTO based on closure implementation for HTTP GET method.
     *
     * @param closure Expression that provides DTO.
     * @return DTO for GET HTTP call.
     * @throws javax.ws.rs.WebApplicationException when specified entity was not found
     * or RuntimeException when closure call fails.
     */
    public static <T> T getOrThrow(EntitySupplier<T> closure) {
        try {
            return closure.get();
        } catch (ContextResolvingException e) {
            throw notFound();
        }
    }
    
    public interface EntitySupplier<V> {
        V get() throws ContextResolvingException;
    }

}

