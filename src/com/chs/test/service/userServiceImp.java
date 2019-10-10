package com.chs.test.service;

import java.util.Map;

import com.chs.test.dao.recodeDao;
import com.chs.test.dao.userDao;
import com.chs.wheel.core.Auto;
import com.chs.wheel.core.PageRes;

public class userServiceImp implements userService{
	
	@Auto
	public userDao user_Dao;
	
	@Auto
	public recodeDao recode_Dao;
	
	@Auto
	public recodeService2 recode_Service;
	
	@Override
	public void newUser(String account,String password,String name,int status) throws Exception{
		System.out.println(user_Dao.addUser(null, account, password, name, status));
		System.out.println(recode_Dao.addrecode(null, "xxx"));
		System.out.println(user_Dao.updUser(null, 3, "qwer", "ccc", 1));
		System.out.println(user_Dao.getUserById(null, 20));
		System.out.println(user_Dao.getAllUser(null, account, "id", 1, 5).data.toString());
	}

	@Override
	public PageRes getSomeUser(String account, int page, int pageSize) throws Exception {
		//recode_Service.test();
		//user_Dao.addUser(null, "xxx", "1223", "xc", 1);
		return user_Dao.getAllUser(null, account, "id", page, pageSize);
	}

	@Override
	public Map<String, Object> getTT() throws Exception {
		Map<String, Object> m=user_Dao.getTT(null);
		user_Dao.getTT(null);
		user_Dao.getUserById(null, 1);
		return m;
	}

	@Override
	public boolean setTT(String name) throws Exception {
		
		return user_Dao.setTT(null, name);
	}

	@Override
	public Map<String, Object> test() throws Exception {
		
		recode_Dao.addrecode(null, "xxx");
		
		throw new Exception("报错！！");
	}
	
}
