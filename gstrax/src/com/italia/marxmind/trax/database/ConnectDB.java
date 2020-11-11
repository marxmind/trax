package com.italia.marxmind.trax.database;



import java.sql.Connection;
import java.sql.DriverManager;

import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.security.SecureChar;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class ConnectDB {

	public static Connection getConnection(){
		Connection conn = null;
		
		try{
			Conf conf = Conf.getInstance();
			Class.forName(conf.getDatabaseDriver());
			String url = conf.getDatabaseUrl() + ":" + conf.getDatabasePort() + "/" +conf.getDatabaseName()+ "?" + conf.getDatabaseServerTime() + "&" + conf.getDatabaseSSL();
			String u_name = conf.getDatabaseUserName();
			String pword = conf.getDatabasePassword();
			conn = DriverManager.getConnection(url, u_name, pword);
			//System.out.println("Accessing database.....");
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
				//System.out.println("Closing database...");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Connection c = getConnection();
		System.out.println("Successfully connected" + c.toString());
	}
	
}
