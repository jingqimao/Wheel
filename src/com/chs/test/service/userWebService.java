package com.chs.test.service;

import com.chs.wheel.core.WheelService;

import com.chs.wheel.core.PageRes;

public class userWebService{


	public static void newUser(String account,String password,String name,int status) throws Exception {
		String url="http://localhost:8088/ws";
		String service="com.chs.test.service.userService-newUser";
		String callBackType=null;
		String[] argsType={"String","String","String","int"};
		byte[][] args={((String)account).getBytes(),((String)password).getBytes(),((String)name).getBytes(),WheelService.intToBytes(status)};
		WheelService.doit(url,service,callBackType,argsType,args);
	}

	public static PageRes getSomeUser(String account,int page,int pageSize) throws Exception {
		String url="http://localhost:8088/ws";
		String service="com.chs.test.service.userService-getSomeUser";
		String callBackType="PageRes";
		String[] argsType={"String","int","int"};
		byte[][] args={((String)account).getBytes(),WheelService.intToBytes(page),WheelService.intToBytes(pageSize)};
		return (PageRes)WheelService.deserializeToObject(WheelService.doit(url,service,callBackType,argsType,args));
	}

}