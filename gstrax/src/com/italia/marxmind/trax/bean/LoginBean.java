package com.italia.marxmind.trax.bean;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.trax.application.ClientInfo;
import com.italia.marxmind.trax.controller.Business;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.enm.UserAccess;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.security.License;
import com.italia.marxmind.trax.security.Module;
import com.italia.marxmind.trax.sessions.IBean;
import com.italia.marxmind.trax.sessions.SessionBean;
import com.italia.marxmind.trax.utils.DateUtils;
import com.italia.marxmind.trax.utils.LogU;
import com.italia.marxmind.trax.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */

@ManagedBean(name="loginBean", eager=true)
@SessionScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String name;
	private String password;
	private String errorMessage;
	private Login login;
	private String keyPress;
	private int businessId;
	private List business;
	private Map<Integer, Business> businessData = Collections.synchronizedMap(new HashMap<Integer, Business>());
	
	public String getCurrentDate(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date_ = new Date();
		String _date = dateFormat.format(date_);
		return _date;
	}
	
	
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@PostConstruct
	public void init(){
		//invalidate session
		//IBean.removeBean();
		
		//load business wallpaper
		String wallpaper = ReadConfig.value(Gstrax.BUSINESS_WALLPAPER_FILE);
		//System.out.println("wallpaper >>> " + wallpaper);
		copyProductImg(wallpaper);
	}
	
	public void copyProductImg(String wallpaper){
		String pathToSave = FacesContext.getCurrentInstance()
                .getExternalContext().getRealPath(Gstrax.SEPERATOR.getName()) + Gstrax.SEPERATOR.getName() +
                Gstrax.APP_RESOURCES_LOC.getName() + Gstrax.SEPERATOR.getName() + 
                Gstrax.BUSSINES_WALLPAPER_IMG.getName() + Gstrax.SEPERATOR.getName();
		//System.out.println("pathToSave " + pathToSave + wallpaper);
		File logdirectory = new File(pathToSave);
		if(logdirectory.isDirectory()){
			//System.out.println("is directory");
		}
		
		
		String productFile = ReadConfig.value(Gstrax.APP_IMG_FILE) + wallpaper;
		File file = new File(productFile);
		//if(!file.exists()){
			//System.out.println("copying file.... " + file.getName());
			try{
			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
			        StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException e){}
			
		//}
	}
	
	//validate login
	public String validateUserNamePassword(){
		
		//if(getBusinessId()==0){
		//set database on on business type
		changeDatabaseConnection();
		//}
		
		
		String sql = "SELECT * FROM login WHERE username=? and password=?";
		String[] params = new String[2];
		         params[0] = Whitelist.remove(name);
		         params[1] = Whitelist.remove(password);
		Login in = null;
		try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
		
		/*boolean valid = Login.validate(sql, params);
		System.out.println("Valid: " + valid);*/
		
		String result="login";
		LogU.add("Guest with username : " + name + " and password " + password + " is trying to log in the system.");
		if(in!=null){
			
	        HttpSession session = SessionBean.getSession();
	        session.setAttribute("username", name);
			session.setAttribute("userid", in.getLogid());
			
			boolean isExpired = License.checkLicenseExpiration(Module.GSTRAX);
			
	        /*if(in.getAccessLevel().getLevel()==1 || in.getAccessLevel().getLevel()==2){
	        	result = "main";
	        }else{
	        	result = "pos";
	        }*/
			//if(UserAccess.DEVELOPER.getId() == in.getAccessLevel().getLevel() || 
			//		UserAccess.OWNER.getId() == in.getAccessLevel().getLevel() || 
			//		UserAccess.MANAGER.getId() == in.getAccessLevel().getLevel()){
				result = "main";
			
			
			LogU.add("The user has been successfully login to the application with the username : " + name + " and password " + password);
			
			if(isExpired){
				LogU.add("The application is expired. Please contact application owner.");
				result = "expired";
			}else{
				logUserIn(in);
			}
			
			
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
//			/setErrorMessage("Incorrect username and password.");
			LogU.add("The user was not successfully login to the application with the username : " + name + " and password " + password);
			setName("");
			setPassword("");
			result= "login";
		}
		System.out.println(getErrorMessage());
		return result;
	}
	
	private void logUserIn(Login in){
		if(in==null) in = new Login();
		ClientInfo cinfo = new ClientInfo();
		in.setLogintime(DateUtils.getCurrentDateMMDDYYYYTIME());
		in.setIsOnline(1);
		in.setClientip(cinfo.getClientIP());
		in.setClientbrowser(cinfo.getBrowserName());
		in.save();
	}
	
	private void logUserOut(){
		String sql = "SELECT * FROM login WHERE username=? and logid=?";
		HttpSession session = SessionBean.getSession();
		String userid = session.getAttribute("userid").toString();
		String username = session.getAttribute("username").toString();
		String[] params = new String[2];
    	params[0] = username;
    	params[1] = userid;
    	Login in = null;
    	try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
		ClientInfo cinfo = new ClientInfo();
		if(in!=null){
			in.setLastlogin(DateUtils.getCurrentDateMMDDYYYYTIME());
			in.setIsOnline(0);
			in.setClientip(cinfo.getClientIP());
			in.setClientbrowser(cinfo.getBrowserName());
			in.save();
		}
		LogU.add("The user " + username + " was logging out to the application.");
		
		//Remove registered bean in session
		IBean.removeBean();
		
	}
	
	//logout event, invalidate session
	public String logout(){
		logUserOut();
		setName(null);
		setPassword(null);
		return "login";
	}


	public void changeDatabaseConnection(){
		System.out.println("changing database....");
		
		int size = 6;
		Gstrax[] tag = new Gstrax[size];
		String[] value = new String[size];
		int i=0;
		tag[i] = Gstrax.DB_NAME; value[i] = getBusinessData().get(getBusinessId()).getDatabase(); i++;
		tag[i] = Gstrax.BUSINESS_NAME; value[i] = getBusinessData().get(getBusinessId()).getName(); i++;
		tag[i] = Gstrax.DB_DRIVER; value[i] = getBusinessData().get(getBusinessId()).getDriver(); i++;
		tag[i] = Gstrax.DB_URL; value[i] = getBusinessData().get(getBusinessId()).getUrl(); i++;
		tag[i] = Gstrax.DB_PORT; value[i] = getBusinessData().get(getBusinessId()).getPort(); i++;
		tag[i] = Gstrax.DB_SSL; value[i] = getBusinessData().get(getBusinessId()).getSsl(); i++;
		
		Business.updateBusiness(tag, value);
		
		System.out.println("Database has been changed....");
	}

	public Login getLogin() {
		return login;
	}



	public void setLogin(Login login) {
		this.login = login;
	}



	public String getKeyPress() {
		/*if((getName()!=null && !getName().isEmpty()) && (getPassword()!=null && !getPassword().isEmpty())){
			keyPress = "logId";
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Please provide information to proceed.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}*/
		keyPress = "logId";
		return keyPress;
	}



	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}



	public int getBusinessId() {
		return businessId;
	}



	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}



	public List getBusiness() {
		businessData = Collections.synchronizedMap(new HashMap<Integer, Business>());
		business = new ArrayList<>();
		
		for(Business bz : Business.readBusinessXML()){
			business.add(new SelectItem(bz.getId(), bz.getName()));
			businessData.put(bz.getId(), bz);
		}
		
		return business;
	}



	public void setBusiness(List business) {
		this.business = business;
	}



	public Map<Integer, Business> getBusinessData() {
		return businessData;
	}



	public void setBusinessData(Map<Integer, Business> businessData) {
		this.businessData = businessData;
	}
	
}


