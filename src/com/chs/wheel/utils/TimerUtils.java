package com.chs.wheel.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.quartz.CronExpression;

public class TimerUtils {
	
	/**
	 * 
	 * <p>dayTask</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2018-07-16 10:14
	 *
	 * @param time	HH:mm:ss
	 * @param action	线程任务
	 * @param poolSize	线程池大小
	 */
	public static ScheduledExecutorService dayTask(String time,Runnable action,int poolSize) {
		
		
		long day=24*60*60*1000;
		long delay=0;
		
		try {
			DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
			delay=curDate.getTime();
			delay-=System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
        ScheduledExecutorService service = Executors.newScheduledThreadPool(poolSize);  
        
        service.scheduleAtFixedRate(action, delay, day, TimeUnit.MILLISECONDS);  
        
        return service;
	}
	
	public static ScheduledExecutorService TimeTask(long time,Runnable action,int poolSize) {
		
        ScheduledExecutorService service = Executors.newScheduledThreadPool(poolSize);  
        
        service.scheduleAtFixedRate(action, time, time, TimeUnit.MILLISECONDS);
        
        return service;
	}
	
	public static ScheduledExecutorService TimeTask(long wait,long space,Runnable action,int poolSize) {
		
        ScheduledExecutorService service = Executors.newScheduledThreadPool(poolSize);  
        
        service.scheduleAtFixedRate(action, wait, space, TimeUnit.MILLISECONDS);
        
        return service;
	}
	
	public static ScheduledExecutorService TimeTask(String time,Runnable action,int poolSize) {
		
		try {
			CronExpression ce = new CronExpression(time);
			
			Date nextDate=ce.getNextValidTimeAfter(new Date());
			
			ScheduledExecutorService service = Executors.newScheduledThreadPool(poolSize);  
	        
	        service.schedule(action, nextDate.getTime()-new Date().getTime(), TimeUnit.MILLISECONDS);
	        
	        return service;
	        
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        return null;
	}
	
	/**
	 * 
	 * <p>getNow</p>
	 * Description: 获取当前时间Timestamp
	 * 
	 * @author chenhaishan
	 * @date 2018-07-20 12:39
	 *
	 * @return
	 */
	public static Timestamp getNow() {
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		Date dadate = new Date();
		String nowDaDate = sdf.format(dadate);
		return Timestamp.valueOf(nowDaDate);
	}
	
	/**
	 * 
	 * <p>getNowTime</p>
	 * Description: 获取当前时间毫秒数
	 * 
	 * @author chenhaishan
	 * @date 2019-10-31 14:56
	 *
	 * @return
	 */
	public static long getNowTime() {
		return new Date().getTime();
	}
	
	/**
	 * 
	 * <p>getNow</p>
	 * Description: 获取当前时间格式化字符串
	 * 
	 * @author chenhaishan
	 * @date 2018-08-10 14:05
	 *
	 * @param format
	 * @return
	 */
	public static String getNow(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date dadate = new Date();
		return sdf.format(dadate);
	}
	
	/**
	 * 
	 * <p>getNow</p>
	 * Description: 获取一个时间得格式化字符串
	 * 
	 * @author chenhaishan
	 * @date 2018-08-10 14:08
	 *
	 * @param dadate
	 * @param format
	 * @return
	 */
	public static String getNow(Date dadate,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(dadate);
	}
	
	/**
	 * 
	 * <p>getNowDate</p>
	 * Description: 获取今天日期
	 * 
	 * @author chenhaishan
	 * @date 2018-08-10 14:09
	 *
	 * @return
	 */
	public static String getToday() {
		return getNow(new Date(),"yyyyMMdd");
	}
	
	/**
	 * 
	 * <p>getYesterday</p>
	 * Description: 获取昨天时间
	 * 
	 * @author chenhaishan
	 * @date 2018-08-10 14:12
	 *
	 * @return
	 */
	public static String getYesterday() {
		Calendar   cal = Calendar.getInstance();
		cal.add(Calendar.DATE,   -1);
		return getNow(cal.getTime(),"yyyyMMdd");
	}
	
	/**
	 * 
	 * <p>getMonday</p>
	 * Description: 获取本周星期一
	 * 
	 * @author chenhaishan
	 * @date 2018-08-10 14:23
	 *
	 * @return
	 */
	public static String getMonday() {
		Calendar   cal = Calendar.getInstance();
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){  
            cal.add(Calendar.DATE, -1);  
        }  
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return getNow(cal.getTime(),"yyyyMMdd");
	}
	
	/**
	 * 
	 * <p>getTimeSize</p>
	 * Description: 根据字符串获取毫秒数
	 * 
	 * @author chenhaishan
	 * @date 2018-08-10 14:23
	 *
	 * @return
	 */
	
	public static long getTimeSize(String str) {
		String[] times= {"d","h","m","s"};
		long[] timess= {24*60*60*1000,60*60*1000,60*1000,1000};
		for(int i=0;i<times.length;i++) {
			if(str.contains(times[i])) {
				return(long)(Float.parseFloat(str.substring(0, str.length()-1))*timess[i]);
			}
		}
		return 0;
	}
}
