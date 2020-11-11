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

public class Location {

	private int id;
	private String name;
	private int isActive;
	
	private Barangay barangay;
	private Municipality municipality;
	private Province province;
	
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
				str.add(rs.getString("locname"));
			}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
			
		return str;
	}
	
	public static List<Location> retrieve(String sqlAdd, String[] params){
		List<Location> locs = new ArrayList<Location>();
		
		String tableLoc = "lc";
		String tableBg = "bg";
		String tableMun = "mu";
		String tablePrv = "prv";
		String sql = "SELECT * FROM locations " + tableLoc + ", barangay " + tableBg + ", municipality " + tableMun + ", province " + tablePrv + " WHERE " +
				tableLoc + ".bgid=" + tableBg + ".bgid AND " +
				tableLoc + ".munid=" + tableMun + ".munid AND " +
				tableLoc + ".provid=" + tablePrv + ".provid ";
		
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
		
		System.out.println("LOCATION SQL: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Location loc = new Location();
			try{loc.setId(rs.getInt("locid"));}catch(NullPointerException e){}
			try{loc.setName(rs.getString("locname"));}catch(NullPointerException e){}
			try{loc.setIsActive(rs.getInt("isactiveloc"));}catch(NullPointerException e){}
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			loc.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			loc.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			loc.setProvince(prov);
			
			locs.add(loc);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return locs;
	}
	
	public static Location save(Location loc){
		if(loc!=null){
			int id = Location.getInfo(loc.getId() ==0? Location.getLatestId()+1 : loc.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				loc = Location.insertData(loc, "1");
			}else if(id==2){
				LogU.add("update Data ");
				loc = Location.updateData(loc);
			}else if(id==3){
				LogU.add("added new Data ");
				loc = Location.insertData(loc, "3");
			}
			
		}
		return loc;
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
	
	public static Location insertData(Location loc, String type){
		String sql = "INSERT INTO locations ("
				+ "locid,"
				+ "locname,"
				+ "isactiveloc,"
				+ "bgid,"
				+ "munid,"
				+ "provid)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table locations");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			loc.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			loc.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, loc.getName());
		ps.setInt(cnt++, loc.getIsActive());
		ps.setInt(cnt++, loc.getBarangay()==null? 0 : loc.getBarangay().getId());
		ps.setInt(cnt++, loc.getMunicipality()==null? 0 : loc.getMunicipality().getId());
		ps.setInt(cnt++, loc.getProvince()==null? 0 : loc.getProvince().getId());
		
		LogU.add(loc.getName());
		LogU.add(loc.getIsActive());
		LogU.add(loc.getBarangay()==null? 0 : loc.getBarangay().getId());
		LogU.add(loc.getMunicipality()==null? 0 : loc.getMunicipality().getId());
		LogU.add(loc.getProvince()==null? 0 : loc.getProvince().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to locations : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return loc;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO locations ("
				+ "locid,"
				+ "locname,"
				+ "isactiveloc,"
				+ "bgid,"
				+ "munid,"
				+ "provid)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table locations");
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
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(cnt++, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(cnt++, getProvince()==null? 0 : getProvince().getId());
		
		LogU.add(getName());
		LogU.add(getIsActive());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to locations : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Location updateData(Location loc){
		String sql = "UPDATE locations SET "
				+ "locname=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=? " 
				+ " WHERE locid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table locations");
		
		ps.setString(cnt++, loc.getName());
		ps.setInt(cnt++, loc.getBarangay()==null? 0 : loc.getBarangay().getId());
		ps.setInt(cnt++, loc.getMunicipality()==null? 0 : loc.getMunicipality().getId());
		ps.setInt(cnt++, loc.getProvince()==null? 0 : loc.getProvince().getId());
		ps.setInt(cnt++, loc.getId());
		
		LogU.add(loc.getName());
		LogU.add(loc.getIsActive());
		LogU.add(loc.getBarangay()==null? 0 : loc.getBarangay().getId());
		LogU.add(loc.getMunicipality()==null? 0 : loc.getMunicipality().getId());
		LogU.add(loc.getProvince()==null? 0 : loc.getProvince().getId());
		LogU.add(loc.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to locations : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return loc;
	}
	
	public void updateData(){
		String sql = "UPDATE locations SET "
				+ "locname=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=? " 
				+ " WHERE locid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table locations");
		
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(cnt++, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(cnt++, getProvince()==null? 0 : getProvince().getId());
		ps.setInt(cnt++, getId());
		
		LogU.add(getName());
		LogU.add(getIsActive());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to locations : " + s.getMessage());
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
		sql="SELECT locid FROM locations  ORDER BY locid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("locid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestId();	
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT locid FROM locations WHERE locid=?");
		ps.setInt(1, id);
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
		String sql = "UPDATE locations set isactiveloc=0 WHERE locid=?";
		
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
	
	
}


