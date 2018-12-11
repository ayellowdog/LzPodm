/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.rest.redfish.controller;

import static com.inspur.podm.api.business.services.context.PathParamConstants.getPathParameterNames;
import static com.inspur.podm.service.rest.error.PodmExceptions.notFound;
import static com.intel.podm.common.types.Id.fromString;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeStart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.service.rest.redfish.resources.context.ContextBuilder;
import com.inspur.podm.service.rest.redfish.resources.context.ContextBuilderException;
import com.intel.podm.common.types.Id;

/**
 * @ClassName: BaseController
 * @Description: TODO
 *
 * @author: zhangdian
 * @date: 2018年11月27日 下午4:47:59
 */
public class BaseController {
	
    /** @Fields uriInfo: 路径信息  */
    protected Map<String, String> uriInfo = new HashMap<String, String>();
	
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
    
    /**
     * Gets current context from URI.
     *
     * @return current context from URI
     * @throws NotFoundException if URI is incorrect
     */
    protected com.inspur.podm.api.business.services.context.Context getCurrentContext() {
        ContextBuilder builder = new ContextBuilder();

        try {
            for (String paramName : getPathParameterNames()) {
                String param = unwrapParam(paramName);
                if (hasParam(param)) {
                    builder.add(paramName, getId(param));
                }
            }
            return builder.build();
        } catch (NumberFormatException | ContextBuilderException e) {
            throw new NotFoundException(e);
        }
    }
    
    /**
     * <p> unwrapParam from {} </p>
     * 
     * @author: zhangdian
     * @date: 2018年11月29日 下午2:37:40
     * @param wrappedParamName
     * @return
     */
    private String unwrapParam(String wrappedParamName) {
        String param = removeStart(wrappedParamName, "{");
        return removeEnd(param, "}");
    }
    
    /**
     * <p> 查看url里面是否有param </p>
     * 
     * @author: zhangdian
     * @date: 2018年11月29日 下午2:37:26
     * @param paramName
     * @return
     */
    private boolean hasParam(String paramName) {
        return uriInfo.containsKey(paramName);
    }
    
    /**
     * <p> 从url里面获取id </p>
     * 
     * @author: zhangdian
     * @date: 2018年11月29日 下午2:37:12
     * @param paramName
     * @return
     */
    private Id getId(String paramName) {
        String param = getParam(paramName);
        return fromString(param);
    }
    
    /**
     * <p> getParam </p>
     * 
     * @author: zhangdian
     * @date: 2018年11月29日 下午2:36:54
     * @param paramName
     * @return
     */
    private String getParam(String paramName) {
        if (!hasParam(paramName)) {
            String msg = format("Path parameter '%s' does not exist", paramName);
            throw new IllegalStateException(msg);
        }

        String paramValues = uriInfo.get(paramName);


        return paramValues;
    }

}

