package com.chs.wheel.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.chs.wheel.utils.IDUtils;
import com.chs.wheel.utils.MD5Utils;
import com.chs.wheel.utils.TimerUtils;

/**
 * 
 * <p>ServiceAOPHandle</p>
 * Description: 服务接口代理类
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:24
 *
 */
public class ServiceAOPHandle implements InvocationHandler{
	
	private Object o;
	
    public ServiceAOPHandle(Object obj) {
        this.o=obj;
    }
    
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Object ret = null;
		
		AcheService as=method.getAnnotation(AcheService.class);
		String ak=null;
		boolean ache=false;
		if(as!=null) {
			String set=as.set();
			int num=0;
			long time=0;
			if(set.contains(":")) {
				num=Integer.parseInt(set.split(":")[0]);
				time=TimerUtils.getTimeSize(set.split(":")[1]);
			}else {
				time=TimerUtils.getTimeSize(set.split(":")[1]);
			}
			String ar="";
			if(args!=null)for(Object arg:args) {
				ar+=arg==null?"null":arg.toString();
			}
			String langTag=(String)BaseServiceImp.context.get().getSession().getData(Wheel.UserLanguage_Tag);
			ak=o.getClass().getName()+"--"+method.getName()+(args!=null?"--"+MD5Utils.toString(ar):"")+(langTag!=null?"--"+langTag:"");
			if(Wheel.acheService.get(ak)==null) {
				Wheel.acheService.put(ak,new HashMap<String,Object>());
				Wheel.acheService.get(ak).put("stTime",System.currentTimeMillis());
				Wheel.acheService.get(ak).put("time",time);
				Wheel.acheService.get(ak).put("num",0);
				Wheel.acheService.get(ak).put("tnum",num);
			}
			if(Wheel.acheService.get(ak).get("acheTime")!=null) {
				if(System.currentTimeMillis()-Long.parseLong(Wheel.acheService.get(ak).get("acheTime").toString())<=time) {
					return Wheel.acheService.get(ak).get("data");
				}else {
					Wheel.acheService.get(ak).remove("acheTime");
					Wheel.acheService.get(ak).put("num",0);
				}
			}
			if(Wheel.acheService.get(ak).get("acheTime")==null){
				Wheel.acheService.get(ak).put("num",Integer.parseInt(Wheel.acheService.get(ak).get("num").toString())+1);
				if(Integer.parseInt(Wheel.acheService.get(ak).get("num").toString())>=num) {
					Wheel.acheService.get(ak).put("acheTime",System.currentTimeMillis());
					ache=true;
				}
			}
		}
		
		String CONNECTION_TAG=IDUtils.getID()+"";
		
		try {
			Wheel.CONNECTION_TAG.set(CONNECTION_TAG);
			ret=method.invoke(o, args);
			if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
				for(Connection conn:Wheel.connectionMapping.get(CONNECTION_TAG)) {
					try {
						conn.commit();
					} catch (SQLException e) {
						WheelLogger.get(this).error("error",e);
					}
				}
			}
		} catch (Exception e) {
			if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
				
				for(Connection conn:Wheel.connectionMapping.get(CONNECTION_TAG)) {
					try {
						conn.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
			if(e.getClass()== InvocationTargetException.class) {
				throw ((InvocationTargetException)e).getTargetException();
			}else {
				WheelLogger.get(this).error("error",e);
			}
		} finally {
			if(Wheel.connectionMapping.get(CONNECTION_TAG)!=null) {
				for(Connection conn:Wheel.connectionMapping.get(CONNECTION_TAG)) {
					try {
						conn.close();
					} catch (SQLException e) {
						WheelLogger.get(this).error("error",e);
					}
				}
				Wheel.serviceMapping.remove(CONNECTION_TAG);
			}
		}
		
		if(ache) {
			Wheel.acheService.get(ak).put("data",ret);
		}
		
		return ret;
	}
	
	
	public static Object getOne(Object obj) {
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(),obj.getClass().getInterfaces(), new ServiceAOPHandle(obj));
	}
}
