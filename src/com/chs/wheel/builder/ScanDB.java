package com.chs.wheel.builder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chs.wheel.utils.DBUtils;
import com.chs.wheel.utils.FileUtils;

public class ScanDB {
	
	public static void main(String[] args) {
		
		String ip="localhost";
		int port=3306;
		String dbName="test1";
		String userName="root";
		String password="root";
		
		String pack="com.chs.test.table.test1";
		
		String path=FileUtils.getClassPathByPack(pack);
		
		Connection conn=DBUtils.getConnection("com.mysql.jdbc.Driver", "jdbc:mysql://"+ip+":"+port+"/"+dbName+"?useSSL=false", userName, password);
		
		List<String> tables=getAllTable(conn);
		
		FileUtils.delFolder(path);
		
		FileUtils.dir(path);
		
		for(String tb:tables) {
			System.out.println("初始化:"+tb);
			Map<String,String> columns=getColumnTypes(conn,tb);
			
			FileUtils.write(path+"\\"+"t_"+tb+".java", getContext(pack,"t_"+tb,columns), "utf-8");
		}
		
		DBUtils.closeConnection(conn);
		
		System.out.println("初始化完成！");
	}
	
	private static String getContext(String pack,String tb,Map<String,String> columns) {
		String context="package "+pack+";\n";
		
		context+="\nimport com.chs.wheel.builder.BaseBuilderDao;\n";
		
		context+="\npublic class  "+tb+" extends BaseBuilderDao{\n\n";
		
		for(String name:columns.keySet()) {
			context+="	public final static String "+name+"=\""+columns.get(name)+":"+name+"\";\n";
		}
		
		context+="\n}";
		
		return context;
	}
	
	private static List<String> getAllTable(Connection conn) {
		
		List<String> tableNames = new ArrayList<>();
        ResultSet rs = null;
        try {
            DatabaseMetaData db = conn.getMetaData();
            rs = db.getTables(null, null, null, new String[] { "TABLE" });
            while(rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        return tableNames;
	}
	
	private static Map<String,String> getColumnTypes(Connection conn,String tableName) {
		Map<String,String> columnTypes = new HashMap<String,String>();
        
        PreparedStatement pStemt = null;
        String tableSql = "SELECT * FROM " + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.put( rsmd.getColumnName(i+1),format(rsmd.getColumnTypeName(i+1)));
            }
        } catch (Exception e) {
        	 e.printStackTrace();
        } 
        return columnTypes;
    }
	
	private static String format(String DBType) {
		switch (DBType) {
			case "INT":return "int";
			case "VARCHAR":return "String";
			case "DOUBLE":return "double";
			case "FLOAT":return "float";
			case "CHAR":return "String";
			case "DECIMAL":return "double";
		}
		return DBType;
	}
}
