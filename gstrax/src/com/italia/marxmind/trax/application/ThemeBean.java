package com.italia.marxmind.trax.application;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;
/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */
@Named
@ApplicationScoped
public class ThemeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 147868854437557L;

	public String getApplicationTheme(){
		String theme = "none";
		System.out.println("Applying theme...");
		try{theme = ReadConfig.value(Gstrax.THEME_STYLE);
		System.out.println("Theme " + theme + " has been applied...");}catch(Exception e){}
		return theme;
	}
	
}

