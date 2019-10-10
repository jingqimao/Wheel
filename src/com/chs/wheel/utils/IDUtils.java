package com.chs.wheel.utils;

import java.util.Random;

/**
 * 
 * <p>IDUtil</p>
 * Description: ID工具
 * 
 * @author chenhaishan
 * @date 2018-04-23 10:19
 *
 */
public class IDUtils {
	
	private static long autoNum=0;
	
	/**
	 * 
	 * <p>getID</p>
	 * Description: 生成唯一ID
	 * 				毫秒数+自增数
	 * 
	 * @author chenhaishan
	 * @date 2018-04-23 10:19
	 *
	 * @return
	 */
	public static long getID(){
		
		autoNum=autoNum<10000?autoNum+1:0;
		return System.currentTimeMillis()*1000+autoNum;
	}
	
	/**
	 * 
	 * <p>getCode</p>
	 * Description: 生成随机码，数字+大小写字母
	 * 
	 * @author chenhaishan
	 * @date 2018-07-26 13:27
	 *
	 * @param size	长度
	 * @return
	 */
	public static String getCode(int size) {
		StringBuilder res=new StringBuilder();
		
		Random rand=new Random();
        Random randdata=new Random();
        int data=0;
		for(int i=0;i<size;i++) {
			int index=rand.nextInt(3);
            switch(index){
	            case 0:
	                 data=randdata.nextInt(10);
	                 res.append(data);
	                break;
	            case 1:
	                data=randdata.nextInt(26)+65;
	                res.append((char)data);
	                break;
	            case 2:
	                data=randdata.nextInt(26)+97;
	                res.append((char)data);
	                break;
            }
		}
		return res.toString();
	}
	
	/**
	 * 
	 * <p>getCodeNumber</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-08-15 14:35
	 *
	 * @param size
	 * @return
	 */
	public static String getCodeNumber(int size) {
		
		if(size>0) {
			int n=1,m=10;
			for(int i=1;i<size;i++)n*=m;
			int number=(int)((Math.random()*9+1)*n);
			return number+"";
		}
		return null;
	}
}
