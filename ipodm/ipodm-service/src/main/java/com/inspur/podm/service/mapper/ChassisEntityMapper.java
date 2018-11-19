/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.inspur.podm.api.entity.ChassisEntity;
import com.inspur.podm.common.persistence.BaseMapper;

/**
 * @ClassName: ChassisEntityMapper
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月16日 上午11:13:14
 */
public interface ChassisEntityMapper extends BaseMapper<ChassisEntity> {
	public List<ChassisEntity> getChassissById(@Param("id")int id);
}

