package com.chs.wheel.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.chs.wheel.core.WheelLogger;

public class NETUtils {
	
	public static String post(String urlstr) {
		return post(urlstr,null,null,true);
	}
	public static String post(String urlstr,Map<String,String> head,Map<String,String> args) {
		return post(urlstr,head,args,true);
	}
	public static String post(String urlstr,Map<String,String> head,Map<String,String> args,boolean def) {
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
		try {
			URL url=new URL(urlstr);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000*20);
			conn.setReadTimeout(1000*20);
			if(def) {
				conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			}
            if(head!=null)for(String k:head.keySet()) {
            	conn.setRequestProperty(k, head.get(k));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            if(args!=null) {
            	String param="";
            	 for(String k:args.keySet()) {
                 	if(param.length()==0) {
                 		param+=k+"="+args.get(k);
                 	}else {
                 		param+="&"+k+"="+args.get(k);
                 	}
                 }
                out.print(param);
            }
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
	}
	
	public static String get(String urlstr) {
		return get(urlstr,null,null,true);
	}
	public static String get(String urlstr,Map<String,String> head,Map<String,String> args) {
		return get(urlstr,head,args,true);
	}
	public static String get(String urlstr,Map<String,String> head,Map<String,String> args,boolean def) {
        BufferedReader in = null;
        String result = "";
		try {
			if(args!=null) {
            	String param="";
                for(String k:args.keySet()) {
                	if(param.length()==0) {
                		param+=k+"="+args.get(k);
                	}else {
                		param+="&"+k+"="+args.get(k);
                	}
                }
                if(param.length()>0)urlstr=urlstr+"?"+param;
            }
			URL url=new URL(urlstr);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000*20);
			conn.setReadTimeout(1000*20);
			if(def) {
				conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			}
            if(head!=null)for(String k:head.keySet()) {
            	conn.setRequestProperty(k, head.get(k));
            }
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
            try{
                if(in!=null){
                    in.close();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
	}
	
	public static String postJson(String urlstr,String args) {
		return postJson(urlstr,null,args,true);
	}
	
	public static String postJson(String urlstr,Map<String,String> head,String args) {
		return postJson(urlstr,head,args,true);
	}
	
	public static String postJson(String urlstr,Map<String,String> head,String args,boolean def) {
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
		try {
			URL url=new URL(urlstr);
			if("https".equalsIgnoreCase(url.getProtocol())){
				SslUtils.ignoreSsl();
			}
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			if(def) {
				conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			}
            if(head!=null)for(String k:head.keySet()) {
            	conn.setRequestProperty(k, head.get(k));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            if(args!=null) {
                out.print(args);
            }
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
	}
	
	/**
	 * 
	 * <p>urlIsOK</p>
	 * Description: 判断urlO不OK
	 * 
	 * @author chenhaishan
	 * @date 2019-07-09 14:07
	 *
	 * @param url
	 * @return
	 */
	public static boolean urlIsOK(String url) {
		
		boolean res=false;
		
		HttpURLConnection conn = null;
		
		try{
        	URL httpUrl=new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(1000*10);
            conn.setReadTimeout(1000*10);
            if(conn.getResponseCode()<400)return true;
        } catch (Exception e) {
        	return false;
        }
		
		return res;
	}
	
	
	/**
	 * 
	 * <p>getHead</p>
	 * Description: 获取URL头部信息
	 * 
	 * @author chenhaishan
	 * @date 2019-07-09 13:43
	 *
	 * @param url
	 * @return
	 */
	public static Map<String,String> getHead(String url) {
		
		HttpURLConnection conn = null;
		
		Map<String,String> res=new HashMap<String,String>();
		
		try{
        	
        	URL httpUrl=new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
           
            conn.setRequestMethod("HEAD");  
            
            Map<String, List<String>> headerMap = conn.getHeaderFields();  
            Iterator<String> iterator = headerMap.keySet().iterator();
            while (iterator.hasNext()) {  
                String key = iterator.next();  
                List<String> values = headerMap.get(key);  
                
                res.put(key, values.toString());
                //System.out.println(key + ":" + values.toString()); 
            } 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return res;
	}
	
	/**
	 * 
	 * <p>downLoadFile</p>
	 * Description: 下载文件
	 * 
	 * @author chenhaishan
	 * @date 2019-07-24 09:46
	 *
	 * @param url
	 * @param save_path
	 * @param file_name
	 * @return
	 */
	public static boolean downLoadFile(String url,String save_path,String file_name) {
		return downLoadFile(url,save_path,file_name,10*1000);
	}
	
	/**
	 * 
	 * <p>downLoadFile</p>
	 * Description: 下载文件
	 * 
	 * @author chenhaishan
	 * @date 2019-07-09 13:20
	 *
	 * @param url
	 * @param save_path
	 * @param file_name
	 */
	public static boolean downLoadFile(String url,String save_path,String file_name,int time_out) {
		
		HttpURLConnection conn = null;
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        FileOutputStream out = null;
        
        boolean result=true;
        
        try{
        	
        	File file0=new File(save_path);
           if(!file0.isDirectory()&&!file0.exists()){
               file0.mkdirs();
           }
           out = new FileOutputStream(file0+"\\"+file_name);
           
           URL httpUrl=new URL(url);
           conn=(HttpURLConnection) httpUrl.openConnection();
           
           conn.setRequestMethod("GET");
           conn.setDoInput(true);  
           conn.setDoOutput(true);
            
           conn.setUseCaches(false);
           conn.setConnectTimeout(time_out);
           conn.setReadTimeout(time_out);
           
           conn.connect();
           
           inputStream=conn.getInputStream();
           bis = new BufferedInputStream(inputStream);
           byte b [] = new byte[1024];
           int len = 0;
           while((len=bis.read(b))!=-1){
               out.write(b, 0, len);
           }
           
       } catch (Exception e) {
           
           if(e.getClass().equals(java.net.SocketTimeoutException.class)) {
        	   WheelLogger.get(NETUtils.class).error("\n"+url+"\n文件下载超时！"); 
           }else {
        	   e.printStackTrace();
           }
           result=false;
       }finally{
           try {
               if(out!=null){
                   out.close();
               }
               if(bis!=null){
                   bis.close();
               }
               if(inputStream!=null){
                   inputStream.close();
               }
               if(!result)FileUtils.del(save_path+"\\"+file_name);
           } catch (Exception e2) {
               e2.printStackTrace();
               result=false;
           }
       }
		
       return result;
	}
	
	/**
	 * 
	 * 模拟客户端
	 * 
	 * */
	public static class NetClient{
		
		public CloseableHttpClient httpClient;
		public CookieStore cookieStore;
		
		public NetClient() {
			//cookieStore = new BasicCookieStore();
			//httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
			httpClient = HttpClients.createDefault();
		}
		
		public String get(String url) {
			return post(url,null,null);
		}
		public String post(String url,Map<String,String> parm) {
			return post(url,parm,null);
		}
		public String post(String url,Map<String,String> parm,Map<String,String> head) {
			
			String res=null;
			
			try {
				
				CloseableHttpResponse httpResponse=null;
				if(parm!=null) {
					HttpPost httpPost = new HttpPost(url);
					
					httpPost.setHeader("accept","*/*");
					httpPost.setHeader("connection","Keep-Alive");
					httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
					if(head!=null)for(String k:head.keySet()) {
						httpPost.setHeader(k,head.get(k));
					}
					List<NameValuePair> parm_list=new ArrayList<NameValuePair>();
					for(String k:parm.keySet()) {
						parm_list.add(new BasicNameValuePair(k, parm.get(k)));
					}
					UrlEncodedFormEntity entity=new UrlEncodedFormEntity(parm_list,"UTF-8");
					httpPost.setEntity(entity);
					httpResponse = httpClient.execute(httpPost);
				}else {
					HttpGet httpGet = new HttpGet(url);
					
					httpGet.setHeader("accept","*/*");
					httpGet.setHeader("connection","Keep-Alive");
					httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
					
					httpResponse = httpClient.execute(httpGet);
				}
				
				Thread.sleep(2000);
				
				HttpEntity resEntity = httpResponse.getEntity();
				res=EntityUtils.toString(resEntity, "UTF-8").toString();
				
				EntityUtils.consume(resEntity);

				httpResponse.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
		}
	}
	
}
