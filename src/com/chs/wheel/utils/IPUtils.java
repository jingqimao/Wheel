package com.chs.wheel.utils;

/**
 * 
 * <p>IDUtil</p>
 * Description: ID工具
 * 
 * @author chenhaishan
 * @date 2018-04-23 10:19
 *
 */
public class IPUtils {
	
	/**
	 * IP地址转Int数字
	 * @param ipAddr
	 * @return
	 */
	public static int ipToInt(String ipAddr) {
		
		byte[] ret = new byte[4];  
        try {  
            String[] ipArr = ipAddr.split("\\.");  
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);  
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);  
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);  
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);  
            
            try {  
            	int ipInt = ret[3] & 0xFF;  
            	ipInt |= ((ret[2] << 8) & 0xFF00);  
            	ipInt |= ((ret[1] << 16) & 0xFF0000);  
            	ipInt |= ((ret[0] << 24) & 0xFF000000);  
                return ipInt;
            } catch (Exception e) {  
                throw new IllegalArgumentException(ipAddr + " is invalid IP");  
            } 
        } catch (Exception e) {  
            throw new IllegalArgumentException(ipAddr + " is invalid IP");  
        }  
	}
	
	/**
	 * Int数字转IP地址
	 * @param addr
	 * @return
	 */
	public static String intToIp(int ipInt) {
		return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.')  
                .append((ipInt >> 16) & 0xff).append('.').append(  
                        (ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff))  
                .toString(); 
	}
}
