package com.chs.wheel.utils;

import java.util.ArrayList;
import java.util.List;

public class JSONsList {

	public List<Object> list= new ArrayList<Object>();
	
	public JSONsList add(Object value) {
		list.add(value);
		return this;
	}
	
	public List<Object> get() {
		return list;
	}
	
	public String getJSON() {
		return JSONUtils.toJsonString(list,false);
	}
}
