package com.chs.wheel.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>PageRes</p>
 * Description: 查询结果分页类
 * 
 * @author chenhaishan
 * @date 2018-07-19 17:36
 *
 */
public class PageRes implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -3992284066484573454L;
	public List<Map<String,Object>> data;
	public int count;
	public int code;
	public String msg;
	
	public PageRes(List<Map<String,Object>> data,int count){
		this.data=data;
		this.count=count;
		this.code=0;
		this.msg="";
	}
	
	public PageRes(List<Map<String,Object>> data,int count,int code,String msg){
		this.data=data;
		this.count=count;
		this.code=code;
		this.msg=msg;
	}
}
