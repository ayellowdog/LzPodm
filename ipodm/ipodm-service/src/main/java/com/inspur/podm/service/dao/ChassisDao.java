/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inspur.podm.common.persistence.entity.Chassis;

/**
 * @ClassName: ChassisDao
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月24日 下午2:11:37
 */
@Repository
public interface ChassisDao extends JpaRepository<MyChassis, Long>{

}

