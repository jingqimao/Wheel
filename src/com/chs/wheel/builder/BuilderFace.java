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

public class BuilderFace {

	public static void scan(String con,String ser,String dao) {
		List<File> xmls=FileUtils.getFileByPack(con,".xml");
		for(File xml:xmls) {
			newSer(xml.getAbsolutePath(),xml.getName().split("\\.")[0].replace("Face", ""), con,ser,dao);
			newCon(xml.getAbsolutePath(),xml.getName().split("\\.")[0].replace("Face", ""), con,ser,dao);
		}
	}
	
	
	public static void newSer(String xml,String faceName,String conPkg,String serPkg,String daoPkg) {
		
		String output=FileUtils.getClassPathByPack(serPkg);
		
		boolean neew=true;
		if(FileUtils.isFile(output+"\\"+faceName+"Service.java")) {
			neew=false;
		}
		
		StringBuilder face=new StringBuilder();
		StringBuilder Imp=new StringBuilder();
		
		if(neew)face.append("package "+serPkg+";\n\n");
		if(neew)Imp.append("package "+serPkg+";\n\n");
		
		
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance(); 
		try {
			DocumentBuilder docb = docbf.newDocumentBuilder();
			Document doc = docb.parse(xml);
			NodeList ff = doc.getChildNodes().item(1).getChildNodes();
			
			if(neew) {
				face.append("{import}\n");
				Imp.append("import com.chs.wheel.core.Auto;\n"
						+ "import com.chs.wheel.core.BaseServiceImp;\n");
				Imp.append("{import}\n");
				
				face.append("\npublic interface "+faceName+"Service {\n\n");
				Imp.append("\npublic class "+faceName+"ServiceImp extends BaseServiceImp implements "+faceName+"Service{\n\n");
				Imp.append("{set}\n");
				
				face.append("\n	//Builder-Head");
				Imp.append("\n	//Builder-Head");
			}
			
			
			Map<String,String> fp=new HashMap<String,String>(),ip=new HashMap<String,String>(),ips=new HashMap<String,String>();
			for(int i=0;i<ff.getLength();i++){
				if(ff.item(i).getNodeType()==Node.ELEMENT_NODE){
					String dao=((Element)ff.item(i)).getAttribute("dao");
					String name=((Element)ff.item(i)).getAttribute("name");
					String out=((Element)ff.item(i)).getAttribute("out");
					String ache=((Element)ff.item(i)).getAttribute("ache");
					String empty=((Element)ff.item(i)).getAttribute("empty");
					
					if(empty!=null&&empty.contains("ser")) {
						continue;
					}
					
					System.out.print("Ser---"+faceName+"---building method "+name+" ...");
					
					NodeList daox = ff.item(i).getChildNodes();
					List<Map<String,String>> list=new ArrayList<Map<String,String>>();
					for(int j=0;j<daox.getLength();j++){
						if(daox.item(j).getNodeType()==Node.ELEMENT_NODE){
							Map<String,String> m=new HashMap<String,String>();
							m.put("tag", daox.item(j).getNodeName());
							m.put("arg", ((Element)daox.item(j)).getAttribute("arg"));
							if(((Element)daox.item(j)).hasAttribute("val"))m.put("val", ((Element)daox.item(j)).getAttribute("val"));
							list.add(m);
						}
					}
					
					String args=new String();
					String argss=new String();
					for(Map<String,String> m:list){
						if(m.get("tag").equals("in")) {
							String[] as=m.get("arg").split(":");
							if(m.get("val")==null) {
								argss+=","+as[0];
								args+=","+imp(as[1])+" "+as[0];
							}else {
								if(as[1].equals("s")) {
									argss+=",\""+m.get("val")+"\"";
								}else {
									argss+=","+m.get("val");
								}
							}
						}
					}
					if(args.length()>0)args=args.substring(1);
					if(argss.length()>0)argss=argss.substring(1);
					
					if("med".equals(ff.item(i).getNodeName())) {
						if(!ache.isEmpty()) {
							fp.put("import com.chs.wheel.core.AcheService;", "true");
							face.append("\n	@AcheService(set=\""+ache+"\")");
						}
						if(out!=null&&!out.isEmpty()) {
							face.append("\n	public "+imp(out)+" "+name+"("+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
									"	public "+imp(out)+" "+name+"("+args+") throws Exception {");
							
							if(out.equals("page")) {
								fp.put("import com.chs.wheel.core.PageRes;", "true");
								ip.put("import com.chs.wheel.core.PageRes;", "true");
								ip.put("import com.chs.wheel.core.PageRes;", "true");
							}else if(out.equals("list")) {
								ip.put("import java.util.List;", "true");
								fp.put("import java.util.List;", "true");
							}else if(out.equals("map")) {
								ip.put("import java.util.Map;", "true");
								fp.put("import java.util.Map;", "true");
							}else if(out.equals("listmap")) {
								ip.put("import java.util.List;", "true");
								ip.put("import java.util.Map;", "true");
								fp.put("import java.util.List;", "true");
								fp.put("import java.util.Map;", "true");
							}
						}else {
							face.append("\n	public boolean "+name+"("+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
										"	public boolean "+name+"("+args+") throws Exception {");
							
						}
						
						
						Imp.append("\n		return "+dao.replace("Dao", "_Dao")+"."+name+"(null"+(argss.length()>0?",":"")+argss+");");
						
						ip.put("import "+daoPkg+"."+dao+";", "true");
						ips.put("	@Auto\r\n	public "+dao+" "+dao.replace("Dao", "_Dao")+";", "true");
						
						
						Imp.append("\n	}\n");
						
					}
					System.out.println(" success!");
				}
			}
			
			if(neew) {
				face.append("\n	//Builder-Tail\n\n");
				Imp.append("\n	//Builder-Tail\n\n");
				
				face.append("\n}");
				Imp.append("\n}");
			}
			
			
			String ffp="",iip="",iips="";
			for(String k:fp.keySet()) {
				ffp+="\n"+k;
			}
			for(String k:ip.keySet()) {
				iip+="\n"+k;
			}
			for(String k:ips.keySet()) {
				iips+="\n"+k;
			}
			
			if(neew) {
				FileUtils.write(output+"\\"+faceName+"Service.java", face.toString().replace("{import}", ffp), "utf-8");
				FileUtils.write(output+"\\"+faceName+"ServiceImp.java", Imp.toString().replace("{import}", iip).replace("{set}", iips), "utf-8");
			}else {
				String ss=FileUtils.read(output+"\\"+faceName+"Service.java", "utf-8");
				ss=ss.substring(0, ss.indexOf("//Builder-Head")+14)+face.toString()+ss.substring(ss.indexOf("//Builder-Tail")-2);
				FileUtils.write(output+"\\"+faceName+"Service.java", ss, "utf-8");
				ss=FileUtils.read(output+"\\"+faceName+"ServiceImp.java", "utf-8");
				ss=ss.substring(0, ss.indexOf("//Builder-Head")+14)+Imp.toString()+ss.substring(ss.indexOf("//Builder-Tail")-2);
				FileUtils.write(output+"\\"+faceName+"ServiceImp.java", ss, "utf-8");
			}
			
			
			System.out.println("build Ser "+faceName+" success!");
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace(); 
		} catch (SAXException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public static void newCon(String xml,String faceName,String conPkg,String serPkg,String daoPkg) {
		
		String output=FileUtils.getClassPathByPack(conPkg);
		
		boolean neew=true;
		if(FileUtils.isFile(output+"\\"+faceName+"Controller.java")) {
			neew=false;
		}
		
		StringBuilder Imp=new StringBuilder();
		
		if(neew)Imp.append("package "+conPkg+";\n\n");
		
		
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance(); 
		try {
			DocumentBuilder docb = docbf.newDocumentBuilder();
			Document doc = docb.parse(xml);
			NodeList ff = doc.getChildNodes().item(1).getChildNodes();
			String head=((Element)doc.getChildNodes().item(1)).getAttribute("head");
			
			if(neew) {
				Imp.append("import com.chs.wheel.core.Auto;\r\n" + 
						"import com.chs.wheel.core.BackHand;\r\n" + 
						"import com.chs.wheel.core.ControllerMapping;\n");
				Imp.append("{import}\n");
				
				Imp.append("\n@ControllerMapping(url = \""+head+"\")"
						+ "\npublic class "+faceName+"Controller{\n\n");
				Imp.append("{set}\n");
				
				Imp.append("\n	//Builder-Head\n\n");
			}
			
			
			Map<String,String> ip=new HashMap<String,String>(),ips=new HashMap<String,String>();
			for(int i=0;i<ff.getLength();i++){
				if(ff.item(i).getNodeType()==Node.ELEMENT_NODE){
					String head2=((Element)ff.item(i)).getAttribute("head");
					String name=((Element)ff.item(i)).getAttribute("name");
					String out=((Element)ff.item(i)).getAttribute("out");
					String cout=((Element)ff.item(i)).getAttribute("cout");
					String empty=((Element)ff.item(i)).getAttribute("empty");
					
					if(empty!=null&&empty.contains("con")) {
						continue;
					}
					
					System.out.print("Con---"+faceName+"---building method "+name+" ...");
					
					NodeList daox = ff.item(i).getChildNodes();
					List<Map<String,String>> list=new ArrayList<Map<String,String>>();
					for(int j=0;j<daox.getLength();j++){
						if(daox.item(j).getNodeType()==Node.ELEMENT_NODE){
							Map<String,String> m=new HashMap<String,String>();
							m.put("tag", daox.item(j).getNodeName());
							m.put("arg", ((Element)daox.item(j)).getAttribute("arg"));
							if(((Element)daox.item(j)).hasAttribute("val"))m.put("val", ((Element)daox.item(j)).getAttribute("val"));
							list.add(m);
						}
					}
					
					String args=new String();
					String argss=new String();
					for(Map<String,String> m:list){
						if(m.get("tag").equals("in")) {
							String[] as=m.get("arg").split(":");
							if(m.get("val")==null) {
								argss+=","+as[0];
								args+=","+imp(as[1])+" "+as[0];
							}
						}
					}
					if(args.length()>0)args=args.substring(1);
					if(argss.length()>0)argss=argss.substring(1);
					
					if("med".equals(ff.item(i).getNodeName())) {
						
						Imp.append("\n	@ControllerMapping(url = \""+head2+"\")\n" + 
								"	public void "+name+"(BackHand backhand) throws Exception {\n");
						
						if(out.equals("page")) {
							ip.put("import com.chs.wheel.core.PageRes;", "true");
							ip.put("import com.chs.wheel.core.PageRes;", "true");
						}else if(out.equals("list")) {
							ip.put("import java.util.List;", "true");
						}else if(out.equals("map")) {
							ip.put("import java.util.Map;", "true");
						}else if(out.equals("listmap")) {
							ip.put("import java.util.List;", "true");
							ip.put("import java.util.Map;", "true");
						}
						
						for(Map<String,String> m:list){
							if(m.get("tag").equals("in")&&m.get("val")==null) {
								String[] as=m.get("arg").split(":");
								if(as[1].equals("s")) {
									Imp.append("\n		"+imp(as[1])+" "+as[0]+"=backhand.get(\""+as[0]+"\");");
								}else {
									Imp.append("\n		"+imp(as[1])+" "+as[0]+"="+getimpx(as[1])+"(backhand.get(\""+as[0]+"\"));");
								}
							}
						}
						Imp.append("\n		"+imp(out)+" res="+faceName+"_Service."+name+"("+argss+");");
						
						if(out.equals("i")&&cout!=null&&!cout.isEmpty()&&cout.equals("b")) {
							Imp.append("\n		backhand.output(res!=-1);");
						}else if(out.equals("page")){
							ip.put("import com.chs.wheel.utils.JSONUtils;", "true");
							Imp.append("\n		backhand.output(JSONUtils.newMap()\r\n" + 
									"				.put(\"count\", res.count)\r\n" + 
									"				.put(\"data\", res.data)\r\n" + 
									"				.put(\"code\", 0)\r\n" + 
									"				.put(\"msg\", \"\")\r\n" + 
									"				.get());");
						}else if(out.equals("i")||out.equals("f")||out.equals("d")||out.equals("l")){
							Imp.append("\n		backhand.output(res+\"\");");
						}else {
							Imp.append("\n		backhand.output(res);");
						}
						
						ip.put("import "+serPkg+"."+faceName+"Service;", "true");
						ips.put("	@Auto\r\n	public "+faceName+"Service "+faceName+"_Service"+";", "true");
						
						
						Imp.append("\n	}\n");
						
					}
					System.out.println(" success!");
				}
			}
			
			if(neew)Imp.append("\n	//Builder-Tail\n");
			
			if(neew)Imp.append("\n}");
			
			String iip="",iips="";
			for(String k:ip.keySet()) {
				iip+="\n"+k;
			}
			for(String k:ips.keySet()) {
				iips+="\n"+k;
			}
			
			if(neew) {
				FileUtils.write(output+"\\"+faceName+"Controller.java", Imp.toString().replace("{import}", iip).replace("{set}", iips), "utf-8");
			}else {
				String ss=FileUtils.read(output+"\\"+faceName+"Controller.java", "utf-8");
				if(Imp.toString().isEmpty()) { 
					Imp=new StringBuilder("\n");  
				}else {
					ss=ss.substring(0, ss.indexOf("//Builder-Head")+14)+Imp.toString()+ss.substring(ss.indexOf("//Builder-Tail")-2);
				}
				FileUtils.write(output+"\\"+faceName+"Controller.java", ss, "utf-8");
			}
			
			System.out.println("build Con "+faceName+" success!");
			
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
			case "b": return "boolean";
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
			case "s": return "getString";
			case "i": return "getInt";
			case "f": return "getFloat";
			case "l": return "getLong";
			case "d": return "getDouble";
			default :return null;
		}
	}
	public static String getimpx(String code) {
		switch (code) {
			case "i": return "Integer.parseInt";
			case "f": return "Float.parseFloat";
			case "l": return "Long.parseLong";
			case "d": return "Double.parseDouble";
			case "b": return "Boolean.parseBoolean";
			default :return null;
		}
	}
}
