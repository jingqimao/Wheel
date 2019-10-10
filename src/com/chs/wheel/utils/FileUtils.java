package com.chs.wheel.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.Part;

/**
 * 
 * <p>FileUtil</p>
 * Description: 文件工具
 * 
 * @author chenhaishan
 * @date 2018-04-17 09:14
 *
 */
public class FileUtils {
	
	public static final String fileName="name";
	public static final String fileSize="size";
	public static final String fileUpdateTime="update";
	public static final String fileType="type";
	public static final String fileisDir="isDir";
	
	private static final String[] sizeStr = {"bit","KB","MB","GB","TB"};
	
	/**
	 * 
	 * <p>isFile</p>
	 * Description: 存在且是文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-24 11:43
	 *
	 * @param url
	 * @return
	 */
	public static boolean isFile(String url){
		File f=new File(url);
		return f.exists()&&f.isFile();
	}
	
	/**
	 * 
	 * <p>isDir</p>
	 * Description: 存在且是文件夹
	 * 
	 * @author chenhaishan
	 * @date 2018-04-24 11:43
	 *
	 * @param url
	 * @return
	 */
	public static boolean isDir(String url){
		File f=new File(url);
		return f.exists()&&f.isDirectory();
	}
	
	/**
	 * 
	 * <p>getSize</p>
	 * Description: 获取文件大小
	 * 
	 * @author chenhaishan
	 * @date 2018-04-26 09:41
	 *
	 * @param url
	 * @return
	 */
	public static String getSize(String url) {
		File f=new File(url);
		return formatFileSize(f.length());
	}
	
	/**
	 * 
	 * <p>getType</p>
	 * Description: 获取后缀名
	 * 
	 * @author chenhaishan
	 * @date 2018-04-26 09:45
	 *
	 * @param name
	 * @return
	 */
	public static String getType(String name) {
		return name.split("\\.").length>1?name.split("\\.")[1]:"未知";
	}
	
	/**
	 * 
	 * <p>del</p>
	 * Description: 删除文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-26 10:45
	 *
	 * @param url
	 * @return
	 */
	public static boolean del(String url){
		File f=new File(url);
		return f.delete();
	}
	
