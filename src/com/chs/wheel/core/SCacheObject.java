package com.chs.wheel.core;

import java.util.Map;

import com.chs.wheel.utils.TimerUtils;

public class SCacheObject {
	
	public Map<String,Object> data;
	private long time;
	public boolean isSet=false;
	
	public SCacheObject(Map<String,Object> data,long time) {
		this.data=data;
		this.time=time;
	}
	
	public boolean isOverTime(long cacheTime) {
		return TimerUtils.getNow().getTime()>this.time+cacheTime;
	}
	
	public void updateCache() {
		this.time=TimerUtils.getNow().getTime();
	}
	
	public void Set() {
		if(!this.isSet)this.isSet=true;
	}
}
