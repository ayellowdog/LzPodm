/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inspur.podm.common.persistence.entity.Chassis;

/**
 * @ClassName: MyChassisDao
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月27日 下午5:08:36
 */
public interface MyChassisDao extends JpaRepository<Chassis,Long>{

}

