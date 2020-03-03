package com.chs.wheel.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.chs.wheel.utils.FileUtils;
import com.chs.wheel.utils.TimerUtils;

/**
 * 
 * <p>WheelInit</p>
 * Description: 项目初始化辅助类
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:25
 *
 */
public class WheelInit {
	
	public static void init() {
		
		//定时清理超时缓存
		TimerUtils.TimeTask(10*60*1000, new Runnable() {
			
			@Override
			public void run() {
				for(String k:Wheel.acheService.keySet()) {
					if(Integer.parseInt(Wheel.acheService.get(k).get("num").toString())<Integer.parseInt(Wheel.acheService.get(k).get("tnum").toString())) {
						if(System.currentTimeMillis()-Long.parseLong(Wheel.acheService.get(k).get("stTime").toString())>Long.parseLong(Wheel.acheService.get(k).get("time").toString())) {
							Wheel.acheService.remove(k);
						}
					}else {
						if(System.currentTimeMillis()-Long.parseLong(Wheel.acheService.get(k).get("acheTime").toString())>Long.parseLong(Wheel.acheService.get(k).get("time").toString())) {
							Wheel.acheService.remove(k);
						}
					}
				}
			}
		}, 2);
		System.out.println("\nstart up ServiceAche cleaner!");
		
		//定时清理超时session
		TimerUtils.TimeTask(10*60*1000, new Runnable() {
			
			@Override
			public void run() {
				Wheel.Session.clearOverTimeSession();
			}
		}, 2);
		System.out.println("\nstart up Session cleaner!\n");
		
		//获取语言标识
		List<File> fs=FileUtils.getFileByPath(Wheel.ProjectPath+"/view/_lang/", ".properties");
		for(File f:fs) {
			Map<String,LinkedHashMap<String,String>> pm=FileUtils.getProperties(f.getAbsolutePath(), "utf-8");
			for(String tagName:pm.keySet()) {
				Wheel.languageTag.put(tagName.toLowerCase(), tagName.toUpperCase());
				System.out.println("add language ["+tagName.toUpperCase()+"]!");
			}
		}
		
	}
	
	public static void destroy() { 
		
	}
}
