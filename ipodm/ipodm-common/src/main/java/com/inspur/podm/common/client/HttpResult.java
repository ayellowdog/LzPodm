/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.common.client;

/**
 * @ClassName: HttpResult
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月17日 上午11:33:54
 */
public class HttpResult {

    // 响应码
    private Integer code;

    // 响应体
    private String body;


    public HttpResult() {
        super();
    }

    public HttpResult(Integer code, String body) {
        super();
        this.code = code;
        this.body = body;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}