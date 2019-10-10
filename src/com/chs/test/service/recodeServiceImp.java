package com.chs.test.service;

import com.chs.wheel.core.Auto;
import com.chs.wheel.core.BaseServiceImp;

import java.util.List;
import java.util.Map;

import com.chs.test.dao.recodeDao;

public class recodeServiceImp extends BaseServiceImp implements recodeService{


	@Auto
	public recodeDao recode_Dao;

	//Builder-Head
	@Override
	public int addrecode(String tag) throws Exception {
		return recode_Dao.addrecode(null,tag);
	}

	@Override
	public boolean updrecode(int id,String tag) throws Exception {
		return recode_Dao.updrecode(null,id,tag);
	}

	@Override
	public boolean delrecode(int id) throws Exception {
		return recode_Dao.delrecode(null,id);
	}

	@Override
	public List<Map<String,Object>> selrecode(int id) throws Exception {
		return recode_Dao.selrecode(null,id);
	}

	//Builder-Tail


}