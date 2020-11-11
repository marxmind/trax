package com.italia.marxmind.trax.enm;

import java.io.File;
/**
 * 
 * @author mark italia
 * @since created 09/27/2016
 * @version 1.0
 *
 */
public enum Gstrax {

	DB_NAME("databaseName"),
	DB_DRIVER("driver"),
	DB_URL("url"),
	DB_PORT("port"),
	DB_SSL("SSL"),
	DB_SERVER_TIME("servertime"),
	USER_NAME("username"),
	USER_PASS("password"),
	APP_EXP("applicationExp"),
	APP_VER("applicationVersion"),
	APP_COPYRIGHT("copyright"),
	APP_OWNER("author"),
	APP_EMAIL("supportEamil"),
	APP_PHONE("supportNo"),
	APP_LOG_PATH("logPath"),
	APP_LOG("includeLog"),
	APP_FOLDER("gstrax"),
	APP_CONFIG_FLDR("conf"),
	APP_CONFIG_FILE("application.xml"),
	APP_LICENSE_FILE_NAME("license.xml"),
	APP_IMG_FILE("imgPath"),
	APP_BUSINESS_TYPE("businessType"),
	APP_RESOURCES_LOC("resources"),
	APP_RESOURCES_LOC_IMG("img"),
	SECURITY_ENCRYPTION_FORMAT("utf-8"),
	PRIMARY_DRIVE(System.getenv("SystemDrive")),
	SEPERATOR(File.separator),
	REPORT_CONFIG_FILENAME("reports.xml"),
	REPORT("reports"),
	BUSINESS_NAME("businessName"),
	BUSINESS_WALLPAPER_FILE("businessWallpaperFile"),
	BUSSINES_WALLPAPER_IMG("gif"),
	THEME_STYLE("themeStyle"),
	BUSINESS("business.xml"),
	RECEIPT_INFO("receiptsinfo.xml"),
	RECEIPTS_LOG("receiptLog"),
	NORMAL_RENDERED_HOURS("hourperday"),
	OTSTART("otstart"),
	LABOR_SPRAYMAN_DAILY_RATE("laborspraymandailyrate"),
	LABOR_HARVESTER_DAILY_RATE("laborharvesterdailyrate"),
	DRIVER_NORMAL_PER_DRUM_RATE("driverperdrumrate"),
	PER_DRUM_DIVIDER("perdrumdivider"),
	FORCING1("forcing1"),
	FORCING2("forcing2"),
	FORCING_RATE_PER_DRUM("forcingrateperdrum"),
	FIELD_SPRAY_DRIVER("fieldspraydriver"),
	OTHER_SPRAY_RATE_PER_DRUM("othersprayrate");
	private String name;
	
	
	private Gstrax(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
