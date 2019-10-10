package com.chs.wheel.builder;

import java.io.File;
import java.util.List;

import com.chs.wheel.utils.FileUtils;

public class Builder {
	
	public static void main(String[] args) {
//		BuilderDao.scan("com.chs.test.dao");
//		BuilderWebService.scan("com.chs.test.service");
//		
		String propath=FileUtils.fileUp(Builder.class.getClassLoader().getResource("").toString(),3).replace("file:/","");
		
		List<File> fs=FileUtils.getFileByPath(propath+"/WebContent/view/_lang/", ".properties");
		for(File f:fs) {
			BuilderLangFile.scan(f.getAbsolutePath(),propath+"/WebContent/view/def/");
		}
		
//		BuilderFace.scan("com.chs.test.controller","com.chs.test.service","com.chs.test.dao");
	}
}
