package com.chs.test.service;

import java.util.List;
import java.util.Map;

public interface recodeService {


	//Builder-Head
	public int addrecode(String tag) throws Exception;
	public boolean updrecode(int id,String tag) throws Exception;
	public boolean delrecode(int id) throws Exception;
	public List<Map<String,Object>> selrecode(int id) throws Exception;
	//Builder-Tail


}