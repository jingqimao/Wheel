package com.chs.wheel.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.chs.wheel.utils.FileUtils;

public class BuilderDao {

	public static void scan(String pkg) {
		List<File> xmls=FileUtils.getFileByPack(pkg,".xml");
		for(File xml:xmls) {
			newDao(xml.getAbsolutePath(),xml.getName().split("\\.")[0], xml.getPath().substring(0, xml.getPath().lastIndexOf("\\")).replace("build\\classes", "src"),pkg);
		}
	}
	
	
	public static void newDao(String xml,String daoName,String output,String pkg) {
		
		StringBuilder face=new StringBuilder();
		StringBuilder Imp=new StringBuilder();
		
		face.append("package "+pkg+";\n\n");
		Imp.append("package "+pkg+";\n\n");
		
		
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance(); 
		try {
			DocumentBuilder docb = docbf.newDocumentBuilder();
			Document doc = docb.parse(xml);
			NodeList dao = doc.getChildNodes().item(1).getChildNodes();
			String db=((Element)doc.getChildNodes().item(1)).getAttribute("db");
			
			face.append("import java.sql.Connection;\n");
			face.append("import com.chs.wheel.core.AutoConn;\n");
			face.append("{import}\n");
			Imp.append("import java.sql.Connection;\n");
			Imp.append("{import}\n");
			
			face.append("\npublic interface "+daoName+" {\n\n");
			Imp.append("\npublic class "+daoName+"Imp implements "+daoName+"{\n\n");
			Imp.append("\n	public String DATASOURCE=\""+db+"\";\n\n");
			
			Map<String,String> fp=new HashMap<String,String>(),ip=new HashMap<String,String>();
			for(int i=0;i<dao.getLength();i++){
				if(dao.item(i).getNodeType()==Node.ELEMENT_NODE){
					String name=((Element)dao.item(i)).getAttribute("name");
					String tb=((Element)dao.item(i)).getAttribute("tb");
					String out=((Element)dao.item(i)).getAttribute("out");
					
					System.out.print("DAO---"+daoName+"---building method "+name+" ...");
					
					NodeList daox = dao.item(i).getChildNodes();
					List<Map<String,String>> list=new ArrayList<Map<String,String>>();
					for(int j=0;j<daox.getLength();j++){
						if(daox.item(j).getNodeType()==Node.ELEMENT_NODE){
							Map<String,String> m=new HashMap<String,String>();
							m.put("tag", daox.item(j).getNodeName());
							m.put("arg", ((Element)daox.item(j)).getAttribute("arg"));
							if(((Element)daox.item(j)).getAttribute("handle")!=null)m.put("handle", ((Element)daox.item(j)).getAttribute("handle"));
							if(((Element)daox.item(j)).getAttribute("eq")!=null&&!((Element)daox.item(j)).getAttribute("eq").isEmpty())m.put("eq", ((Element)daox.item(j)).getAttribute("eq").replaceAll("&lt;", "<").replaceAll("&gt;", ">"));
							if(((Element)daox.item(j)).getFirstChild()!=null&&((Text)((Element)daox.item(j)).getFirstChild()).getData()!=null)m.put("val", ((Text)((Element)daox.item(j)).getFirstChild()).getData());
							list.add(m);
						}
					}
					
					String args=new String();
					for(Map<String,String> m:list){
						if(m.get("tag").equals("in")||m.get("tag").equals("wh")) {
							String[] as=m.get("arg").split(":");
							args+=","+imp(as[1])+" "+as[0];
						}
					}
					if(args.length()>0)args=args.substring(1);
					
					if("add".equals(dao.item(i).getNodeName())) {
						
						if(out!=null&&!out.isEmpty()) {
							String[] as=out.split(":");
							face.append("\n	public "+imp(as[1])+" "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
									"	public "+imp(as[1])+" "+name+"(Connection conn,"+args+") throws Exception {");
						}else {
							face.append("\n	public boolean "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
										"	public boolean "+name+"(Connection conn,"+args+") throws Exception {");
							
						}
						
						String sql="insert into "+tb+" (";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("in")) {
								String[] ass=m.get("arg").split(":");
								sql+=ass[0]+",";
							}
						}
						sql=sql.substring(0, sql.length()-1);
						sql+=") values (";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("in"))sql+="?,";
						}
						sql=sql.substring(0, sql.length()-1);
						sql+=")";
						Imp.append("\n		String sql=\""+sql+"\";");
						Imp.append("\n		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);");
						ip.put("import java.sql.Statement;", "true");
						ip.put("import java.sql.PreparedStatement;", "true");
						
						int n=1;
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("in")) {
								String[] ass=m.get("arg").split(":");
								Imp.append("\n		ps.setObject("+n+", "+ass[0]+");");
								n++;
							}
						}
						Imp.append("\n		int res=ps.executeUpdate();");
						
						if(out!=null&&!out.isEmpty()) {
							String[] as=out.split(":");
							Imp.append("\n		if(res!=-1){");
							Imp.append("\n			ResultSet rs=ps.getGeneratedKeys();");
							ip.put("import java.sql.ResultSet;", "true");
							Imp.append("\n			"+imp(as[1])+" "+as[0]+" = "+defimp(as[1])+";");
							Imp.append("\n			if(rs.next()) {\r\n" + 
									"				"+as[0]+" = rs."+getimp(as[1])+"(1);\r\n" + 
									"			}");
							Imp.append("\n			return "+as[0]+";");
							Imp.append("\n		}\n		return "+defimp(as[1])+";");
						}else {
							Imp.append("\n		if(res!=0){");
							Imp.append("\n			return true;");
							Imp.append("\n		}\n		return false;");
						}
						
						Imp.append("\n	}\n");
						
					}
					if("del".equals(dao.item(i).getNodeName())) {
						if(out!=null&&!out.isEmpty()&&out.equals("count:i")) {
							String[] as=out.split(":");
							face.append("\n	public "+imp(as[1])+" "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
									"	public "+imp(as[1])+" "+name+"(Connection conn,"+args+") throws Exception {");
						}else {
							face.append("\n	public boolean "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
										"	public boolean "+name+"(Connection conn,"+args+") throws Exception {");
							
						}
						
						String sql="delete from "+tb+" where ";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("wh")) {
								String[] ass=m.get("arg").split(":");
								if(m.get("eq")!=null) {
									if(m.get("eq").equals("like")) {
										sql+="locate(?,"+ass[0]+")>0 and ";
									}else {
										sql+=ass[0]+" "+m.get("eq")+" ? and ";
									}
								}else {
									sql+=ass[0]+" = ? and ";
								}
								
							}
						}
						sql=sql.substring(0, sql.length()-5);
						
						Imp.append("\n		String sql=\""+sql+"\";");
						Imp.append("\n		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);");
						ip.put("import java.sql.Statement;", "true");
						ip.put("import java.sql.PreparedStatement;", "true");
						
						int n=1;
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("wh")) {
								String[] ass=m.get("arg").split(":");
								Imp.append("\n		ps.setObject("+n+", "+ass[0]+");");
								n++;
							}
						}
						if(out!=null&&!out.isEmpty()&&out.equals("count:i")) {
							Imp.append("\n		return ps.executeUpdate();");
						}else {
							Imp.append("\n		int res=ps.executeUpdate();");
							Imp.append("\n		if(res!=0){");
							Imp.append("\n			return true;");
							Imp.append("\n		}\n		return false;");
						}
						
						Imp.append("\n	}\n");
					}
					if("upd".equals(dao.item(i).getNodeName())) {
						if(out!=null&&!out.isEmpty()&&out.equals("count:i")) {
							String[] as=out.split(":");
							face.append("\n	public "+imp(as[1])+" "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
									"	public "+imp(as[1])+" "+name+"(Connection conn,"+args+") throws Exception {");
						}else {
							face.append("\n	public boolean "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
										"	public boolean "+name+"(Connection conn,"+args+") throws Exception {");
							
						}
						
						String sql="update "+tb+" set ";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("in")) {
								String[] ass=m.get("arg").split(":");
								sql+=ass[0]+" = ?,";
							}
						}
						sql=sql.substring(0, sql.length()-1);
						
						sql+=" where ";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("wh")) {
								String[] ass=m.get("arg").split(":");
								if(m.get("eq")!=null) {
									if(m.get("eq").equals("like")) {
										sql+="locate(?,"+ass[0]+")>0 and ";
									}else {
										sql+=ass[0]+" "+m.get("eq")+" \"+"+ass[0]+"+\" and ";
									}
								}else {
									if(ass[1].equals("s")) {
										sql+=ass[0]+" = '\"+"+ass[0]+"+\"' and ";
									}else {
										sql+=ass[0]+" = \"+"+ass[0]+"+\" and ";
									}
								}
							}
						}
						sql=sql.substring(0, sql.length()-5);
						
						
						Imp.append("\n		String sql=\""+sql+"\";");
						Imp.append("\n		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);");
						ip.put("import java.sql.Statement;", "true");
						ip.put("import java.sql.PreparedStatement;", "true");
						
						int n=1;
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("in")) {
								String[] ass=m.get("arg").split(":");
								Imp.append("\n		ps.setObject("+n+", "+ass[0]+");");
								n++;
							}
						}
						if(out!=null&&!out.isEmpty()&&out.equals("num:i")) {
							Imp.append("\n		return ps.executeUpdate();");
						}else {
							Imp.append("\n		int res=ps.executeUpdate();");
							Imp.append("\n		if(res!=0){");
							Imp.append("\n			return true;");
							Imp.append("\n		}\n		return false;");
						}
						
						Imp.append("\n	}\n");
					}
					if("sel".equals(dao.item(i).getNodeName())) {
						String key=((Element)dao.item(i)).getAttribute("key");
						String rank=((Element)dao.item(i)).getAttribute("rank");
						//String ache=((Element)dao.item(i)).getAttribute("ache");
						
						if(rank!=null&&!rank.isEmpty()) {
							String[] rr=rank.split(":");
							if(rr[0].equals("*")) {
								args+=",String rank";
							}
							if(rr.length>1&&rr[1].equals("*")) {
								args+=",boolean desc";
							}
						}
						
						if(out!=null&&!out.isEmpty()) {
							if(out.contains(":")) {
								face.append("\n	public "+imp(out.split(":")[1])+" "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
								Imp.append("\n	@Override\n" + 
										"	public "+imp(out.split(":")[1])+" "+name+"(Connection conn,"+args+") throws Exception {");
							}else {
								if(out.equals("page")) {
									args+=",int page";
									args+=",int pageSize";
								}
								face.append("\n	public "+imp(out)+" "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
								Imp.append("\n	@Override\n" + 
										"	public "+imp(out)+" "+name+"(Connection conn,"+args+") throws Exception {");
							}
						}else {
							face.append("\n	public "+imp("listmap")+" "+name+"(@AutoConn Connection conn,"+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
										"	public "+imp("listmap")+" "+name+"(Connection conn,"+args+") throws Exception {");
							
						}
						
						
						String sql="select * from "+tb;
						
						if(key!=null&&!key.isEmpty()) {
							sql="select "+key+" from "+tb;
						}
						
						String sqlx=sql+"";
						
						sql+=" where ";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("wh")) {
								String[] ass=m.get("arg").split(":");
								if(m.get("eq")!=null) {
									if(m.get("eq").equals("like")) {
										sql+="locate(?,"+ass[0]+")>0 and ";
									}else {
										m.put("eq", m.get("eq").replace("&gt;", ">").replace("&lt;", "<"));
										sql+=ass[0]+" "+m.get("eq")+" ? and ";
									}
								}else {
									sql+=ass[0]+" = ? and ";
								}
							}
						}
						sql=sql.substring(0, sql.length()-5);
						
						if(out!=null&&!out.isEmpty()&&out.equals("page"))sqlx=sql.replace(sqlx, "select count(*) as count from "+tb);
						
						if(rank!=null&&!rank.isEmpty()) {
							String[] rr=rank.split(":");
							if(rr[0].equals("*")) {
								sql+=" order by \"+rank+\" ";
							}else {
								sql+=" order by "+rank+" ";
							}
							if(rr.length>1) {
								if(rr[1].equals("*")) {
									sql+="\"+(desc==true?\"desc\":\"asc\")+\"";
								}else if(rr[1].equals("d")) {
									sql+="desc";
								}else if(rr[1].equals("a")){
									sql+="asc";
								}
							}else {
								sql+="desc";
							}
						}
						
						if(out!=null&&!out.isEmpty()&&out.equals("page")) {
							Imp.append("\n		String sql=\""+sql+" limit \"+((page-1)*pageSize)+\",\"+pageSize;");
						}else {
							Imp.append("\n		String sql=\""+sql+"\";");
						}
						
						if(out!=null&&!out.isEmpty()&&out.equals("page"))Imp.append("\n		String sqlx=\""+sqlx+"\";");
						Imp.append("\n		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);");
						ip.put("import java.sql.Statement;", "true");
						ip.put("import java.sql.PreparedStatement;", "true");
						
						int n=1;
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("wh")) {
								String[] ass=m.get("arg").split(":");
								Imp.append("\n		ps.setObject("+n+", "+ass[0]+");");
								n++;
							}
						}
						ip.put("import java.util.List;", "true");
						ip.put("import java.util.Map;", "true");
						ip.put("import com.chs.wheel.utils.DBUtils;", "true");
						if(out==null||out.isEmpty()||out.equals("listmap"))fp.put("import java.util.List;", "true");
						if(out==null||out.isEmpty()||(out.equals("listmap")||out.equals("map")))fp.put("import java.util.Map;", "true");
						Imp.append("\n		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());");
						
						if(out!=null&&!out.isEmpty()&&out.equals("map")) {
							Imp.append("\n		if(list.size()>0){");
							Imp.append("\n			return list.get(0);");
							Imp.append("\n		}\n		return null;");
						}
						if(out!=null&&!out.isEmpty()&&out.contains(":")) {
							Imp.append("\n		if(list.size()>0){");
							Imp.append("\n			return ("+imp(out.split(":")[1])+")list.get(0).get(\""+out.split(":")[0]+"\");");
							Imp.append("\n		}\n		return null;");
						}
						if(out==null||out.isEmpty()||out.equals("listmap")) {
							Imp.append("\n		return list;");
						}
						if(out!=null&&!out.isEmpty()&&out.equals("page")) {
							ip.put("import com.chs.wheel.core.PageRes;", "true");
							fp.put("import com.chs.wheel.core.PageRes;", "true");
							Imp.append("\n		PreparedStatement psx=conn.prepareStatement(sqlx,Statement.RETURN_GENERATED_KEYS);");
							n=1;
							for(Map<String,String> m:list) {
								if(m.get("tag").equals("wh")) {
									String[] ass=m.get("arg").split(":");
									Imp.append("\n		psx.setObject("+n+", "+ass[0]+");");
									n++;
								}
							}
							Imp.append("\n		List<Map<String, Object>> listx=DBUtils.getMapByResult(psx.executeQuery());");
							Imp.append("\n		int count=Integer.parseInt(listx.get(0).get(\"count\").toString());");
							Imp.append("\n		return new PageRes(list,count);");
						}
						
						Imp.append("\n	}\n");
					}
					if("exc".equals(dao.item(i).getNodeName())) {
						//String ache=((Element)dao.item(i)).getAttribute("ache");
						String sp=args.length()>0?",":"";
						if(out!=null&&!out.isEmpty()) {
							if(out.equals("page")) {
								args+=",int page";
								args+=",int pageSize";
							}
							if(out!=null&&!out.isEmpty()&&out.contains(":")) {
								face.append("\n	public "+imp(out.split(":")[1])+" "+name+"(@AutoConn Connection conn"+sp+args+") throws Exception;");
								Imp.append("\n	@Override\n" + 
										"	public "+imp(out.split(":")[1])+" "+name+"(Connection conn"+sp+args+") throws Exception {");
							}else {
								face.append("\n	public "+imp(out)+" "+name+"(@AutoConn Connection conn"+sp+args+") throws Exception;");
								Imp.append("\n	@Override\n" + 
										"	public "+imp(out)+" "+name+"(Connection conn"+sp+args+") throws Exception {");
							}
						}else {
							face.append("\n	public boolean "+name+"(@AutoConn Connection conn"+sp+args+") throws Exception;");
							Imp.append("\n	@Override\n" + 
										"	public boolean "+name+"(Connection conn"+sp+args+") throws Exception {");
							
						}
						
						String sql="";
						for(Map<String,String> m:list) {
							if(m.get("tag").equals("sql")) {
								sql=m.get("val").replace("&gt;", ">").replace("&lt;", "<");
							}
						}
						
						
						Pattern pa = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
						Matcher ma = pa.matcher(sql);
						List<Map<String,String>> listx=new ArrayList<Map<String,String>>();
						
						while(ma.find()) {
							String ss=ma.group().toString();
							for(Map<String,String> m:list) {
								String[] as=m.get("arg").split(":");
								if(m.get("tag").equals("in")&&as[0].equals(ss)) {
									listx.add(m);
								}
							}
							sql=sql.replaceAll("\\{"+ss+"\\}", "?");
						}
						
						Pattern pa2 = Pattern.compile("(?<=\\[)(.+?)(?=\\])");
						Matcher ma2 = pa2.matcher(sql);
						List<Map<String,String>> listx2=new ArrayList<Map<String,String>>();
						
						while(ma2.find()) {
							String ss=ma2.group().toString();
							for(Map<String,String> m:list) {
								String[] as=m.get("arg").split(":");
								if(m.get("tag").equals("wh")&&as[0].equals(ss)) {
									listx2.add(m);
									sql=sql.replaceAll("\\["+ss+"\\]", "\"+"+ss+"+\"");
								}
							}
						}
						
						
						
						String sqlx=sql+"";
						if(out!=null&&!out.isEmpty()&&out.equals("page")) {
							sqlx="select count(*) as count from ("+sql+") as tb";
						}
						
						int n=1;
						if((out==null||out.isEmpty())||(out!=null&&!out.isEmpty()&&!out.equals("count"))) {
							if(out!=null&&!out.isEmpty()&&out.equals("page")) {
								Imp.append("\n		String sql=\""+sql+" limit \"+((page-1)*pageSize)+\",\"+pageSize;");
							}else {
								Imp.append("\n		String sql=\""+sql+"\";");
							}
							
							Imp.append("\n		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);");
							n=1;
							for(Map<String,String> m:listx) {
								String[] ass=m.get("arg").split(":");
								Imp.append("\n		ps.setObject("+n+", "+ass[0]+");");
								n++;
							}
							
							ip.put("import java.sql.Statement;", "true");
							ip.put("import java.sql.PreparedStatement;", "true");
						}
						
						if(out==null||out.isEmpty()) {
							Imp.append("\n		int res=ps.executeUpdate();");
							Imp.append("\n		if(res!=0){");
							Imp.append("\n			return true;");
							Imp.append("\n		}\n		return false;");
						}else if(out.equals("id:i")){
							Imp.append("\n		int res=ps.executeUpdate();");
							String[] as=out.split(":");
							Imp.append("\n		if(res!=-1){");
							Imp.append("\n			ResultSet rs=ps.getGeneratedKeys();");
							ip.put("import java.sql.ResultSet;", "true");
							Imp.append("\n			"+imp(as[1])+" "+as[0]+" = "+defimp(as[1])+";");
							Imp.append("\n			if(rs.next()) {\r\n" + 
									"				"+as[0]+" = rs."+getimp(as[1])+"(1);\r\n" + 
									"			}");
							Imp.append("\n			return "+as[0]+";");
							Imp.append("\n		}\n		return "+defimp(as[1])+";");
						}else {
							if(sql.trim().toLowerCase().startsWith("select")) {
								if(out.equals("count")) {
									Imp.append("\n		String sqlx=\"select count(*) as count from("+sql+") as tb\";");
									Imp.append("\n		PreparedStatement psx=conn.prepareStatement(sqlx,Statement.RETURN_GENERATED_KEYS);");
									n=1;
									for(Map<String,String> m:listx) {
										String[] ass=m.get("arg").split(":");
										Imp.append("\n		psx.setObject("+n+", "+ass[0]+");");
										n++;
									}
									Imp.append("\n		List<Map<String, Object>> listx=DBUtils.getMapByResult(psx.executeQuery());");
									Imp.append("\n		int count=Integer.parseInt(listx.get(0).get(\"count\").toString());");
									Imp.append("\n		return count;");
								}else {
									Imp.append("\n		List<Map<String, Object>> list=DBUtils.getMapByResult(ps.executeQuery());");
									if(out!=null&&!out.isEmpty()&&out.contains(":")) {
										if(!out.split(":")[0].contains("[")) {
											Imp.append("\n		if(list.size()>0){");
											Imp.append("\n			return ("+imp(out.split(":")[1])+")list.get(0).get(\""+out.split(":")[0]+"\");");
											Imp.append("\n		}\n		return null;");
										}else {
											Imp.append("\n		if(list.size()>0){");
											Imp.append("\n			return ("+imp(out.split(":")[1])+")list.get(0).get("+out.split(":")[0].replace("[", "").replace("]", "")+");");
											Imp.append("\n		}\n		return null;");
										}
									}
									if(out.equals("map")) {
										Imp.append("\n		if(list.size()>0){");
										Imp.append("\n			return list.get(0);");
										Imp.append("\n		}\n		return null;");
									}
									if(out.equals("listmap")) {
										Imp.append("\n		return list;");
									}
									if(out.equals("page")) {
										Imp.append("\n		String sqlx=\""+sqlx+"\";");
										Imp.append("\n		PreparedStatement psx=conn.prepareStatement(sqlx,Statement.RETURN_GENERATED_KEYS);");
										n=1;
										for(Map<String,String> m:listx) {
											String[] ass=m.get("arg").split(":");
											Imp.append("\n		psx.setObject("+n+", "+ass[0]+");");
											n++;
										}
										
										ip.put("import com.chs.wheel.core.PageRes;", "true");
										fp.put("import com.chs.wheel.core.PageRes;", "true");
										Imp.append("\n		List<Map<String, Object>> listx=DBUtils.getMapByResult(psx.executeQuery());");
										Imp.append("\n		int count=Integer.parseInt(listx.get(0).get(\"count\").toString());");
										Imp.append("\n		return new PageRes(list,count);");
									}
								}
							}else {
								if(out.equals("count")) {
									Imp.append("\n		return ps.executeUpdate();");
								}else {
									Imp.append("\n		int res=ps.executeUpdate();");
									Imp.append("\n		if(res!=0){");
									Imp.append("\n			return true;");
									Imp.append("\n		}\n		return false;");
								}
							}
							
						}
						
						Imp.append("\n	}\n");
					}
					System.out.println(" success!");
				}
			}
			
			face.append("\n}");
			Imp.append("\n}");
			
			String ffp="",iip="";
			for(String k:fp.keySet()) {
				ffp+="\n"+k;
			}
			for(String k:ip.keySet()) {
				iip+="\n"+k;
			}
			
			
			FileUtils.write(output+"\\"+daoName+".java", face.toString().replace("{import}", ffp), "utf-8");
			FileUtils.write(output+"\\"+daoName+"Imp.java", Imp.toString().replace("{import}", iip), "utf-8");
			
			System.out.println("build "+daoName+" success!");
			
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
			case "s": return "getString";
			case "i": return "getInt";
			case "f": return "getFloat";
			case "l": return "getLong";
			case "d": return "getDouble";
			default :return null;
		}
	}
}