	/**
	 * 
	 * <p>getAllFileInfo</p>
	 * Description: 获取所有文件基本信息
	 * 
	 * @author chenhaishan
	 * @date 2018-04-25 17:17
	 *
	 * @param url
	 * @return
	 */
	public static List<Map<String,Object>> getAllFileInfo(String url){
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		if(isDir(url)) {
			File[] fs=new File(url).listFiles();
			for(File f:fs) {
				Map<String,Object> m=new HashMap<String,Object>();
				m.put(fileName, f.getName());
				Date date=new Date(f.lastModified());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				m.put(fileUpdateTime, sdf.format(date));
				if(f.isFile()) {
					m.put(fileSize, formatFileSize(f.length()));
					String suf=f.getName().split("\\.").length>1?f.getName().split("\\.")[1]:"未知";
					m.put(fileType, suf.toLowerCase());
					m.put(fileisDir, false);
				}else {
					m.put(fileisDir, true);
				}
				list.add(m);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * <p>formatFileSize</p>
	 * Description: 文件大小格式化
	 * 
	 * @author chenhaishan
	 * @date 2018-04-25 17:17
	 *
	 * @param length
	 * @return
	 */
	private static String formatFileSize(long length) {
		for(String ss:sizeStr){
			if(length<1024L) {
				return length+ss;
			}else {
				length/=1024L;
			}
		}
		return length+"";
	}
	
	/**
	 * 
	 * <p>rename</p>
	 * Description: 重命名
	 * 
	 * @author chenhaishan
	 * @date 2018-04-26 14:06
	 *
	 * @param url
	 * @param name
	 * @return
	 */
	public static boolean rename(String url,String name){
		File f=new File(url);
		if(f.exists()) {
			File nf=new File(f.getParent()+File.separator+name);
			return f.renameTo(nf);
		}
		return false;
	}
	
	/**
	 * 
	 * <p>getFileByPack</p>
	 * Description: 扫描包下的所有class文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-17 09:15
	 *
	 * @param pack	包路径
	 * @param suffix	后缀过滤
	 * @return
	 */
	public static List<File> getFileByPack(String pack,String suffix){
		List<File> files=new ArrayList<File>();
		try{
			 Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(pack.replace('.', '/'));
			 URL url = dirs.nextElement();
             String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
             File file=new File(filePath);
             File[] tempList = file.listFiles();
             for(int i=0;i<tempList.length;i++){
            	 String fileName=tempList[i].getName().toLowerCase();
            	 if(fileName.endsWith(suffix)){
            		 files.add(tempList[i]);
            	 }
             }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return files;
	}
	
	/**
	 * 
	 * <p>getFileNameByPath</p>
	 * Description: 获取路径下文件名
	 * 
	 * @author chenhaishan
	 * @date 2018-04-23 12:11
	 *
	 * @param path
	 * @param suffix
	 * @param all
	 * @return
	 */
	public static String[] getFileNameByPath(String path,String suffix,boolean allname){
		List<File> files=getFileByPath( path, suffix);
		String[] filename=new String[files.size()];
		for(int i=0;i<files.size();i++) {
			filename[i]=files.get(i).getName();
			if(!allname) {
				filename[i]=filename[i].replace(suffix, "");
			}
		}
		return filename;
	}
	
	/**
	 * 
	 * <p>getFileByPath</p>
	 * Description: 通过路径获取目录下文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-17 09:50
	 *
	 * @param path	文件夹路径
	 * @param suffix	后缀过滤
	 * @return
	 */
	public static List<File> getFileByPath(String path,String suffix){
		List<File> files=new ArrayList<File>();
		try{
             File file=new File(path);
             File[] tempList = file.listFiles();
             for(int i=0;i<tempList.length;i++){
            	 String fileName=tempList[i].getName().toLowerCase();
            	 if(fileName.endsWith(suffix)){
            		 files.add(tempList[i]);
            	 }
             }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return files;
	}
	
	/**
	 * 
	 * <p>copyFile</p>
	 * Description: 复制文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-20 10:41
	 *
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public static void copyFile(String fromFile,String toFile) throws IOException {
		copyFile(new File(fromFile),new File(toFile));
	}
	
	/**
	 * 
	 * <p>copyFile</p>
	 * Description: 复制文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-20 10:41
	 *
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public static void copyFile(File fromFile,File toFile) throws IOException{
		
		if (fromFile.isDirectory()) {
			if(!toFile.exists()) {
	    		toFile.mkdir();
	    	}
			String[] fs = fromFile.list();
			for(String fn:fs) {
				copyFile(new File(fromFile.getPath()+File.separator+fn),new File(toFile.getPath()+File.separator+fn));
			}
        }else {
        	FileInputStream ins = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            byte[] b = new byte[1024];
            int n=0;
            while((n=ins.read(b))!=-1){
            	out.write(b, 0, n);
            }
            
            ins.close();
            out.close();
        }
    }
	public static boolean delFile(String path) {  
		File file = new File(path);  
		if(file.isFile()) {
			return file.delete();
		}
		return false;
	}
	
	/**
	 * 
	 * <p>delFolder</p>
	 * Description: 删除整个目录
	 * 
	 * @author chenhaishan
	 * @date 2018-04-20 13:58
	 *
	 * @param folderPath
	 */
	public static void delFolder(String folderPath) {  
	     try {  
	        delAllFile(folderPath); //删除完里面所有内容  
	        String filePath = folderPath;  
	        filePath = filePath.toString();  
	        java.io.File myFilePath = new java.io.File(filePath);  
	        myFilePath.delete(); //删除空文件夹  
	     } catch (Exception e) {  
	       e.printStackTrace();   
	     }  
	}  
	
	/**
	 * 
	 * <p>delAllFile</p>
	 * Description: 遍历文件夹删除所有文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-20 13:59
	 *
	 * @param path
	 * @return
	 */
	private static boolean delAllFile(String path) {  
      	boolean flag = false;  
      	File file = new File(path);  
      	if (!file.exists()) {  
      		return flag;  
      	}  
      	if (!file.isDirectory()) {  
      		return flag;  
      	}  
      	String[] tempList = file.list();  
      	File temp = null;  
      	for (int i = 0; i < tempList.length; i++) {  
      		if (path.endsWith(File.separator)) {  
      			temp = new File(path + tempList[i]);  
      		} else {  
      			temp = new File(path + File.separator + tempList[i]);  
      		}  
        	if (temp.isFile()) {  
        		temp.delete();  
        	}  
        	if (temp.isDirectory()) {  
        		delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件  
        		delFolder(path + "/" + tempList[i]);//再删除空文件夹  
        		flag = true;  
        	}  
      	}  
      	return flag;  
     }   
	
	/**
	 * 
	 * <p>unZipFiles</p>
	 * Description: 解压ZIP文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-20 10:43
	 *
	 * @param zipFile
	 * @param descDir
	 * @param newPath
	 * @throws IOException
	 */
	public static void unZipFiles(String zipFile, String descDir,boolean newPath) throws IOException {
		unZipFiles(new File(zipFile),descDir,newPath);
	}
	
	/**
	 * 
	 * <p>unZipFiles</p>
	 * Description:  解压ZIP文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-20 11:15
	 *
	 * @param zipFile	加压文件
	 * @param descDir	解压目录
	 * @param newPath	是否新建目录
	 * @throws IOException
	 */
	public static void unZipFiles(File zipFile, String descDir,boolean newPath) throws IOException {  
        
        @SuppressWarnings("resource")
		ZipFile zip = new ZipFile(zipFile,Charset.forName("GBK"));//解决中文文件夹乱码  
        String name = zip.getName().substring(zip.getName().lastIndexOf('\\')+1, zip.getName().lastIndexOf('.'));  
        
        if(newPath)descDir+=name;
        
        File pathFile = new File(descDir);  
        if (!pathFile.exists()) {  
            pathFile.mkdirs();  
        }  
          
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {  
            ZipEntry entry = (ZipEntry) entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir +"/"+ zipEntryName).replaceAll("\\*", "/");  
              
            // 判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if (!file.exists()) {  
                file.mkdirs();  
            }  
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if (new File(outPath).isDirectory()) {  
                continue;  
            }  
            // 输出文件路径信息  
//          System.out.println(outPath);  
  
            FileOutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while ((len = in.read(buf1)) > 0) {  
                out.write(buf1, 0, len);  
            }  
            in.close();  
            out.close();  
        }  
        System.out.println(zipFile.getName()+"解压完毕!");  
    }  
	
	
	/**
	 * 
	 * <p>read</p>
	 * Description: 按编码读取文件字符串
	 * 
	 * @author chenhaishan
	 * @date 2018-04-13 14:00
	 *
	 * @param fileName	文件路径
	 * @param code	编码
	 * @return	整个文件的字符串
	 */
	public static String read(String fileName,String code) {
		String txt=null;
		try {
			File f=new File(fileName);
			FileInputStream in=new FileInputStream(f); 
			Long filelength = f.length(); 
			byte[] filecontent = new byte[filelength.intValue()];
			in.read(filecontent);
			in.close();
			txt=new String(filecontent,code);
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return txt;
	}
	
	/**
	 * 
	 * <p>write</p>
	 * Description: 按编码写入文件
	 * 
	 * @author chenhaishan
	 * @date 2018-04-13 14:01
	 *
	 * @param fileName	文件路径
	 * @param content	写入内容
	 * @param code	编码
	 */
	public static void write(String fileName,String content,String code) {
		try{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),code));
            writer.write(content);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * <p>getFilename</p>
	 * Description: 获取上传文件名
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:27
	 *
	 * @param part
	 * @return
	 */
	public static String getFilename(Part part) {
		String header = part.getHeader("Content-Disposition");
		String filename = header.substring(header.indexOf("filename=\"") + 10,header.lastIndexOf("\""));
		if(filename.contains("\\")){
			return filename.substring(filename.lastIndexOf("\\")+1);
		}else {
			return filename;
		}
		
	}
	
	/**
	 * 
	 * <p>getProperties</p>
	 * Description: 读取配置文件
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:27
	 *
	 * @param path
	 * @param code
	 * @return
	 */
	public static Map<String,LinkedHashMap<String,String>> getProperties(String path,String code){
		String[] text=read(path, code).split("\n");
		Map<String,LinkedHashMap<String,String>> mm=new HashMap<String,LinkedHashMap<String,String>>();
		String tagName=null;
		
		String vf=null;
		for(String s:text) {
			s=s.trim();
			if(s!=null&&!s.isEmpty()) {
				if(s.startsWith("[")) {
					tagName=s.replaceAll("\\[", "").replaceAll("\\]", "").trim();
					LinkedHashMap<String,String> m=new LinkedHashMap<String,String>();
					mm.put(tagName, m);
				}else if(s.contains("=")&&tagName!=null){
					if(s.split("=")[0].equals("@")) {
						if(s.split("=")[1].isEmpty()) {
							vf=null;
						}else {
							vf=s.split("=")[1];
						}
					}else {
						if(vf!=null) {
							mm.get(tagName).put(s.split("=")[0]+"=@@="+vf, s.split("=")[1]);
						}else {
							mm.get(tagName).put(s.split("=")[0], s.split("=")[1]);
						}
						
					}
				}
			}
		}
		
		return mm;
	}
	
	/**
	 * 
	 * <p>fileUp</p>
	 * Description: 获取上层目录
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:28
	 *
	 * @param path
	 * @param up
	 * @return
	 */
	public static String fileUp(String path,int up) {
		for(int i=0;i<up;i++) {
			if(path.contains("/")) {
				path=path.substring(0, path.lastIndexOf("/"));
			}
		}
		return path;
	}

	/**
	 * 
	 * <p>dir</p>
	 * Description: 新建文件夹
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:28
	 *
	 * @param path
	 */
	public static void dir(String path) {
		File file=new File(path);
		if(!file.exists()) {
			file.mkdir();
    	}
	}
	
	/**
	 * 
	 * <p>doFile</p>
	 * Description: 遍历文件执行动作
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:28
	 *
	 * @param path
	 * @param dofile
	 */
	public static void doFile(String path,DoFile dofile) {
		File file=new File(path);
		file.renameTo(dofile.doit(file.getAbsolutePath(), file.getName(), file.isFile(), file));
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for(File fn:fs) {
				doFile(fn.getAbsolutePath(),dofile);
			}
        }
	}
	
	/**
	 * 
	 * <p>getPathByPack</p>
	 * Description: 获取包文件路径
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:29
	 *
	 * @param pack
	 * @return
	 */
	public static String getPathByPack(String pack){
		try{
			 Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(pack.replace('.', '/'));
			 URL url = dirs.nextElement();
             return URLDecoder.decode(url.getFile(), "UTF-8");
             
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * <p>getClassPathByPack</p>
	 * Description: 获取包类文件路径
	 * 
	 * @author chenhaishan
	 * @date 2018-10-09 09:35
	 *
	 * @param pack
	 * @return
	 */
	public static String getClassPathByPack(String pack){
		try{
			 Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(pack.replace('.', '/'));
			 URL url = dirs.nextElement();
            String path= URLDecoder.decode(url.getFile(), "UTF-8");
            return path.replace("build/classes", "src");
            
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
