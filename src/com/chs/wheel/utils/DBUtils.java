package com.chs.wheel.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.chs.wheel.core.PageRes;
import com.chs.wheel.core.Wheel;

public class DBUtils {
	
	/**
	 * 
	 * <p>getConnection</p>
	 * Description: 从连接池获取数据接口
	 * 
	 * @author chenhaishan
	 * @date 2018-07-06 15:37
	 *
	 * @param db
	 * @return
	 */
	public static Connection getConnection(String db) {
		DataSource ds = null;
		Connection conn = null;
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/"+db);
			conn = ds.getConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 
	 * <p>getConnection</p>
	 * Description: JDBC获取数据接口
	 * 
	 * @author chenhaishan
	 * @date 2018-11-01 14:09
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 */
	public static Connection getConnection(String driver,String url,String user,String password) {
		
		Connection conn=null;
		try {
            Class.forName(driver);
            conn=DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return conn;
	}
	
	/**
	 * 
	 * <p>rollBackConnection</p>
	 * Description: 回滚数据事务
	 * 
	 * @author chenhaishan
	 * @date 2019-10-09 11:29
	 *
	 * @param conn
	 */
	public static void rollBackConnection(Connection conn) {
		
		try {
			if(conn!=null)conn.rollback();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * <p>closeConnection</p>
	 * Description: 关闭数据接口
	 * 
	 * @author chenhaishan
	 * @date 2018-11-01 14:10
	 *
	 * @param conn
	 */
	public static void closeConnection(Connection conn) {
		
		try {
			if(conn!=null)conn.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * <p>getResult</p>
	 * Description: 数据合集转为实体类列表
	 * 
	 * @author chenhaishan
	 * @date 2018-07-06 15:37
	 *
	 * @param res
	 * @param cla
	 * @return
	 */
	public static <T> List<T> getBeansByResult(ResultSet res,Class<T> cla){
		List<T> result=new ArrayList<T>();
		try {
			ResultSetMetaData md = res.getMetaData(); 
	        int columnCount = md.getColumnCount();
			while(res.next()) {
				T obj=cla.newInstance();
				for(int i=1;i<=columnCount;i++) {
					Field field=cla.getDeclaredField(md.getColumnName(i));
					if(field!=null) {
						field.setAccessible(true);
						if(res.getObject(i).getClass()==Timestamp.class) {
							field.set(obj, res.getObject(i).toString());
						}else {
							field.set(obj, res.getObject(i));
						}
					}
				}
				result.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * <p>getMapbyResult</p>
	 * Description: 数据合集转映射列表
	 * 
	 * @author chenhaishan
	 * @date 2018-07-06 15:47
	 *
	 * @param res
	 * @return
	 */
	public static List<Map<String,Object>> getMapByResult(ResultSet res){
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		try {
			ResultSetMetaData md = res.getMetaData(); 
	        int columnCount = md.getColumnCount();
			while(res.next()) {
				Map<String,Object> map=new HashMap<String,Object>();
				for(int i=1;i<=columnCount;i++) {
					map.put(md.getColumnLabel(i), res.getObject(i));
				}
				result.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * <p>addData</p>
	 * Description: 万能方法：增
	 * 
	 * @author chenhaishan
	 * @date 2019-03-20 14:34
	 *
	 * @param DATASOURCE
	 * @param table
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static boolean addData(String DATASOURCE,String table,Map<String,Object> kv) throws Exception{
		
		String CONNECTION_TAG=Wheel.CONNECTION_TAG.get();
		
		Connection conn=DBUtils.getConnection(DATASOURCE);
		
		List<String> keys=new ArrayList<String>();
		String ks="";
		String kss="";
		for(String k:kv.keySet()) {
			if(ks.length()==0) {
				ks+=k;
				kss+="?";
			}else {
				ks+=","+k;
				kss+=",?";
			}
			keys.add(k);
		}
		
		String sql="insert into "+table+" ("+ks+") values ("+kss+")";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		int n=1;
		for(String k:keys) {
			ps.setObject(n++, kv.get(k));
		}
		
		int res=ps.executeUpdate();
		
		if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
			Wheel.connectionMapping.get(CONNECTION_TAG).add(conn);
		}else {
			List<Connection> list=new ArrayList<Connection>();
			list.add(conn);
			Wheel.connectionMapping.put(CONNECTION_TAG,list);
		}
		
		if(res>-1) {
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * 
	 * <p>delData</p>
	 * Description: 万能方法：删
	 * 
	 * @author chenhaishan
	 * @date 2019-03-20 14:35
	 *
	 * @param DATASOURCE
	 * @param table
	 * @param where
	 * @param where_val
	 * @return
	 * @throws Exception
	 */
	public static boolean delData(String DATASOURCE,String table,String where,Object where_val) throws Exception{
		
		String CONNECTION_TAG=Wheel.CONNECTION_TAG.get();
		
		Connection conn=DBUtils.getConnection(DATASOURCE);
		
		String sql="delete from "+table+" where "+where+"=?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		ps.setObject(1, where_val);
		
		int res=ps.executeUpdate();
		
		if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
			Wheel.connectionMapping.get(CONNECTION_TAG).add(conn);
		}else {
			List<Connection> list=new ArrayList<Connection>();
			list.add(conn);
			Wheel.connectionMapping.put(CONNECTION_TAG,list);
		}
		
		if(res>-1) {
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * 
	 * <p>ClearData</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-07-08 15:08
	 *
	 * @param DATASOURCE
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public static boolean ClearData(String DATASOURCE,String table) throws Exception{
		
		String CONNECTION_TAG=Wheel.CONNECTION_TAG.get();
		
		Connection conn=DBUtils.getConnection(DATASOURCE);
		
		String sql="truncate table "+table;
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		
		int res=ps.executeUpdate();
		
		if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
			Wheel.connectionMapping.get(CONNECTION_TAG).add(conn);
		}else {
			List<Connection> list=new ArrayList<Connection>();
			list.add(conn);
			Wheel.connectionMapping.put(CONNECTION_TAG,list);
		}
		
		if(res>-1) {
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * 
	 * <p>updData</p>
	 * Description: 万能方法：改
	 * 
	 * @author chenhaishan
	 * @date 2019-03-20 14:35
	 *
	 * @param DATASOURCE
	 * @param table
	 * @param key
	 * @param value
	 * @param where
	 * @param where_val
	 * @return
	 * @throws Exception
	 */
	public static boolean updData(String DATASOURCE,String table,Map<String,String> kv,String where,Object where_val) throws Exception{
		
		String CONNECTION_TAG=Wheel.CONNECTION_TAG.get();
		
		Connection conn=DBUtils.getConnection(DATASOURCE);
		
		List<String> keys=new ArrayList<String>();
		String ks="";
		for(String k:kv.keySet()) {
			if(ks.length()==0) {
				ks+=k+"=?";
			}else {
				ks+=","+k+"=?";
			}
			keys.add(k);
		}
		
		String sql="update "+table+" set "+ks+" where "+where+"=?";
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		int n=1;
		for(String k:keys) {
			ps.setObject(n++, kv.get(k));
		}
		ps.setObject(n++, where_val);
		
		int res=ps.executeUpdate();
		
		if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
			Wheel.connectionMapping.get(CONNECTION_TAG).add(conn);
		}else {
			List<Connection> list=new ArrayList<Connection>();
			list.add(conn);
			Wheel.connectionMapping.put(CONNECTION_TAG,list);
		}
		
		if(res>-1) {
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * 
	 * <p>getData</p>
	 * Description: 万能方法：查
	 * 
	 * @author chenhaishan
	 * @date 2019-03-20 14:35
	 *
	 * @param DATASOURCE
	 * @param table
	 * @param mkey
	 * @param page
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static PageRes getData(String DATASOURCE,String table,String mkey,int page,int size) throws Exception{
		
		String CONNECTION_TAG=Wheel.CONNECTION_TAG.get();
		
		Connection conn=DBUtils.getConnection(DATASOURCE);
		
		String sql="select * from "+table+"order by "+mkey+" desc limit "+((page-1)*size)+","+size;
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		
		String sqlx="select count(*) as count from "+table;
		PreparedStatement psx=conn.prepareStatement(sqlx,Statement.RETURN_GENERATED_KEYS);
		
		List<Map<String, Object>> data=getMapByResult(ps.executeQuery());
		List<Map<String, Object>> datax=DBUtils.getMapByResult(psx.executeQuery());
		
		if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
			Wheel.connectionMapping.get(CONNECTION_TAG).add(conn);
		}else {
			List<Connection> list=new ArrayList<Connection>();
			list.add(conn);
			Wheel.connectionMapping.put(CONNECTION_TAG,list);
		}
		
		int count=Integer.parseInt(datax.get(0).get("count").toString());
		return new PageRes(data,count);
		
	}
	
	
	/**
	 * 
	 * <p>sqlWhereIn</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-05-30 11:15
	 *
	 * @param arr
	 * @return
	 */
	public static String sqlWhereIn(String key,List<Object> arr) {
		String sql=" and "+key+" in (";
		if(arr.size()>0) {
			for(int i=0;i<arr.size();i++) {
				if(i==0) {
					sql+=arr.get(i).toString();
				}else {
					sql+=","+arr.get(i).toString();
				}
			}
			return sql+=") ";
		}else {
			return "";
		}
	}
	
	/**
	 * 
	 * <p>sqlWhereOr</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-05-30 11:23
	 *
	 * @param arr
	 * @return
	 */
	public static String sqlWhereOr(List<List<Map<String,Object>>> arr) {
		String sql=" and (";
		if(arr.size()>0) {
			for(int i=0;i<arr.size();i++) {
				if(i>0)sql+=" or ";
				for(int j=0;j<arr.get(i).size();j++) {
					if(j>0)sql+=" and ";
					sql+=arr.get(i).get(j).get("key")+" "+arr.get(i).get(j).get("eq")+" "+arr.get(i).get(j).get("val");
				}
			}
			return sql+=") ";
		}else {
			return "";
		}
	}
}
