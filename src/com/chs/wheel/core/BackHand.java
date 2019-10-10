package com.chs.wheel.core;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.chs.wheel.utils.FileUtils;
import com.chs.wheel.utils.JSONUtils;

/**
 * 
 * <p>BackHand</p>
 * Description: 控制器辅助类
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:22
 *
 */
public class BackHand {
	
	public HttpServletRequest request;
	public HttpServletResponse response;
	
	public BackHand(HttpServletRequest request,HttpServletResponse response) {
		this.request=request;
		this.response=response;
	}
	
	/**
	 * 
	 * <p>excStr</p>
	 * Description: 过滤字符串
	 * 
	 * @author chenhaishan
	 * @date 2019-06-11 13:03
	 *
	 * @return
	 */
	public String excStr(String str) {
		return str.replaceAll("\r", "").replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;").replaceAll("\\\\r", "").replaceAll("\\\\n", "<br>").replaceAll("\\\\t", "&nbsp;");
	}
	
	/**
	 * 
	 * <p>output</p>
	 * Description: 输出字符串
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:03
	 *
	 * @param result
	 * @throws IOException
	 */
	public void output(String result) throws IOException {
		response.getWriter().print(excStr(result));
	}
	
	/**
	 * 
	 * <p>output</p>
	 * Description: 输出map-json
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:03
	 *
	 * @param result
	 * @throws IOException
	 */
	public void output(Map<String,Object> result) throws IOException {
		if(checkResult(result,false))response.getWriter().print(excStr(JSONUtils.toJsonString(result)));
	}
	
	/**
	 * 
	 * <p>output</p>
	 * Description: 输出listmap-json
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:04
	 *
	 * @param result
	 * @throws IOException
	 */
	public void output(List<Map<String,Object>> result) throws IOException {
		if(checkResult(result,false))response.getWriter().print(excStr(JSONUtils.toJsonString(result)));
	}
	
	public void output(PageRes result) throws IOException {
		if(checkResult(result,false))response.getWriter().print(excStr(JSONUtils.toJsonString(JSONUtils.newMap()
				.put("count", result.count)
				.put("data", result.data)
				.put("code", result.code)
				.put("msg", result.msg)
				.get())));
	}
	
	/**
	 * 
	 * <p>output</p>
	 * Description: 输出boolean
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:41
	 *
	 * @param result
	 * @throws IOException
	 */
	public void output(boolean result) throws IOException {
		if(checkResult(result,false))response.getWriter().print(result);
	}
	
	/**
	 * 
	 * <p>call</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-03-22 10:45
	 *
	 * @param code
	 * @param msg
	 * @param data
	 * @throws IOException
	 */
	public void call(int code,String msg,String data) throws IOException {
		if(checkResult(data,true))response.getWriter().print(JSONUtils.toJsonString(JSONUtils.newMap()
				.put("code", code)
				.put("msg", msg)
				.put("data", data)
				.get()));
	}
	
	/**
	 * 
	 * <p>call</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-03-22 10:46
	 *
	 * @param code
	 * @param msg
	 * @param data
	 * @throws IOException
	 */
	public void call(int code,String msg,Map<String,Object> data) throws IOException {
		if(checkResult(data,true))response.getWriter().print(JSONUtils.toJsonString(JSONUtils.newMap()
				.put("code", code)
				.put("msg", msg)
				.put("data", data)
				.get()));
	}
	
	/**
	 * 
	 * <p>call</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-03-22 10:46
	 *
	 * @param code
	 * @param msg
	 * @param data
	 * @throws IOException
	 */
	public void call(int code,String msg,List<Map<String,Object>> data) throws IOException {
		if(checkResult(data,true))response.getWriter().print(JSONUtils.toJsonString(JSONUtils.newMap()
				.put("code", code)
				.put("msg", msg)
				.put("data", data)
				.get()));
	}
	
	/**
	 * 
	 * <p>call</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-03-22 10:46
	 *
	 * @param data
	 * @throws IOException
	 */
	public void call(PageRes data) throws IOException {
		if(checkResult(data,true))response.getWriter().print(JSONUtils.toJsonString(JSONUtils.newMap()
				.put("code", data.code)
				.put("msg", data.msg)
				.put("data", data.data)
				.put("count", data.count)
				.get()));
	}
	
	/**
	 * 
	 * <p>call</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-03-22 10:46
	 *
	 * @param code
	 * @param msg
	 * @param data
	 * @throws IOException
	 */
	public void call(int code,String msg,boolean data) throws IOException {
		if(checkResult(data,true))response.getWriter().print(JSONUtils.toJsonString(JSONUtils.newMap()
				.put("code", code)
				.put("msg", msg)
				.put("data", data)
				.get()));
	}
	
