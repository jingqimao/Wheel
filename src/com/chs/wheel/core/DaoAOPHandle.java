package com.chs.wheel.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.chs.wheel.utils.DBUtils;

/**
 * 
 * <p>DaoAOPHandle</p>
 * Description: DAO接口代理类
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:24
 *
 */
public class DaoAOPHandle implements InvocationHandler{
	
	public Object o;
	
    public DaoAOPHandle(Object obj) {
        this.o=obj;
    }
    
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Object ret = null;
		
		Connection conn=null;
		
		String DATASOURCE=null;
		String CONNECTION_TAG=Wheel.CONNECTION_TAG.get();
		
		Field[] fields=o.getClass().getDeclaredFields();
		for(Field field:fields) {
			if(field.getName().equals("DATASOURCE")) {
				try {
					DATASOURCE=field.get(o).toString();
					break;
				} catch (Exception e) {
					WheelLogger.get(this).error("error",e);
				}
			}
		}
		
		if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
			for(Connection conn_item:Wheel.connectionMapping.get(CONNECTION_TAG)) {
				if(DATASOURCE.equals(conn_item.getCatalog())) {
					conn=conn_item;
					break;
				}
			}
			if(conn==null)conn=DBUtils.getConnection(DATASOURCE);
		}else {
			conn=DBUtils.getConnection(DATASOURCE);
		}
		
		if(conn!=null) {
			conn.setAutoCommit(false);
			
			Parameter[] pas= method.getParameters();
			for(int i=0;i<pas.length;i++) {
				AutoConn ac = pas[i].getAnnotation(AutoConn.class);
				if(ac!=null) {
					args[i]=conn;
					break;
				}
			}
			
			if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
				if(!Wheel.connectionMapping.get(CONNECTION_TAG).contains(conn))Wheel.connectionMapping.get(CONNECTION_TAG).add(conn);
			}else {
				List<Connection> list=new ArrayList<Connection>();
				list.add(conn);
				Wheel.connectionMapping.put(CONNECTION_TAG,list);
			}
			try {
				ret=method.invoke(o, args);
				//conn.commit();
			} catch (Exception e) {
				throw ((InvocationTargetException)e).getTargetException();
			}
		}
		return ret;
	}
	
	
	public static Object getOne(Object obj) {
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(),obj.getClass().getInterfaces(), new DaoAOPHandle(obj));
	}
}
