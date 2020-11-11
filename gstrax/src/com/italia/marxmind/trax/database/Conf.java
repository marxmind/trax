package com.italia.marxmind.trax.database;

import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.security.SecureChar;

/**
 * 
 * @author Mark Italia
 * @since 10-12-2020
 * @version 1.0
 *
 */
public class Conf {

	private static volatile Conf conf;
	private String databaseName;
	private String databasePort;
	private String databaseUrl;
	private String databaseDriver;
	private String databaseSSL;
	private String databaseServerTime;
	private String databaseUserName;
	private String databasePassword;
	
	
	private Conf() {}
	
	public static Conf getInstance() {
		if(conf == null) {
			synchronized(Conf.class) {
				if(conf==null) {
					conf = new Conf();
					System.out.println("Create configuration new instance");
					conf.readConf();
				}
			}
		}
		return conf;
	}
	
	public static void createNewInstance() {
		conf = null;
	}
	
	private void readConf() {
		setDatabaseDriver(ReadConfig.value(Gstrax.DB_DRIVER));
		setDatabaseUrl(ReadConfig.value(Gstrax.DB_URL));
		String port = ReadConfig.value(Gstrax.DB_PORT);
		port = SecureChar.decode(port);
		setDatabasePort(port);
		setDatabaseServerTime(ReadConfig.value(Gstrax.DB_SERVER_TIME));
		setDatabaseName(ReadConfig.value(Gstrax.DB_NAME));
		String u_name = ReadConfig.value(Gstrax.USER_NAME);
		   u_name = SecureChar.decode(u_name);
		   u_name = u_name.replaceAll("mark", "");
		   u_name = u_name.replaceAll("rivera", "");
		   u_name = u_name.replaceAll("italia", "");
		   setDatabaseUserName(u_name);
	    String pword = ReadConfig.value(Gstrax.USER_PASS);
		   pword =  SecureChar.decode(pword);
		   pword = pword.replaceAll("mark", "");
		   pword = pword.replaceAll("rivera", "");
		   pword = pword.replaceAll("italia", "");
		   setDatabasePassword(pword);
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDatabasePort() {
		return databasePort;
	}
	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}
	public String getDatabaseUrl() {
		return databaseUrl;
	}
	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}
	public String getDatabaseDriver() {
		return databaseDriver;
	}
	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}
	public String getDatabaseSSL() {
		return databaseSSL;
	}
	public void setDatabaseSSL(String databaseSSL) {
		this.databaseSSL = databaseSSL;
	}
	public String getDatabaseUserName() {
		return databaseUserName;
	}
	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatabaseServerTime() {
		return databaseServerTime;
	}

	public void setDatabaseServerTime(String databaseServerTime) {
		this.databaseServerTime = databaseServerTime;
	}
	
}