	/**
	 * 
	 * <p>checkResult</p>
	 * Description: 检查返回值
	 * 
	 * @author chenhaishan
	 * @date 2019-08-26 18:30
	 *
	 * @param result
	 * @return
	 * @throws IOException
	 */
	public boolean checkResult(Object result,boolean isJson) throws IOException {
		if(result==null) {
			if(isJson) {
				response.getWriter().print(JSONUtils.toJsonString(JSONUtils.newMap()
						.put("code", -1)
						.put("msg", "服务器出错：返回数据为null")
						.put("data", null)
						.get()));
			}else {
				response.getWriter().print("null");
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * <p>isLogin</p>
	 * Description: 登录判断
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:12
	 *
	 * @param tag
	 * @return
	 */
	public boolean isLogin(String tag) {
		return request.getSession().getAttribute(tag)!=null;
	}
	
	/**
	 * 
	 * <p>has</p>
	 * Description: 
	 * 
	 * @author chenhaishan
	 * @date 2019-04-09 14:15
	 *
	 * @param parameter
	 * @return
	 */
	public boolean has(String parameter) {
		return request.getParameter(parameter)!=null;
	}
	
	/**
	 * 
	 * <p>get</p>
	 * Description: 获取请求参数
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:13
	 *
	 * @param parameter
	 * @return
	 */
	public String get(String parameter) {
		return request.getParameter(parameter);
	}
	
	/**
	 * 
	 * <p>getPart</p>
	 * Description: 获取part
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:13
	 *
	 * @param parameter
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public Part getPart(String parameter) throws IOException, ServletException {
		return request.getPart(parameter);
	}
	
	/**
	 * 
	 * <p>getParts</p>
	 * Description: 获取parts
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:13
	 *
	 * @param parameter
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public Collection<Part> getParts() throws IOException, ServletException {
		return request.getParts();
	}
	
	/**
	 * 
	 * <p>getFileName</p>
	 * Description: 获取上传文件名
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:13
	 *
	 * @param parameter
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public String getFileName(String parameter) throws IOException, ServletException {
		return FileUtils.getFilename(request.getPart(parameter));
	}
	
	/**
	 * 
	 * <p>getFile</p>
	 * Description: 获取上传文件并输出到指定位置
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:14
	 *
	 * @param parameter
	 * @param ouput
	 * @throws IOException
	 * @throws ServletException
	 */
	public void getFile(String parameter,String ouput) throws IOException, ServletException {
		Part part=request.getPart(parameter);
		part.write(ouput);
	}
	
	/**
	 * 
	 * <p>forward</p>
	 * Description: 转发
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:31
	 *
	 * @param url
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forward(String url) throws ServletException, IOException {
		
		String fum=CoreFilter.getMappingURL(Wheel.forwardUrlMapping, url);
		if(fum!=null) {
			request.getRequestDispatcher(fum).forward(request, response);
		}else {
			request.getRequestDispatcher(url).forward(request, response);
		}
		
	}
	
	/**
	 * 
	 * <p>redirect</p>
	 * Description: 重定向
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:33
	 *
	 * @param url
	 * @throws IOException
	 */
	public void redirect(String url) throws IOException {
		
		String rum=CoreFilter.getMappingURL(Wheel.redirectUrlMapping, url);
		if(rum!=null) {
			response.sendRedirect(rum);
		}else {
			response.sendRedirect(url);
		}
		
	}
	
	/**
	 * 
	 * <p>setAttribute</p>
	 * Description: 设置会话属性
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:34
	 *
	 * @param key
	 * @param val
	 */
	public void setAttribute(String key,Object val) {
		request.setAttribute(key, val);
	}
	
	/**
	 * 
	 * <p>getAttribute</p>
	 * Description: 获取会话属性
	 * 
	 * @author chenhaishan
	 * @date 2018-08-28 14:35
	 *
	 * @param key
	 * @param val
	 * @return
	 */
	public Object getAttribute(String key,Object val) {
		return request.getAttribute(key);
	}
	
	/**
	 * 
	 * <p>getURL</p>
	 * Description: 获取当前请求路径
	 * 
	 * @author chenhaishan
	 * @date 2018-09-21 17:12
	 *
	 * @return
	 */
	public String getURL() {
		return request.getRequestURI();
	}
	
	/**
	 * 
	 * <p>getSession</p>
	 * Description: 获取Session
	 * 
	 * @author chenhaishan
	 * @date 2018-09-29 17:20
	 *
	 * @return
	 */
	public HttpSession getSession() {
		return request.getSession();
	}
	
	/**
	 * 
	 * <p>getSessMap</p>
	 * Description: 从session中获取map
	 * 
	 * @author chenhaishan
	 * @date 2018-10-12 16:02
	 *
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getSessMap(String key){
		return (Map<String,Object>)request.getSession().getAttribute(key);
	}
}
