package com.chs.wheel.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * 
 * <p>WheelService</p>
 * Description: 分布式调用辅助类
 * 
 * @author chenhaishan
 * @date 2018-09-25 17:26
 *
 */
@WebService
public class WheelService {
	
	public String exc(String service,String callBackType,String argsType_s,String args_s) throws Exception {
		String res=null;
		
		String serviceName=service.split("-")[0]+"Imp";
		String serviceMethod=service.split("-")[1];
		
		String[] argsType=argsType_s.split(",");
		byte[][] args=(byte[][]) deserializeToObject(args_s);
		
		if(Wheel.serviceMapping.get(serviceName)!=null) {
			Object obj=Wheel.serviceMapping.get(serviceName);
			Method[] methods=obj.getClass().getDeclaredMethods();
			for(Method method:methods) {
				if(method.getName().equals(serviceMethod)) {
					if(argsType.length==args.length) {
						Object[] argss=new Object[args.length];
						for(int i=0;i<args.length;i++) {
							argss[i]=getObject(argsType[i], args[i]);
						}
						try {
							if(callBackType!=null) {
								res=serializeToString(method.invoke(obj, argss));
							}else {
								method.invoke(obj, argss);
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					break;
				}
			}
		}
		
		return res;
	}
	
	@WebMethod(exclude=true)
	public static String doit(String url,String service,String callBackType,String[] argsType,byte[][] args) {
		String res=null;
		
		try {
			URL realUrl = new URL(url+"?wsdl");
			HttpURLConnection  conn = (HttpURLConnection )realUrl.openConnection();
			
			conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int timeout = 30 * 1000;
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
			
            
            OutputStream os=conn.getOutputStream();
            
            String argsTypes="";
            for(String s:argsType) {
            	argsTypes+=s+",";
            }
            argsTypes=argsTypes.substring(0, argsTypes.length()-1);
            
            String  requestBody = new String("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <m:exc xmlns:m=\"http://core.wheel.chs.com/\">\n" +
                    "      <arg0>"+service+"</arg0>\n" +
                    "      <arg1>"+callBackType+"</arg1>\n" +
                    "      <arg2>"+argsTypes+"</arg2>\n" +
                    "      <arg3>"+serializeToString(args)+"</arg3>\n" +
                    "    </m:exc>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>");
            
            os.write(requestBody.getBytes());
        	os.flush();
        	os.close();
        	
        	int code = conn.getResponseCode();
        	if (code == 200) {
        		InputStream is = conn.getInputStream();
     	        
        		String xml=new String(inputToBytes(is),"utf-8");
        		
        		Pattern pattern = Pattern.compile("<return>(.*)</return>");
        		Matcher matcher = pattern.matcher(xml);
        		matcher.find();
        		
     	       	res=matcher.group(1);
     	        is.close();
        	}else {
        		System.out.println("500 Error!");
        	}
        	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	@WebMethod(exclude=true)
	public static Object getObject(String type,byte[] arg) {
		switch (type) {
			case "int":
				return bytesToInt(arg);
			case "float":
				return bytesToFloat(arg);
			case "long":
				return bytesToLong(arg);
			case "double":
				return bytesToDouble(arg);
			case "short":
				return bytesToShort(arg);
			case "boolean":
				return bytesToBoolean(arg);
			case "String":
				return new String(arg);
		}
		return null;
	}
	
	@WebMethod(exclude=true)
	public static byte[] getBytes(String type,Object arg) {
		switch (type) {
			case "int":
				return intToBytes(Integer.parseInt((String)arg));
			case "float":
				return floatToBytes(Float.parseFloat((String)arg));
			case "long":
				return longToBytes(Long.parseLong((String)arg));
			case "double":
				return doubleToBytes(Double.parseDouble((String)arg));
			case "short":
				return shortToBytes(Short.parseShort((String)arg));
			case "boolean":
				return booleanToBytes(Boolean.parseBoolean((String)arg));
			case "String":
				return ((String)arg).getBytes();
			default:
				return objectToBytes(arg);
		}
	}
	
	@WebMethod(exclude=true)
	public static byte[] intToBytes(int value)   
	{   
	    byte[] byte_src = new byte[4];  
	    byte_src[3] = (byte) ((value & 0xFF000000)>>24);  
	    byte_src[2] = (byte) ((value & 0x00FF0000)>>16);  
	    byte_src[1] = (byte) ((value & 0x0000FF00)>>8);    
	    byte_src[0] = (byte) ((value & 0x000000FF));          
	    return byte_src;  
	} 
	
	@WebMethod(exclude=true)
	public static int bytesToInt(byte[] ary) {  
	    int value;int offset=0;
	    value = (int) ((ary[offset]&0xFF)   
	            | ((ary[offset+1]<<8) & 0xFF00)  
	            | ((ary[offset+2]<<16)& 0xFF0000)   
	            | ((ary[offset+3]<<24) & 0xFF000000));  
	    return value;  
	}  
	
	@WebMethod(exclude=true)
	public static float bytesToFloat(byte[] b) { 
        int accum = 0; 
        accum = accum|(b[0] & 0xff) << 0;
        accum = accum|(b[1] & 0xff) << 8; 
        accum = accum|(b[2] & 0xff) << 16; 
        accum = accum|(b[3] & 0xff) << 24; 
        System.out.println(accum);
        return Float.intBitsToFloat(accum); 
    }
	
	@WebMethod(exclude=true)
	public static byte[] floatToBytes(float val) {
        return ByteBuffer.allocate(4).putFloat(val).array();
    }
	
	@WebMethod(exclude=true)
	public static long bytesToLong(byte[] b){ 
		long values = 0; 
		for (int i = 0; i < 8; i++) { 
			values <<= 8; values|= (b[i] & 0xff); 
		} 
		return values; 
	}
	
	@WebMethod(exclude=true)
	public static byte[] longToBytes(long number) {  
        long temp = number;  
        byte[] b = new byte[8];  
        for (int i = 0; i < b.length; i++) {  
            b[i] = new Long(temp & 0xff).byteValue(); 
        }  
        return b;  
    }  
	
	@WebMethod(exclude=true)
	public static double bytesToDouble(byte[] arr) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (arr[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	}
	
	@WebMethod(exclude=true)
	public static byte[] doubleToBytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return byteRet;
	}
	
	@WebMethod(exclude=true)
	public static short bytesToShort(byte[] b) { 
        short s = 0; 
        short s0 = (short) (b[0] & 0xff); 
        short s1 = (short) (b[1] & 0xff); 
        s1 <<= 8; 
        s = (short) (s0 | s1); 
        return s; 
    }
	
	@WebMethod(exclude=true)
	public static byte[] shortToBytes(short number) {  
        int temp = number;  
        byte[] b = new byte[2];  
        for (int i = 0; i < b.length; i++) {  
            b[i] = new Integer(temp & 0xff).byteValue(); 
            temp = temp >> 8; 
        }  
        return b;  
    }  
	
	@WebMethod(exclude=true)
	public static boolean bytesToBoolean(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        int tmp = ByteBuffer.wrap(data, 0, 4).getInt();
        return (tmp == 0) ? false : true;
    }
	
	@WebMethod(exclude=true)
	public static byte[] booleanToBytes(boolean val) {
        int tmp = (val == false) ? 0 : 1;
        return ByteBuffer.allocate(4).putInt(tmp).array();
    }
	
	@WebMethod(exclude=true)
	public static byte[] objectToBytes(Object obj) {     
        byte[] bytes = null;     
        ByteArrayOutputStream bos = new ByteArrayOutputStream();     
        try {       
            ObjectOutputStream oos = new ObjectOutputStream(bos);        
            oos.writeObject(obj);       
            oos.flush();        
            bytes = bos.toByteArray ();     
            oos.close();        
            bos.close();       
        } catch (IOException ex) {       
            ex.printStackTrace();  
        }     
        return bytes;   
    }  
	
	@WebMethod(exclude=true)
	public static Object bytesToObject(byte[] bytes) {    
		if(bytes==null)return null;
        Object obj = null;     
        try {       
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);       
            ObjectInputStream ois = new ObjectInputStream (bis);       
            obj = ois.readObject();     
            ois.close();  
            bis.close();  
        } catch (IOException ex) {       
            ex.printStackTrace();  
        } catch (ClassNotFoundException ex) {       
            ex.printStackTrace();  
        }     
        return obj;   
    }  
	
	@WebMethod(exclude=true)
	public static byte[] inputToBytes(InputStream inStream)  throws IOException {  
		ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096];
	    int n = 0;
	    while (-1 != (n = inStream.read(buffer))) {
	        output.write(buffer, 0, n);
	    }
	    return output.toByteArray();  
	} 
	
	
	//序列化
	@WebMethod(exclude=true)
    public static String serializeToString(Object obj) throws Exception{
		
		Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(objectToBytes(obj)));
    }
    //反序列化
	@WebMethod(exclude=true)
    public static Object deserializeToObject(String str) throws Exception{
		
		Base64.Decoder decoder = Base64.getDecoder();
        return bytesToObject(decoder.decode(str)); 
    }
	
	//判断服务是否启动
	public static boolean urlIsReach(String url) { 
		if (url==null) {
			return false;
		}		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			if (HttpURLConnection.HTTP_OK==connection.getResponseCode()) {
				return true;
			}		
		} catch (Exception e) {
			return false;		
		}		
		return false;	
	}

}
