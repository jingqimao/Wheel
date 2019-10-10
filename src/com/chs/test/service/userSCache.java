package com.chs.test.service;

import java.util.Map;

/**
 * Propeller made
 * 
 * db:test1
 * tabl:user
 * mkey:id
 * skey:account##String@@name##String@@status##int
 * cacheTime:30s
 * checkTime:10s
 */
public interface userSCache {
	
	/**
	 * Propeller made
	 * 
	 * name:getUser
	 * type:get/getAll/set/cus
	 */
	public Map<String,Object> getUser(int id);
	
	/**
	 * Propeller made
	 * 
	 * name:setUserName
	 * type:get/getAll/set/cus
	 * set:name
	 */
	public boolean setUserName(int id,String name);
}
