package com.italia.marxmind.trax.application;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.sessions.SessionBean;
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
		HttpSession session = SessionBean.getSession();
		
		
		String theme = "vela";
		System.out.println("Applying theme... "+ theme);
		try{theme = session.getAttribute("themes").toString();
		System.out.println("Theme " + theme + " has been applied...");}catch(Exception e){theme = ReadConfig.value(Gstrax.THEME_STYLE);}
		return theme;
	}
	
}

