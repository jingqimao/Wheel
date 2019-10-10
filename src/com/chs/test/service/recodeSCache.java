package com.chs.test.service;

import java.util.Map;

public interface recodeSCache {
	
	public Map<String,Object> getRecode(int id);
	
	public boolean setRecodeName(int id,String name);
}
