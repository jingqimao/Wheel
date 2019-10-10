package com.chs.wheel.utils;

import java.util.List;
import java.util.Map;

public class JSONItem {
	
	public String json;
	
	public boolean isArr=false;
	public Map<String, Object> obj;
	public List<Object> arr;
	
	public JSONItem(String json,boolean isArr) {
		if(JSONUtils.isJson(json)) {
			this.json=json;
			this.isArr=isArr;
			if(isArr) {
				arr=JSONUtils.getListObject(json);
			}else {
				obj=JSONUtils.getMap(json);
			}
		}else {
			new RuntimeException("错误，非json格式！");
		}
	}
	
	public Object get(String key) {
		return obj.get(key);
	}
	
	public Object get(int index) {
		return arr.get(index);
	}
	
	public JSONItem get(String key,boolean isArr) {
		return new JSONItem(obj.get(key).toString(),isArr);
	}
	
	public JSONItem get(int index,boolean isArr) {
		return new JSONItem(arr.get(index).toString(),isArr);
	}
}
