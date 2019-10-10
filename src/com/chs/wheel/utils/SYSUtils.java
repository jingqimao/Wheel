package com.chs.wheel.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SYSUtils {
	 
	private static DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
	
	/**
	 * 
	 * <p>getDiskInfo</p>
	 * Description: 获取磁盘信息
	 * 
	 * @author chenhaishan
	 * @date 2019-08-12 13:46
	 *
	 * @return
	 */
	public static List<Map<String, Object>> getDiskInfo() {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
		File[] roots = File.listRoots();
		for (File file : roots) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			long freeSpace=file.getFreeSpace();
			long totalSpace=file.getTotalSpace();
			long usableSpace=totalSpace-freeSpace;
			
			map.put("path", file.getPath());
			map.put("freeSpace", freeSpace);
			map.put("usableSpace", usableSpace);
			map.put("totalSpace",totalSpace);
			map.put("percent", DECIMALFORMAT.format(((double)usableSpace/(double)totalSpace)*100)+"%");
			
			list.add(map);
		}
 
		return list;
	}
	
	/**
	 * 
	 * <p>getMemoryInfo</p>
	 * Description: 获取虚拟机内存信息
	 * 
	 * @author chenhaishan
	 * @date 2019-08-12 13:52
	 *
	 * @return
	 */
	public static Map<String,Object> getMemoryInfo(){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Runtime run = Runtime.getRuntime();
		
		long totalMemory=run.totalMemory();
		long maxMemory=run.maxMemory();
		long freeMemory=run.freeMemory();
		long usedMemory=totalMemory-freeMemory;
		
		map.put("maxMemory", maxMemory);
		map.put("freeMemory", freeMemory);
		map.put("usedMemory", usedMemory);
		map.put("percent", DECIMALFORMAT.format(((double)usedMemory/(double)maxMemory)*100)+"%");
		
		return map;
	}
}
