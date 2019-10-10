package com.chs.wheel.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.chs.wheel.utils.FileUtils;

public class BuilderWebService {
	
	public static void scan(String pkg) {
		List<File> xmls=FileUtils.getFileByPack(pkg,".xml");
		for(File xml:xmls) {
			newWebService(xml.getAbsolutePath(),xml.getName().split("\\.")[0], xml.getPath().substring(0, xml.getPath().lastIndexOf("\\")).replace("build\\classes", "src"),pkg);
		}
	}
	
	public static void newWebService(String xml,String webServiceName,String output,String pkg) {
		
		StringBuilder face=new StringBuilder();
		
		face.append("package "+pkg+";\n\n");
		
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance(); 
		
		try {
			DocumentBuilder docb = docbf.newDocumentBuilder();
			Document doc = docb.parse(xml);
			NodeList ws = doc.getChildNodes().item(1).getChildNodes();
			String wsname=((Element)doc.getChildNodes().item(1)).getAttribute("name");
			String url=((Element)doc.getChildNodes().item(1)).getAttribute("url");
			
			face.append("import com.chs.wheel.core.WheelService;\n");
			face.append("{import}\n");
			
			face.append("\npublic class "+webServiceName+"{\n\n");
			
			
			Map<String,String> fp=new HashMap<String,String>();
			
			for(int i=0;i<ws.getLength();i++){
				if(ws.item(i).getNodeType()==Node.ELEMENT_NODE){
					String name=((Element)ws.item(i)).getAttribute("name");
					String out=((Element)ws.item(i)).getAttribute("out");
					
					
					
					System.out.println("WebService---"+webServiceName+"---building method "+name+" ...");
					
					NodeList wsx = ws.item(i).getChildNodes();
					List<Map<String,String>> list=new ArrayList<Map<String,String>>();
					for(int j=0;j<wsx.getLength();j++){
						if(wsx.item(j).getNodeType()==Node.ELEMENT_NODE){
							Map<String,String> m=new HashMap<String,String>();
							m.put("arg", ((Element)wsx.item(j)).getAttribute("arg"));
							list.add(m);
						}
					}
					
					String args=new String();
					String[] argsType=new String[list.size()];
					String[] argsTypex=new String[list.size()];
					String[] arglist=new String[list.size()];
					int n=0;
					for(Map<String,String> m:list){
						String[] as=m.get("arg").split(":");
						args+=","+imp(as[1])+" "+as[0];
						argsType[n]=imp(as[1]);
						argsTypex[n]=as[1];
						arglist[n]=as[0];
						n++;
					}
					if(args.length()>0)args=args.substring(1);
					
					if("method".equals(ws.item(i).getNodeName())) {
						
						if(out!=null&&!out.isEmpty()) {
							face.append("\n	public static "+imp(out)+" "+name+"("+args+") throws Exception {");
							if(out.equals("page")) {
								fp.put("import com.chs.wheel.core.PageRes;", "true");
							}
						}else {
							face.append("\n	public static void "+name+"("+args+") throws Exception {");
						}
						
						face.append("\n		String url=\""+url+"\";");
						face.append("\n		String service=\""+wsname+"-"+name+"\";");
						if(out!=null&&!out.isEmpty()) {
							face.append("\n		String callBackType=\""+imp(out)+"\";");
						}else {
							face.append("\n		String callBackType=null;");
						}
						
						
						String tps="\n		String[] argsType={";
						for(String tp:argsType) {
							tps+="\""+tp+"\",";
						}
						tps=tps.substring(0, tps.length()-1);
						tps+="};";
						face.append(tps);
						
						String as="\n		byte[][] args={";
						for(int j=0;j<arglist.length;j++) {
							if(argsTypex[j].equals("s")) {
								as+="((String)"+arglist[j]+").getBytes(),";
							}else {
								as+="WheelService."+getimpx(argsTypex[j])+"("+arglist[j]+"),";
							}
						}
						as=as.substring(0, as.length()-1);
						as+="};";
						face.append(as);
						
						if(out!=null&&!out.isEmpty()) {
							face.append("\n		return "+getimp(out)+"(WheelService.doit(url,service,callBackType,argsType,args));");
						}else {
							face.append("\n		WheelService.doit(url,service,callBackType,argsType,args);");
						}
						
						
						face.append("\n	}\n");
						
					}
					
				}
			}
			
			face.append("\n}");
			
			String ffp="";
			for(String k:fp.keySet()) {
				ffp+="\n"+k;
			}
		
			FileUtils.write(output+"\\"+webServiceName+".java", face.toString().replace("{import}", ffp), "utf-8");
			
			System.out.println("build "+webServiceName+" success!");
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace(); 
		} catch (SAXException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	public static String imp(String code) {
		switch (code) {
			case "s": return "String";
			case "i": return "int";
			case "f": return "float";
			case "l": return "long";
			case "d": return "double";
			case "map": return "Map<String,Object>";
			case "listmap": return "List<Map<String,Object>>";
			case "page": return "PageRes";
			case "count": return "int";
			default :return null;
		}
	}
	public static String defimp(String code) {
		switch (code) {
			case "s": return "null";
			case "i": return "-1";
			case "f": return "-1"; 
			case "l": return "-1";
			case "d": return "-1";
			case "map": return "null";
			case "listmap": return "null";
			case "page": return "null";
			case "count": return "-1";
			default :return null;
		}
	}
	
	public static String getimp(String code) {
		switch (code) {
			case "s": return "new String";
			case "i": return "bytesToInt";
			case "f": return "bytesToFloat";
			case "l": return "bytesToLong";
			case "d": return "bytesToDouble";
			case "page": return "(PageRes)WheelService.deserializeToObject";
			default :return null;
		}
	}
	
	public static String getimpx(String code) {
		switch (code) {
			case "s": return "new String";
			case "i": return "intToBytes";
			case "f": return "floatToBytes";
			case "l": return "longToBytes";
			case "d": return "doubleToBytes";
			default :return null;
		}
	}
}
