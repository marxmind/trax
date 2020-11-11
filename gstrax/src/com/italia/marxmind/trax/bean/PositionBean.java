package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Job;

/**
 * 
 * @author mark italia
 * @since 09/03/2017
 * @version 1.0
 *
 */
@ManagedBean(name="posBean", eager=true)
@ViewScoped
public class PositionBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 545458769661L;

	private String searchPosition;
	private List<Job> positions = new ArrayList<Job>();
	private Job jobData;
	private String jobName;
	
	private double sprayRegularRate;
	private double spraySpecialRate;
	
	@PostConstruct
	public void init(){
		positions = new ArrayList<Job>();
		
		String sql = "SELECT * FROM jobtitle WHERE isactivejob=1 ";
		
		if(getSearchPosition()!=null && !getSearchPosition().isEmpty()){
			sql += " AND jobname like '%"+ getSearchPosition().replace("--", "") +"%'";
		}
		
		positions = Job.retrieve(sql, new String[0]);
		
		if(positions!=null && positions.size()==1){
			clickItem(positions.get(0));
		}else{
			clear();
			Collections.reverse(positions);
		}
		
	}
	
	public void saveData(){
		Job job = new Job();
		
		if(getJobData()!=null){
			job = getJobData();
		}else{
			job.setIsActive(1);
		}
		
		if(getJobName()!=null && !getJobName().isEmpty()){
			
			job.setJobname(getJobName());
			job.setSprayRegularRate(getSprayRegularRate());
			job.setSpraySpecialRate(getSpraySpecialRate());
			job = Job.save(job);
			setJobData(job);
			init();
		}else{
			Application.addMessage(3, "Please provide Job name", "");
		}
		
	}
	
	public void clickItem(Job job){
		clear();
		setJobData(job);
		setJobName(job.getJobname());
		setSprayRegularRate(job.getSprayRegularRate());
		setSpraySpecialRate(job.getSpraySpecialRate());
	}
	
	public void deleteRow(Job job){
		job.delete();
		init();
	}
	
	public void clear(){
		setSearchPosition(null);
		setJobData(null);
		setJobName(null);
		setSprayRegularRate(0);
		setSpraySpecialRate(0);
	}
	
	public String getSearchPosition() {
		return searchPosition;
	}

	public void setSearchPosition(String searchPosition) {
		this.searchPosition = searchPosition;
	}

	public List<Job> getPositions() {
		return positions;
	}

	public void setPositions(List<Job> positions) {
		this.positions = positions;
	}

	public Job getJobData() {
		return jobData;
	}

	public void setJobData(Job jobData) {
		this.jobData = jobData;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public double getSprayRegularRate() {
		return sprayRegularRate;
	}

	public void setSprayRegularRate(double sprayRegularRate) {
		this.sprayRegularRate = sprayRegularRate;
	}

	public double getSpraySpecialRate() {
		return spraySpecialRate;
	}

	public void setSpraySpecialRate(double spraySpecialRate) {
		this.spraySpecialRate = spraySpecialRate;
	}
	
}
