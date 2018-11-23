/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.common.persistence.handler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.inspur.podm.common.intel.types.Id;

/**
 * @ClassName: IdTypeHandler
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月22日 下午6:05:45
 */
public class IdTypeHandler implements TypeHandler<Id>{

	@Override
	public void setParameter(PreparedStatement ps, int i, Id parameter, JdbcType jdbcType) throws SQLException {
		if(parameter == null) {
			ps.setNull(i, Types.VARCHAR);
		} 
		ps.setString(i, parameter.getValue());
		
	}

	@Override
	public Id getResult(ResultSet rs, String columnName) throws SQLException {
		String columnValue = rs.getString(columnName);
		return Id.id(columnValue);
	}

	@Override
	public Id getResult(ResultSet rs, int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Id getResult(CallableStatement cs, int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}

