/**
 *<P> Copyright © 2017 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.itaskbase.data.consts;

import java.io.Serializable;
/**
 * Itask常量类.
 * @author chenchunfeng
 * @date 2018年4月10日
 */
public class TaskConst implements Serializable {
    /**
     * 私有构造器.
     */
    private TaskConst() {
        super();
    }
    /**
     * 序列号.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 任务新创建.
     */
    public static final String STATUS_CREATE = "0";
    
    /**
     * 正在运行.
     */
    public static final String STATUS_RUNNING = "1";
    
    /**
     * 任务成功.
     */
    public static final String STATUS_SUCCESS = "2";
    
    /**
     * 任务失败.
     */
    public static final String STATUS_FAIL = "3";
    
	/**
	 * 锁定.
	 */
    public static final String LOCK_VAL = "1";
	
	/**
	 * 解锁.
	 */
    public static final String UNLOCAL_VAL = "0";
    
    /**
     * 定时任务.
     */
    public static final String TASK_TYPE_TIME = "0";
    
    /**
     * 一次性任务.
     */
    public static final String TASK_TYPE_ONE = "1";
    
    /**
     * 任务启动.
     */
    public static final String TASK_START = "0";
    
    /**
     * 任务停止.
     */
    public static final String TASK_STOP = "1";
    
    /**
     * 默认组.
     */
    public static final String DEFAULT_MODULAR = "DEFAULT";
}
