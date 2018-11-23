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

/**
 * @ClassName: URITypeHandler
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月22日 下午1:46:07
 */
public class URITypeHandler implements TypeHandler<URI>{

	@Override
	public void setParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
		if(parameter == null) {
			ps.setNull(i, Types.VARCHAR);
		} 
		ps.setString(i, "this is a test by lc");
		
	}

	@Override
	public URI getResult(ResultSet rs, String columnName) throws SQLException {
		String columnValue = rs.getString(columnName);
		try {
			return new URI(columnValue);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public URI getResult(ResultSet rs, int columnIndex) throws SQLException {
		String columnValue = rs.getString(columnIndex);
		try {
			return new URI(columnValue);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public URI getResult(CallableStatement cs, int columnIndex) throws SQLException {
		  String columnValue = cs.getString(columnIndex);
		  try {
				return new URI(columnValue);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return null;
	}

}

