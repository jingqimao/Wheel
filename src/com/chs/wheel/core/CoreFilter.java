package com.chs.wheel.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * 
 * <p>CoreFilter</p>
 * Description: 核心过滤器
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:23
 *
 */
public class CoreFilter implements Filter{
	
	@Override
	public void destroy() {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		String url=((HttpServletRequest)request).getRequestURI();
		
		//权限拦截
		for(String k:Wheel.powerMapping.keySet()){
			if(url.matches(k)){
				String pws=Wheel.powerMapping.get(k).get("power");
				String[] pps=pws.split(",");
				boolean op=false;
				HttpSession session=((HttpServletRequest)request).getSession();
				for(String ps:pps) {
					if(ps.contains(".")) {
						String[] px=ps.split("\\.");
						if(session.getAttribute(px[0])==null||
								((Map<String, ?>)session.getAttribute(px[0])).get(px[1])==null) {
							op=true;
							break;
						}
					}else {
						if(session.getAttribute(ps)==null) {
							op=true;
							break;
						}
					}
				}
				if(op) {
					boolean def=false;
					//放开默认请求
					for(String kk:Wheel.powerMapping.get(k).keySet()) {
						if(kk.indexOf("def")>-1&&Wheel.powerMapping.get(k).get(kk).equals(url)) {
							def=true;
						}
					}
					if(!def) {
						if("GET".equals(((HttpServletRequest)request).getMethod())) {
							((HttpServletResponse)response).sendRedirect(Wheel.powerMapping.get(k).get("to")); 
						}else {
							((HttpServletResponse)response).getWriter().print("{\"code\":-1,\"data\":\"error\",\"msg\":\"Permission denied!\"}");
						}
						return;
					}
				}
			}
		}
		
		//设置语言标识
		url=setLang(request,url);
		
		//过滤语言标识
		url=filterLang(request,url);
		
		
		//匹配重定向映射
		String rum=getMappingURL(Wheel.redirectUrlMapping, url);
		if(rum!=null) {
			//设置编码
			((HttpServletRequest)request).setCharacterEncoding("UTF-8");
			((HttpServletResponse)response).setContentType("text/html; charset=UTF-8");  
			
			//切换语言路径
			HttpSession session=((HttpServletRequest)request).getSession();
			String lang=null;
			if(session.getAttribute(Wheel.UserLanguage_Tag)!=null)lang=session.getAttribute(Wheel.UserLanguage_Tag).toString().toLowerCase();
			if(rum.contains("/view/def/")&&lang!=null)rum=rum.replace("/view/def/", "/view/"+lang+"/");
			
			((HttpServletResponse)response).sendRedirect(rum); 
			return;
		}
		
		//匹配转发映射
		String fum=getMappingURL(Wheel.forwardUrlMapping, url);
		if(fum!=null) {
			//设置编码
			((HttpServletRequest)request).setCharacterEncoding("UTF-8");
			((HttpServletResponse)response).setContentType("text/html; charset=UTF-8");  
			
			//切换语言路径
			HttpSession session=((HttpServletRequest)request).getSession();
			String lang=null;
			if(session.getAttribute(Wheel.UserLanguage_Tag)!=null)lang=session.getAttribute(Wheel.UserLanguage_Tag).toString().toLowerCase();
			if(fum.contains("/view/def/")&&lang!=null) {
				fum=fum.replace("/view/def/", "/view/"+lang+"/");
			}
			
			((HttpServletRequest)request).getRequestDispatcher(fum).forward(request, response);
			return;
		}
		
		chain.doFilter(request, response);
		
	}

