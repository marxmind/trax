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

public class MaterialTransactions {

	private long id;
	private String dateTrans;
	private double qty;
	private int isActive;
	private Timestamp timestamp;
	
	private Materials materials;
	private UOM uom;
	private ActivityTransactions activityTransactions;
	
	public static List<MaterialTransactions> retrieve(String sqlAdd, String[] params){
		List<MaterialTransactions> mats = Collections.synchronizedList(new ArrayList<MaterialTransactions>());
		
		String tableMats = "mt";
		String tableMat = "mat";
		String tableUOM = "uom";
		String tableAcTrans = "act";
		
		String sql = "SELECT * FROM materialtrans " + tableMats + ", materials " + tableMat + ", uom " + tableUOM + ", activitytrans " + tableAcTrans +" WHERE " +
				tableMats +".matid=" + tableMat + ".matid AND " +
				tableMats +".uomid=" + tableUOM + ".uomid AND " +
				tableMats +".actransid=" + tableAcTrans + ".actransid ";
		
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
			
			MaterialTransactions mt = new MaterialTransactions();
			try{mt.setId(rs.getLong("matransid"));}catch(NullPointerException e){}
			try{mt.setDateTrans(rs.getString("matDateTrans"));}catch(NullPointerException e){}
			try{mt.setQty(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{mt.setIsActive(rs.getInt("isactivematrans"));}catch(NullPointerException e){}
			
			Materials mat = new Materials();
			try{mat.setId(rs.getInt("matid"));}catch(NullPointerException e){}
			try{mat.setName(rs.getString("matname"));}catch(NullPointerException e){}
			try{mat.setIsActive(rs.getInt("isactivemat"));}catch(NullPointerException e){}
			mt.setMaterials(mat);
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			mt.setUom(uom);
			
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
			
			mt.setActivityTransactions(trn);
			
			
			mats.add(mt);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return mats;
	}
	
	public static MaterialTransactions retrieve(long materialId){
		MaterialTransactions mt = new MaterialTransactions();
		
		String tableMats = "mt";
		String tableMat = "mat";
		String tableUOM = "uom";
		String tableAcTrans = "act";
		
		String sql = "SELECT * FROM materialtrans " + tableMats + ", materials " + tableMat + ", uom " + tableUOM + ", activitytrans " + tableAcTrans +" WHERE " +
				tableMats +".matid=" + tableMat + ".matid AND " +
				tableMats +".uomid=" + tableUOM + ".uomid AND " +
				tableMats +".actransid=" + tableAcTrans + ".actransid AND " + tableMats + ".matransid="+materialId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{mt.setId(rs.getLong("matransid"));}catch(NullPointerException e){}
			try{mt.setDateTrans(rs.getString("matDateTrans"));}catch(NullPointerException e){}
			try{mt.setQty(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{mt.setIsActive(rs.getInt("isactivematrans"));}catch(NullPointerException e){}
			
			Materials mat = new Materials();
			try{mat.setId(rs.getInt("matid"));}catch(NullPointerException e){}
			try{mat.setName(rs.getString("matname"));}catch(NullPointerException e){}
			try{mat.setIsActive(rs.getInt("isactivemat"));}catch(NullPointerException e){}
			mt.setMaterials(mat);
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			mt.setUom(uom);
			
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
			
			mt.setActivityTransactions(trn);
			
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return mt;
	}
	
	public static MaterialTransactions save(MaterialTransactions mat){
		if(mat!=null){
			
			long id = MaterialTransactions.getInfo(mat.getId() ==0? MaterialTransactions.getLatestId()+1 : mat.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mat = MaterialTransactions.insertData(mat, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mat = MaterialTransactions.updateData(mat);
			}else if(id==3){
				LogU.add("added new Data ");
				mat = MaterialTransactions.insertData(mat, "3");
			}
			
		}
		return mat;
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
	
	public static MaterialTransactions insertData(MaterialTransactions mat, String type){
		String sql = "INSERT INTO materialtrans ("
				+ "matransid,"
				+ "matDateTrans,"
				+ "matqty,"
				+ "isactivematrans,"
				+ "matid,"
				+ "uomid,"
				+ "actransid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table materialtrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			mat.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			mat.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, mat.getDateTrans());
		ps.setDouble(cnt++, mat.getQty());
		ps.setInt(cnt++, mat.getIsActive());
		ps.setInt(cnt++, mat.getMaterials()==null? 0 : mat.getMaterials().getId());
		ps.setInt(cnt++, mat.getUom()==null? 0 : mat.getUom().getId());
		ps.setLong(cnt++, mat.getActivityTransactions()==null? 0 : mat.getActivityTransactions().getId());
		
		LogU.add(mat.getDateTrans());
		LogU.add(mat.getQty());
		LogU.add(mat.getIsActive());
		LogU.add(mat.getMaterials()==null? 0 : mat.getMaterials().getId());
		LogU.add(mat.getUom()==null? 0 : mat.getUom().getId());
		LogU.add(mat.getActivityTransactions()==null? 0 : mat.getActivityTransactions().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to materialtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mat;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO materialtrans ("
				+ "matransid,"
				+ "matDateTrans,"
				+ "matqty,"
				+ "isactivematrans,"
				+ "matid,"
				+ "uomid,"
				+ "actransid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table materialtrans");
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
		ps.setDouble(cnt++, getQty());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getMaterials()==null? 0 : getMaterials().getId());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getId());
		ps.setLong(cnt++, getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getQty());
		LogU.add(getIsActive());
		LogU.add(getMaterials()==null? 0 : getMaterials().getId());
		LogU.add(getUom()==null? 0 : getUom().getId());
		LogU.add(getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to materialtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MaterialTransactions updateData(MaterialTransactions mat){
		String sql = "UPDATE materialtrans SET "
				+ "matDateTrans=?,"
				+ "matqty=?,"
				+ "matid=?,"
				+ "uomid=?,"
				+ "actransid=?" 
				+ " WHERE matransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table materialtrans");
		
		ps.setString(cnt++, mat.getDateTrans());
		ps.setDouble(cnt++, mat.getQty());
		ps.setInt(cnt++, mat.getMaterials()==null? 0 : mat.getMaterials().getId());
		ps.setInt(cnt++, mat.getUom()==null? 0 : mat.getUom().getId());
		ps.setLong(cnt++, mat.getActivityTransactions()==null? 0 : mat.getActivityTransactions().getId());
		ps.setLong(cnt++, mat.getId());
		
		LogU.add(mat.getDateTrans());
		LogU.add(mat.getQty());
		LogU.add(mat.getMaterials()==null? 0 : mat.getMaterials().getId());
		LogU.add(mat.getUom()==null? 0 : mat.getUom().getId());
		LogU.add(mat.getActivityTransactions()==null? 0 : mat.getActivityTransactions().getId());
		LogU.add(mat.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to materialtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mat;
	}
	
	public void updateData(){
		String sql = "UPDATE materialtrans SET "
				+ "matDateTrans=?,"
				+ "matqty=?,"
				+ "matid=?,"
				+ "uomid=?,"
				+ "actransid=?" 
				+ " WHERE matransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table materialtrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setDouble(cnt++, getQty());
		ps.setInt(cnt++, getMaterials()==null? 0 : getMaterials().getId());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getId());
		ps.setLong(cnt++, getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getQty());
		LogU.add(getMaterials()==null? 0 : getMaterials().getId());
		LogU.add(getUom()==null? 0 : getUom().getId());
		LogU.add(getActivityTransactions()==null? 0 : getActivityTransactions().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to materialtrans : " + s.getMessage());
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
		sql="SELECT matransid FROM materialtrans  ORDER BY matransid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("matransid");
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
		ps = conn.prepareStatement("SELECT matransid FROM materialtrans WHERE matransid=?");
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
		String sql = "UPDATE materialtrans set isactivematrans=0 WHERE matransid=?";
		
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
	public double getQty() {
		return qty;
	}
	public void setQty(double qty) {
		this.qty = qty;
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
	public Materials getMaterials() {
		return materials;
	}
	public void setMaterials(Materials materials) {
		this.materials = materials;
	}
	public UOM getUom() {
		return uom;
	}
	public void setUom(UOM uom) {
		this.uom = uom;
	}
	public ActivityTransactions getActivityTransactions() {
		return activityTransactions;
	}
	public void setActivityTransactions(ActivityTransactions activityTransactions) {
		this.activityTransactions = activityTransactions;
	}
	
}
