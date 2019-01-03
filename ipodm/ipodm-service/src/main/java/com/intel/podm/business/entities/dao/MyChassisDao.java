/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.business.entities.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.Chassis;

/**
 * @ClassName: MyChassisDao
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月27日 下午5:08:36
 */
@Repository
public interface MyChassisDao extends JpaRepository<Chassis,Long>{

}

