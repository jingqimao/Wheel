package com.chs.test.service;

import java.util.Map;

import com.chs.wheel.core.AcheService;
import com.chs.wheel.core.PageRes;

public interface userService{
	
	public void newUser(String account,String password,String name,int status) throws Exception;
	
	@AcheService(set="3:10m")
	public PageRes getSomeUser(String account,int page,int pageSize) throws Exception;
	
	public Map<String, Object> getTT() throws Exception;
	
	public boolean setTT(String name) throws Exception;
	
	public Map<String, Object> test() throws Exception;
	
}
