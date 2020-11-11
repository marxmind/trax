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

public class ActivityTransactions {

	private long id;
	private String dateTrans;
	private String loads;
	private String drums;
	private String blocks;
	private String cuts;
	private String remarks;
	private int isActive;
	private Timestamp timestamp;
	
	private Activity activity;
	private UserDtls userDtls;
	
	private LocationTransactions locationTransactions;
	private List<MaterialTransactions> materialTransactions;
	
	private List<TimeSheets> timeSheets;
	
	private String timeIn;
	private String timeOut;
	private int totalMandays;
	private double totalHours;
	private String totalExpenses;
	
	private TimeSheets EmployeeTimeSheets;
	private Location location;
	private Employee employee;
	
	private int index;
	private String materialUsed;
	private boolean tagOT;
	private boolean replicate;
	
	private double totalActivityExpenses;
	
	public static List<ActivityTransactions> retrieve(String sqlAdd, String[] params){
		List<ActivityTransactions> acs = new ArrayList<ActivityTransactions>();
		
		String tableTrans = "trn";
		String tableAc = "ac";
		String tableUsr = "usr";
		
		String sql = "SELECT * FROM activitytrans " + tableTrans + ", activity " + tableAc + ", userdtls " + tableUsr +" WHERE " +
				tableTrans +".acid=" + tableAc + ".acid AND " +
				tableTrans +".userdtlsid=" + tableUsr + ".userdtlsid ";
		
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
			try{ac.setName(rs.getString("acname"));}catch(NullPointerException e){}
			try{ac.setIsActive(rs.getInt("isactiveac"));}catch(NullPointerException e){}
			trn.setActivity(ac);
			
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
			trn.setUserDtls(user);
			
			acs.add(trn);
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return acs;
	}
	
	public static ActivityTransactions retrieve(long activityId){
		ActivityTransactions trn = new ActivityTransactions();
		
		String tableTrans = "trn";
		String tableAc = "ac";
		String tableUsr = "usr";
		
		String sql = "SELECT * FROM activitytrans " + tableTrans + ", activity " + tableAc + ", userdtls " + tableUsr +" WHERE " +
				tableTrans +".acid=" + tableAc + ".acid AND " +
				tableTrans +".userdtlsid=" + tableUsr + ".userdtlsid AND trn.isactiveactrans=1 AND " + tableTrans + ".actransid="+activityId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
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
			try{ac.setName(rs.getString("acname"));}catch(NullPointerException e){}
			try{ac.setIsActive(rs.getInt("isactiveac"));}catch(NullPointerException e){}
			trn.setActivity(ac);
			
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
			trn.setUserDtls(user);
			
			
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception ex){}
		
