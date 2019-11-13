package com.italia.marxmind.trax.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.trax.database.ConnectDB;
import com.italia.marxmind.trax.utils.DateUtils;
import com.italia.marxmind.trax.utils.LogU;
/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 08/21/2017
 */

public class Employee {

	private long id;
	private String dateRegistered;
	private String dateResigned;
	private String firstName;
	private String middleName;
	private String lastName;
	private String fullName;
	private int age;
	private int gender;
	private double salary;
	private double overtime;
	
	private int isResigned;
	private int isActiveEmployee;
	private Timestamp timestamp;
	
	private String contactNo;
	private String purok;
	
	private Job job;
	
	private Barangay barangay;
	private Municipality municipality;
	private Province province;
	
	private String dailyRate;
	private String hourlyRate;
	private String overTimeRate;
	private String payableServices;
	
	private String timeRecordedInfo;
	
	public static List<String> retriveNames(String sql){
		List<String> str = new ArrayList<>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
			while(rs.next()){
				str.add(rs.getString("fullname"));
			}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
			
		return str;
	}
	
	public static List<Employee> retrieve(String sqlAdd, String[] params){
		List<Employee> emps = new ArrayList<>();
		
		String tableEmp = "emp";
		String tableJob = "pos";
		String tableBg = "bg";
		String tableMun = "mu";
		String tablePrv = "prv";
		String sql = "SELECT * FROM employee " + tableEmp + ", jobtitle " + tableJob + ", barangay " + tableBg + ", municipality " + tableMun + ", province " + tablePrv + " WHERE " +
				tableEmp +".jobtitleid=" + tableJob + ".jobtitleid AND " +
				tableEmp + ".bgid=" + tableBg + ".bgid AND " +
				tableEmp + ".munid=" + tableMun + ".munid AND " +
				tableEmp + ".provid=" + tablePrv + ".provid ";
		
		sql = sql + sqlAdd;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL: " + ps.toString());
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
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
			try{job.setJobname(rs.getString("jobname"));}catch(NullPointerException e){}
			emp.setJob(job);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			emp.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			emp.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			emp.setProvince(prov);
			
			emps.add(emp);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return emps;
	}
	
	public static Employee retrieve(long emplyeeId){
		Employee emp = new Employee();
		
		String tableEmp = "emp";
		String tableJob = "pos";
		String tableBg = "bg";
		String tableMun = "mu";
		String tablePrv = "prv";
		String sql = "SELECT * FROM employee " + tableEmp + ", jobtitle " + tableJob + ", barangay " + tableBg + ", municipality " + tableMun + ", province " + tablePrv + " WHERE " +
				tableEmp +".jobtitleid=" + tableJob + ".jobtitleid AND " + 
				tableEmp + ".bgid=" + tableBg + ".bgid AND " +
				tableEmp + ".munid=" + tableMun + ".munid AND " +
				tableEmp + ".provid=" + tablePrv + ".provid AND emp.isactiveemp=1 AND emp.empid="+ emplyeeId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
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
			try{job.setJobname(rs.getString("jobname"));}catch(NullPointerException e){}
			emp.setJob(job);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			emp.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			emp.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			emp.setProvince(prov);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return emp;
	}
	
