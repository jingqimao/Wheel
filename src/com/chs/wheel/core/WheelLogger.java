package com.chs.wheel.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * <p>WheelLogger</p>
 * Description:日志工具类 
 * 
 * @author chenhaishan
 * @date 2019-10-09 11:55
 *
 */
public class WheelLogger {
	
	private static Map<String,Logger> items=new HashMap<String,Logger>();
	
	public static Logger get(Object item) {
		if(items.containsKey(item.getClass().getName())) {
			return items.get(item.getClass().getName());
		}else {
			Logger logger = Logger.getLogger(item.getClass());
			items.put(item.getClass().getName(), logger);
			return logger;
		}
	}
}
