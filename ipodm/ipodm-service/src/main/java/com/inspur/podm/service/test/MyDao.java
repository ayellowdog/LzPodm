/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

/**
 * @ClassName: MyDao
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月23日 下午3:31:30
 */
@Repository
public class MyDao {
	@PersistenceContext
    private EntityManager entityManager;
}

