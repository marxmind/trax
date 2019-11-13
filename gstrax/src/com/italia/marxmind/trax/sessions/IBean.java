package com.italia.marxmind.trax.sessions;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.trax.bean.LoginBean;

/**
 * 
 * @author mark italia
 * @since 10/01/2016
 * @version 1.0
 *
 */
public class IBean {

	/**
	 * Remove and invalidate user session
	 */
	public static void removeBean(){
		try{
		HttpSession session = SessionBean.getSession();
		String[] beans = {
				"featuresBean","actBean","auserBean","customerBean","empBean","fldBean","invBean",
				"locBean","matBean","menuBean","posBean", "rptBean", "atimeBean", "uomBean","loginBean"
				};
		for(String bean : beans){
			FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(bean);
		}
		session.setAttribute("username", null);
		session.setAttribute("userid", null);
		session.invalidate();
		}catch(Exception e){}
	}
	
}
