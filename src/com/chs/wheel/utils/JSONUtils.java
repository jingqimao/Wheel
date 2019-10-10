package com.chs.wheel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtils {
	
	
	public static String toJsonString(Map<String,Object> map) {
		return toJsonString(map,false);
	}
	
	@SuppressWarnings("unchecked")
	public static String toJsonString(Map<String,Object> map,boolean block) {
		StringBuilder res=new StringBuilder();
		res.append("{");
		if(block)res.append("\r\n");
		for(String k:map.keySet()) {
			Object obj=map.get(k);
			if(block)res.append("	");
			if(obj instanceof String) {
				res.append("\""+k+"\":\""+obj.toString().replaceAll("\"", "\\\\\"")+"\",");
			}else if(obj instanceof Integer||obj instanceof Long||obj instanceof Float||obj instanceof Double) {
				res.append("\""+k+"\":"+obj+",");
			}else if(obj instanceof Map) {
				res.append("\""+k+"\":"+toJsonString((Map<String,Object>)obj,block)+",");
			}else if(obj instanceof List) {
				res.append("\""+k+"\":"+toJsonString((List<Object>)obj,block)+",");
			}else if(obj==null){
				res.append("\""+k+"\":null,");
			}else {
				res.append("\""+k+"\":\""+obj.toString().replaceAll("\"", "\\\\\"")+"\",");
			}
			if(block)res.append("\r\n");
		}
		if(block) {
			res=new StringBuilder(res.substring(0, res.length()-3));
		}else {
			res=new StringBuilder(res.substring(0, res.length()-1));
		}
		res.append("}");
		return res.toString();
	}
	
	public static String toJsonString(List<Map<String,Object>> list) {
		List<Object> li=new ArrayList<Object>();
		for(Map<String,Object> m:list) {
			li.add(m);
		}
		return toJsonString(li,false);
	}
	
	@SuppressWarnings("unchecked")
	public static String toJsonString(List<Object> list,boolean block) {
		StringBuilder res=new StringBuilder();
		res.append("[");
		if(block)res.append("\r\n");
		for(Object obj:list) {
			if(block)res.append("	");
			if(obj instanceof String) {
				res.append("\""+obj.toString().replaceAll("\"", "\\\\\"")+"\",");
			}else if(obj instanceof Integer||obj instanceof Long||obj instanceof Float||obj instanceof Double) {
				res.append(obj+",");
			}else if(obj instanceof Map) {
				res.append(toJsonString((Map<String,Object>)obj,block)+",");
			}else if(obj instanceof List) {
				res.append(toJsonString((List<Object>)obj,block)+",");
			}else if(obj==null){
				res.append("null,");
			}else {
				res.append("\""+obj.toString().replaceAll("\"", "\\\\\"")+"\",");
			}
			if(block)res.append("\r\n");
		}
		if(list.size()>0) {
			if(block) {
				res=new StringBuilder(res.substring(0, res.length()-3));
			}else {
				res=new StringBuilder(res.substring(0, res.length()-1));
			}
		}
		res.append("]");
		return res.toString();
	}
	
	public static Map<String,Object> getMapByJson(String json) {
		Map<String,Object> res=new HashMap<String,Object>();
		int i=0;
		int step=0;
		Map<String,Object> m=new HashMap<String,Object>();
		List<String> li=new ArrayList<String>();
		while(i<json.length()) {
			char c=json.charAt(i);
			switch (c){
				case '{':
					if(i==0||i>0&&json.charAt(i-1)!='\\') {
						step++;
						m.put(step+"", i);
					}
					break;
				case '}':
					if(i>0&&json.charAt(i-1)!='\\') {
						li.add(step+"-{}-"+m.get(step+"")+"-"+i);
						m.remove(step+"");
						step--;
					}
					break;
				case '[':
					if(i==0||i>0&&json.charAt(i-1)!='\\') {
						step++;
						m.put(step+"", i);
					}
					break;
				case ']':
					if(i>0&&json.charAt(i-1)!='\\') {
						li.add(step+"-[]-"+m.get(step+"")+"-"+i);
						m.remove(step+"");
						step--;
					}
					break;
				case '"':
					if(i>0&&json.charAt(i-1)!='\\') {
						
					}
					break;
				default:break;
			}
			i++;
		}
		for(String k:li) {
			System.out.println(k);
		}
		System.out.println(json);
		return res;
	}
	
	public static Map<String,Object> getMap(String json) {
		JSONObject jo=JSONObject.parseObject(json);
		return jo.getInnerMap();
	}
	
	public static List<Map<String,Object>> getList(String json) {
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		JSONArray arr=JSONArray.parseArray(json);
		for(int i=0;i<arr.size();i++) {
			list.add(((JSONObject)arr.get(i)).getInnerMap());
		}
		return list;
	}
	
	public static List<Object> getListObject(String json) {
		List<Object> list=new ArrayList<Object>();
		JSONArray arr=JSONArray.parseArray(json);
		for(int i=0;i<arr.size();i++) {
			list.add(arr.get(i));
		}
		return list;
	}
	
	public static Map<String,Object> search(List<Map<String,Object>> list,String key,Object val) {
		for(Map<String,Object> m:list) {
			if(m.get(key).equals(val)) {
				return m;
			}
		}
		return null;
	}
	
	public static JSONsMAP newMap() {
		return new JSONsMAP();
	}
	
	public static JSONsList newList() {
		return new JSONsList();
	}
	
	public static JSONsListMAP newListMap() {
		return new JSONsListMAP();
	}
	
	public static boolean isJson(String json) {
		try {
			JSONObject.parse(json);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
}
