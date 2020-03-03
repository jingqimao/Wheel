package com.chs.wheel.utils;

import java.util.Random;
import java.util.UUID;

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
	
	private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	
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
	
	/**
	 * 
	 * <p>getShortUuid</p>
	 * Description: 生成8位UUID
	 * 
	 * @author chenhaishan
	 * @date 2019-10-31 14:51
	 *
	 * @return
	 */
	public static String getShortUuid() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}
}
