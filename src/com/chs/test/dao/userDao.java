package com.chs.test.dao;

import java.sql.Connection;
import com.chs.wheel.core.AutoConn;

import java.util.List;
import java.util.Map;
import com.chs.wheel.core.PageRes;

public interface userDao {


	public int addUser(@AutoConn Connection conn,String account,String password,String name,int status) throws Exception;
	public int delUser(@AutoConn Connection conn,int id) throws Exception;
	public boolean updUser(@AutoConn Connection conn,int id,String account,String name,int status) throws Exception;
	public List<Map<String,Object>> getUser(@AutoConn Connection conn,String account,String name) throws Exception;
	public PageRes getAllUser(@AutoConn Connection conn,String account,String rank,int page,int pageSize) throws Exception;
	public boolean newUser(@AutoConn Connection conn,String account,String password) throws Exception;
	public List<Map<String,Object>> getUserById(@AutoConn Connection conn,int id) throws Exception;
	
	public Map<String,Object> getTT(@AutoConn Connection conn) throws Exception;
	public boolean setTT(@AutoConn Connection conn,String name) throws Exception;
}