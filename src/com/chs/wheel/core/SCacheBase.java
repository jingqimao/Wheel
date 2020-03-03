package com.chs.wheel.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chs.wheel.utils.DBUtils;
import com.chs.wheel.utils.TimerUtils;

public class SCacheBase {
	
	public Map<Object,SCacheObject> Cache=new HashMap<Object,SCacheObject>();
	
	public String db,table,mkey,getsql,setsql;
	public long cacheTime;
	public Map<String,Object> skey;
	
	/**
	 * 
	 * <p>_INIT</p>
	 * Description: 初始化缓存
	 * 
	 * @author chenhaishan
	 * @date 2019-10-09 11:17
	 *
	 * @param db
	 * @param table
	 * @param mkey
	 * @param cacheTime
	 * @param skey
	 * @param checkTime
	 */
	protected void _INIT(String db,String table,String mkey,String cacheTime,Map<String,Object> skey,String checkTime){
		this.db=db;
		this.table=table;
		this.mkey=mkey;
		this.skey=skey;
		this.cacheTime=TimerUtils.getTimeSize(cacheTime);
		
		String keylist="";
		String setlist="";
		for(String k:skey.keySet()){
			if(keylist.length()==0) {
				keylist+="`"+k+"`";
				setlist+="`"+k+"`"+"=?";
			}else {
				keylist+=","+"`"+k+"`";
				setlist+=","+"`"+k+"`"+"=?";
			}
		}
		this.getsql="select "+keylist+" from "+"`"+this.table+"`"+" where "+"`"+this.mkey.split("#")[0]+"`"+" = ?";
		this.setsql="update "+this.table+" set "+setlist+" where "+"`"+this.mkey.split("#")[0]+"`"+" = ?";
		
		TimerUtils.TimeTask(TimerUtils.getTimeSize(checkTime), new Runnable() {
			
			@Override
			public void run() {
				
				if(Cache.size()>0)_OVER();
			}
			
		}, 1);
	}
	
	/**
	 * 
	 * <p>_OVER</p>
	 * Description: 缓存结束时固化数据
	 * 
	 * @author chenhaishan
	 * @date 2019-10-09 11:16
	 *
	 */
	protected void _OVER(){
		Connection conn=null;
		
		for(Object key:Cache.keySet()) {
			if(Cache.get(key).isOverTime(cacheTime)) {
				if(Cache.get(key).isSet) {
					try {
						if(conn==null) {
							conn=DBUtils.getConnection(db);
							conn.setAutoCommit(false);
						}
						PreparedStatement ps=conn.prepareStatement(setsql,Statement.RETURN_GENERATED_KEYS);
						int n=1;
						Map<String,Object> item=Cache.get(key).data;
						for(String k:skey.keySet()) {
							ps.setObject(n++, item.get(k));
						}
						ps.setObject(n++, key);
						int res=ps.executeUpdate();
						if(res!=0){
							conn.commit();
						}
					}catch(Exception e) {
						WheelLogger.get(this).error("error",e);
					}
				}
				Cache.remove(key);
			}
		}
		
		if(conn!=null)DBUtils.closeConnection(conn);
		
	}
	
	/**
	 * 
	 * <p>_GET</p>
	 * Description: 获取缓存操作对象
	 * 
	 * @author chenhaishan
	 * @date 2019-10-09 11:15
	 *
	 * @param key
	 * @return
	 */
	protected SCacheObject _GET(Object key){
		if(Cache.containsKey(key)) {
			return Cache.get(key);
		}else {
			Connection conn=DBUtils.getConnection(db);
			
			try {
				conn.setAutoCommit(false);
				PreparedStatement ps=conn.prepareStatement(getsql,Statement.RETURN_GENERATED_KEYS);
				ps.setObject(1, key);
				List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());
				if(list.size()>0)Cache.put(key, new SCacheObject(list.get(0),TimerUtils.getNow().getTime()));
				conn.commit();
				
				return Cache.get(key);
			}catch(Exception e) {
				WheelLogger.get(this).error("error",e);
			}finally{
				DBUtils.closeConnection(conn);
			}
			return null;
		}
		
	}

}
