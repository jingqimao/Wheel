package com.chs.wheel.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * <p>MD5</p>
 * Description: MD5加密工具
 * 
 * @author chenhaishan
 * @date 2018-04-18 13:26
 *
 */
public class MD5Utils {
	
	/**
	 * 
	 * <p>toString</p>
	 * Description: md5加密
	 * 
	 * @author chenhaishan
	 * @date 2018-04-18 13:26
	 *
	 * @param context	要加密的内容
	 * @return	返回密文
	 */
	public static String toString(String context) {  
		String md5Str=null;
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(context.getBytes());//update处理    
            byte [] encryContext = md.digest();//调用该方法完成计算    
   
            int i;    
            StringBuffer buf = new StringBuffer("");    
            for (int offset = 0; offset < encryContext.length; offset++) {//做相应的转化（十六进制）  
                i = encryContext[offset];    
                if (i < 0) i += 256;    
                if (i < 16) buf.append("0");    
                buf.append(Integer.toHexString(i));    
           }    
            md5Str=buf.toString(); 
        } catch (NoSuchAlgorithmException e) {      
            e.printStackTrace();    
        }
        return md5Str;
    }    
}
