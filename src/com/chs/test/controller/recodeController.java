package com.chs.test.controller;

import com.chs.wheel.core.Auto;
import com.chs.wheel.core.BackHand;
import com.chs.wheel.core.ControllerMapping;

import java.util.List;
import java.util.Map;

import com.chs.test.service.recodeService;

@ControllerMapping(url = "/recode")
public class recodeController{


	@Auto
	public recodeService recode_Service;

	//Builder-Head
	@ControllerMapping(url = "/addrecode")
	public void addrecode(BackHand backhand) throws Exception {

		String tag=backhand.get("tag");
		int res=recode_Service.addrecode(tag);
		backhand.output(res+"");
	}

	@ControllerMapping(url = "/updrecode")
	public void updrecode(BackHand backhand) throws Exception {

		int id=Integer.parseInt(backhand.get("id"));
		String tag=backhand.get("tag");
		boolean res=recode_Service.updrecode(id,tag);
		backhand.output(res);
	}

	@ControllerMapping(url = "/delrecode")
	public void delrecode(BackHand backhand) throws Exception {

		int id=Integer.parseInt(backhand.get("id"));
		boolean res=recode_Service.delrecode(id);
		backhand.output(res);
	}

	@ControllerMapping(url = "/selrecode")
	public void selrecode(BackHand backhand) throws Exception {

		int id=Integer.parseInt(backhand.get("id"));
		List<Map<String,Object>> res=recode_Service.selrecode(id);
		backhand.output(res);
	}

	//Builder-Tail

}