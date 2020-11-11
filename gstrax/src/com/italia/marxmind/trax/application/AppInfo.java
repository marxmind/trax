package com.italia.marxmind.trax.application;

import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.trax.security.Copyright;
import com.italia.marxmind.trax.security.License;

/**
 * 
 * @author Mark Italia
 * @since 10-13-2020
 * @version 1.0
 *
 */
public class AppInfo {
	
	private static volatile AppInfo appInfo;
	private static Object[] obj = new Object[4];
	private AppInfo() {}
	/**
	 * 
	 * @return [ApplicationVersionController],[Copyright],[List<ApplicationFixes>],[List<License>]
	 */
	public static Object[] getInstance() {
		if(appInfo == null) {
			synchronized(AppInfo.class) {
				if(appInfo==null) {
					appInfo = new AppInfo();
					System.out.println("Create AppInfo new instance");
					obj = AppInfo.readAppInfoOnDB();
				}
			}
		}
		return obj;
	}
	
	public static void createNewInstance() {
		appInfo = null;
	}
	
	/**
	 * 
	 * @return [ApplicationVersionController],[Copyright],[List<ApplicationFixes>],[List<License>]
	 */
	private static Object[] readAppInfoOnDB() {
		System.out.println("reading app info data on database.....");
		Object[] obj = new Object[4];
		String sql = "SELECT * FROM app_version_control ORDER BY timestamp DESC LIMIT 1";
		String[] params = new String[0];
		ApplicationVersionController versionController = ApplicationVersionController.retrieve(sql, params).get(0);
		obj[0] = versionController;
		sql = "SELECT * FROM copyright ORDER BY id desc limit 1";
		params = new String[0];
		Copyright copyright = Copyright.retrieve(sql, params).get(0);
		obj[1] = copyright;
		List<ApplicationFixes> fixes = new ArrayList<ApplicationFixes>();
		try{fixes = new ArrayList<ApplicationFixes>();}catch(Exception e){}
		sql = "SELECT * FROM buildfixes WHERE buildid=?";
		params = new String[1];
		params[0] = versionController.getBuildid()+"";
		try{fixes = ApplicationFixes.retrieve(sql, params);}catch(Exception e){}
		obj[2]=fixes;
		sql = "SELECT * FROM license";
		List<License> licenses = new ArrayList<License>();
		licenses = License.retrieve(sql, new String[0]);
		obj[3]=licenses;
		System.out.println("end reading app info....");
		return obj;
	}
	
}
