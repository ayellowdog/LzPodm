package com.inspur.podm.common.persistence;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by Wanxian.He on 17/3/23.
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
