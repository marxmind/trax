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
 * @since 09/11/2017
 * @version 1.0
 *
 */

public class QuantityRunning {

	private long id;
	private String dateTrans;
	private int transactionType;
	private double priceCost;
	private double quantity;
	private Timestamp timestamp;
	private MaterialProperties materialProperties;
	private Materials materials;
	private UOM uom;
	private UserDtls userDtls;
	
	public static List<QuantityRunning> retrieve(String sqlAdd, String[] params){
		List<QuantityRunning> qtys = Collections.synchronizedList(new ArrayList<QuantityRunning>());
		
		String tableRun = "qty";
		String tableProp = "prop";
		String tableMat = "mat";
		String tableUOM = "uom";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM qtyrunning "+ tableRun + ", materialprops " + tableProp + ", materials " + tableMat + ", UOM " + tableUOM + ", userdtls " + tableUser +
				" WHERE " + tableRun + ".propid=" + tableProp + ".propid AND " +
				tableRun + ".matid=" + tableMat + ".matid AND "+
				tableRun + ".uomid="+tableUOM + ".uomid AND "+
				tableRun + ".userdtlsid="+tableUser + ".userdtlsid ";
		
		sql += sqlAdd;
		
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
			
			QuantityRunning qty = new QuantityRunning();
			try{qty.setId(rs.getLong("runid"));}catch(NullPointerException e){}
			try{qty.setDateTrans(rs.getString("rundate"));}catch(NullPointerException e){}
			try{qty.setTransactionType(rs.getInt("transtype"));}catch(NullPointerException e){}
			try{qty.setPriceCost(rs.getDouble("matcost"));}catch(NullPointerException e){}
			try{qty.setQuantity(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{qty.setTimestamp(rs.getTimestamp("timestamprun"));}catch(NullPointerException e){}
			
			MaterialProperties prop = new MaterialProperties();
			try{prop.setId(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setDateTrans(rs.getString("propdate"));}catch(NullPointerException e){}
			try{prop.setPrice(rs.getDouble("matprice"));}catch(NullPointerException e){}
			try{prop.setCurrentQty(rs.getDouble("currentqty"));}catch(NullPointerException e){}
			try{prop.setPreviousQty(rs.getDouble("prevqty"));}catch(NullPointerException e){}
			try{prop.setIsUsed(rs.getInt("isproductused"));}catch(NullPointerException e){}
			try{prop.setIsActive(rs.getInt("isactiveprop"));}catch(NullPointerException e){}
			qty.setMaterialProperties(prop);
			
			Materials mat = new Materials();
			try{mat.setId(rs.getInt("matid"));}catch(NullPointerException e){}
			try{mat.setName(rs.getString("matname"));}catch(NullPointerException e){}
			try{mat.setIsActive(rs.getInt("isactivemat"));}catch(NullPointerException e){}
			qty.setMaterials(mat);
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			qty.setUom(uom);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			qty.setUserDtls(user);
			
			qtys.add(qty);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		
		return qtys;
	}
	
	public static QuantityRunning retrieve(long qtyId){
		QuantityRunning qty = new QuantityRunning();
		
		String tableRun = "qty";
		String tableProp = "prop";
		String tableMat = "mat";
		String tableUOM = "uom";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM qtyrunning "+ tableRun + ", materialprops " + tableProp + ", materials " + tableMat + ", UOM " + tableUOM + ", userdtls " + tableUser +
				" WHERE " + tableRun + ".propid=" + tableProp + ".propid AND " +
				tableRun + ".matid=" + tableMat + ".matid AND "+
				tableRun + ".uomid="+tableUOM + ".uomid AND "+
				tableRun + ".userdtlsid="+tableUser + ".userdtlsid AND " + tableRun + ".runid="+qtyId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{qty.setId(rs.getLong("runid"));}catch(NullPointerException e){}
			try{qty.setDateTrans(rs.getString("rundate"));}catch(NullPointerException e){}
			try{qty.setTransactionType(rs.getInt("transtype"));}catch(NullPointerException e){}
			try{qty.setPriceCost(rs.getDouble("matcost"));}catch(NullPointerException e){}
			try{qty.setQuantity(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{qty.setTimestamp(rs.getTimestamp("timestamprun"));}catch(NullPointerException e){}
			
			MaterialProperties prop = new MaterialProperties();
			try{prop.setId(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setDateTrans(rs.getString("propdate"));}catch(NullPointerException e){}
			try{prop.setPrice(rs.getDouble("matprice"));}catch(NullPointerException e){}
			try{prop.setCurrentQty(rs.getDouble("currentqty"));}catch(NullPointerException e){}
			try{prop.setPreviousQty(rs.getDouble("prevqty"));}catch(NullPointerException e){}
			try{prop.setIsUsed(rs.getInt("isproductused"));}catch(NullPointerException e){}
			try{prop.setIsActive(rs.getInt("isactiveprop"));}catch(NullPointerException e){}
			qty.setMaterialProperties(prop);
			
			Materials mat = new Materials();
			try{mat.setId(rs.getInt("matid"));}catch(NullPointerException e){}
			try{mat.setName(rs.getString("matname"));}catch(NullPointerException e){}
			try{mat.setIsActive(rs.getInt("isactivemat"));}catch(NullPointerException e){}
			qty.setMaterials(mat);
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			qty.setUom(uom);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			qty.setUserDtls(user);
			
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		
		return qty;
	}
	
	public static QuantityRunning save(QuantityRunning mat){
		if(mat!=null){
			
			long id = QuantityRunning.getInfo(mat.getId() ==0? QuantityRunning.getLatestId()+1 : mat.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mat = QuantityRunning.insertData(mat, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mat = QuantityRunning.updateData(mat);
			}else if(id==3){
				LogU.add("added new Data ");
				mat = QuantityRunning.insertData(mat, "3");
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
	
	public static QuantityRunning insertData(QuantityRunning mat, String type){
		String sql = "INSERT INTO qtyrunning ("
				+ "runid,"
				+ "rundate,"
				+ "transtype,"
				+ "propid,"
				+ "matid,"
				+ "uomid,"
				+ "matcost,"
				+ "matqty,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table qtyrunning");
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
		ps.setInt(cnt++, mat.getTransactionType());
		ps.setLong(cnt++, mat.getMaterialProperties().getId());
		ps.setInt(cnt++, mat.getMaterials().getId());
		ps.setInt(cnt++, mat.getUom().getId());
		ps.setDouble(cnt++, mat.getPriceCost());
		ps.setDouble(cnt++, mat.getQuantity());
		ps.setLong(cnt++, mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		
		LogU.add(mat.getDateTrans());
		LogU.add(mat.getTransactionType());
		LogU.add(mat.getMaterialProperties().getId());
		LogU.add(mat.getMaterials().getId());
		LogU.add(mat.getUom().getId());
		LogU.add(mat.getPriceCost());
		LogU.add(mat.getQuantity());
		LogU.add(mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to qtyrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mat;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO qtyrunning ("
				+ "runid,"
				+ "rundate,"
				+ "transtype,"
				+ "propid,"
				+ "matid,"
				+ "uomid,"
				+ "matcost,"
				+ "matqty,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table qtyrunning");
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
		ps.setInt(cnt++, getTransactionType());
		ps.setLong(cnt++, getMaterialProperties().getId());
		ps.setInt(cnt++, getMaterials().getId());
		ps.setInt(cnt++, getUom().getId());
		ps.setDouble(cnt++, getPriceCost());
		ps.setDouble(cnt++, getQuantity());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getTransactionType());
		LogU.add(getMaterialProperties().getId());
		LogU.add(getMaterials().getId());
		LogU.add(getUom().getId());
		LogU.add(getPriceCost());
		LogU.add(getQuantity());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to qtyrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static QuantityRunning updateData(QuantityRunning mat){
		String sql = "UPDATE qtyrunning SET "
				+ "rundate=?,"
				+ "transtype=?,"
				+ "propid=?,"
				+ "matid=?,"
				+ "uomid=?,"
				+ "matcost=?,"
				+ "matqty=?,"
				+ "userdtlsid=?" 
				+ " WHERE runid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table qtyrunning");
		
		ps.setString(cnt++, mat.getDateTrans());
		ps.setInt(cnt++, mat.getTransactionType());
		ps.setLong(cnt++, mat.getMaterialProperties().getId());
		ps.setInt(cnt++, mat.getMaterials().getId());
		ps.setInt(cnt++, mat.getUom().getId());
		ps.setDouble(cnt++, mat.getPriceCost());
		ps.setDouble(cnt++, mat.getQuantity());
		ps.setLong(cnt++, mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, mat.getId());
		
		LogU.add(mat.getDateTrans());
		LogU.add(mat.getTransactionType());
		LogU.add(mat.getMaterialProperties().getId());
		LogU.add(mat.getMaterials().getId());
		LogU.add(mat.getUom().getId());
		LogU.add(mat.getPriceCost());
		LogU.add(mat.getQuantity());
		LogU.add(mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		LogU.add(mat.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to qtyrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mat;
	}
	
	public void updateData(){
		String sql = "UPDATE qtyrunning SET "
				+ "rundate=?,"
				+ "transtype=?,"
				+ "propid=?,"
				+ "matid=?,"
				+ "uomid=?,"
				+ "matcost=?,"
				+ "matqty=?,"
				+ "userdtlsid=?" 
				+ " WHERE runid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table qtyrunning");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getTransactionType());
		ps.setLong(cnt++, getMaterialProperties().getId());
		ps.setInt(cnt++, getMaterials().getId());
		ps.setInt(cnt++, getUom().getId());
		ps.setDouble(cnt++, getPriceCost());
		ps.setDouble(cnt++, getQuantity());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getTransactionType());
		LogU.add(getMaterialProperties().getId());
		LogU.add(getMaterials().getId());
		LogU.add(getUom().getId());
		LogU.add(getPriceCost());
		LogU.add(getQuantity());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to qtyrunning : " + s.getMessage());
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
		sql="SELECT runid FROM qtyrunning ORDER BY runid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("runid");
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
		ps = conn.prepareStatement("SELECT runid FROM qtyrunning WHERE runid=?");
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
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	public double getPriceCost() {
		return priceCost;
	}
	public void setPriceCost(double priceCost) {
		this.priceCost = priceCost;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public MaterialProperties getMaterialProperties() {
		return materialProperties;
	}
	public void setMaterialProperties(MaterialProperties materialProperties) {
		this.materialProperties = materialProperties;
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
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
}
