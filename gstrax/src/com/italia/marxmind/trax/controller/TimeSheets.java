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
 * @version 1.0
 * @since 08/21/2017
 *
 */

public class TimeSheets {

	private long id;
	private String dateTrans;
	private String timeIn;
	private String timeOut;
	private double countHour;
	private int isActive;
	private int isOvertime;
	private Timestamp timestamp;
	
	private Employee employee;
	private ActivityTransactions activityTransactions;
	
	public static List<TimeSheets> retrieve(String sqlAdd, String[] params){
		List<TimeSheets> times = Collections.synchronizedList(new ArrayList<TimeSheets>());
		
		String tableTime = "tme";
		String tableEmp = "emp";
		String tableAct = "ac";
		String sql = "SELECT * FROM timesheets " + tableTime + ", employee " + tableEmp + ", activitytrans " + tableAct + " WHERE " +
				tableTime +".empid=" + tableEmp + ".empid AND "+
				tableTime +".actransid=" + tableAct + ".actransid ";
		
		sql = sql + sqlAdd;
		
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
		
		System.out.println("SQL TIMES : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			TimeSheets time = new TimeSheets();
			try{time.setId(rs.getLong("timeid"));}catch(NullPointerException e){}
			try{time.setDateTrans(rs.getString("timeDateTrans"));}catch(NullPointerException e){}
			try{time.setTimeIn(rs.getString("timein"));}catch(NullPointerException e){}
			try{time.setTimeOut(rs.getString("timeout"));}catch(NullPointerException e){}
			try{time.setCountHour(rs.getDouble("counthr"));}catch(NullPointerException e){}
			try{time.setIsActive(rs.getInt("isactivetime"));}catch(NullPointerException e){}
			try{time.setTimestamp(rs.getTimestamp("timestamptime"));}catch(NullPointerException e){}
			try{time.setIsOvertime(rs.getInt("isot"));}catch(NullPointerException e){}
			
			Employee emp = new Employee();
			try{emp.setId(rs.getLong("empid"));}catch(NullPointerException e){}
			try{emp.setDateRegistered(rs.getString("datereg"));}catch(NullPointerException e){}
			try{emp.setDateResigned(rs.getString("dateend"));}catch(NullPointerException e){}
			try{emp.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{emp.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{emp.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{emp.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{emp.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{emp.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			try{emp.setContactNo(rs.getString("contactno"));}catch(NullPointerException e){}
			try{emp.setPurok(rs.getString("purok"));}catch(NullPointerException e){}
			try{emp.setIsActiveEmployee(rs.getInt("isactiveemp"));}catch(NullPointerException e){}
			try{emp.setSalary(rs.getDouble("salary"));}catch(NullPointerException e){}
			try{emp.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{emp.setOvertime(rs.getDouble("overtimerate"));}catch(NullPointerException e){}
			
			Job job = new Job();
			try{job.setJobid(rs.getInt("jobtitleid"));}catch(NullPointerException e){}
			emp.setJob(job);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			emp.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			emp.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			emp.setProvince(prov);
			
			time.setEmployee(emp);
			
			
			ActivityTransactions trn = new ActivityTransactions();
			try{trn.setId(rs.getLong("actransid"));}catch(NullPointerException e){}
			try{trn.setDateTrans(rs.getString("actDateTrans"));}catch(NullPointerException e){}
			try{trn.setLoads(rs.getString("loads"));}catch(NullPointerException e){}
			try{trn.setDrums(rs.getString("drums"));}catch(NullPointerException e){}
			try{trn.setBlocks(rs.getString("blocks"));}catch(NullPointerException e){}
			try{trn.setCuts(rs.getString("cuts"));}catch(NullPointerException e){}
			try{trn.setRemarks(rs.getString("remarks"));}catch(NullPointerException e){}
			try{trn.setIsActive(rs.getInt("isactiveactrans"));}catch(NullPointerException e){}
			try{trn.setTimestamp(rs.getTimestamp("timestampactrans"));}catch(NullPointerException e){}
			
			Activity ac = new Activity();
			try{ac.setId(rs.getInt("acid"));}catch(NullPointerException e){}
			trn.setActivity(ac);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			trn.setUserDtls(user);
			
			time.setActivityTransactions(trn);
			
			times.add(time);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return times;
	}
	
	public static TimeSheets retrieve(long timeId){
		TimeSheets time = new TimeSheets();
		
		String tableTime = "tme";
		String tableEmp = "emp";
		String tableAct = "ac";
		String sql = "SELECT * FROM timesheets " + tableTime + ", employee " + tableEmp + ", activitytrans " + tableAct + " WHERE " +
				tableTime +".empid=" + tableEmp + ".empid AND "+
				tableTime +".actransid=" + tableAct + ".actransid AND tme.isactivetime=1 AND " + tableTime + ".timeid="+timeId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{time.setId(rs.getLong("timeid"));}catch(NullPointerException e){}
			try{time.setDateTrans(rs.getString("timeDateTrans"));}catch(NullPointerException e){}
			try{time.setTimeIn(rs.getString("timein"));}catch(NullPointerException e){}
			try{time.setTimeOut(rs.getString("timeout"));}catch(NullPointerException e){}
			try{time.setCountHour(rs.getDouble("counthr"));}catch(NullPointerException e){}
			try{time.setIsActive(rs.getInt("isactivetime"));}catch(NullPointerException e){}
			try{time.setTimestamp(rs.getTimestamp("timestamptime"));}catch(NullPointerException e){}
			try{time.setIsOvertime(rs.getInt("isot"));}catch(NullPointerException e){}
			
			Employee emp = new Employee();
			try{emp.setId(rs.getLong("empid"));}catch(NullPointerException e){}
			try{emp.setDateRegistered(rs.getString("datereg"));}catch(NullPointerException e){}
			try{emp.setDateResigned(rs.getString("dateend"));}catch(NullPointerException e){}
			try{emp.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{emp.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{emp.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{emp.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{emp.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{emp.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			try{emp.setContactNo(rs.getString("contactno"));}catch(NullPointerException e){}
			try{emp.setPurok(rs.getString("purok"));}catch(NullPointerException e){}
			try{emp.setIsActiveEmployee(rs.getInt("isactiveemp"));}catch(NullPointerException e){}
			try{emp.setSalary(rs.getDouble("salary"));}catch(NullPointerException e){}
			try{emp.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{emp.setOvertime(rs.getDouble("overtimerate"));}catch(NullPointerException e){}
			
			Job job = new Job();
			try{job.setJobid(rs.getInt("jobtitleid"));}catch(NullPointerException e){}
			emp.setJob(job);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			emp.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			emp.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			emp.setProvince(prov);
			
			time.setEmployee(emp);
			
			ActivityTransactions trn = new ActivityTransactions();
			try{trn.setId(rs.getLong("actransid"));}catch(NullPointerException e){}
			try{trn.setDateTrans(rs.getString("actDateTrans"));}catch(NullPointerException e){}
			try{trn.setLoads(rs.getString("loads"));}catch(NullPointerException e){}
			try{trn.setDrums(rs.getString("drums"));}catch(NullPointerException e){}
			try{trn.setBlocks(rs.getString("blocks"));}catch(NullPointerException e){}
			try{trn.setCuts(rs.getString("cuts"));}catch(NullPointerException e){}
			try{trn.setRemarks(rs.getString("remarks"));}catch(NullPointerException e){}
			try{trn.setIsActive(rs.getInt("isactiveactrans"));}catch(NullPointerException e){}
			try{trn.setTimestamp(rs.getTimestamp("timestampactrans"));}catch(NullPointerException e){}
			
			Activity ac = new Activity();
			try{ac.setId(rs.getInt("acid"));}catch(NullPointerException e){}
			trn.setActivity(ac);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			trn.setUserDtls(user);
			
			time.setActivityTransactions(trn);
			
			
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return time;
	}
	
	public static TimeSheets save(TimeSheets time){
		if(time!=null){
			
			long id = TimeSheets.getInfo(time.getId() ==0? TimeSheets.getLatestId()+1 : time.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				time = TimeSheets.insertData(time, "1");
			}else if(id==2){
				LogU.add("update Data ");
				time = TimeSheets.updateData(time);
			}else if(id==3){
				LogU.add("added new Data ");
				time = TimeSheets.insertData(time, "3");
			}
			
		}
		return time;
	}
	
	public void save(){
			
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
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
	
	public static TimeSheets insertData(TimeSheets time, String type){
		String sql = "INSERT INTO timesheets ("
				+ "timeid,"
				+ "timeDateTrans,"
				+ "timein,"
				+ "timeout,"
				+ "counthr,"
				+ "isactivetime,"
				+ "empid,"
				+ "actransid,"
				+ "isot)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table timesheets");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			time.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			time.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, time.getDateTrans());
		ps.setString(cnt++, time.getTimeIn());
		ps.setString(cnt++, time.getTimeOut());
		ps.setDouble(cnt++, time.getCountHour());
		ps.setInt(cnt++, time.getIsActive());
		ps.setLong(cnt++, time.getEmployee()==null? 0 : time.getEmployee().getId());
		ps.setLong(cnt++, time.getActivityTransactions()==null? 0 : time.getActivityTransactions().getId());
		ps.setInt(cnt++, time.getIsOvertime());
		
		LogU.add(time.getDateTrans());
		LogU.add(time.getTimeIn());
		LogU.add(time.getTimeOut());
		LogU.add(time.getCountHour());
		LogU.add(time.getIsActive());
		LogU.add(time.getEmployee()==null? 0 : time.getEmployee().getId());
		LogU.add(time.getActivityTransactions()==null? 0 : time.getActivityTransactions().getId());
		LogU.add(time.getIsOvertime());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to timesheets : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return time;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO timesheets ("
				+ "timeid,"
				+ "timeDateTrans,"
				+ "timein,"
				+ "timeout,"
				+ "counthr,"
				+ "isactivetime,"
				+ "empid,"
				+ "actransid,"
				+ "isot)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table timesheets");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getTimeIn());
		ps.setString(cnt++, getTimeOut());
		ps.setDouble(cnt++, getCountHour());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setLong(cnt++, getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		ps.setInt(cnt++, getIsOvertime());
		
		LogU.add(getDateTrans());
		LogU.add(getTimeIn());
		LogU.add(getTimeOut());
		LogU.add(getCountHour());
		LogU.add(getIsActive());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		LogU.add(getIsOvertime());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to timesheets : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static TimeSheets updateData(TimeSheets time){
		String sql = "UPDATE timesheets SET "
				+ "timeDateTrans=?,"
				+ "timein=?,"
				+ "timeout=?,"
				+ "counthr=?,"
				+ "empid=?,"
				+ "actransid=?,"
				+ "isot=?" 
				+ " WHERE timeid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table timesheets");
		
		ps.setString(cnt++, time.getDateTrans());
		ps.setString(cnt++, time.getTimeIn());
		ps.setString(cnt++, time.getTimeOut());
		ps.setDouble(cnt++, time.getCountHour());
		ps.setLong(cnt++, time.getEmployee()==null? 0 : time.getEmployee().getId());
		ps.setLong(cnt++, time.getActivityTransactions()==null? 0 : time.getActivityTransactions().getId());
		ps.setInt(cnt++, time.getIsOvertime());
		ps.setLong(cnt++, time.getId());
		
		LogU.add(time.getDateTrans());
		LogU.add(time.getTimeIn());
		LogU.add(time.getTimeOut());
		LogU.add(time.getCountHour());
		LogU.add(time.getEmployee()==null? 0 : time.getEmployee().getId());
		LogU.add(time.getActivityTransactions()==null? 0 : time.getActivityTransactions().getId());
		LogU.add(time.getIsOvertime());
		LogU.add(time.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to timesheets : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return time;
	}
	
	public void updateData(){
		String sql = "UPDATE timesheets SET "
				+ "timeDateTrans=?,"
				+ "timein=?,"
				+ "timeout=?,"
				+ "counthr=?,"
				+ "empid=?,"
				+ "actransid=?,"
				+ "isot=?" 
				+ " WHERE timeid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table timesheets");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getTimeIn());
		ps.setString(cnt++, getTimeOut());
		ps.setDouble(cnt++, getCountHour());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setLong(cnt++, getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		ps.setInt(cnt++, getIsOvertime());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getTimeIn());
		LogU.add(getTimeOut());
		LogU.add(getCountHour());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		LogU.add(getIsOvertime());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to timesheets : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT timeid FROM timesheets  ORDER BY timeid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("timeid");
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
		ps = conn.prepareStatement("SELECT timeid FROM timesheets WHERE timeid=?");
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
		String sql = "UPDATE timesheets set isactivetime=0 WHERE timeid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
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
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDateTrans() {
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	public double getCountHour() {
		return countHour;
	}

	public void setCountHour(double countHour) {
		this.countHour = countHour;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public ActivityTransactions getActivityTransactions() {
		return activityTransactions;
	}

	public void setActivityTransactions(ActivityTransactions activityTransactions) {
		this.activityTransactions = activityTransactions;
	}

	public int getIsOvertime() {
		return isOvertime;
	}

	public void setIsOvertime(int isOvertime) {
		this.isOvertime = isOvertime;
	}
	
}
