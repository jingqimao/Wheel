package com.chs.wheel.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Endpoint;

import com.chs.wheel.utils.FileUtils;

/**
 * 
 * <p>CoreServlet</p>
 * Description: 核心控制器
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:23
 *
 */
@WebServlet(urlPatterns={"/"},loadOnStartup=1)
@MultipartConfig
public class CoreServlet extends HttpServlet{

	private static final long serialVersionUID = 3696158947253647617L;
	
	public static Map<String,Map<String,Object>> controllerMapping=new HashMap<String,Map<String,Object>>();
	
	private Endpoint WheelEndpoint;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String url=request.getRequestURI();
		boolean iscon=selController(url,request, response);
		if(!iscon&&!url.equals("/404")) {
			response.sendRedirect("/404");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url=request.getRequestURI();
		boolean iscon=selController(url,request, response);
		if(!iscon) {
			BackHand backhand=new BackHand(request,response);
			try {
				backhand.call(-1, "url error", "error");
			} catch (IOException e1) {
				WheelLogger.get(this).error("error",e1);
			}
		}
	}
	
	/**
	 * 
	 * <p>selController</p>
	 * Description: 分发控制器
	 * 
	 * @author chenhaishan
	 * @date 2018-04-17 09:07
	 *
	 * @param url	请求路径
	 * @param request
	 * @param response
	 */
	protected boolean selController(String url,HttpServletRequest request, HttpServletResponse response){
		
		if(url.indexOf("/")>-1){
			for(String key:controllerMapping.keySet()){
				if(url.matches(key)){
					
					//设置编码
					try {
						((HttpServletRequest)request).setCharacterEncoding("UTF-8");
						((HttpServletResponse)response).setContentType("text/html; charset=UTF-8");  
					} catch (UnsupportedEncodingException e) {
						WheelLogger.get(this).error("error",e);
					}
					
					try {
						try {
							//反射调用对应方法
							Method met=(Method)controllerMapping.get(key).get("method");
							BackHand backhand=new BackHand(request,response);
							BaseServiceImp.context.set(new WheelContext(CoreFilter.getSession(request), backhand));
							met.invoke(controllerMapping.get(key).get("class"),backhand);
							
						} catch (IllegalAccessException e) {
							WheelLogger.get(this).error("error",e);
						} catch (IllegalArgumentException e) {
							WheelLogger.get(this).error("error",e);
						} catch (InvocationTargetException e) {
							WheelLogger.get(this).error("error",e);
							BackHand backhand=new BackHand(request,response);
							try {
								String error_msg=e.getTargetException().toString();
								String error_msg_callback="";
								
								if(error_msg.contains("null"))error_msg_callback+="请求参数为空;";
								if(error_msg.contains("java.lang.NumberFormatException"))error_msg_callback+="数字参数格式化失败;";
								
								if(error_msg_callback.length()!=0) {
									backhand.call(-1, error_msg_callback, "error");
								}else {
									backhand.call(-1, error_msg, "error");
								}
							} catch (IOException e1) {
								WheelLogger.get(this).error("error",e1);
							}
						}
					} catch (SecurityException e) {
						WheelLogger.get(this).error("error",e);
					}  
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		
		long startTime=new Date().getTime();
		System.out.println("\ninit-dao:start");
		
		//扫描数据接口
		List<File> daolist=FileUtils.getFileByPack(Wheel.daoPkg,".class");
		for(File f:daolist) {
			String classname=f.getName().split("\\.")[0];
			if(classname.contains("Imp")) {
				try {
					Class<?> cc=Class.forName(Wheel.daoPkg+"."+classname);
					//实例化接口并放入容器
					Object dao=DaoAOPHandle.getOne(cc.newInstance());
					Wheel.daoMapping.put(cc.getName(), dao);
					System.out.println("init-dao:"+cc.getName());
				} catch (Exception e) {
					WheelLogger.get(this).error("error",e);
				}
			}
		}
		System.out.println("init-dao:end");
		
		
		System.out.println("\ninit-service:start");
		
		//扫描服务接口
		List<File> servicelist=FileUtils.getFileByPack(Wheel.servicePkg,".class");
		for(File f:servicelist) {
			String classname=f.getName().split("\\.")[0];
			if(classname.contains("Imp")) {
				try {
					Class<?> cc=Class.forName(Wheel.servicePkg+"."+classname);
					
					
					Object service=cc.newInstance();
					
					//注入数据接口
					Field[] fields=cc.getDeclaredFields();
					for(Field field:fields) {
						Auto ad=field.getAnnotation(Auto.class);
						if(ad!=null&&Wheel.daoMapping.get(field.getType().getName()+"Imp")!=null) {
							//将数据接口注入带注解的属性
							field.setAccessible(true);
							field.set(service, Wheel.daoMapping.get(field.getType().getName()+"Imp"));
						}
					}
					
					//实例化接口并放入容器
					//service=ServiceAOPHandle.getOne(service);
					
					Wheel.serviceMapping.put(cc.getName(), service);
					
				} catch (Exception e) {
					System.out.println(f.getName()); 
					WheelLogger.get(this).error("error",e);
				}
			}
		}
		for(String key:Wheel.serviceMapping.keySet()){
			try {
				
				Object service=Wheel.serviceMapping.get(key);
				
				Class<?> cc=service.getClass();
				
				//注入数据接口
				Field[] fields=cc.getDeclaredFields();
				for(Field field:fields) {
					Auto ad=field.getAnnotation(Auto.class);
					if(ad!=null&&Wheel.serviceMapping.get(field.getType().getName()+"Imp")!=null) {
						//将数据接口注入带注解的属性
						field.setAccessible(true);
						field.set(service, Wheel.serviceMapping.get(field.getType().getName()+"Imp"));
					}
				}
				
				//实例化接口并放入容器
				if(cc.getName().contains("ServiceImp"))service=ServiceAOPHandle.getOne(service);
				
				Wheel.serviceMapping.put(cc.getName(), service);
				System.out.println("init-service:"+cc.getName());
				
			} catch (Exception e) {
				WheelLogger.get(this).error("error",e);
			}
		}
		
		//分布式服务注册
		String address = "http://localhost:"+Wheel.servicePort+"/ws";
		try {
			if(!WheelService.urlIsReach(address+"?wsdl")) {
				WheelEndpoint=Endpoint.publish(address, new WheelService());
			}
		}catch(Exception e) {
			WheelLogger.get(this).error("error",e);
		}
        
		
		System.out.println("init-service:end");
		
		//通过扫描类文件工具获取所有控制器类名
		List<File> fls=FileUtils.getFileByPack(Wheel.controllerPkg,".class");
		System.out.println("\ninit-controller:start");
		for(File f:fls){
			String classname=f.getName().split("\\.")[0];
			try{
				//反射获取类
				Class<?> cc=Class.forName(Wheel.controllerPkg+"."+classname);
				//获取类级注解
				ControllerMapping cp=cc.getAnnotation(ControllerMapping.class);
				//初始化实例
				Object controller=cc.newInstance();
				//注入数据接口
				/*Field[] fields=cc.getDeclaredFields();
				for(Field field:fields) {
					AutoDao ad=field.getAnnotation(AutoDao.class);
					if(ad!=null) {
						//将数据接口注入带注解的属性
						field.setAccessible(true);
						field.set(controller, Wheel.daoMapping.get(field.getType().getName()+"Imp"));
					}
				}*/
				//注入数据接口
				Field[] fields=cc.getDeclaredFields();
				for(Field field:fields) {
					Auto ad=field.getAnnotation(Auto.class);
					if(ad!=null&&Wheel.serviceMapping.get(field.getType().getName()+"Imp")!=null) {
						//将数据接口注入带注解的属性
						field.setAccessible(true);
						field.set(controller, Wheel.serviceMapping.get(field.getType().getName()+"Imp"));
					}
				}
				
				//获取所有方法并遍历
				Method[] method=cc.getDeclaredMethods();
				for(Method met:method){
					//获取方法级注解
					ControllerMapping cpm=met.getAnnotation(ControllerMapping.class);
					if(cpm!=null){
						Map<String,Object> m=new HashMap<String,Object>();
						//保存类、方法及映射路径
						m.put("class", controller);
						m.put("method", met);
						controllerMapping.put(cp.url()+cpm.url(), m);
						System.out.println("init-controller-mapping:["+cp.url()+cpm.url()+"]-->"+Wheel.controllerPkg+"."+classname+"-->"+met.getName());
					}
					if("init".equals(met.getName())) {
						met.invoke(controller);
					}
				}
				//吧控制器装进容器
				Wheel.controllerMapping.put(classname.replace("Controller", ""), controller);
				
			}catch(Exception e){
				WheelLogger.get(this).error("error",e);
			}
		}
		System.out.println("init-controller:end");
		
		System.out.println("\nstart up CoreServlet success for "+((new Date().getTime())-startTime)+"ms !");
		
		//其他初始化
		WheelInit.init();
		
		System.out.println("\r\n" + 
				"	 __     __   __  __   ______   ______   __        \r\n" + 
				"	/\\ \\  _ \\ \\ /\\ \\_\\ \\ /\\  ___\\ /\\  ___\\ /\\ \\       \r\n" + 
				"	\\ \\ \\/ \".\\ \\\\ \\  __ \\\\ \\  __\\ \\ \\  __\\ \\ \\ \\____  \r\n" + 
				"	 \\ \\__/\".~\\_\\\\ \\_\\ \\_\\\\ \\_____\\\\ \\_____\\\\ \\_____\\ \r\n" + 
				"	  \\/_/   \\/_/ \\/_/\\/_/ \\/_____/ \\/_____/ \\/_____/ Made By ChenHaiShan\r\n" + 
				"	                                                  \r\n" + 
				"");
		
		System.out.println("Startup Wheel success!");
	}

	@Override
	public void destroy() { 
		
		if(WheelEndpoint!=null)WheelEndpoint.stop(); 
		
		//其他销毁
		WheelInit.destroy(); 
	}

}
