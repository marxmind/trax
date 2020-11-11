package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.italia.marxmind.trax.controller.Department;
import com.italia.marxmind.trax.controller.Job;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.controller.UserAccessLevel;
import com.italia.marxmind.trax.controller.UserDtls;
import com.italia.marxmind.trax.utils.DateUtils;
import com.italia.marxmind.trax.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 09/29/2016
 *@version 1.0
 */
@ManagedBean(name="auserBean", eager=true)
@ViewScoped
public class AdminUserBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094801425228386363L;
	private List<UserDtls> users = new ArrayList<UserDtls>();
	private UserDtls userdtls;
	
	private String regdate;
	private String firstname = "Inpute Firstname";
	private String middlename  = "Inpute Middle"; 
	private String lastname  = "Inpute Last";
	private String address  = "Inpute Address";
	private String contactno  = "Inpute Contact no";
	private String age = "0";
	
	private String username = "Input Username";
	private String password = "Input Password";
	private String accesslevedid;
	private List accesslevellist = new ArrayList<>();
	
	private Job job;
	private Department department;
	private List ageList = new ArrayList<>();
	private List jobList = new ArrayList<>();
	private List departmentList = new ArrayList<>();
	private String jobId;
	private String departmentId;
	private String genderId;
	private List genderList = new ArrayList<>();
	
	private List<UserDtls> selectedUser;
	
	public void deactivateUser(boolean isValid){
		System.out.println("deactiavting user : " + isValid);
		if(isValid){
			System.out.println("checking selected user " + getSelectedUser().size());
			if(getSelectedUser()!=null){
				for(UserDtls user : getSelectedUser()){
					LogU.add("Deactivating user " + " id : " + user.getUserdtlsid() + " " + user.getFirstname() + " " + user.getMiddlename() + " " + user.getLastname());
					user.delete(true);
					user.getLogin().delete(true);
				}
				selectedUser = new ArrayList<>();
				init();
			}
		}
	}
	
	@PostConstruct
	public void init(){
		if(Login.checkUserStatus()){
			
			System.out.println("initialize AdminUserBean");
			users = new ArrayList<UserDtls>();
			UserDtls user = new UserDtls();
			user.setIsActive(1);
			users = UserDtls.retrieve(user);
			
			Collections.reverse(users);
		}
	}
	
	public void print(){
		System.out.println("Print");
	}
	
	public void printAll(){
		System.out.println("Print All");
	}
	
	public void close(){
		System.out.println("close");
		clearFields();
	}
	
	public void save(){
		System.out.println("Save");
		
		UserDtls user = new UserDtls();
		Login in = new Login();
		
		if(getUserdtls()!=null){
			user = getUserdtls();
			in = user.getLogin();
			in.setUsername(getUsername());
			in.setPassword(getPassword());
			in.setAccessLevel(UserAccessLevel.userAccessLevel(getAccesslevedid()));
			in.save();
			
		}else{
			user.setRegdate(getRegdate());
			user.setIsActive(1);
			user.setUserDtls(Login.getUserLogin().getUserDtls());
			
			in.setUsername(getUsername());
			in.setPassword(getPassword());
			in.setIsOnline(0);
			in.setAccessLevel(UserAccessLevel.userAccessLevel(getAccesslevedid()));
			
			long id = Login.getInfo(in.getLogid()==null? Login.getLatestId()+1 : in.getLogid());
			UserDtls userD = new UserDtls();
			userD.setUserdtlsid(Login.getLatestId()+1);
			in.setUserDtls(userD);
			if(id==1){
				in = Login.insertData(in, "1");
			}else if(id==3){
				in = Login.insertData(in, "3");
			}
			
		}
		user.setLogin(in);
		user.setFirstname(getFirstname());
		user.setMiddlename(getMiddlename());
		user.setLastname(getLastname());
		user.setAddress(getAddress());
		user.setContactno(getContactno());
		user.setAge(Integer.valueOf(getAge()));
		user.setGender(Integer.valueOf(getGenderId()));
		user.setJob(Job.job(Integer.valueOf(getJobId())));
		user.setDepartment(Department.department(getDepartmentId()));
		user.save();
		
		init();
		clearFields();
	}
	
	public void clearFields(){
		setUserdtls(null);
		setRegdate(null);
		setFirstname("Input Firstname");
		setMiddlename("Input Middlename");
		setLastname("Input Lastname");
		setAddress("Input Address");
		setContactno("Input Contact");
		setAge(null);
		setGenderId(null);
		setJobId(null);
		setDepartmentId(null);
		
		setUsername("Input Username");
		setPassword("Input Password");
		setAccesslevedid(null);
	}
	
	public void clickItem(UserDtls userDtls){
		System.out.println("clickItem");
		setUserdtls(userDtls);
		setRegdate(userDtls.getRegdate());
		setFirstname(userDtls.getFirstname());
		setMiddlename(userDtls.getMiddlename());
		setLastname(userDtls.getLastname());
		setAddress(userDtls.getAddress());
		setContactno(userDtls.getContactno());
		setAge(userDtls.getAge()+"");
		setGenderId(userDtls.getGender()+"");
		setJobId(userDtls.getJob().getJobid()+"");
		setDepartmentId(userDtls.getDepartment().getDepid()+"");
		
		Login in = userDtls.getLogin();
		setUsername(in.getUsername());
		setPassword(in.getPassword());
		setAccesslevedid(in.getAccessLevel().getUseraccesslevelid()+"");
	}
	
	public void deleteRow(UserDtls userDtls, boolean isValid){
		System.out.println("deleteRow");
		if(isValid){
			LogU.add("Deactivating user " + " id : " + userDtls.getUserdtlsid() + " " + userDtls.getFirstname() + " " + userDtls.getMiddlename() + " " + userDtls.getLastname());
			userDtls.delete(true);
			userDtls.getLogin().delete(true);
			init();
		}
	}

	public List<UserDtls> getUsers() {
		return users;
	}

	public void setUsers(List<UserDtls> users) {
		this.users = users;
	}

	public UserDtls getUserdtls() {
		return userdtls;
	}

	public void setUserdtls(UserDtls userdtls) {
		this.userdtls = userdtls;
	}
	

	public String getFirstname() {
		return firstname;
	}

	public String getRegdate() {
		if(regdate==null)
			regdate = DateUtils.getCurrentDateYYYYMMDD();
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactno() {
		return contactno;
	}

	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	public Job getJob() {
		return job;
	}

	public String getAge() {
		return age;
	}

	public List getAgeList() {
		ageList = Collections.synchronizedList(new ArrayList<>());
		
		for(int i=1; i<=100;i++){
			ageList.add(new SelectItem(i+"",i+""));
		}
		
		return ageList;
	}

	public void setAgeList(List ageList) {
		this.ageList = ageList;
	}

	public List getJobList() {
		jobList = Collections.synchronizedList(new ArrayList<>());
		String sql = "SELECT * FROM jobtitle WHERE isactivejob=1";
		for(Job job : Job.retrieve(sql, new String[0])){
			jobList.add(new SelectItem(job.getJobid()+"",job.getJobname()));
		}
		
		return jobList;
	}

	public String getJobId() {
		return jobId;
	}

	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public List getGenderList() {
		genderList = Collections.synchronizedList(new ArrayList<>());
		genderList.add(new SelectItem("1","Male"));
		genderList.add(new SelectItem("2","Female"));
		return genderList;
	}

	public void setGenderList(List genderList) {
		this.genderList = genderList;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
 
	public void setJobList(List jobList) {
		this.jobList = jobList;
	} 

	public List getDepartmentList() {
		departmentList = Collections.synchronizedList(new ArrayList<>());
		String sql = "SELECT * FROM department";
		for(Department dep : Department.retrieve(sql, new String[0])){
			departmentList.add(new SelectItem(dep.getDepid()+"",dep.getDepartmentName()));
		}
		return departmentList;
	}

	public void setDepartmentList(List departmentList) {
		this.departmentList = departmentList;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccesslevedid() {
		return accesslevedid;
	}

	public void setAccesslevedid(String accesslevedid) {
		this.accesslevedid = accesslevedid;
	}

	public List getAccesslevellist() {
		accesslevellist = new ArrayList<>();
		String sql = "SELECT * FROM useraccesslevel";
		for(UserAccessLevel lvl : UserAccessLevel.retrieve(sql, new String[0])){
			accesslevellist.add(new SelectItem(lvl.getUseraccesslevelid()+"",lvl.getName()));
		}
		
		return accesslevellist;
	}

	public void setAccesslevellist(List accesslevellist) {
		this.accesslevellist = accesslevellist;
	}

	public List<UserDtls> getSelectedUser() {
		if(selectedUser!=null){
			System.out.println("User selected " +selectedUser.size());
		}
		return selectedUser;
	}

	public void setSelectedUser(List<UserDtls> selectedUser) {
		this.selectedUser = selectedUser;
	}
	
}
