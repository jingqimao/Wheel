package com.chs.test.other;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chs.test.dao.userDao;
import com.chs.test.dao.userDaoImp;
import com.chs.wheel.core.DaoAOPHandle;
import com.chs.wheel.utils.DBUtils;
import com.chs.wheel.utils.IDUtils;
import com.chs.wheel.utils.SYSUtils;
import com.chs.wheel.utils.XMLUtils;

import ognl.Ognl;
import ognl.OgnlException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class testing {
	
	public static void main(String[] args) {
		
		/*// 设置默认工厂类
		System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
		// 设置日志打印类
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		//设置默认日志级别
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.simplelog.defaultlog", "error");
		
		
		
		NETUtils.NetClient client=new NETUtils.NetClient();
		
		String login_url="https://dive.site";
		String html=client.get(login_url);
		Pattern p = Pattern.compile("name=\"vauth\" content=\"(.+?)\"");
	    Matcher m = p.matcher(html);
	    m.find();
	    String vauth=m.group(1);
	    
	    String url="https://dive.site/public/files/markers.json";
	    
	    Map<String,String> parm=new HashMap<String, String>();
	    parm.put("v", "KWx0Q6RbB");
		parm.put("vauth", vauth);
		String res=client.post(url, parm);
		
		System.out.println(res);*/
		
		/*url="https://dive.site/explore/getActivityAJAX";
		
		parm=new HashMap<String, String>();
		parm.put("vauth", vauth);
		parm.put("sw_lat", "10.0");
		parm.put("sw_lng", "120.0");
		parm.put("ne_lat", "12.0");
		parm.put("ne_lng", "122.0");
		res=client.post(url, parm);
		
		System.out.println(res);*/
		
		/*// 设置默认工厂类
		System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
		// 设置日志打印类
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		//设置默认日志级别
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.simplelog.defaultlog", "error");
		
		
		
		NETUtils.NetClient client=new NETUtils.NetClient();
		
		String url="https://www.divessi.com/typo3conf/ext/travel/Proxy/GetStores.php?fallback=%2Findex.php%3Fid%3D5971%26type%3D9001%26tx_travel_dataview%5Bcontroller%5D%3DApi%5CStore%26tx_travel_dataview%5Baction%5D%3DgetStores";
		

		String res=client.get(url);
		
		System.out.println(res);*/
		
		
		/*String url="https://prodcdn.tritondive.co/apis/divespot/v0/boundSearch?top=-8.383735922625313&left=114.84164247773128&bottom=-8.577290093611953&right=116.04930172226864&skip=0&limit=999";
		Map<String,String> head=new HashMap<String, String>();
		head.put("accept-language", "zh-cn");
		System.out.println(NETUtils.get(url,head,null));*/
		
		/*String url="https://diveshopmedia.padiww.com/dsl-media/17104/logo/201804110502-Logo.jpg";
		System.out.println(NETUtils.getHead(url));
		NETUtils.downLoadFile(url, "C:\\Users\\G2server01\\Desktop", "xxoo.jpg");*/
		
		//System.out.print(DOCUtils.readWord("C:\\Users\\G2server01\\Desktop\\Anilao.docx"));
		/*Map<String,List<List<String>>> excel=DOCUtils.readExcel("C:\\Users\\G2server01\\Desktop\\新建 Microsoft Excel 工作表 (2).xlsx");
		for(String sname:excel.keySet()) {
			System.out.println("---:"+sname);
			for(List<String> row:excel.get(sname)) {
				for(String s:row)System.out.print(s);
				System.out.print("\n");
			}
		}*/
		
		//System.out.println(IDUtils.getCodeNumber(6));
		
		/*String con1 = "n != null and n != ''";
		Map<String,Object> root = new HashMap<>();
		root.put("n", 0);
		try {
			System.out.println(Ognl.getValue(con1, root));
		} catch (OgnlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*String sql_xml="select "
				+ "<set>"
				+ "<if test=\"id!=null\">id,</if>"
				+ "<if test=\"name!=null\">name,</if>"
				+ "<if test=\"record!=null\">record,</if>"
				+ "</set>"
				+ " from test1 "
				+ "<where>"
				+ "<if test=\"id!=-1\">and id={id}</if>"
				+ "<if test=\"name!=null and name!=''\">and name={name}</if>"
				+ "<if test=\"record!=null\">and record in "
				+ "<foreach collection=\"list\">"
				+ "'{item}'"
				+ "</foreach>"
				+ "</if>"
				+ "</where>"
				+ " order by id";
		List<String> list=new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Map<String,Object> params = new HashMap<>();
		params.put("id", 2);
		params.put("name", "tom");
		params.put("record", 1);
		params.put("list", list);
		
		try {
			Map<String,Object> res =XMLUtils.getDYSQL(sql_xml, params);
			System.out.println(res.get("sql"));
			System.out.println(res.get("args"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}*/
		
		/*Connection conn=DBUtils.getConnection("com.mysql.jdbc.Driver", "jdbc:mysql://120.77.11.234:3306/works_wordgame?useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false", "root", "87494556");
		
		String sql="update `plays_ready` set `status` = 1 where 1 = 1 and `id` = 46;";
		String sql2="update `plays_ready` set `history` = '' where 1 = 1 and `id` = 46;";
		try {
			conn.setAutoCommit(false);
			
			PreparedStatement ps=conn.prepareStatement(sql);
			ps.executeUpdate();
			
			PreparedStatement ps2=conn.prepareStatement(sql2);
			ps2.executeUpdate();
			
			conn.commit();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/
	}
	
	
}
