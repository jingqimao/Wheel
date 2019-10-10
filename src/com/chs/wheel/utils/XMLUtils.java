package com.chs.wheel.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import ognl.Ognl;
import ognl.OgnlException;

public class XMLUtils {
	
	/**
	 * 
	 * <p>getDYSQL</p>
	 * Description: 处理动态模板，返回语句和注入参数
	 * 
	 * @author chenhaishan
	 * @date 2019-10-08 09:29
	 *
	 * @param xml	模板
	 * @param params	动态参数
	 * @return	包含语句sql字符串和注入参数args集合
	 * @throws Exception
	 */
	public static Map<String, Object> getDYSQL(String xml, Map<String, Object> params) throws Exception {
		
		//给输入的动态sql套一层xml标签
		xml = "<sql>"+xml+"</sql>";
		SAXReader reader = new SAXReader(false);
		Document document = reader.read(new StringReader(xml));
		Element element = document.getRootElement();
		
	    String sql=parserElement(element, params);
	    
	    Pattern pa = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
		Matcher ma = pa.matcher(sql);
		
		List<Object> args=new ArrayList<Object>();
		while(ma.find()) {
			String arg=ma.group().toString();
			sql=sql.replaceAll("\\{"+arg+"\\}", "?");
			args.add(params.get(arg));
		}
	    
	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("sql", sql);
	    result.put("args", args);
		
		return result;
	}

	//递归处理节点，返回拼接SQL
	private static String parserElement(Element element,Map<String, Object> params) throws OgnlException {
		
		StringBuilder sb = new StringBuilder();
		
		List<Node> nodes = element.content();
		for (Node node : nodes) {
			if(node.getName()==null) {
				sqlAppend(sb,node.getText());
			}else if(node.getName().equals("if")) {
				Element ele=(Element)node;
			    String test=ele.attributeValue("test");
			    if(test!=null) {
			    	boolean if_ok=(boolean)Ognl.getValue(test, params);
			    	if(if_ok) {
			    		sqlAppend(sb,parserElement(ele,params));
			    	}
			    }
			}else if(node.getName().equals("set")) {
				String ss=parserElement((Element)node,params);
				if(ss.lastIndexOf(",")==ss.length()-1)ss=ss.substring(0, ss.lastIndexOf(","));
				sqlAppend(sb,ss);
			}else if(node.getName().equals("where")) {
				String ss=parserElement((Element)node,params);
				if(ss!=null&&!ss.trim().isEmpty()) {
					if(ss.trim().startsWith("and"))ss=ss.replaceFirst("and", "");
					sqlAppend(sb,"where "+ss);
				}
			}else if(node.getName().equals("trim")) {
				Element ele=(Element)node;
				String ss=parserElement(ele,params);
				if(ss!=null&&!ss.trim().isEmpty()) {
					ss=ss.trim();
					String prefix=ele.attributeValue("prefix");
				    if(prefix!=null) {
				    	ss=prefix+" "+ss;
				    }
				    String prefixOverrides=ele.attributeValue("prefixOverrides");
				    if(prefixOverrides!=null) {
				    	ss=ss.replaceFirst(prefixOverrides, "");
				    }
				    String suffix=ele.attributeValue("suffix");
				    if(suffix!=null) {
				    	ss=ss+" "+suffix;
				    }
				    String suffixOverrides=ele.attributeValue("suffixOverrides");
				    if(suffixOverrides!=null) {
				    	ss=replaceLast(ss, suffixOverrides, "");
				    }
					sqlAppend(sb,ss);
				}
			}else if(node.getName().equals("foreach")) {
				Element ele=(Element)node;
				String collection=ele.attributeValue("collection");
				String item=ele.attributeValue("item");
				String index=ele.attributeValue("index");
				String open=ele.attributeValue("open");
				String separator=ele.attributeValue("separator");
				String close=ele.attributeValue("close");
			    if(collection!=null) {
			    	if(item==null)item="item";
			    	if(index==null)index="index";
			    	if(open==null)open="(";
			    	if(separator==null)separator=",";
			    	if(close==null)close=")";
			    	Collection<?> list=(Collection<?>)params.get(collection);
			    	
			    	String sss="";
			    	int idx=0;
			    	for(Object list_item:list) {
			    		HashMap<String,Object> list_map=new HashMap<String,Object>();
			    		list_map.putAll(params);
			    		list_map.put(item, list_item);
			    		list_map.put(index, idx);
			    		String ss=parserElement(ele,list_map);
			    		if(ss!=null&&!ss.trim().isEmpty()) {
			    			Pattern pa = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
			    			Matcher ma = pa.matcher(ss);
			    			
			    			while(ma.find()) {
			    				String arg=ma.group().toString();
			    				if(item.equals(arg)||index.equals(arg))ss=ss.replaceAll("\\{"+arg+"\\}", list_map.get(arg).toString());
			    			}
			    			if(idx==0) {
			    				sss=ss;
			    			}else {
			    				sss+=separator+ss;
			    			}
						}
			    		idx++;
			    	}
			    	if(sss.length()>0)sqlAppend(sb,open+sss+close);
			    }
				
			}
		}
		return sb.toString();
	}
	
	//字符串拼接整理
	private static void sqlAppend(StringBuilder sb,String sql) {
		sql=sql.trim().replaceAll("\\s{1,}", " ");
		if(sb.length()==0) {
			sb.append(sql);
		}else {
			if(sb.lastIndexOf(",")==sb.length()-1) {
				sb.append(sql);
			}else {
				sb.append(" "+sql);
			}
		}
	}
	
	//替换最后匹配字符
	public static String replaceLast(String string, String match, String replace) {
	    if (string==null || string.isEmpty() || null == replace) {
	        return string;
	    }
	    StringBuilder sBuilder = new StringBuilder(string);
	    int lastIndexOf = sBuilder.lastIndexOf(match);
	    if (-1 == lastIndexOf) {
	        return string;
	    }
	    return sBuilder.replace(lastIndexOf, lastIndexOf + match.length(), replace).toString();
	}
}