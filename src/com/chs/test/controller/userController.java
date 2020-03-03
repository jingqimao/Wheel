package com.chs.test.controller;

import java.util.Map;

import com.chs.test.service.userSCache;
import com.chs.test.service.userService;
//import com.chs.test.service.userWebService;
import com.chs.wheel.core.Auto;
import com.chs.wheel.core.BackHand;
import com.chs.wheel.core.ControllerMapping;
import com.chs.wheel.core.WheelUpload;

@ControllerMapping(url = "/user")
public class userController{
	
	@Auto
	public userService userSer;
	
	@Auto
	public userSCache userSC;
	
	@ControllerMapping(url = "/register")
	public void register(BackHand backhand) throws Exception {
		long tt=System.currentTimeMillis();
		backhand.output(userSer.getSomeUser("", 1, 5).data);
		System.out.println(System.currentTimeMillis()-tt);
		
//		long tt=System.currentTimeMillis();
//		System.out.println(userWebService.getSomeUser("test", 1, 5).data);
//		System.out.println(System.currentTimeMillis()-tt);
	}
	
	@ControllerMapping(url = "/upload")
	public void upload(BackHand backhand) throws Exception {
		
		Map<String,Object> res=WheelUpload.uploads(backhand.getParts(), "C:\\Users\\G2server01\\Desktop\\save\\test-upload\\userdata\\", ".jpg", "/test_upload/userdata/");
		
		backhand.output(res);
	}
	
	@ControllerMapping(url = "/getxx")
	public void getxx(BackHand backhand) throws Exception {
		
		int id=Integer.parseInt(backhand.get("id"));
		backhand.output(userSC.getUser(id));
	}
	
	@ControllerMapping(url = "/setxx")
	public void setxx(BackHand backhand) throws Exception {
		
		int id=Integer.parseInt(backhand.get("id"));
		
		String name=backhand.get("name");
		
		backhand.output(userSC.setUserName(id, name));
	}
	
	@ControllerMapping(url = "/division")
	public void division(BackHand backhand) throws Exception {
		
		int a=Integer.parseInt(backhand.get("a"));
		int b=Integer.parseInt(backhand.get("b"));
		
		int c=a/b;
		
		backhand.call(0, "success", c+"");
	}
	
	@ControllerMapping(url = "/get_tt")
	public void getTT(BackHand backhand) throws Exception {
		Map<String, Object> m=userSer.getTT();
		backhand.output(m);
	}
	
	@ControllerMapping(url = "/set_tt")
	public void setTT(BackHand backhand) throws Exception {
		
		String name=backhand.get("name");
		
		backhand.output(userSer.setTT(name));
	}
	
	@ControllerMapping(url = "/test")
	public void test(BackHand backhand) throws Exception {
		
		backhand.output(userSer.test());
	}
	
	@ControllerMapping(url = "/test_token")
	public void test_token(BackHand backhand) throws Exception {
		
		backhand.output(userSer.test_token());
	}
}