	public static Employee save(Employee emp){
		if(emp!=null){
			
			long id = Employee.getInfo(emp.getId() ==0? Employee.getLatestId()+1 : emp.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				emp = Employee.insertData(emp, "1");
			}else if(id==2){
				LogU.add("update Data ");
				emp = Employee.updateData(emp);
			}else if(id==3){
				LogU.add("added new Data ");
				emp = Employee.insertData(emp, "3");
			}
			
		}
		return emp;
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
	
	public static Employee insertData(Employee emp, String type){
		String sql = "INSERT INTO employee ("
				+ "empid,"
				+ "datereg,"
				+ "dateend,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "age,"
				+ "gender,"
				+ "isresigned,"
				+ "isactiveemp,"
				+ "jobtitleid,"
				+ "contactno,"
				+ "purok,"
				+ "bgid,"
				+ "munid,"
				+ "provid,"
				+ "salary,"
				+ "fullname,"
				+ "overtimerate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employee");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			emp.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			emp.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, emp.getDateRegistered());
		ps.setString(cnt++, emp.getDateResigned());
		ps.setString(cnt++, emp.getFirstName());
		ps.setString(cnt++, emp.getMiddleName());
		ps.setString(cnt++, emp.getLastName());
		ps.setInt(cnt++, emp.getAge());
		ps.setInt(cnt++, emp.getGender());
		ps.setInt(cnt++, emp.getIsResigned());
		ps.setInt(cnt++, emp.getIsActiveEmployee());
		ps.setInt(cnt++, emp.getJob()==null? 0 : emp.getJob().getJobid());
		ps.setString(cnt++, emp.getContactNo());
		ps.setString(cnt++, emp.getPurok());
		ps.setInt(cnt++, emp.getBarangay()==null? 0 : emp.getBarangay().getId());
		ps.setInt(cnt++, emp.getMunicipality()==null? 0 : emp.getMunicipality().getId());
		ps.setInt(cnt++, emp.getProvince()==null? 0 : emp.getProvince().getId());
		ps.setDouble(cnt++, emp.getSalary());
		ps.setString(cnt++, emp.getFullName());
		ps.setDouble(cnt++, emp.getOvertime());
		
