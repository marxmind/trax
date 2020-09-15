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
			String driver = ReadConfig.value(Gstrax.DB_DRIVER);
				   //driver = SecureChar.decode(driver);
			Class.forName(driver);
			String db_url = ReadConfig.value(Gstrax.DB_URL);
				   //db_url = SecureChar.decode(db_url);
			String port = ReadConfig.value(Gstrax.DB_PORT);
			       port = SecureChar.decode(port);
			String url = db_url + ":" + port + "/" +ReadConfig.value(Gstrax.DB_NAME)+ "?serverTimezone=UTC&" + ReadConfig.value(Gstrax.DB_SSL);
			String u_name = ReadConfig.value(Gstrax.USER_NAME);
				   u_name = SecureChar.decode(u_name);
				   u_name = u_name.replaceAll("mark", "");
				   u_name = u_name.replaceAll("rivera", "");
				   u_name = u_name.replaceAll("italia", "");
			String pword = ReadConfig.value(Gstrax.USER_PASS);
				   pword =  SecureChar.decode(pword);
				   pword = pword.replaceAll("mark", "");
				   pword = pword.replaceAll("rivera", "");
				   pword = pword.replaceAll("italia", "");
			conn = DriverManager.getConnection(url, u_name, pword);
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
