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

public class LocationTransactions {

	private long id;
	private String dateTrans;
	private int isActive;
	private Timestamp timestamp;
	
	private Location location;
	private ActivityTransactions activityTransactions;
	
	public static List<LocationTransactions> retrieve(String sqlAdd, String[] params){
		List<LocationTransactions> locs = Collections.synchronizedList(new ArrayList<LocationTransactions>());
		
		String tableLocTrans = "lct";
		String tableLoc = "lc";
		String tableAcTrans = "act";
		
		String sql = "SELECT * FROM locationtrans " + tableLocTrans + ", locations " + tableLoc + ", activitytrans " + tableAcTrans +" WHERE " +
				tableLocTrans +".locid=" + tableLoc + ".locid AND " +
				tableLocTrans +".actransid=" + tableAcTrans + ".actransid ";
		
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
		System.out.println("SQL Location Trans : " + ps.toString());
		
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			LocationTransactions ltrn = new LocationTransactions();
			ltrn.setId(rs.getLong("loctransid"));
			ltrn.setDateTrans(rs.getString("locDateTrans"));
			ltrn.setIsActive(rs.getInt("isactiveloctrans"));
			ltrn.setTimestamp(rs.getTimestamp("timestamploctrans"));
			
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
			ltrn.setActivityTransactions(trn);
			
			Location loc = new Location();
			try{loc.setId(rs.getInt("locid"));}catch(NullPointerException e){}
			try{loc.setName(rs.getString("locname"));}catch(NullPointerException e){}
			try{loc.setIsActive(rs.getInt("isactiveloc"));}catch(NullPointerException e){}
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			loc.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			loc.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			loc.setProvince(prov);
			ltrn.setLocation(loc);
			
			locs.add(ltrn);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return locs;
	}
	
	public static LocationTransactions retrieve(long locationId){
		LocationTransactions ltrn = new LocationTransactions();
		
		String tableLocTrans = "lct";
		String tableLoc = "lc";
		String tableAcTrans = "act";
		
		String sql = "SELECT * FROM locationtrans " + tableLocTrans + ", locations " + tableLoc + ", activitytrans " + tableAcTrans +" WHERE " +
				tableLocTrans +".locid=" + tableLoc + ".locid AND " +
				tableLocTrans +".actransid=" + tableAcTrans + ".actransid AND " + tableLocTrans +".loctransid="+locationId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			ltrn.setId(rs.getLong("loctransid"));
			ltrn.setDateTrans(rs.getString("locDateTrans"));
			ltrn.setIsActive(rs.getInt("isactiveloctrans"));
			ltrn.setTimestamp(rs.getTimestamp("timestamploctrans"));
			
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
			ltrn.setActivityTransactions(trn);
			
			Location loc = new Location();
			try{loc.setId(rs.getInt("locid"));}catch(NullPointerException e){}
			try{loc.setName(rs.getString("locname"));}catch(NullPointerException e){}
			try{loc.setIsActive(rs.getInt("isactiveloc"));}catch(NullPointerException e){}
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			loc.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			loc.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			loc.setProvince(prov);
			ltrn.setLocation(loc);
			
		
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return ltrn;
	}
	
	public static LocationTransactions save(LocationTransactions loc){
		if(loc!=null){
			
			long id = LocationTransactions.getInfo(loc.getId() ==0? LocationTransactions.getLatestId()+1 : loc.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				loc = LocationTransactions.insertData(loc, "1");
			}else if(id==2){
				LogU.add("update Data ");
				loc = LocationTransactions.updateData(loc);
			}else if(id==3){
				LogU.add("added new Data ");
				loc = LocationTransactions.insertData(loc, "3");
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
	
	public static LocationTransactions insertData(LocationTransactions loc, String type){
		String sql = "INSERT INTO locationtrans ("
				+ "loctransid,"
				+ "locDateTrans,"
				+ "isactiveloctrans,"
				+ "locid,"
				+ "actransid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table locationtrans");
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
		ps.setString(cnt++, loc.getDateTrans());
		ps.setInt(cnt++, loc.getIsActive());
		ps.setInt(cnt++, loc.getLocation()==null? 0 : loc.getLocation().getId());
		ps.setLong(cnt++, loc.getActivityTransactions()==null? 0 : loc.getActivityTransactions().getId());
		
		LogU.add(loc.getDateTrans());
		LogU.add(loc.getIsActive());
		LogU.add(loc.getLocation()==null? 0 : loc.getLocation().getId());
		LogU.add(loc.getActivityTransactions()==null? 0 : loc.getActivityTransactions().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to locationtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return loc;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO locationtrans ("
				+ "loctransid,"
				+ "locDateTrans,"
				+ "isactiveloctrans,"
				+ "locid,"
				+ "actransid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table locationtrans");
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
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getLocation()==null? 0 : getLocation().getId());
		ps.setLong(cnt++, getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getLocation()==null? 0 : getLocation().getId());
		LogU.add(getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to locationtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static LocationTransactions updateData(LocationTransactions loc){
		String sql = "UPDATE locationtrans SET "
				+ "locDateTrans=?,"
				+ "locid=?,"
				+ "actransid=?" 
				+ " WHERE loctransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table locationtrans");
		
		ps.setString(cnt++, loc.getDateTrans());
		ps.setInt(cnt++, loc.getLocation()==null? 0 : loc.getLocation().getId());
		ps.setLong(cnt++, loc.getActivityTransactions()==null? 0 : loc.getActivityTransactions().getId());
		ps.setLong(cnt++, loc.getId());
		
		LogU.add(loc.getDateTrans());
		LogU.add(loc.getLocation()==null? 0 : loc.getLocation().getId());
		LogU.add(loc.getActivityTransactions()==null? 0 : loc.getActivityTransactions().getId());
		LogU.add(loc.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to locationtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return loc;
	}
	
	public void updateData(){
		String sql = "UPDATE locationtrans SET "
				+ "locDateTrans=?,"
				+ "locid=?,"
				+ "actransid=?" 
				+ " WHERE loctransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table locationtrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getLocation()==null? 0 : getLocation().getId());
		ps.setLong(cnt++, getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getLocation()==null? 0 : getLocation().getId());
		LogU.add(getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to locationtrans : " + s.getMessage());
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
		sql="SELECT loctransid FROM locationtrans  ORDER BY loctransid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("loctransid");
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
		ps = conn.prepareStatement("SELECT loctransid FROM locationtrans WHERE loctransid=?");
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
		String sql = "UPDATE locationtrans set isactiveloctrans=0 WHERE loctransid=?";
		
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public ActivityTransactions getActivityTransactions() {
		return activityTransactions;
	}
	public void setActivityTransactions(ActivityTransactions activityTransactions) {
		this.activityTransactions = activityTransactions;
	}
}
