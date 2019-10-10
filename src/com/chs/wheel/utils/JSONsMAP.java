package com.chs.wheel.utils;

import java.util.HashMap;
import java.util.Map;

public class JSONsMAP {

	public Map<String,Object> map= new HashMap<String,Object>();
	
	public JSONsMAP put(String key,Object value) {
		map.put(key, value);
		return this;
	}
	
	public Map<String,Object> get() {
		return map;
	}
	
	public String getJSON() {
		return JSONUtils.toJsonString(map);
	}
}
