package com.chs.wheel.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Part;
import com.chs.wheel.utils.IDUtils;

public class WheelUpload {
	
	public static Map<String,Object> uploads(Collection<Part> parts,String filedoc,String suffix,String linkdoc) throws Exception {
		Map<String,Object> res=new HashMap<String,Object>();
		
		File f=new File(filedoc);
		
		if(!f.exists()) {
			f.mkdirs(); 
		}
		
		List<String> list=new ArrayList<String>();
		for(Part part:parts) {
			String fname=IDUtils.getID()+suffix;
			if(part.getName().startsWith("file")) {
				String savePath=filedoc+fname;
				part.write(savePath);
				
				list.add(linkdoc+fname);
			}
		}
		
		res.put("urls", list);
		res.put("msg", "suc");
		res.put("code", 0);
		
		return res;
	}
}
