package com.chs.test.service;


import java.util.HashMap;
import java.util.Map;

import com.chs.wheel.core.SCacheBase;
import com.chs.wheel.core.SCacheObject;

public class userSCacheImp extends SCacheBase implements userSCache{
	
	public userSCacheImp(){
		Map<String,Object> skey=new HashMap<String,Object>();
		skey.put("account", "String");
		skey.put("name", "String");
		skey.put("status", "int");
		_INIT("test1","user","id","30s",skey,"10s");
	}

	@Override
	public Map<String, Object> getUser(int id){
		
		SCacheObject obj=_GET(id);
		
		return obj.data;
	}

	@Override
	public boolean setUserName(int id, String name){
		
		SCacheObject obj=_GET(id);
		
		obj.data.put("name", name);
		obj.Set();
		
		return true;
	}
	
}
