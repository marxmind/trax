package com.italia.marxmind.trax.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.trax.database.ConnectDB;
import com.italia.marxmind.trax.enm.CardType;
import com.italia.marxmind.trax.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 12/29/2017
 * @version 1.0
 */
public class EmployeeCards {

	private long id;
	private String dateRegister;
	private String accountNumber;
	private int cardType;
	private int isActive;
	
	private Employee employee;
	
	private String cardTypeName;
	private int index;
	
	public static List<EmployeeCards> retrieve(String sqlAdd, String[] params){
		List<EmployeeCards> cards = new ArrayList<EmployeeCards>();
		
		String tableCard = "card";
		String tableEmp = "emp";
		
		String sql = "SELECT * FROM  employeecards "+ tableCard +", employee " + tableEmp +  " WHERE " +
				tableCard +".empid=" + tableEmp + ".empid ";
		
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			EmployeeCards card = new EmployeeCards();
			try{card.setId(rs.getLong("cardid"));}catch(NullPointerException e){}
			try{card.setDateRegister(rs.getString("datereg"));}catch(NullPointerException e){}
			try{card.setAccountNumber(rs.getString("accountno"));}catch(NullPointerException e){}
			try{card.setCardType(rs.getInt("cardtype"));}catch(NullPointerException e){}
			try{card.setIsActive(rs.getInt("isactivecard"));}catch(NullPointerException e){}
			try{card.setCardTypeName(CardType.typeName(card.getCardType()));}catch(NullPointerException e){}
			
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
			
			card.setEmployee(emp);
			
			cards.add(card);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cards;
	}
	
	public static EmployeeCards retrieve(long id){
		EmployeeCards card = new EmployeeCards();
		
		String tableCard = "card";
		String tableEmp = "emp";
		
		String sql = "SELECT * FROM  employeecards "+ tableCard +", employee " + tableEmp +  " WHERE " +
				tableCard +".empid=" + tableEmp + ".empid AND " + tableCard + ".isactivecard=1 AND " + tableCard + ".cardid=" + id;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{card.setId(rs.getLong("cardid"));}catch(NullPointerException e){}
			try{card.setDateRegister(rs.getString("datereg"));}catch(NullPointerException e){}
			try{card.setAccountNumber(rs.getString("accountno"));}catch(NullPointerException e){}
			try{card.setCardType(rs.getInt("cardtype"));}catch(NullPointerException e){}
			try{card.setIsActive(rs.getInt("isactivecard"));}catch(NullPointerException e){}
			try{card.setCardTypeName(CardType.typeName(card.getCardType()));}catch(NullPointerException e){}
			
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
			card.setEmployee(emp);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return card;
	}
	
	public static EmployeeCards save(EmployeeCards emp){
		if(emp!=null){
			
			long id = EmployeeCards.getInfo(emp.getId() ==0? EmployeeCards.getLatestId()+1 : emp.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				emp = EmployeeCards.insertData(emp, "1");
			}else if(id==2){
				LogU.add("update Data ");
				emp = EmployeeCards.updateData(emp);
			}else if(id==3){
				LogU.add("added new Data ");
				emp = EmployeeCards.insertData(emp, "3");
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
	
	public static EmployeeCards insertData(EmployeeCards emp, String type){
		String sql = "INSERT INTO employeecards ("
				+ "cardid,"
				+ "datereg,"
				+ "accountno,"
				+ "cardtype,"
				+ "isactivecard,"
				+ "empid)" 
				+ " values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employeecards");
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
		ps.setString(cnt++, emp.getDateRegister());
		ps.setString(cnt++, emp.getAccountNumber());
		ps.setInt(cnt++, emp.getCardType());
		ps.setInt(cnt++, emp.getIsActive());
		ps.setLong(cnt++, emp.getEmployee()==null? 0 : emp.getEmployee().getId()); 
		
		LogU.add(emp.getDateRegister());
		LogU.add(emp.getAccountNumber());
		LogU.add(emp.getCardType());
		LogU.add(emp.getIsActive());
		LogU.add(emp.getEmployee()==null? 0 : emp.getEmployee().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeecards : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return emp;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO employeecards ("
				+ "cardid,"
				+ "datereg,"
				+ "accountno,"
				+ "cardtype,"
				+ "isactivecard,"
				+ "empid)" 
				+ " values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table employeecards");
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
		ps.setString(cnt++, getDateRegister());
		ps.setString(cnt++, getAccountNumber());
		ps.setInt(cnt++, getCardType());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId()); 
		
		LogU.add(getDateRegister());
		LogU.add(getAccountNumber());
		LogU.add(getCardType());
		LogU.add(getIsActive());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeecards : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static EmployeeCards updateData(EmployeeCards emp){
		String sql = "UPDATE employeecards SET "
				+ "datereg=?,"
				+ "accountno=?,"
				+ "cardtype=?,"
				+ "empid=?" 
				+ " WHERE cardid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employeecards");
		
		ps.setString(cnt++, emp.getDateRegister());
		ps.setString(cnt++, emp.getAccountNumber());
		ps.setInt(cnt++, emp.getCardType());
		ps.setLong(cnt++, emp.getEmployee()==null? 0 : emp.getEmployee().getId()); 
		ps.setLong(cnt++, emp.getId());
		
		LogU.add(emp.getDateRegister());
		LogU.add(emp.getAccountNumber());
		LogU.add(emp.getCardType());
		LogU.add(emp.getEmployee()==null? 0 : emp.getEmployee().getId());
		LogU.add(emp.getId());
		
		LogU.add("updating for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeecards : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return emp;
	}
	
	public void updateData(){
		String sql = "UPDATE employeecards SET "
				+ "datereg=?,"
				+ "accountno=?,"
				+ "cardtype=?,"
				+ "empid=?" 
				+ " WHERE cardid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table employeecards");
		
		ps.setString(cnt++, getDateRegister());
		ps.setString(cnt++, getAccountNumber());
		ps.setInt(cnt++, getCardType());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId()); 
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateRegister());
		LogU.add(getAccountNumber());
		LogU.add(getCardType());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getId());
		
		LogU.add("updating for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeecards : " + s.getMessage());
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
		sql="SELECT cardid FROM employeecards  ORDER BY cardid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("cardid");
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
		ps = conn.prepareStatement("SELECT cardid FROM employeecards WHERE cardid=?");
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
		String sql = "UPDATE employeecards set isactivecard=0 WHERE cardid=?";
		
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

	public String getDateRegister() {
		return dateRegister;
	}

	public void setDateRegister(String dateRegister) {
		this.dateRegister = dateRegister;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getCardTypeName() {
		return cardTypeName;
	}

	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}



	public Employee getEmployee() {
		return employee;
	}



	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
