package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Activity;
/**
 * 
 * @author mark italia
 * @since 09/02/2017
 * @version 1.0
 *
 */
@Named
@ViewScoped
public class ActivityBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5456689564541L;
	
	private String actName;
	private String searchName;
	private Activity activityData;
	private List<Activity> activitys = new ArrayList<Activity>();
	
	@PostConstruct
	public void init(){
		
		activitys = new ArrayList<Activity>();
		String sql = "SELECT * FROM activity WHERE isactiveac=1";
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			sql += " AND acname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		
		activitys = Activity.retrieve(sql, new String[0]);
		
		if(activitys!=null && activitys.size()==1){
			clickItem(activitys.get(0));
		}else{
			clear();
			Collections.reverse(activitys);
		}
		
	}
	
	public void saveData(){
		Activity act = new Activity();
		if(getActivityData()!=null){
			act = getActivityData();
		}else{
			act.setIsActive(1);
		}
		
		
		if(getActName()!=null && !getActName().isEmpty()){
		
		act.setName(getActName());
		
		act = Activity.save(act);
		
		setActivityData(act);
		init();
			Application.addMessage(1, "Successfully saved", "");
		}else{
			Application.addMessage(3, "Please provide Activity name", "");
		}
		
	}
	
	public void deleteRow(Activity act){
		act.delete();
		init();
		Application.addMessage(1, "Successfully deleted", "");
	}
	
	public void clickItem(Activity act){
		clear();
		setActName(act.getName());
		setActivityData(act);
	}
	
	public void clear(){
		setActivityData(null);
		setSearchName(null);
		setActName(null);
	}
	
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public Activity getActivityData() {
		return activityData;
	}
	public void setActivityData(Activity activityData) {
		this.activityData = activityData;
	}
	public List<Activity> getActivitys() {
		return activitys;
	}
	public void setActivitys(List<Activity> activitys) {
		this.activitys = activitys;
	}
	
}