		return trn;
	}
	
	public static ActivityTransactions save(ActivityTransactions ac){
		if(ac!=null){
			
			long id = ActivityTransactions.getInfo(ac.getId() ==0? ActivityTransactions.getLatestId()+1 : ac.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ac = ActivityTransactions.insertData(ac, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ac = ActivityTransactions.updateData(ac);
			}else if(id==3){
				LogU.add("added new Data ");
				ac = ActivityTransactions.insertData(ac, "3");
			}
			
		}
		return ac;
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
	
	public static ActivityTransactions insertData(ActivityTransactions ac, String type){
		String sql = "INSERT INTO activitytrans ("
				+ "actransid,"
				+ "actDateTrans,"
				+ "loads,"
				+ "drums,"
				+ "blocks,"
				+ "cuts,"
				+ "remarks,"
				+ "isactiveactrans,"
				+ "acid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table activitytrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			ac.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			ac.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, ac.getDateTrans());
		ps.setString(cnt++, ac.getLoads());
		ps.setString(cnt++, ac.getDrums());
		ps.setString(cnt++, ac.getBlocks());
		ps.setString(cnt++, ac.getCuts());
		ps.setString(cnt++, ac.getRemarks());
		ps.setInt(cnt++, ac.getIsActive());
		ps.setInt(cnt++, ac.getActivity()==null? 0 : ac.getActivity().getId());
		ps.setLong(cnt++, ac.getUserDtls()==null? 0 : ac.getUserDtls().getUserdtlsid());
		
		LogU.add(ac.getDateTrans());
		LogU.add(ac.getLoads());
		LogU.add(ac.getDrums());
		LogU.add(ac.getBlocks());
		LogU.add(ac.getCuts());
		LogU.add(ac.getRemarks());
		LogU.add(ac.getIsActive());
		LogU.add(ac.getActivity()==null? 0 : ac.getActivity().getId());
		LogU.add(ac.getUserDtls()==null? 0 : ac.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to activitytrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ac;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO activitytrans ("
				+ "actransid,"
				+ "actDateTrans,"
				+ "loads,"
				+ "drums,"
				+ "blocks,"
				+ "cuts,"
				+ "remarks,"
				+ "isactiveactrans,"
				+ "acid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table activitytrans");
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
		ps.setString(cnt++, getLoads());
		ps.setString(cnt++, getDrums());
		ps.setString(cnt++, getBlocks());
		ps.setString(cnt++, getCuts());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getActivity()==null? 0 : getActivity().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getLoads());
		LogU.add(getDrums());
		LogU.add(getBlocks());
		LogU.add(getCuts());
		LogU.add(getRemarks());
		LogU.add(getIsActive());
		LogU.add(getActivity()==null? 0 : getActivity().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to activitytrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ActivityTransactions updateData(ActivityTransactions ac){
		String sql = "UPDATE activitytrans SET "
				+ "actDateTrans=?,"
				+ "loads=?,"
				+ "drums=?,"
				+ "blocks=?,"
				+ "cuts=?,"
				+ "remarks=?,"
				+ "acid=?,"
				+ "userdtlsid=?" 
				+ " WHERE actransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table activitytrans");
		
		ps.setString(cnt++, ac.getDateTrans());
		ps.setString(cnt++, ac.getLoads());
		ps.setString(cnt++, ac.getDrums());
		ps.setString(cnt++, ac.getBlocks());
		ps.setString(cnt++, ac.getCuts());
		ps.setString(cnt++, ac.getRemarks());
		ps.setInt(cnt++, ac.getActivity()==null? 0 : ac.getActivity().getId());
		ps.setLong(cnt++, ac.getUserDtls()==null? 0 : ac.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, ac.getId());
		
		LogU.add(ac.getDateTrans());
		LogU.add(ac.getLoads());
		LogU.add(ac.getDrums());
		LogU.add(ac.getBlocks());
		LogU.add(ac.getCuts());
		LogU.add(ac.getRemarks());
		LogU.add(ac.getActivity()==null? 0 : ac.getActivity().getId());
		LogU.add(ac.getUserDtls()==null? 0 : ac.getUserDtls().getUserdtlsid());
		LogU.add(ac.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to activitytrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ac;
	}
	
	public void updateData(){
		String sql = "UPDATE activitytrans SET "
				+ "actDateTrans=?,"
				+ "loads=?,"
				+ "drums=?,"
				+ "blocks=?,"
				+ "cuts=?,"
				+ "remarks=?,"
				+ "acid=?,"
				+ "userdtlsid=?" 
				+ " WHERE actransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table activitytrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getLoads());
		ps.setString(cnt++, getDrums());
		ps.setString(cnt++, getBlocks());
		ps.setString(cnt++, getCuts());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getActivity()==null? 0 : getActivity().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getLoads());
		LogU.add(getDrums());
		LogU.add(getBlocks());
		LogU.add(getCuts());
		LogU.add(getRemarks());
		LogU.add(getActivity()==null? 0 : getActivity().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to activitytrans : " + s.getMessage());
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
		sql="SELECT actransid FROM activitytrans  ORDER BY actransid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("actransid");
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
		ps = conn.prepareStatement("SELECT actransid FROM activitytrans WHERE actransid=?");
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
		String sql = "UPDATE activitytrans set isactiveactrans=0 WHERE actransid=?";
		
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
	public String getLoads() {
		return loads;
	}
	public void setLoads(String loads) {
		this.loads = loads;
	}
	public String getDrums() {
		return drums;
	}
	public void setDrums(String drums) {
		this.drums = drums;
	}
	public String getBlocks() {
		return blocks;
	}
	public void setBlocks(String blocks) {
		this.blocks = blocks;
	}
	public String getCuts() {
		return cuts;
	}
	public void setCuts(String cuts) {
		this.cuts = cuts;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public LocationTransactions getLocationTransactions() {
		return locationTransactions;
	}

	public void setLocationTransactions(LocationTransactions locationTransactions) {
		this.locationTransactions = locationTransactions;
	}

	public List<MaterialTransactions> getMaterialTransactions() {
		return materialTransactions;
	}

	public void setMaterialTransactions(List<MaterialTransactions> materialTransactions) {
		this.materialTransactions = materialTransactions;
	}

	public List<TimeSheets> getTimeSheets() {
		return timeSheets;
	}

	public void setTimeSheets(List<TimeSheets> timeSheets) {
		this.timeSheets = timeSheets;
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

	public int getTotalMandays() {
		return totalMandays;
	}

	public void setTotalMandays(int totalMandays) {
		this.totalMandays = totalMandays;
	}

	public double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(double totalHours) {
		this.totalHours = totalHours;
	}

	public String getTotalExpenses() {
		return totalExpenses;
	}

	public void setTotalExpenses(String totalExpenses) {
		this.totalExpenses = totalExpenses;
	}

	public TimeSheets getEmployeeTimeSheets() {
		return EmployeeTimeSheets;
	}

	public void setEmployeeTimeSheets(TimeSheets employeeTimeSheets) {
		EmployeeTimeSheets = employeeTimeSheets;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getMaterialUsed() {
		return materialUsed;
	}

	public void setMaterialUsed(String materialUsed) {
		this.materialUsed = materialUsed;
	}

	public boolean isTagOT() {
		return tagOT;
	}

	public void setTagOT(boolean tagOT) {
		this.tagOT = tagOT;
	}

	public double getTotalActivityExpenses() {
		return totalActivityExpenses;
	}

	public void setTotalActivityExpenses(double totalActivityExpenses) {
		this.totalActivityExpenses = totalActivityExpenses;
	}

	public boolean isReplicate() {
		return replicate;
	}

	public void setReplicate(boolean replicate) {
		this.replicate = replicate;
	}
	
}
