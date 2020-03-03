package com.chs.wheel.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WheelContext {
	
	private Map<String,Object> ache=new HashMap<String,Object>();
	
	private final String session="session";
	private final String backhand="backhand";
	
	public WheelContext(WheelSession session,BackHand backhand) {
		ache.put(this.session, session);
		ache.put(this.backhand, backhand);
	}
	
	public WheelSession  getSession() {
		return (WheelSession)ache.get(session);
	}
	
	public BackHand getBackHand() {
		return (BackHand)ache.get(backhand);
	}
}
