package com.chs.wheel.utils;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtils {
	
	public final static String e163="smtp.163.com";
	public final static String e126="smtp.126.com";
	public final static String sina="smtp.sina.com.cn";
	public final static String qq="smtp.qq.com";
	public final static String eqq="smtp.exmail.qq.com";
	public final static String google="smtp.gmail.com";
	public final static String foxmail="smtp.foxmail.com";
	
	private static final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public static void send(String host,String fromAddress,String fromTag,String account,String password,String text,String title,String toAddress,boolean toSelf){ 

		threadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				Properties prop=new Properties();
				prop.setProperty("mail.host",host );
				prop.setProperty("mail.transport.protocol", "smtp");
				
				prop.setProperty("mail.smtp.port", "465");
				prop.setProperty("mail.smtp.starttls.enablet", "true");
	            prop.setProperty("mail.smtp.socketFactory.port", "465");
	            prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	            prop.setProperty("mail.smtp.ssl.enable", "true");
	            prop.put("mail.smtp.auth", true);
				try {
					
					Session session=Session.getInstance(prop);
					session.setDebug(true);
					Transport ts=session.getTransport();
					ts.connect(account, password);
					Message msg=new MimeMessage(session);
					if(fromTag!=null&&!fromTag.isEmpty()) {
						msg.setFrom(new InternetAddress(fromAddress,fromTag));
					}else {
						msg.setFrom(new InternetAddress(fromAddress));
					}
					if(toSelf)msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(fromAddress));
					msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
					msg.setSubject(title);
					msg.setContent(text, "text/html;charset=gbk");
					ts.sendMessage(msg, msg.getAllRecipients());
				} catch (Exception e) {
				}
				
			}
		});
		
	}
}
