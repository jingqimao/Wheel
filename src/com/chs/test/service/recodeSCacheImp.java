package com.chs.test.service;


import java.util.HashMap;
import java.util.Map;

import com.chs.wheel.core.SCacheBase;
import com.chs.wheel.core.SCacheObject;

public class recodeSCacheImp extends SCacheBase implements recodeSCache{
	
	public recodeSCacheImp(){
		Map<String,Object> skey=new HashMap<String,Object>();
		skey.put("tag", "String");
		_INIT("test1","recode","id","30s",skey,"15s");
	}

	@Override
	public Map<String, Object> getRecode(int id){
		
		SCacheObject obj=_GET(id);
		
		return obj.data;
	}

	@Override
	public boolean setRecodeName(int id, String tag){
		
		SCacheObject obj=_GET(id);
		
		obj.data.put("tag", tag);
		obj.Set();
		
		return true;
	}
	
}
