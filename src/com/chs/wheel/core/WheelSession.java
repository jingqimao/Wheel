package com.chs.wheel.core;

import java.util.HashMap;
import java.util.Map;

import com.chs.wheel.utils.TimerUtils;

public class WheelSession {

	private String sessionId;
	private Map<String,Object> allData=new HashMap<String,Object>();
	private long createTime=TimerUtils.getNowTime();
	public boolean isUpdate=false;
	private long updateTime=TimerUtils.getNowTime();
	private long overTime=1000*60*30;
	
	public WheelSession(String sessionId) {
		this.sessionId=sessionId;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public Object getData(String key) {
		return allData.get(key);
	}
	
	public Object getAttribute(String key) {
		return this.getData(key);
	}
	
	public Object setData(String key,Object data) {
		return allData.put(key, data);
	}
	
	public Object setAttribute(String key,Object data) {
		return this.setData(key,data);
	}
	
	public Object removeData(String key) {
		return allData.remove(key);
	}
	
	public Object removeAttribute(String key) {
		return this.removeData(key);
	}
	
	public void setOverTime(long overTime) {
		this.overTime=overTime;
	}
	
	public boolean isOverTime() {
		if(TimerUtils.getNowTime()>this.updateTime+this.overTime) {
			return true;
		}
		return false;
	}
	
	public void updateTime() {
		this.updateTime=TimerUtils.getNowTime();
	}
}