		LogU.add(emp.getDateRegistered());
		LogU.add(emp.getDateResigned());
		LogU.add(emp.getFirstName());
		LogU.add(emp.getMiddleName());
		LogU.add(emp.getLastName());
		LogU.add(emp.getAge());
		LogU.add(emp.getGender());
		LogU.add(emp.getIsResigned());
		LogU.add(emp.getIsActiveEmployee());
		LogU.add(emp.getJob()==null? 0 : emp.getJob().getJobid());
		LogU.add(emp.getContactNo());
		LogU.add(emp.getPurok());
		LogU.add(emp.getBarangay()==null? 0 : emp.getBarangay().getId());
		LogU.add(emp.getMunicipality()==null? 0 : emp.getMunicipality().getId());
		LogU.add(emp.getProvince()==null? 0 : emp.getProvince().getId());
		LogU.add(emp.getSalary());
		LogU.add(emp.getFullName());
		LogU.add(emp.getOvertime());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employee : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return emp;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO employee ("
				+ "empid,"
				+ "datereg,"
				+ "dateend,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "age,"
				+ "gender,"
				+ "isresigned,"
				+ "isactiveemp,"
				+ "jobtitleid,"
				+ "contactno,"
				+ "purok,"
				+ "bgid,"
				+ "munid,"
				+ "provid,"
				+ "salary,"
				+ "fullname,"
				+ "overtimerate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employee");
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
		ps.setString(cnt++, getDateRegistered());
		ps.setString(cnt++, getDateResigned());
		ps.setString(cnt++, getFirstName());
		ps.setString(cnt++, getMiddleName());
		ps.setString(cnt++, getLastName());
		ps.setInt(cnt++, getAge());
		ps.setInt(cnt++, getGender());
		ps.setInt(cnt++, getIsResigned());
		ps.setInt(cnt++, getIsActiveEmployee());
		ps.setInt(cnt++, getJob()==null? 0 : getJob().getJobid());
		ps.setString(cnt++, getContactNo());
		ps.setString(cnt++, getPurok());
		ps.setInt(cnt++, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(cnt++, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(cnt++, getProvince()==null? 0 : getProvince().getId());
		ps.setDouble(cnt++, getSalary());
		ps.setString(cnt++, getFullName());
		ps.setDouble(cnt++, getOvertime());
		
		LogU.add(getDateRegistered());
		LogU.add(getDateResigned());
		LogU.add(getFirstName());
		LogU.add(getMiddleName());
		LogU.add(getLastName());
		LogU.add(getAge());
		LogU.add(getGender());
		LogU.add(getIsResigned());
		LogU.add(getIsActiveEmployee());
		LogU.add(getJob()==null? 0 : getJob().getJobid());
		LogU.add(getContactNo());
		LogU.add(getPurok());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		LogU.add(getSalary());
		LogU.add(getFullName());
		LogU.add(getOvertime());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employee : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Employee updateData(Employee emp){
		String sql = "UPDATE employee SET "
				+ "datereg=?,"
				+ "dateend=?,"
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "isresigned=?,"
				+ "jobtitleid=?,"
				+ "contactno=?,"
				+ "purok=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=?,"
				+ "salary=?,"
				+ "fullname=?,"
				+ "overtimerate=? " 
				+ " WHERE empid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employee");
		
		ps.setString(cnt++, emp.getDateRegistered());
		ps.setString(cnt++, emp.getDateResigned());
		ps.setString(cnt++, emp.getFirstName());
		ps.setString(cnt++, emp.getMiddleName());
		ps.setString(cnt++, emp.getLastName());
		ps.setInt(cnt++, emp.getAge());
		ps.setInt(cnt++, emp.getGender());
		ps.setInt(cnt++, emp.getIsResigned());
		ps.setInt(cnt++, emp.getJob()==null? 0 : emp.getJob().getJobid());
		ps.setString(cnt++, emp.getContactNo());
		ps.setString(cnt++, emp.getPurok());
		ps.setInt(cnt++, emp.getBarangay()==null? 0 : emp.getBarangay().getId());
		ps.setInt(cnt++, emp.getMunicipality()==null? 0 : emp.getMunicipality().getId());
		ps.setInt(cnt++, emp.getProvince()==null? 0 : emp.getProvince().getId());
		ps.setDouble(cnt++, emp.getSalary());
		ps.setString(cnt++, emp.getFullName());
		ps.setDouble(cnt++, emp.getOvertime());
		ps.setLong(cnt++, emp.getId());
		
		LogU.add(emp.getDateRegistered());
		LogU.add(emp.getDateResigned());
		LogU.add(emp.getFirstName());
		LogU.add(emp.getMiddleName());
		LogU.add(emp.getLastName());
		LogU.add(emp.getAge());
		LogU.add(emp.getGender());
		LogU.add(emp.getIsResigned());
		LogU.add(emp.getJob()==null? 0 : emp.getJob().getJobid());
		LogU.add(emp.getContactNo());
		LogU.add(emp.getPurok());
		LogU.add(emp.getBarangay()==null? 0 : emp.getBarangay().getId());
		LogU.add(emp.getMunicipality()==null? 0 : emp.getMunicipality().getId());
		LogU.add(emp.getProvince()==null? 0 : emp.getProvince().getId());
		LogU.add(emp.getSalary());
		LogU.add(emp.getFullName());
		LogU.add(emp.getOvertime());
		LogU.add(emp.getId());
		
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employee : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return emp;
	}
	
	public void updateData(){
		String sql = "UPDATE employee SET "
				+ "datereg=?,"
				+ "dateend=?,"
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "isresigned=?,"
				+ "jobtitleid=?,"
				+ "contactno=?,"
				+ "purok=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=?,"
				+ "salary=?,"
				+ "fullname=?,"
				+ "overtimerate=? " 
				+ " WHERE empid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employee");
		
		ps.setString(cnt++, getDateRegistered());
		ps.setString(cnt++, getDateResigned());
		ps.setString(cnt++, getFirstName());
		ps.setString(cnt++, getMiddleName());
		ps.setString(cnt++, getLastName());
		ps.setInt(cnt++, getAge());
		ps.setInt(cnt++, getGender());
		ps.setInt(cnt++, getIsResigned());
		ps.setInt(cnt++, getJob()==null? 0 : getJob().getJobid());
		ps.setString(cnt++, getContactNo());
		ps.setString(cnt++, getPurok());
		ps.setInt(cnt++, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(cnt++, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(cnt++, getProvince()==null? 0 : getProvince().getId());
		ps.setDouble(cnt++, getSalary());
		ps.setString(cnt++, getFullName());
		ps.setDouble(cnt++, getOvertime());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateRegistered());
		LogU.add(getDateResigned());
		LogU.add(getFirstName());
		LogU.add(getMiddleName());
		LogU.add(getLastName());
		LogU.add(getAge());
		LogU.add(getGender());
		LogU.add(getIsResigned());
		LogU.add(getJob()==null? 0 : getJob().getJobid());
		LogU.add(getContactNo());
		LogU.add(getPurok());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		LogU.add(getSalary());
		LogU.add(getFullName());
		LogU.add(getOvertime());
		LogU.add(getId());
		
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employee : " + s.getMessage());
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
		sql="SELECT empid FROM employee  ORDER BY empid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("empid");
		}
		
		System.out.println("getLatestId() " + id);
		
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
		System.out.println("getting info of id " + id);
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
		ps = conn.prepareStatement("SELECT empid FROM employee WHERE empid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		System.out.println("isIdNoExist ? " + result);
		
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
		String sql = "UPDATE employee set isactiveemp=0 WHERE empid=?";
		
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
	public String getDateRegistered() {
		return dateRegistered;
	}
	public void setDateRegistered(String dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	public String getDateResigned() {
		return dateResigned;
	}
	public void setDateResigned(String dateResigned) {
		this.dateResigned = dateResigned;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	
	public int getIsResigned() {
		return isResigned;
	}
	public void setIsResigned(int isResigned) {
		this.isResigned = isResigned;
	}
	public int getIsActiveEmployee() {
		return isActiveEmployee;
	}
	public void setIsActiveEmployee(int isActiveEmployee) {
		this.isActiveEmployee = isActiveEmployee;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getPurok() {
		return purok;
	}

	public void setPurok(String purok) {
		this.purok = purok;
	}

	public Barangay getBarangay() {
		return barangay;
	}

	public void setBarangay(Barangay barangay) {
		this.barangay = barangay;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(String dailyRate) {
		this.dailyRate = dailyRate;
	}

	public String getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(String hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public String getPayableServices() {
		return payableServices;
	}

	public void setPayableServices(String payableServices) {
		this.payableServices = payableServices;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public double getOvertime() {
		return overtime;
	}

	public void setOvertime(double overtime) {
		this.overtime = overtime;
	}

	public String getOverTimeRate() {
		return overTimeRate;
	}

	public void setOverTimeRate(String overTimeRate) {
		this.overTimeRate = overTimeRate;
	}

	public String getTimeRecordedInfo() {
		return timeRecordedInfo;
	}

	public void setTimeRecordedInfo(String timeRecordedInfo) {
		this.timeRecordedInfo = timeRecordedInfo;
	}

	public static void main(String[] args) {
		
		//Employee emp = new Employee();
		
		/*for(Employee e : retrieve(" AND emp.isactiveemp=1 ", new String[0])){
			System.out.println("UPDATE employee SET fullname='"+ e.getFirstName() + " " + e.getLastName()+"' WHERE empid="+e.getId()+";");
		}
		emp.setId(1);
		emp.setDateRegistered(DateUtils.getCurrentDateYYYYMMDD());
		emp.setDateResigned(DateUtils.getCurrentDateYYYYMMDD());
		emp.setFirstName("First");
		emp.setMiddleName("middle");
		emp.setLastName("last");
		emp.setAge(1);
		emp.setGender(1);
		emp.setSalary(100);
		emp.setIsResigned(1);
		emp.setIsActiveEmployee(1);
		
		Job pos = new Job();
		pos.setJobid(1);
		emp.setJob(pos);
		
		
		
		emp.save();*/
		
		
	}
	
	
}
