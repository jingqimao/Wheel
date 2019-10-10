package com.chs.test.dao;

import java.sql.Connection;

import java.sql.Statement;
import java.util.List;
import java.util.Map;
import com.chs.wheel.utils.DBUtils;
import com.chs.wheel.core.BaseServiceImp;
import com.chs.wheel.core.PageRes;
import com.chs.wheel.core.Wheel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class userDaoImp implements userDao{


	public String DATASOURCE="test1";


	@Override
	public int addUser(Connection conn,String account,String password,String name,int status) throws Exception {
		String sql="insert into user (account,password,name,status) values (?,?,?,?)";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, account);
		ps.setObject(2, password);
		ps.setObject(3, name);
		ps.setObject(4, status);
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
	public int delUser(Connection conn,int id) throws Exception {
		String sql="delete from user where id <= ?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, id);
		return ps.executeUpdate();
	}

	@Override
	public boolean updUser(Connection conn,int id,String account,String name,int status) throws Exception {
		String sql="update user set account = ?,name = ?,status = ? where id = "+id+"";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, account);
		ps.setObject(2, name);
		ps.setObject(3, status);
		int res=ps.executeUpdate();
		if(res!=0){
			return true;
		}
		return false;
	}

	@Override
	public List<Map<String,Object>> getUser(Connection conn,String account,String name) throws Exception {
		String sql="select * from user where account = ? and name = ?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, account);
		ps.setObject(2, name);
		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());
		return list;
	}

	@Override
	public PageRes getAllUser(Connection conn,String account,String rank,int page,int pageSize) throws Exception {
		String sql="select account,name from user where locate(?,account)>0 order by "+rank+" desc limit "+((page-1)*pageSize)+","+pageSize;
		String sqlx="select count(*) as count from user where locate(?,account)>0";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, account);
		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());
		PreparedStatement psx=conn.prepareStatement(sqlx,Statement.RETURN_GENERATED_KEYS);
		psx.setObject(1, account);
		List<Map<String, Object>> listx=DBUtils.getMapByResult(psx.executeQuery());
		int count=Integer.parseInt(listx.get(0).get("count").toString());
		return new PageRes(list,count);
	}

	@Override
	public boolean newUser(Connection conn,String account,String password) throws Exception {
		String sql="instert into user(account,password) value(?,?)";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, account);
		ps.setObject(2, password);
		int res=ps.executeUpdate();
		if(res!=0){
			return true;
		}
		return false;
	}

	@Override
	public List<Map<String,Object>> getUserById(Connection conn,int id) throws Exception {
		String sql="select * from user where id=?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, id);
		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());
		return list;
	}

	@Override
	public Map<String, Object> getTT(Connection conn) throws Exception {
		String sql="select name from tt where id=?";
		String langTag=(String)BaseServiceImp.context.get().getSession().getAttribute(Wheel.UserLanguage_Tag);
		if(langTag!=null){
			if("EN".equals(langTag))sql="select name_EN as 'name' from tt where id=?";
			if("JP".equals(langTag))sql="select name_JP as 'name' from tt where id=?";
		};
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, 1);
		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());
		return list.get(0);
	}

	@Override
	public boolean setTT(Connection conn,String name) throws Exception {
		String sql="update user set name = ? where id = "+1+"";
		String langTag=(String)BaseServiceImp.context.get().getSession().getAttribute(Wheel.UserLanguage_Tag);
		if(langTag!=null){
			if("EN".equals(langTag))sql="update user set name_EN = ? where id = "+1+"";
			if("JP".equals(langTag))sql="update user set name_JP = ? where id = "+1+"";
		};
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, name);
		int res=ps.executeUpdate();
		if(res!=0){
			return true;
		}
		return false;
	}

}