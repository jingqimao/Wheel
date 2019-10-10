package com.chs.test.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chs.test.service.userService;
import com.chs.wheel.core.Auto;
import com.chs.wheel.core.BackHand;
import com.chs.wheel.core.ControllerMapping;

@ControllerMapping(url = "/main")
public class mainController{
	
	@Auto
	public userService userSer;
	
	@ControllerMapping(url = "/(\\S{3}).html")
	public void register(BackHand backhand) throws Exception {
		
		String url=backhand.getURL();
		
		Pattern pattern = Pattern.compile("/main/(\\S{3}).html");
		Matcher matcher = pattern.matcher(url);
		matcher.find();
		String title=matcher.group(1);
		
		backhand.setAttribute("title", title);
		backhand.setAttribute("content", title); 
		
		backhand.forward("/view/def/jsp/mode.jsp");
	}
	
}