	//过滤器初始化，读取映射配置文件
	@Override
	public void init(FilterConfig config) throws ServletException {
		
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
		
		try {
			long startTime=new Date().getTime();
			
			DocumentBuilder docb = docbf.newDocumentBuilder();
			
			//加载总配置文件
			Document cmindoc = docb.parse(Wheel.ProjectPath+Wheel.CminPath);
			NodeList cminroot = cmindoc.getChildNodes().item(1).getChildNodes();
			
			System.out.println("init-conf:start");
			for(int i=0;i<cminroot.getLength();i++){
				if(cminroot.item(i).getNodeType()==Node.ELEMENT_NODE){
					if("conf".equals(cminroot.item(i).getNodeName())) {
						String confName=((Element)cminroot.item(i)).getAttribute("name");
						if(confName!=null&&!confName.isEmpty()) {
							//保存配置参数
							Wheel.confMap.put(confName,((Text)cminroot.item(i).getFirstChild()).getData());
							System.out.println("init-conf:"+confName+"-->"+((Text)cminroot.item(i).getFirstChild()).getData());
						}
					}
				}
			}
			//反射修改Pro配置属性
			for(String k:Wheel.confMap.keySet()) {
				String val=Wheel.confMap.get(k);
				if(val!=null&&!val.isEmpty()) {
					try {
						Field field=Wheel.class.getField(k);
						field.setAccessible(true);
						
						Field modifiersField = Field.class.getDeclaredField("modifiers");
						modifiersField.setAccessible(true);
						modifiersField.set(field, field.getModifiers()&~Modifier.FINAL);
						
						field.set(Wheel.class, Wheel.confMap.get(k));
					} catch (NoSuchFieldException e) {
						WheelLogger.get(this).error("error",e); 
					} catch (SecurityException e) {
						WheelLogger.get(this).error("error",e); 
					} catch (IllegalArgumentException e) {
						WheelLogger.get(this).error("error",e); 
					} catch (IllegalAccessException e) {
						WheelLogger.get(this).error("error",e); 
					}
				}
			}
			System.out.println("init-conf:end");
			
			//路径映射初始化
			String type=null,power=null,from=null,to=null;
			System.out.println("\nload-url-mapping:start");
			System.out.println("load-url-mapping-xml:"+Wheel.urlMappingXml);
			Document mapdoc = docb.parse(Wheel.ProjectPath+Wheel.urlMappingXml);
			NodeList maproot = mapdoc.getChildNodes().item(1).getChildNodes();
			
			//循环遍历映射文件
			for(int i=0;i<maproot.getLength();i++){
				if(maproot.item(i).getNodeType()==Node.ELEMENT_NODE){
					type=((Element)maproot.item(i)).getAttribute("type");
					//默认映射类型为转发
					if(type==null||type.isEmpty())type="forward";
					NodeList mapping=maproot.item(i).getChildNodes();
					for(int j=0;j<mapping.getLength();j++){
						if(mapping.item(j).getNodeType()==Node.ELEMENT_NODE){
							//取得映射前后路径
							if("from".equals(mapping.item(j).getNodeName()))from=((Text)mapping.item(j).getFirstChild()).getData();
							if("to".equals(mapping.item(j).getNodeName()))to=((Text)mapping.item(j).getFirstChild()).getData();
						}
					}
					if("forward".equals(type))Wheel.forwardUrlMapping.put(from,to);
					if("redirect".equals(type))Wheel.redirectUrlMapping.put(from,to);
					System.out.println("load-url-mapping:("+type+")["+from+"]-->["+to+"]");
				}
			}
			System.out.println("load-url-mapping:end");
			
			
			//权限初始化
			System.out.println("\nload-power-mapping:start");
			System.out.println("load-power-mapping-xml:"+Wheel.powerMappingXml);
			Document powerdoc = docb.parse(Wheel.ProjectPath+Wheel.powerMappingXml);
			NodeList powerroot = powerdoc.getChildNodes().item(1).getChildNodes();
			//循环遍历映射文件
			for(int i=0;i<powerroot.getLength();i++){
				if(powerroot.item(i).getNodeType()==Node.ELEMENT_NODE){
					power=((Element)powerroot.item(i)).getAttribute("power");
					//默认映射类型为转发
					if(power==null||power.isEmpty())break;
					NodeList mapping=powerroot.item(i).getChildNodes();
					Map<String,String> m=new HashMap<String,String>();
					int defnum=0;
					for(int j=0;j<mapping.getLength();j++){
						if(mapping.item(j).getNodeType()==Node.ELEMENT_NODE){
							//取得映射前后路径
							if("from".equals(mapping.item(j).getNodeName()))from=((Text)mapping.item(j).getFirstChild()).getData();
							if("to".equals(mapping.item(j).getNodeName()))to=((Text)mapping.item(j).getFirstChild()).getData();
							if("def".equals(mapping.item(j).getNodeName())) {
								m.put("def"+defnum, ((Text)mapping.item(j).getFirstChild()).getData());
								defnum++;
							}
						}
					}
					
					m.put("power", power);
					m.put("to", to);
					Wheel.powerMapping.put(from,m);
					System.out.println("load-power-mapping:("+power+")["+from+"]-->["+to+"]");
				}
			}
			System.out.println("load-power-mapping:end");
			
			System.out.println("\nStartup CoreFilter success for "+((new Date().getTime())-startTime)+"ms !\n");
			
		} catch (ParserConfigurationException e) {
			WheelLogger.get(this).error("error",e); 
		} catch (SAXException e) {
			WheelLogger.get(this).error("error",e); 
		} catch (IOException e) {
			WheelLogger.get(this).error("error",e); 
		}
		
	}

