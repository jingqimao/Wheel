package com.chs.wheel.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chs.wheel.utils.DoFile;
import com.chs.wheel.utils.FileUtils;

public class BuilderLangFile {
	
	public static void scan(String properties,String path) {
		
		Map<String,LinkedHashMap<String,String>> pm=FileUtils.getProperties(properties, "utf-8");
		
		for(String tagName:pm.keySet()) {
			System.out.println("正在翻译"+tagName+"版本..");
			translate(path, tagName, pm.get(tagName));
			System.out.println("翻译"+tagName+"版本完成!");
		}
	}
	
	public static void translate(String path,String tagName,LinkedHashMap<String,String> lang) {
		
		String pathx=FileUtils.fileUp(path, 2)+"/"+tagName+"/";
		
		FileUtils.dir(pathx);
		
		try {
			FileUtils.copyFile(path, pathx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		List<Map<String,Object>> lang_list=new ArrayList<Map<String,Object>>();
		for(String k:lang.keySet()) {
			Map<String,Object> m=new HashMap<String,Object>();
			m.put("k", k.split("=@@=")[0]);
			m.put("v", lang.get(k));
			m.put("index", 0);
			if(k.split("=@@=").length>1)m.put("vf", k.split("=@@=")[1]);
			lang_list.add(m);
		}
		for(Map<String,Object> m:lang_list) {
			for(Map<String,Object> mm:lang_list) {
				if(m.get("k").toString().contains(mm.get("k").toString())) {
					m.put("index", Integer.parseInt(m.get("index").toString())+1);
				}
			}
		}
		lang_list.sort(new Comparator<Map<String,Object>>() {

			@Override
			public int compare(Map<String,Object> m1, Map<String,Object> m2) {
				
				return Integer.parseInt(m2.get("index").toString())-Integer.parseInt(m1.get("index").toString());
			}
		});
		Map<String,String> lang2=new LinkedHashMap<String,String>();
		for(Map<String,Object> m:lang_list) {
			if(m.containsKey("vf")) {
				lang2.put(m.get("k").toString()+"=@@="+m.get("vf"), m.get("v").toString());
			}else {
				lang2.put(m.get("k").toString(), m.get("v").toString());
			}
		}
		
		FileUtils.doFile(pathx, new DoFile() {
			
			@Override
			public File doit(String path, String fileName, boolean isFile, File file) {
				if(isFile&&(FileUtils.getType(fileName).equals("jsp")||FileUtils.getType(fileName).equals("js"))) {
					String text=FileUtils.read(path, "utf-8");
					
					/*Pattern py=Pattern.compile(".*(\\+\\[[^\\]].*\\]).*");
					Pattern pyy=Pattern.compile("(?<=\\[)(.*)(?=\\])");
					Pattern pn=Pattern.compile(".*(-\\[[^\\]].*\\]).*");
					Pattern pnn=Pattern.compile("(?<=\\[)(.*)(?=\\])");*/
					
					Pattern py=Pattern.compile("\\+(.+)");
					Pattern pn=Pattern.compile("-(.+)");
					
					boolean isfilter=true;
					
					for(String k:lang2.keySet()) {
						isfilter=true;
						String[] ks=k.split("=@@=");
						
						if(ks.length>1) {
							String[] vv=ks[1].split(",");
							
							List<String> vfy=new ArrayList<String>();
							List<String> vfn=new ArrayList<String>();
							
							for(String vf:vv) {
								if(py.matcher(vf).matches())vfy.add(vf);
								if(pn.matcher(vf).matches())vfn.add(vf);
							}
							
							if(vfy.size()>0) {
								boolean isVfy=false;
								for(String vf:vfy) {
									vf=vf.substring(1);
									if(path.indexOf(vf)>-1) {
										isVfy=true;
									}
								}
								if(isVfy==false)isfilter=false;
							}
							
							if(isfilter) {
								if(vfn.size()>0) {
									for(String vf:vfn) {
										vf=vf.substring(1);
										if(path.indexOf(vf)>-1) {
											isfilter=false;
										}
									}
								}
							}
						}
						
						
						/*if(py.matcher(k).matches()) {
							isfilter=false;
							
							Matcher m=pyy.matcher(k);
							m.find();
							String filter=m.group();
							for(String fi:filter.split(",")) {
								if(fileName.indexOf(fi)>-1) {
									isfilter=true;
									break;
								}
							}
							
							k=k.replaceAll("\\+\\["+filter+"\\]", "");
						}
						if(pn.matcher(k).matches()) {
							isfilter=true;
							
							Matcher m=pnn.matcher(k);
							m.find();
							String filter=m.group();
							for(String fi:filter.split(",")) {
								if(fileName.indexOf(fi)>-1) {
									isfilter=false;
									break;
								}
							}
							
							k=k.replaceAll("-\\["+filter+"\\]", "");
						}*/
						
						if(isfilter) {
							String key=ks[0].replaceAll("\\{", "\\\\\\{").replaceAll("\\}", "\\\\\\}").replaceAll("\\(", "\\\\\\(").replaceAll("\\)", "\\\\\\)").replaceAll("\\?", "\\\\\\?").replaceAll("\\<", "\\\\\\<").replaceAll("\\[", "\\\\\\[").replaceAll("\\]", "\\\\\\]").replaceAll("\\+", "\\\\\\+");
							String val=lang2.get(k).replaceAll(Matcher.quoteReplacement("$"), "\\\\\\$");
							
							text=text.replaceAll(key, val);
						}
					}
					FileUtils.write(path, text, "utf-8");
				}
				return file;
			}
		});
	}
}
