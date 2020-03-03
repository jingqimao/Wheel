package com.chs.wheel.core;

import java.util.HashMap;
import java.util.Map;

public class CoreSession {

	private Map<String,WheelSession> AllSession=new HashMap<String,WheelSession>();
	
	public CoreSession(){
		
	}
	
	/**
	 * 
	 * <p>getSession</p>
	 * Description: 获取Session
	 * 
	 * @author chenhaishan
	 * @date 2019-10-31 14:52
	 *
	 * @param sessionId
	 * @return
	 */
	public WheelSession getSession(String sessionId) {
		if(AllSession.containsKey(sessionId)) {
			if(!AllSession.get(sessionId).isUpdate) {//System.out.println("有请求操作,加上标记"+sessionId);
				AllSession.get(sessionId).isUpdate=true;
			}
			return AllSession.get(sessionId);
			/*if(AllSession.get(sessionId).isOverTime()) {
				WheelSession session=new WheelSession(sessionId);
				AllSession.put(sessionId, session);
				return session;
			}else {
			}*/
		}else {
			WheelSession session=new WheelSession(sessionId);
			AllSession.put(sessionId, session);
			return session;
		}
		
	}
	
	/**
	 * 
	 * <p>clearOverTimeSession</p>
	 * Description: 清除超时Session
	 * 
	 * @author chenhaishan
	 * @date 2019-10-31 16:21
	 *
	 */
	public void clearOverTimeSession() {//System.out.println("正在清理超时session...");
		for(String k:AllSession.keySet()) {
			if(AllSession.get(k).isUpdate) {//System.out.println("有标记，刷新时间"+AllSession.get(k).getSessionId());
				AllSession.get(k).updateTime();
				AllSession.get(k).isUpdate=false;
			}else if(AllSession.get(k).isOverTime()) {//System.out.println("1分钟内无操作，清除"+AllSession.get(k).getSessionId());
				AllSession.remove(k);
			}
		}
	}
}
