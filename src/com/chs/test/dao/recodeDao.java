package com.chs.test.dao;

import java.sql.Connection;
import com.chs.wheel.core.AutoConn;

import java.util.List;
import java.util.Map;

public interface recodeDao {


	public int addrecode(@AutoConn Connection conn,String tag) throws Exception;
	public boolean updrecode(@AutoConn Connection conn,int id,String tag) throws Exception;
	public boolean delrecode(@AutoConn Connection conn,int id) throws Exception;
	public List<Map<String,Object>> selrecode(@AutoConn Connection conn,int id) throws Exception;
}