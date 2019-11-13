package com.italia.marxmind.trax.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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


public class UOM {

	private int id;
	private String name;
	private String symbol;
	private int isActive;
	
	public static List<UOM> retrieve(String sql, String[] params){
		List<UOM> uoms = Collections.synchronizedList(new ArrayList<UOM>());
		
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
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			
			uoms.add(uom);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return uoms;
	}
	
	public static UOM save(UOM uom){
		if(uom!=null){
			
			long id = UOM.getInfo(uom.getId() ==0? UOM.getLatestId()+1 : uom.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				uom = UOM.insertData(uom, "1");
			}else if(id==2){
				LogU.add("update Data ");
				uom = UOM.updateData(uom);
			}else if(id==3){
				LogU.add("added new Data ");
				uom = UOM.insertData(uom, "3");
			}
			
		}
		return uom;
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
	
	public static UOM insertData(UOM uom, String type){
		String sql = "INSERT INTO uom ("
				+ "uomid,"
				+ "uomname,"
				+ "uomsymbol,"
				+ "isactiveuom)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table uom");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			uom.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			uom.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, uom.getName());
		ps.setString(cnt++, uom.getSymbol());
		ps.setInt(cnt++, uom.getIsActive());
		
		LogU.add(uom.getName());
		LogU.add(uom.getSymbol());
		LogU.add(uom.getIsActive());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to uom : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return uom;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO uom ("
				+ "uomid,"
				+ "uomname,"
				+ "uomsymbol,"
				+ "isactiveuom)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table uom");
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
		ps.setString(cnt++, getName());
		ps.setString(cnt++, getSymbol());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getName());
		LogU.add(getSymbol());
		LogU.add(getIsActive());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to uom : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static UOM updateData(UOM uom){
		String sql = "UPDATE uom SET "
				+ "uomname=?,"
				+ "uomsymbol=? "
				+ "WHERE uomid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table uom");
		
		ps.setString(cnt++, uom.getName());
		ps.setString(cnt++, uom.getSymbol());
		ps.setLong(cnt++, uom.getId());
		
		LogU.add(uom.getName());
		LogU.add(uom.getSymbol());
		LogU.add("id: " + uom.getId());
		
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to uom : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return uom;
	}
	
	public void updateData(){
		String sql = "UPDATE uom SET "
				+ "uomname=?,"
				+ "uomsymbol=? "
				+ "WHERE uomid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table uom");
		
		ps.setString(cnt++, getName());
		ps.setString(cnt++, getSymbol());
		ps.setLong(cnt++, getId());
		
		LogU.add(getName());
		LogU.add(getSymbol());
		LogU.add("id: " + getId());
		
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to uom : " + s.getMessage());
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
		sql="SELECT uomid FROM uom  ORDER BY uomid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("uomid");
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
		ps = conn.prepareStatement("SELECT uomid FROM uom WHERE uomid=?");
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
		String sql = "UPDATE uom set isactiveuom=0 WHERE uomid=?";
		
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
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
}

