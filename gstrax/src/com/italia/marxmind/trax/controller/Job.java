package com.italia.marxmind.trax.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.marxmind.trax.database.ConnectDB;
import com.italia.marxmind.trax.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class Job {

	private int jobid;
	private String jobname;
	private int isActive;
	private double sprayRegularRate;
	private double spraySpecialRate;
	private Timestamp timestamp;
	
	public Job(){}
	
	public Job(
			int jobid,
			String jobname
			){
		this.jobid = jobid;
		this.jobname = jobname;
	}
	
	public static String jobSQL(String tablename,Job job){
		String sql="";
		if(job!=null){
			
			if(job.getJobid()!=0){
				sql += " AND "+ tablename +".jobtitleid=" + job.getJobid();
			}
			if(job.getJobname()!=null){
				sql += " AND "+ tablename +".jobname='" + job.getJobname()+"'";
			}
			
		}
		
		return sql;
	}
	
	public static List<Job> retrieve(String sql, String[] params){
		List<Job> jobs = new ArrayList<Job>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Job job = new Job();
			try{job.setJobid(rs.getInt("jobtitleid"));}catch(NullPointerException e){}
			try{job.setJobname(rs.getString("jobname"));}catch(NullPointerException e){}
			try{job.setIsActive(rs.getInt("isactivejob"));}catch(NullPointerException e){}
			try{job.setSprayRegularRate(rs.getDouble("sprayregularrate"));}catch(NullPointerException e){}
			try{job.setSpraySpecialRate(rs.getDouble("sprayspecialrate"));}catch(NullPointerException e){}
			jobs.add(job);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return jobs;
	}
	
	public static Job job(int jobid){
		Job job = new Job();
		String sql = "SELECT * FROM jobtitle WHERE jobtitleid=?";
		String[] params = new String[1];
		params[0] = jobid+"";
		try{
			job = Job.retrieve(sql, params).get(0);
		}catch(Exception e){}
		return job;
	}
	
	public static Job save(Job job){
		if(job!=null){
			
			long id = Job.getInfo(job.getJobid() ==0? Job.getLatestId()+1 : job.getJobid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				job = Job.insertData(job, "1");
			}else if(id==2){
				LogU.add("update Data ");
				job = Job.updateData(job);
			}else if(id==3){
				LogU.add("added new Data ");
				job = Job.insertData(job, "3");
			}
			
		}
		return job;
	}
	
	public void save(){
			
			long id = getInfo(getJobid() ==0? getLatestId()+1 : getJobid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			
	}
	
	public static Job insertData(Job job, String type){
		String sql = "INSERT INTO jobtitle ("
				+ "jobtitleid,"
				+ "jobname,"
				+ "isactivejob,"
				+ "sprayregularrate,"
				+ "sprayspecialrate)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table jobtitle");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			job.setJobid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			job.setJobid(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, job.getJobname());
		ps.setInt(cnt++, job.getIsActive());
		ps.setDouble(cnt++, job.getSprayRegularRate());
		ps.setDouble(cnt++, job.getSpraySpecialRate());
		
		LogU.add(job.getJobname());
		LogU.add(job.getIsActive());
		LogU.add(job.getSprayRegularRate());
		LogU.add(job.getSpraySpecialRate());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to jobtitle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return job;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO jobtitle ("
				+ "jobtitleid,"
				+ "jobname,"
				+ "isactivejob,"
				+ "sprayregularrate,"
				+ "sprayspecialrate)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table jobtitle");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setJobid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setJobid(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, getJobname());
		ps.setInt(cnt++, getIsActive());
		ps.setDouble(cnt++, getSprayRegularRate());
		ps.setDouble(cnt++, getSpraySpecialRate());
		
		LogU.add(getJobname());
		LogU.add(getIsActive());
		LogU.add(getSprayRegularRate());
		LogU.add(getSpraySpecialRate());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to jobtitle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Job updateData(Job job){
		String sql = "UPDATE jobtitle SET "
				+ "jobname=?,"
				+ "sprayregularrate=?,"
				+ "sprayspecialrate=? " 
				+ " WHERE jobtitleid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table jobtitle");
		
		ps.setString(cnt++, job.getJobname());
		ps.setDouble(cnt++, job.getSprayRegularRate());
		ps.setDouble(cnt++, job.getSpraySpecialRate());
		ps.setInt(cnt++, job.getJobid());
		
		LogU.add(job.getJobname());
		LogU.add(job.getSprayRegularRate());
		LogU.add(job.getSpraySpecialRate());
		LogU.add(job.getJobid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to jobtitle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return job;
	}
	
	public void updateData(){
		String sql = "UPDATE jobtitle SET "
				+ "jobname=?,"
				+ "sprayregularrate=?,"
				+ "sprayspecialrate=? " 
				+ " WHERE jobtitleid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table jobtitle");
		
		ps.setString(cnt++, getJobname());
		ps.setDouble(cnt++, getSprayRegularRate());
		ps.setDouble(cnt++, getSpraySpecialRate());
		ps.setInt(cnt++, getJobid());
		
		LogU.add(getJobname());
		LogU.add(getSprayRegularRate());
		LogU.add(getSpraySpecialRate());
		LogU.add(getJobid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to jobtitle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT jobtitleid FROM jobtitle  ORDER BY jobtitleid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("jobtitleid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT jobtitleid FROM jobtitle WHERE jobtitleid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE jobtitle set isactivejob=0 WHERE jobtitleid=?";
		
		String[] params = new String[1];
		params[0] = getJobid()+"";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public int getJobid() {
		return jobid;
	}
	public void setJobid(int jobid) {
		this.jobid = jobid;
	}
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
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
