package com.chs.wheel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONsListMAP {

	public List<Map<String,Object>> listmap= new ArrayList<Map<String,Object>>();
	
	public JSONsListMAP add(Map<String,Object> map) {
		listmap.add(map);
		return this;
	}
	
	public List<Map<String,Object>> get() {
		return listmap;
	}
	
	public String getJSON() {
		return JSONUtils.toJsonString(listmap);
	}
}
