package com.italia.marxmind.trax.bean;


import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.sessions.SessionBean;


/**
 * 
 * @author mark italia
 * @since 09/29/2016
 *@version 1.0
 */
@Named
@ViewScoped
public class MenuBean implements Serializable{

	private static final long serialVersionUID = 1098801825228384363L;
	
	private String dataTransfer;
	private boolean checkUserLogin;
	private String inventory;
	private String position;
	private String location;
	private String activty;
	private String uom;
	private String materials;
	private String fieldtimesheet;
	private String reports;
	private String timesheet;
	private String adminEmployees;
	private String adminuser;
	private String graph;
	
	private String getUI() {
		HttpSession session = SessionBean.getSession();
		return session.getAttribute("ui").toString();
	}
	
	public String graph() {
		if(Login.checkUserStatus()){
			return "graph";
		}else{
			return "login";
		}
	}
	
	public String dataTransfer(){
		if(Login.checkUserStatus()){
			return "datatransfer";
		}else{
			return "login";
		}
	}
	
	private boolean checkUserLogin(){
		if(Login.checkUserStatus()){
			return true;
		}else{
			return false;
		}
	}
	
	public String inventory(){
		if(checkUserLogin()){
			return "inventory" +getUI();
		}else{
			return "login";
		}
	}
	
	public String position(){
		if(checkUserLogin()){
			return "position" +getUI();
		}else{
			return "login";
		}	
	}
	
	public String location(){
		if(checkUserLogin()){
			return "location"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String activty(){
		if(checkUserLogin()){
			return "activity"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String uom(){
		if(checkUserLogin()){
			return "uom"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String materials(){
		if(checkUserLogin()){
			return "materials"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String fieldtimesheet(){
		if(checkUserLogin()){
			return "fieldtimesheet"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String reports(){
		if(checkUserLogin()){
			return "reports"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String timesheet(){
		if(checkUserLogin()){
			return "timesheet"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String adminEmployees(){
		if(checkUserLogin()){
			return "adminEmployees"+getUI();
		}else{
			return "login";
		}	
	}
	
	public String adminuser(){
		if(checkUserLogin()){
			return "adminuser"+getUI();
		}else{
			return "login";
		}	
	}
	
	
}
