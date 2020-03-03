package com.chs.wheel.core;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>Pro</p>
 * Description: 项目主要缓存参数
 * 
 * @author chenhaishan
 * @date 2018-04-27 15:49
 *
 */
public class Wheel {
	
	//总配置
	public static final Map<String,String> confMap=new HashMap<String,String>();
	//重定向映射
	public static final Map<String,String> redirectUrlMapping=new HashMap<String,String>();
	//转发映射
	public static final Map<String,String> forwardUrlMapping=new HashMap<String,String>();
	//权限映射
	public static final Map<String,Map<String,String>> powerMapping=new HashMap<String,Map<String,String>>();
	
	//自定义用户session
	public static final CoreSession Session=new CoreSession();
	
	
	//项目基础路径WebContent
	public static final String ProjectPath=Wheel.class.getClassLoader().getResource("../../").getPath().toString();
	
	
	//总配置文件
	public static final String CminPath="Wheel.xml";
	
	
	//系统配置
	public static Map<String,Object> sysMap=new HashMap<String,Object>();
	
	//数据连接容器
	public static final Map<String,List<Connection>> connectionMapping=new Hashtable<String,List<Connection>>();
	
	//数据事务标识
	public static final ThreadLocal<String> CONNECTION_TAG=new ThreadLocal<String>();
	
	//所有语言标识
	public static final Map<String,String> languageTag=new HashMap<String,String>();
	
	//用户语言标识
	public static final String UserLanguage_Tag="UserLanguage_Tag";
	
	//服务数据缓存
	public static final Map<String,Map<String,Object>> acheService=new HashMap<String,Map<String,Object>>();
	
	
	//数据操作接口映射
	public static final Map<String,Object> daoMapping=new HashMap<String,Object>();
	
	//服务接口映射
	public static final Map<String,Object> serviceMapping=new HashMap<String,Object>();
	
	//控制器容器
	public static final Map<String,Object> controllerMapping=new HashMap<String,Object>();
	
	
	
	//系统文件目录
	public static String sysSrc=null;
	
	//=====================================以下为动态配置属性============================================
	
	
	//路径映射配置文件
	public static final String urlMappingXml=new String("");
	//权限配置文件
	public static final String powerMappingXml=new String("");
	
	//数据接口扫描包
	public static final String daoPkg=new String("");
	//数据接口扫描包
	public static final String servicePkg=new String("");
	//控制器扫描包
	public static final String controllerPkg=new String("");
	
	//分布式服务端口
	public static final String servicePort=new String("");
	
}
