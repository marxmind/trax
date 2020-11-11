package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.CellEditEvent;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Barangay;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.EmployeeCards;
import com.italia.marxmind.trax.controller.Job;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.controller.Municipality;
import com.italia.marxmind.trax.controller.Province;
import com.italia.marxmind.trax.enm.CardType;
import com.italia.marxmind.trax.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 08/21/2017
 *
 */

@ManagedBean(name="empBean", eager=true)
@ViewScoped
public class EmployeeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 16867694353L;
	
	private String dateRegistered;
	private String dateResigned;
	private String firstName ="First Name";
	private String middleName = "Middle Name";
	private String lastName = "Last Name";
	private int age;
	private double salary;
	private double overtime;
	
	private String contactNo = "Contact No";
	private String purok = "Purok";
	
	private boolean resigned;
	
	private List genders;
	private int genderId;
	
	private Employee employeeSelected;
	private List<Employee> employees = new ArrayList<Employee>();
	
	private List positions;
	private int positionId;
	
	private List barangays;
	private int barangayId;
	
	private List municipalitys;
	private int municipalityId;
	
	private List provinces;
	private int provinceId;
	
	private String searchName;
	
	
	private Map<Integer, Job> positionsData = new HashMap<Integer, Job>();
	private Map<Integer, Barangay> bgyData = new HashMap<Integer, Barangay>();
	private Map<Integer, Municipality> munData = new HashMap<Integer, Municipality>();
	private Map<Integer, Province> provData = new HashMap<Integer, Province>();
	
	private List<EmployeeCards> cardSheets = new ArrayList<EmployeeCards>();
	
	private List cardTypes;
	private int cardId;
	
	@PostConstruct
	public void init(){
		
		System.out.println("running employeebean page.....");
		employees = new ArrayList<Employee>();
		
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			sql = " AND emp.isactiveemp=1 AND emp.fullname like '%" + getSearchName() +"%'";
		}else{
			sql = " AND emp.isactiveemp=1";
		}
		
		employees = Employee.retrieve(sql, params);
		
		if(employees!=null && employees.size()==1){
			clickItem(employees.get(0));
		}else{
			clearFields();
			Collections.reverse(employees);
		}
		
	}
	
	public void saveData(){
		Employee em = new Employee();
		if(getEmployeeSelected()!=null){
			em = getEmployeeSelected();
			System.out.println("Employee is existed");
		}
		
		if(isResigned()){
			em.setDateResigned(DateUtils.getCurrentDateYYYYMMDD());
		}else{
			em.setDateResigned(null);
		}
		
		em.setDateRegistered(getDateRegistered());
		em.setFirstName(getFirstName());
		em.setMiddleName(getMiddleName());
		em.setLastName(getLastName());
		em.setFullName(getFirstName() + " " + getLastName());
		em.setAge(getAge());
		em.setContactNo(getContactNo());
		em.setGender(getGenderId());
		em.setPurok(getPurok());
		em.setIsActiveEmployee(1);
		em.setSalary(getSalary());
		em.setOvertime(getOvertime());
		
		em.setIsResigned(isResigned()==true? 1 : 0);
		
		em.setJob(getPositionsData().get(getPositionId()));
		//em.setPosition(getPositionsData().get(getPositionId()));
		//em.setCommittee(getCommiteeData().get(getCommitteeId()));
		
		em.setBarangay(getBgyData().get(getBarangayId()));
		em.setMunicipality(getMunData().get(getMunicipalityId()));
		em.setProvince(getProvData().get(getProvinceId()));
		em.save();
		clearFields();
		init();
		Application.addMessage(1, "Successfully saved.", "");
	}
	
	public void onCellEdit(CellEditEvent event) {
		
		 Object oldValue = event.getOldValue();
	     Object newValue = event.getNewValue();
	     
	     System.out.println("old Value: " + oldValue);
	     System.out.println("new Value: " + newValue);
	     
	     System.out.println("index " + event.getRowIndex());
	     
	     int index = event.getRowIndex();
	     //timeSheets.get(index).setIndex(index);
	     getCardSheets().get(index).setIndex(index);
	     
	     if(getCardId()>0){
	    	 getCardSheets().get(index).setCardType(getCardId());
	    	 getCardSheets().get(index).setCardTypeName(CardType.typeName(getCardId()));
	     }
	     
	}
	
	public void deleteCard(EmployeeCards card){
		try{
		getCardSheets().remove(card.getIndex());
		card.delete();
		Application.addMessage(1, "Success", "Successfully deleted.");
		}catch(Exception e){}
	}
	
	public void addCard(){
		int index = 0;
		EmployeeCards card = new EmployeeCards();
		if(getCardSheets()!=null && getCardSheets().size()>0){
			index = getCardSheets().size();
			card.setIndex(index);
			card.setCardTypeName("Select Card");
			card.setCardType(0);
			card.setAccountNumber("000-000-000");
			getCardSheets().add(card);
		}else{
			card.setIndex(index);
			card.setCardTypeName("Select Card");
			card.setCardType(0);
			card.setAccountNumber("000-000-000");
			getCardSheets().add(card);
		}
	}
	
	public void saveCard(EmployeeCards card){
		if(getEmployeeSelected()!=null){
			
			if(card.getId()==0){
				card.setIsActive(1);
				card.setDateRegister(DateUtils.getCurrentDateYYYYMMDD());
				card.setEmployee(getEmployeeSelected());
			}
			card= EmployeeCards.save(card);
			
			getCardSheets().get(card.getIndex()).setId(card.getId());
			getCardSheets().get(card.getIndex()).setDateRegister(card.getDateRegister());
			getCardSheets().get(card.getIndex()).setCardType(card.getCardType());
			getCardSheets().get(card.getIndex()).setCardTypeName(CardType.typeName(card.getCardType()));
			getCardSheets().get(card.getIndex()).setEmployee(card.getEmployee());
			getCardSheets().get(card.getIndex()).setIsActive(card.getIsActive());
			getCardSheets().get(card.getIndex()).setAccountNumber(card.getAccountNumber());
			setCardId(0);
			Application.addMessage(1, "Success", "Successfully saved.");
			
		}else{
			Application.addMessage(3, "Error", "Please select Employee.");
		}
	}
	
	public void clickItem(Employee em){
		setDateRegistered(em.getDateRegistered());
		setDateResigned(em.getDateResigned());
		setFirstName(em.getFirstName());
		setMiddleName(em.getMiddleName());
		setLastName(em.getLastName());
		setAge(em.getAge());
		setResigned(em.getIsResigned()==1? true : false);
		setGenderId(em.getGender());
		setEmployeeSelected(em);
		setPositionId(em.getJob().getJobid());
		setSalary(em.getSalary());
		setContactNo(em.getContactNo());
		setPurok(em.getPurok());
		setBarangayId(em.getBarangay().getId());
		setMunicipalityId(em.getMunicipality().getId());
		setProvinceId(em.getProvince().getId());
		setOvertime(em.getOvertime());
		
		String sql = " AND emp.empid=? AND card.isactivecard=1 ";
		String[] params = new String[1];
		params[0] = em.getId()+"";
		cardSheets= EmployeeCards.retrieve(sql, params);
		
	}
	
	public void deleteRow(Employee emp){
		if(Login.checkUserStatus()){
			emp.delete();
			clearFields();
			init();
			Application.addMessage(1, "successfully removed employee.","");
		}
	}
	
	
	
	public void clearFields(){
		setDateRegistered(null);
		setDateResigned(null);
		setFirstName("First Name");
		setMiddleName("Middle Name");
		setLastName("Last Name");
		setAge(0);
		setResigned(false);
		setGenderId(1);
		setEmployeeSelected(null);
		setPositionId(0);
		setSearchName(null);
		setContactNo("Contact No");
		setPurok("Purok");
		setBarangayId(0);
		setMunicipalityId(0);
		setProvinceId(0);
		setSalary(0);
		setOvertime(0);
	}
	
	public String getDateRegistered() {
		if(dateRegistered==null){
			dateRegistered = DateUtils.getCurrentDateYYYYMMDD();
		}
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
	
	public boolean isResigned() {
		return resigned;
	}
	public void setResigned(boolean resigned) {
		this.resigned = resigned;
	}
	public List getGenders() {
		genders = new ArrayList<>();
		genders.add(new SelectItem(1, "Male"));
		genders.add(new SelectItem(2, "Female"));
		return genders;
	}
	public void setGenders(List genders) {
		this.genders = genders;
	}
	public int getGenderId() {
		return genderId;
	}
	public void setGenderId(int genderId) {
		this.genderId = genderId;
	}
	public Employee getEmployeeSelected() {
		return employeeSelected;
	}
	public void setEmployeeSelected(Employee employeeSelected) {
		this.employeeSelected = employeeSelected;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	public List getPositions() {
		positions = new ArrayList<>();
		positionsData = new HashMap<Integer, Job>();
		for(Job ps : Job.retrieve("SELECT * FROM jobtitle WHERE isactivejob=1", new String[0])){
			positions.add(new SelectItem(ps.getJobid(), ps.getJobname()));
			positionsData.put(ps.getJobid(), ps);
		}
		
		return positions;
	}
	public void setPositions(List positions) {
		this.positions = positions;
	}
	public int getPositionId() {
		return positionId;
	}
	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}
	
	public String getSearchName() {
		return searchName;
	}


	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List getBarangays() {
		
		barangays = new ArrayList<>();
		bgyData = new HashMap<Integer, Barangay>();
		for(Barangay bg : Barangay.retrieve("SELECT * FROM barangay WHERE bgisactive=1", new String[0])){
			barangays.add(new SelectItem(bg.getId(), bg.getName()));
			bgyData.put(bg.getId(), bg);
		}
		
		return barangays;
	}

	public void setBarangays(List barangays) {
		this.barangays = barangays;
	}

	public int getBarangayId() {
		return barangayId;
	}

	public void setBarangayId(int barangayId) {
		this.barangayId = barangayId;
	}

	public List getMunicipalitys() {
		municipalitys = new ArrayList<>();
		munData = new HashMap<Integer, Municipality>();
		for(Municipality mun : Municipality.retrieve("SELECT * FROM municipality WHERE munisactive=1 ", new String[0])){
			municipalitys.add(new SelectItem(mun.getId(), mun.getName()));
			munData.put(mun.getId(), mun);
		}
		return municipalitys;
	}

	public void setMunicipalitys(List municipalitys) {
		this.municipalitys = municipalitys;
	}

	public int getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(int municipalityId) {
		this.municipalityId = municipalityId;
	}

	public List getProvinces() {
		provinces = new ArrayList<>();
		provData = new HashMap<Integer, Province>();
		for(Province pr : Province.retrieve("SELECT * FROM province WHERE provisactive=1 ", new String[0])){
			provinces.add(new SelectItem(pr.getId(), pr.getName()));
			provData.put(pr.getId(), pr);
		}
		return provinces;
	}

	public void setProvinces(List provinces) {
		this.provinces = provinces;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
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

	public Map<Integer, Job> getPositionsData() {
		return positionsData;
	}

	public void setPositionsData(Map<Integer, Job> positionsData) {
		this.positionsData = positionsData;
	}

	public Map<Integer, Barangay> getBgyData() {
		return bgyData;
	}

	public void setBgyData(Map<Integer, Barangay> bgyData) {
		this.bgyData = bgyData;
	}

	public Map<Integer, Municipality> getMunData() {
		return munData;
	}

	public void setMunData(Map<Integer, Municipality> munData) {
		this.munData = munData;
	}

	public Map<Integer, Province> getProvData() {
		return provData;
	}

	public void setProvData(Map<Integer, Province> provData) {
		this.provData = provData;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public double getOvertime() {
		return overtime;
	}

	public void setOvertime(double overtime) {
		this.overtime = overtime;
	}

	public List<EmployeeCards> getCardSheets() {
		return cardSheets;
	}

	public void setCardSheets(List<EmployeeCards> cardSheets) {
		this.cardSheets = cardSheets;
	}

	public List getCardTypes() {
		cardTypes = new ArrayList<EmployeeCards>();
		cardTypes.add(new SelectItem(0, "Select Card"));
		for(CardType type : CardType.values()){
			cardTypes.add(new SelectItem(type.getId(), type.getValue()));
		}
		return cardTypes;
	}

	public void setCardTypes(List cardTypes) {
		this.cardTypes = cardTypes;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

}

