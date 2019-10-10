package com.chs.test.service;

import com.chs.test.dao.recodeDao;
import com.chs.test.dao.userDao;
import com.chs.wheel.core.Auto;

public class recodeServiceImp2 implements recodeService2{
	
	@Auto
	public userDao user_Dao;
	
	@Auto
	public recodeDao recode_Dao;

	@Override
	public void test() throws Exception {
		System.out.println(user_Dao.getAllUser(null, "test", "id", 1, 3).data);
	}
	
}
