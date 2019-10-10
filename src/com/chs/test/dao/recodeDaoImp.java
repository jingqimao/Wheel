package com.chs.test.dao;

import java.sql.Connection;

import java.sql.Statement;
import java.util.List;
import java.util.Map;
import com.chs.wheel.utils.DBUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class recodeDaoImp implements recodeDao{


	public String DATASOURCE="test1";


	@Override
	public int addrecode(Connection conn,String tag) throws Exception {
		String sql="insert into recode (tag) values (?)";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, tag);
		int res=ps.executeUpdate();
		if(res!=-1){
			ResultSet rs=ps.getGeneratedKeys();
			int id = -1;
			if(rs.next()) {
				id = rs.getInt(1);
			}
			return id;
		}
		return -1;
	}

	@Override
	public boolean updrecode(Connection conn,int id,String tag) throws Exception {
		String sql="update recode set tag = ? where id = "+id+"";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, tag);
		int res=ps.executeUpdate();
		if(res!=0){
			return true;
		}
		return false;
	}

	@Override
	public boolean delrecode(Connection conn,int id) throws Exception {
		String sql="delete from recode where id = ?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, id);
		int res=ps.executeUpdate();
		if(res!=0){
			return true;
		}
		return false;
	}

	@Override
	public List<Map<String,Object>> selrecode(Connection conn,int id) throws Exception {
		String sql="select * from recode where id = ?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, id);
		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());
		return list;
	}

}