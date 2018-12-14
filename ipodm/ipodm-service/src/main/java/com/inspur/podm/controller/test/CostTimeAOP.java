/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.controller.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName: CostTimeAOP
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月13日 下午4:12:27
 */
@Aspect
@Component
public class CostTimeAOP {
    
    final static Logger log = LoggerFactory.getLogger(CostTimeAOP.class);

    @Pointcut("@annotation(com.inspur.podm.controller.test.CostTime)")
    public void costTimePointCut(){}

    @Around("costTimePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        logCostTime(point, time);
        return result;
    }


    private void logCostTime(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        System.out.println("~~~耗时统计：class:"+className+" method:"+methodName + " cost:"+time+"ms");
//        log.info("class:"+className+" method:"+methodName + " cost:"+time+"ms");
    }
}


