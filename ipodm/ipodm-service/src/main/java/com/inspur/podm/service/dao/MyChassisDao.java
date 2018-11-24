/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

/**
 * @ClassName: MyChassisDao
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月24日 下午3:51:10
 */
@Repository
public class MyChassisDao {
    @PersistenceContext
    protected EntityManager entityManager;
    public List<MyChassis> getChassisById(long id) {
    	TypedQuery<MyChassis> query = entityManager
                .createQuery("SELECT e FROM " + "MyChassis" + " e WHERE e.id = :id", MyChassis.class);
            query.setParameter("id", id);
            return query.getResultList();
    }
}