	//获取映射路径
	public static String getMappingURL(Map<String,String> m,String url) {
		
		for(String k:m.keySet()) {
			if(!k.contains("{{")) {
				String rep="[a-z0-9A-Z#%&.\\-]+";
				if(rep.matches(url)) {
					if(url.equals(k)) {
						return m.get(k);
					}
				}else if(url.matches(k)) {
					return m.get(k);
				}
			}else {
				Pattern pa = Pattern.compile("(?<=\\{\\{)(.+?)(?=\\}\\})");
				Matcher ma = pa.matcher(k);
				String kk=k+"";
				String kkk=k+"";
				while(ma.find()) {
					String ss=ma.group();
					kk=kk.replaceAll("\\{\\{"+ss+"\\}\\}", "(.+?)");
					kkk=kkk.replaceAll("\\{\\{"+ss+"\\}\\}", "~");
				}
				kk="^"+kk+"$";
				Pattern paa=Pattern.compile(kk);
				Matcher maa=paa.matcher(url);
				if(maa.find()) {
					String jump=m.get(k);
					Matcher max = pa.matcher(jump);
					Matcher maxx = paa.matcher(url);
					while(max.find()&&maxx.find()) {
						String ss=max.group();
						String sss=maxx.group();
						for(String s:kkk.split("~")) {
							sss=sss.replaceFirst(s, "");
						}
						jump=jump.replaceAll("\\{\\{"+ss+"\\}\\}", sss);
						maa.find();
					}
					return jump;
				}
			}
		}
		return null;
	}
	
	
	//添加语言标识
	public String setLang(ServletRequest request,String url) {
		
		HttpSession session=((HttpServletRequest)request).getSession();
		if(session.getAttribute(Wheel.UserLanguage_Tag)!=null) {
			if(url.split("/").length>2){
				String head=url.split("/")[1];
				if(!Wheel.languageTag.containsKey(head)) {
					url="/"+session.getAttribute(Wheel.UserLanguage_Tag).toString().toLowerCase()+url;
				}
			}else {
				url="/"+session.getAttribute(Wheel.UserLanguage_Tag).toString().toLowerCase()+url;
			}
		}
		
		return url;
	}
	
	
	//过滤语言标识
	public String filterLang(ServletRequest request,String url) {
		if(url.split("/").length>2){
			String head=url.split("/")[1];
			HttpSession session=((HttpServletRequest)request).getSession();
			if(Wheel.languageTag.containsKey(head)){
				if(session.getAttribute(Wheel.UserLanguage_Tag)==null) {
						session.setAttribute(Wheel.UserLanguage_Tag, head.toUpperCase());
				}else {
					if(!session.getAttribute(Wheel.UserLanguage_Tag).equals(head.toUpperCase())) {
						session.setAttribute(Wheel.UserLanguage_Tag, head.toUpperCase());
					}
				}
				url=url.replace("/"+head, "");
			}
			
		}else {
			HttpSession session=((HttpServletRequest)request).getSession();
			if(session.getAttribute(Wheel.UserLanguage_Tag)!=null)session.setAttribute(Wheel.UserLanguage_Tag, null);
		}
		
		return url;
	}
}
