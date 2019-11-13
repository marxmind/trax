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
public class MaterialRETURN {

	private long id;
	private String dateTrans;
	private double priceCost;
	private double quantity;
	private int isActive;
	private String remarks;
	private Timestamp timestamp;
	
	private MaterialProperties materialProperties;
	private Employee returnBy;
	private Materials materials;
	private UOM uom;
	private UserDtls userDtls;
	private Location location;
	private MaterialOUT materialOut;
	
	public static List<MaterialRETURN> retrieve(String sqlAdd, String[] params){
		List<MaterialRETURN> props = Collections.synchronizedList(new ArrayList<MaterialRETURN>());
		
		String tableRet = "ret";
		String tableProp = "prop";
		String tableMat = "mat";
		String tableUOM = "uom";
		String tableUser = "usr";
		String tableEmp = "emp";
		String tableArea = "area";
		String tableOut = "ot";
		
		String sql = "SELECT * FROM materialreturn "+ tableRet + ", employee "+ tableEmp + ", materialprops " + 
		tableProp + ", materials " + tableMat + ", UOM " + tableUOM + ", userdtls " + tableUser + ", locations " + tableArea + ", materialout " + tableOut + 
				" WHERE " + tableRet + ".propid=" + tableProp + ".propid AND " +
				tableRet + ".returnby=" + tableEmp + ".empid AND " +
				tableRet + ".matid=" + tableMat + ".matid AND "+
				tableRet + ".uomid="+tableUOM + ".uomid AND "+
				tableRet + ".userdtlsid="+tableUser + ".userdtlsid AND " +
				tableRet + ".locid=" + tableArea +".locid AND " +
				tableRet + ".outid=" + tableOut + ".outid ";
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
		
		System.out.println("SQL RETURN " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			MaterialRETURN out = new MaterialRETURN();
			try{out.setId(rs.getLong("retid"));}catch(NullPointerException e){}
			try{out.setDateTrans(rs.getString("retdate"));}catch(NullPointerException e){}
			try{out.setRemarks(rs.getString("retremarks"));}catch(NullPointerException e){}
			try{out.setPriceCost(rs.getDouble("matcost"));}catch(NullPointerException e){}
			try{out.setQuantity(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{out.setIsActive(rs.getInt("isactivereturn"));}catch(NullPointerException e){}
			try{out.setTimestamp(rs.getTimestamp("timestampret"));}catch(NullPointerException e){}
			
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
			out.setReturnBy(emp);
			
			MaterialProperties prop = new MaterialProperties();
			try{prop.setId(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setDateTrans(rs.getString("propdate"));}catch(NullPointerException e){}
			try{prop.setPrice(rs.getDouble("matprice"));}catch(NullPointerException e){}
			try{prop.setCurrentQty(rs.getDouble("currentqty"));}catch(NullPointerException e){}
			try{prop.setPreviousQty(rs.getDouble("prevqty"));}catch(NullPointerException e){}
			try{prop.setIsUsed(rs.getInt("isproductused"));}catch(NullPointerException e){}
			try{prop.setIsActive(rs.getInt("isactiveprop"));}catch(NullPointerException e){}
			out.setMaterialProperties(prop);
			
			Materials mat = new Materials();
			try{mat.setId(rs.getInt("matid"));}catch(NullPointerException e){}
			try{mat.setName(rs.getString("matname"));}catch(NullPointerException e){}
			try{mat.setIsActive(rs.getInt("isactivemat"));}catch(NullPointerException e){}
			out.setMaterials(mat);
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			out.setUom(uom);
			
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
			out.setUserDtls(user);
			
			Location loc = new Location();
			try{loc.setId(rs.getInt("locid"));}catch(NullPointerException e){}
			try{loc.setName(rs.getString("locname"));}catch(NullPointerException e){}
			try{loc.setIsActive(rs.getInt("isactiveloc"));}catch(NullPointerException e){}
			out.setLocation(loc);
			
			MaterialOUT ot = new MaterialOUT();
			try{ot.setId(rs.getLong("outid"));}catch(NullPointerException e){}
			try{ot.setDateTrans(rs.getString("outdate"));}catch(NullPointerException e){}
			try{ot.setRemarks(rs.getString("outremarks"));}catch(NullPointerException e){}
			try{ot.setPriceCost(rs.getDouble("matcost"));}catch(NullPointerException e){}
			try{ot.setQuantity(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{ot.setIsActive(rs.getInt("isactiveout"));}catch(NullPointerException e){}
			try{ot.setTimestamp(rs.getTimestamp("timestampout"));}catch(NullPointerException e){}
			out.setMaterialOut(ot);
			
			props.add(out);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		
		return props;
	}
	
	public static MaterialRETURN retrieve(long outId){
		MaterialRETURN out = new MaterialRETURN();
		
		String tableRet = "ret";
		String tableProp = "prop";
		String tableMat = "mat";
		String tableUOM = "uom";
		String tableUser = "usr";
		String tableEmp = "emp";
		String tableArea = "area";
		String tableOut = "ot";
		
		String sql = "SELECT * FROM materialreturn "+ tableRet + ", employee "+ tableEmp + ", materialprops " + tableProp + 
				", materials " + tableMat + ", UOM " + tableUOM + ", userdtls " + tableUser + ", locations " + tableArea + ", materialout " + tableOut +
				" WHERE " + tableRet + ".propid=" + tableProp + ".propid AND " +
				tableRet + ".returnby=" + tableEmp + ".empid AND " +
				tableRet + ".matid=" + tableMat + ".matid AND "+
				tableRet + ".uomid="+tableUOM + ".uomid AND "+
				tableRet + ".userdtlsid="+tableUser + ".userdtlsid AND "+
				tableRet + ".locid=" + tableArea +".locid AND "+ 
				tableRet + ".outid=" + tableOut + ".outid AND ot.isactiveout=1 AND ot.outid="+outId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{out.setId(rs.getLong("retid"));}catch(NullPointerException e){}
			try{out.setDateTrans(rs.getString("retdate"));}catch(NullPointerException e){}
			try{out.setRemarks(rs.getString("retremarks"));}catch(NullPointerException e){}
			try{out.setPriceCost(rs.getDouble("matcost"));}catch(NullPointerException e){}
			try{out.setQuantity(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{out.setIsActive(rs.getInt("isactivereturn"));}catch(NullPointerException e){}
			try{out.setTimestamp(rs.getTimestamp("timestampret"));}catch(NullPointerException e){}
			
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
			out.setReturnBy(emp);
			
			MaterialProperties prop = new MaterialProperties();
			try{prop.setId(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setDateTrans(rs.getString("propdate"));}catch(NullPointerException e){}
			try{prop.setPrice(rs.getDouble("matprice"));}catch(NullPointerException e){}
			try{prop.setCurrentQty(rs.getDouble("currentqty"));}catch(NullPointerException e){}
			try{prop.setPreviousQty(rs.getDouble("prevqty"));}catch(NullPointerException e){}
			try{prop.setIsUsed(rs.getInt("isproductused"));}catch(NullPointerException e){}
			try{prop.setIsActive(rs.getInt("isactiveprop"));}catch(NullPointerException e){}
			out.setMaterialProperties(prop);
			
			Materials mat = new Materials();
			try{mat.setId(rs.getInt("matid"));}catch(NullPointerException e){}
			try{mat.setName(rs.getString("matname"));}catch(NullPointerException e){}
			try{mat.setIsActive(rs.getInt("isactivemat"));}catch(NullPointerException e){}
			out.setMaterials(mat);
			
			UOM uom = new UOM();
			try{uom.setId(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setName(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{uom.setIsActive(rs.getInt("isactiveuom"));}catch(NullPointerException e){}
			out.setUom(uom);
			
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
			out.setUserDtls(user);
		
			Location loc = new Location();
			try{loc.setId(rs.getInt("locid"));}catch(NullPointerException e){}
			try{loc.setName(rs.getString("locname"));}catch(NullPointerException e){}
			try{loc.setIsActive(rs.getInt("isactiveloc"));}catch(NullPointerException e){}
			out.setLocation(loc);
			
			MaterialOUT ot = new MaterialOUT();
			try{ot.setId(rs.getLong("outid"));}catch(NullPointerException e){}
			try{ot.setDateTrans(rs.getString("outdate"));}catch(NullPointerException e){}
			try{ot.setRemarks(rs.getString("outremarks"));}catch(NullPointerException e){}
			try{ot.setPriceCost(rs.getDouble("matcost"));}catch(NullPointerException e){}
			try{ot.setQuantity(rs.getDouble("matqty"));}catch(NullPointerException e){}
			try{ot.setIsActive(rs.getInt("isactiveout"));}catch(NullPointerException e){}
			try{ot.setTimestamp(rs.getTimestamp("timestampout"));}catch(NullPointerException e){}
			out.setMaterialOut(ot);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		
		return out;
	}
	
	public static MaterialRETURN save(MaterialRETURN mat){
		if(mat!=null){
			
			long id = MaterialRETURN.getInfo(mat.getId() ==0? MaterialRETURN.getLatestId()+1 : mat.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mat = MaterialRETURN.insertData(mat, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mat = MaterialRETURN.updateData(mat);
			}else if(id==3){
				LogU.add("added new Data ");
				mat = MaterialRETURN.insertData(mat, "3");
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
	
	public static MaterialRETURN insertData(MaterialRETURN mat, String type){
		String sql = "INSERT INTO materialreturn ("
				+ "retid,"
				+ "retdate,"
				+ "retremarks,"
				+ "propid,"
				+ "matid,"
				+ "uomid,"
				+ "matcost,"
				+ "matqty,"
				+ "returnby,"
				+ "isactivereturn,"
				+ "userdtlsid,"
				+ "locid,"
				+ "outid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table materialout");
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
		ps.setString(cnt++, mat.getRemarks());
		ps.setLong(cnt++, mat.getMaterialProperties().getId());
		ps.setInt(cnt++, mat.getMaterials().getId());
		ps.setInt(cnt++, mat.getUom().getId());
		ps.setDouble(cnt++, mat.getPriceCost());
		ps.setDouble(cnt++, mat.getQuantity());
		ps.setLong(cnt++, mat.getReturnBy().getId());
		ps.setInt(cnt++, mat.getIsActive());
		ps.setLong(cnt++, mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, mat.getLocation().getId());
		ps.setLong(cnt++, mat.getMaterialOut().getId());
		
		LogU.add(mat.getDateTrans());
		LogU.add(mat.getRemarks());
		LogU.add(mat.getMaterialProperties().getId());
		LogU.add(mat.getMaterials().getId());
		LogU.add(mat.getUom().getId());
		LogU.add(mat.getPriceCost());
		LogU.add(mat.getQuantity());
		LogU.add(mat.getReturnBy().getId());
		LogU.add(mat.getIsActive());
		LogU.add(mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		LogU.add(mat.getLocation().getId());
		LogU.add(mat.getMaterialOut().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to materialout : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mat;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO materialreturn ("
				+ "retid,"
				+ "retdate,"
				+ "retremarks,"
				+ "propid,"
				+ "matid,"
				+ "uomid,"
				+ "matcost,"
				+ "matqty,"
				+ "returnby,"
				+ "isactivereturn,"
				+ "userdtlsid,"
				+ "locid,"
				+ "outid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table materialout");
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
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getMaterialProperties().getId());
		ps.setInt(cnt++, getMaterials().getId());
		ps.setInt(cnt++, getUom().getId());
		ps.setDouble(cnt++, getPriceCost());
		ps.setDouble(cnt++, getQuantity());
		ps.setLong(cnt++, getReturnBy().getId());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getLocation().getId());
		ps.setLong(cnt++, getMaterialOut().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getRemarks());
		LogU.add(getMaterialProperties().getId());
		LogU.add(getMaterials().getId());
		LogU.add(getUom().getId());
		LogU.add(getPriceCost());
		LogU.add(getQuantity());
		LogU.add(getReturnBy().getId());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getLocation().getId());
		LogU.add(getMaterialOut().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to materialout : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MaterialRETURN updateData(MaterialRETURN mat){
		String sql = "UPDATE materialreturn SET "
				+ "retdate=?,"
				+ "retremarks=?,"
				+ "propid=?,"
				+ "matid=?,"
				+ "uomid=?,"
				+ "matcost=?,"
				+ "matqty=?,"
				+ "returnby=?,"
				+ "userdtlsid=?,"
				+ "locid=?,"
				+ "outid=?" 
				+ " WHERE retid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table materialout");
		
		ps.setString(cnt++, mat.getDateTrans());
		ps.setString(cnt++, mat.getRemarks());
		ps.setLong(cnt++, mat.getMaterialProperties().getId());
		ps.setInt(cnt++, mat.getMaterials().getId());
		ps.setInt(cnt++, mat.getUom().getId());
		ps.setDouble(cnt++, mat.getPriceCost());
		ps.setDouble(cnt++, mat.getQuantity());
		ps.setLong(cnt++, mat.getReturnBy().getId());
		ps.setLong(cnt++, mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, mat.getLocation().getId());
		ps.setLong(cnt++, mat.getMaterialOut().getId());
		ps.setLong(cnt++, mat.getId());
		
		LogU.add(mat.getDateTrans());
		LogU.add(mat.getRemarks());
		LogU.add(mat.getMaterialProperties().getId());
		LogU.add(mat.getMaterials().getId());
		LogU.add(mat.getUom().getId());
		LogU.add(mat.getPriceCost());
		LogU.add(mat.getQuantity());
		LogU.add(mat.getReturnBy().getId());
		LogU.add(mat.getUserDtls()==null? 0 : mat.getUserDtls().getUserdtlsid());
		LogU.add(mat.getLocation().getId());
		LogU.add(mat.getMaterialOut().getId());
		LogU.add(mat.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to materialout : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mat;
	}
	
	public void updateData(){
		String sql = "UPDATE materialreturn SET "
				+ "retdate=?,"
				+ "retremarks=?,"
				+ "propid=?,"
				+ "matid=?,"
				+ "uomid=?,"
				+ "matcost=?,"
				+ "matqty=?,"
				+ "returnby=?,"
				+ "userdtlsid=?,"
				+ "locid=?,"
				+ "outid=?" 
				+ " WHERE retid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table materialout");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getMaterialProperties().getId());
		ps.setInt(cnt++, getMaterials().getId());
		ps.setInt(cnt++, getUom().getId());
		ps.setDouble(cnt++, getPriceCost());
		ps.setDouble(cnt++, getQuantity());
		ps.setLong(cnt++, getReturnBy().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getLocation().getId());
		ps.setLong(cnt++, getMaterialOut().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getRemarks());
		LogU.add(getMaterialProperties().getId());
		LogU.add(getMaterials().getId());
		LogU.add(getUom().getId());
		LogU.add(getPriceCost());
		LogU.add(getQuantity());
		LogU.add(getReturnBy().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getLocation().getId());
		LogU.add(getMaterialOut().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to materialout : " + s.getMessage());
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
		sql="SELECT retid FROM materialreturn ORDER BY retid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("retid");
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
		ps = conn.prepareStatement("SELECT retid FROM materialreturn WHERE retid=?");
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
		String sql = "UPDATE materialreturn set isactivereturn=0 WHERE retid=?";
		
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Employee getReturnBy() {
		return returnBy;
	}

	public void setReturnBy(Employee returnBy) {
		this.returnBy = returnBy;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public MaterialOUT getMaterialOut() {
		return materialOut;
	}

	public void setMaterialOut(MaterialOUT materialOut) {
		this.materialOut = materialOut;
	}
	
}

