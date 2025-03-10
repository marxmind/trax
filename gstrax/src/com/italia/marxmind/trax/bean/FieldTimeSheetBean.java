package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.CellEditEvent;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Activity;
import com.italia.marxmind.trax.controller.ActivityTransactions;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.Location;
import com.italia.marxmind.trax.controller.LocationTransactions;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.controller.MaterialTransactions;
import com.italia.marxmind.trax.controller.Materials;
import com.italia.marxmind.trax.controller.TimeSheets;
import com.italia.marxmind.trax.controller.UOM;
import com.italia.marxmind.trax.controller.UserDtls;
import com.italia.marxmind.trax.enm.JobTitle;
import com.italia.marxmind.trax.enm.Time;
import com.italia.marxmind.trax.utils.DateUtils;
import com.italia.marxmind.trax.utils.GlobalVar;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author mark italia
 * @since 08/27/2017
 * @version 1.0
 *
 */
@Named
@ViewScoped
public class FieldTimeSheetBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 436746786861L;
	
	private String dateTrans;
	private String searchDescription;
	private Date timeSheetFrom;
	private Date timeSheetTo;
	private List<ActivityTransactions> timeSheets = new ArrayList<ActivityTransactions>();
	private ActivityTransactions timeSheetsData;
	
	private String searchEmployee;
	private List<Employee> employeeSelection = new ArrayList<Employee>();
	
	private String searchActivity;
	private List<Activity> activitySelection = new ArrayList<Activity>();
	
	private int index;
	
	private List timeIns;
	private double timeInId;
	
	private List timeOuts;
	private double timeOutId;
	
	private Map<Double, String> timeInMap = new HashMap<Double, String>();
	private Map<Double, String> timeOutMap = new HashMap<Double, String>();
	
	private String searchMaterial;
	private List<Materials> materialSelection = new ArrayList<Materials>();
	private List<MaterialTransactions> materialSelected = new ArrayList<MaterialTransactions>();
	
	private double quantity;
	private double holdQty;
	private int tmpUomId;
	
	private String searchLocation;
	private List<Location> loctionsSelection = new ArrayList<Location>();
	
	private List uoms;
	private int uomId;
	private Map<Integer, UOM> uomMap = new HashMap<Integer, UOM>();
	
	//private final static double HOUR_IN_DAY = 8.0;
	//private final static double DRIVER_OT = 40.0;
	//private final static double LEAD_SPRAY_MAN_OT = 35.0;
	//private final static double NORMAL_OT = 35.0;
	
	private List<ActivityTransactions> replicateSelectedData;
	private Date newDateReplicate;
	
	@Setter @Getter private List timeInList;
	@Setter @Getter private List timeOutList;
	
	@PostConstruct
	public void init(){
		//loadTimeSheets();
		loadTime();
		loadEnployeeSheets();
		
	}
	
	public void loadTime() {
		timeInList = new ArrayList<>();
		timeOutList = new ArrayList<>();
		for(Time time : Time.values()) {
			timeInList.add(new SelectItem(time.getId(), time.getValue()));
			timeOutList.add(new SelectItem(time.getId(), time.getValue()));
			System.out.println("In:"+time.getValue() + "\tOut:"+time.getValue());
		}
	}
	
	public void selectedReplicate(ActivityTransactions act){
		System.out.println("Is Check : " + act.isReplicate() + " check value " + act.getDrums());
		if(act.isReplicate()){
			if(getReplicateSelectedData()!=null && !getReplicateSelectedData().contains(act)){
				getReplicateSelectedData().add(act);
			}else{
				setReplicateSelectedData(new ArrayList<>());
				getReplicateSelectedData().add(act);
			}
		}else{
			getReplicateSelectedData().remove(act);
		}
	}
	
	public void replicateSelectedData(){
		boolean isOk = true;
		
		if(getNewDateReplicate()==null){
			Application.addMessage(3, "Error", "Please provide date");
			isOk=false;
		}
		
		if(getReplicateSelectedData()==null || getReplicateSelectedData().size()==0){
			Application.addMessage(3, "Error", "Please check first the data before replicate.");
			isOk=false;
		}
		
		if(isOk){
			
			int lastIndex = timeSheets.size();
			
			for(ActivityTransactions copyAct : getReplicateSelectedData()){
				ActivityTransactions asNew = new ActivityTransactions();
				asNew = replicateActivity(lastIndex, copyAct);
				timeSheets.add(asNew);
				saveData(asNew);
				lastIndex++;
			}
			
			Application.addMessage(1, "Success", "Selected data has been successfully replicated");
			setTimeSheetTo(getNewDateReplicate());
			setReplicateSelectedData(null);
		}
	}
	
	public void saveLaborAct(ActivityTransactions act){
		System.out.println("check for saving..... " + act.getLocation().getName());
		boolean isOk = true;
		if(act.getDrums()!=null && !act.getDrums().isEmpty()){
			isOk = false;
			Application.addMessage(3, "This timesheet cannot be change. Please go to Activity Timesheet recording page to edit the information", "");
		}
		
		if(act.getActivity()==null || act.getActivity().getId()==0){
			isOk = false;
			Application.addMessage(3, "Please provide Activity", "");
		}
		
		if(act.getEmployee()==null || act.getEmployee().getId()==0){
			isOk = false;
			Application.addMessage(3, "Please provide Employee name", "");
		}
		
		/*if(act.getLocation()==null || act.getLocation().getId()==0){
			isOk = false;
			Application.addMessage(3, "Please provide Area", "");
		}*/
		
		if("00:00 AM".equalsIgnoreCase(act.getTimeIn())){
			isOk = false;
			Application.addMessage(3, "Please provide Time In", "");
		}
		
		if("00:00 PM".equalsIgnoreCase(act.getTimeOut())){
			isOk = false;
			Application.addMessage(3, "Please provide Time Out", "");
		}
		
		if(isOk){
			isOk = Application.checkingDuplicateRecordedForTheSameTime(act,act.getEmployee(), act.getDateTrans(), act.getTimeIn(), act.getTimeOut());
			
			if(!isOk){
				isOk = false;
				Application.addMessage(3, "Employee name " + act.getEmployee().getFullName() + " has already recorded for this day and time", "");
			}
		}
		
		if(isOk){
			saveData(act);
			Application.addMessage(1, "Successfully saved", "");
		}
		
	}
	
	public UserDtls getUser(){
		return Login.getUserLogin().getUserDtls();
	}
	
	
	private void saveData(ActivityTransactions act){
		//UserDtls user = Login.getUserLogin().getUserDtls();
		
		act.setUserDtls(getUser());
		act = ActivityTransactions.save(act);
		
		System.out.println("saving in save: isOT: " + act.isTagOT());
		
		//Employee
		TimeSheets time = new TimeSheets();
		
		if(act.getEmployeeTimeSheets()==null){
			time.setIsActive(1);
			time.setDateTrans(act.getDateTrans());
			time.setTimeIn(act.getTimeIn());
			time.setTimeOut(act.getTimeOut());
			
			double timein = Time.typeId(act.getTimeIn());
			double timeout = Time.typeId(act.getTimeOut());
			double hrs = timeout - timein;
			time.setCountHour(hrs);
			
			time.setEmployee(act.getEmployee());
			time.setActivityTransactions(act);
			time.setIsOvertime(act.isTagOT()==true? 1 : 0);
			time = TimeSheets.save(time);
		}else{
			time = act.getEmployeeTimeSheets();
			time.setDateTrans(act.getDateTrans());
			time.setTimeIn(act.getTimeIn());
			time.setTimeOut(act.getTimeOut());
			
			double timein = Time.typeId(act.getTimeIn());
			double timeout = Time.typeId(act.getTimeOut());
			double hrs = timeout - timein;
			time.setCountHour(hrs);
			
			time.setEmployee(act.getEmployee());
			time.setActivityTransactions(act);
			time.setIsOvertime(act.isTagOT()==true? 1 : 0);
			time = TimeSheets.save(time);
		}
		
		//location
		LocationTransactions loc = new LocationTransactions();
		if(act.getLocationTransactions()==null){
			loc.setIsActive(1);
			loc.setDateTrans(act.getDateTrans());
			loc.setActivityTransactions(act);
			loc.setLocation(act.getLocation());
			loc = LocationTransactions.save(loc);
		}else{
			loc = act.getLocationTransactions();
			loc.setIsActive(1);
			loc.setDateTrans(act.getDateTrans());
			loc.setActivityTransactions(act);
			loc.setLocation(act.getLocation());
			loc = LocationTransactions.save(loc);
		}
		
		List<MaterialTransactions> mats = new ArrayList<MaterialTransactions>();
		String[] params = new String[1];
		String sql = " AND mt.isactivematrans=1 AND act.actransid=?";
		params[0] = act.getId()+"";
		mats = MaterialTransactions.retrieve(sql, params);
		for(MaterialTransactions tr : mats){
			tr.delete("DELETE FROM materialtrans WHERE matransid=?", params);
		}
		
		mats = new ArrayList<MaterialTransactions>();
		if(act.getMaterialTransactions()!=null){
			for(MaterialTransactions tr : act.getMaterialTransactions()){
				tr.setActivityTransactions(act);
				tr = MaterialTransactions.save(tr);
				mats.add(tr);
			}
		}
		timeSheets.get(act.getIndex()).setId(act.getId());
		timeSheets.get(act.getIndex()).setEmployeeTimeSheets(time);
		timeSheets.get(act.getIndex()).setEmployee(time.getEmployee());
		timeSheets.get(act.getIndex()).setLocationTransactions(loc);
		timeSheets.get(act.getIndex()).setLocation(loc.getLocation());
		timeSheets.get(act.getIndex()).setMaterialTransactions(mats);
	}
	
	public List<String> suggestedName(String query){
		String sql = "SELECT fullname FROM employee WHERE fullname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Employee.retriveNames(sql);
		return result;
	}
	
	public void loadEnployeeSheets(){
		
		List<ActivityTransactions> tempSheets = new ArrayList<ActivityTransactions>();
		tempSheets = timeSheets;
		timeSheets = new ArrayList<ActivityTransactions>();
		
		String sql = " AND tme.isactivetime=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getTimeSheetFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getTimeSheetTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND emp.fullname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		int index = 0;
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			
			sql = " AND trn.isactiveactrans=1 AND (trn.actDateTrans>=? AND trn.actDateTrans<=?) AND trn.loads is null AND trn.actransid=?";
			params = new String[3];
			params[0] = DateUtils.convertDate(getTimeSheetFrom(), "yyyy-MM-dd");
			params[1] = DateUtils.convertDate(getTimeSheetTo(), "yyyy-MM-dd");
			params[2] = time.getActivityTransactions().getId()+"";
			List<ActivityTransactions> acts = ActivityTransactions.retrieve(sql, params);
			
			if(acts.size()>0){
				ActivityTransactions act = acts.get(0);
			
			act.setEmployee(time.getEmployee());
			act.setIndex(index++);
			sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
			params = new String[1];
			params[0] = act.getId()+"";
			List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
			if(locs.size()>0){
				act.setLocation(locs.get(0).getLocation());
				act.setLocationTransactions(locs.get(0));
			}
			
			sql = " AND mt.isactivematrans=1 AND act.actransid=?";
			params[0] = act.getId()+"";
			act.setMaterialTransactions(MaterialTransactions.retrieve(sql, params));
			String materials = "Select Materials"; 
			int cnt=1;
			int size = act.getMaterialTransactions().size();
			for(MaterialTransactions nm : act.getMaterialTransactions()){
				if(cnt==1 && size>1){
					materials = nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName() + ",";
				}else if(cnt==1 && size==1){
					materials = nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName();
				}else if(cnt==size){
					materials += nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName();
				}else{
					materials += nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName() + ",";
				}
				cnt++;
			}
			act.setEmployeeTimeSheets(time);
			act.setMaterialUsed(materials);
			act.setTimeIn(time.getTimeIn());
			act.setTimeOut(time.getTimeOut());
			act.setTagOT(time.getIsOvertime()==1? true : false);
			
			act.setTimeInId(Time.typeId(time.getTimeIn()));
			act.setTimeIns(getTimeInList());
			act.setTimeOutId(Time.typeId(time.getTimeOut()));
			act.setTimeOuts(getTimeOutList());
			
			
			timeSheets.add(act);
			
			}
		}
		
		if(tempSheets!=null && tempSheets.size()>0){
			//int addedIndex = timeSheets.size();
			for(ActivityTransactions ac : tempSheets){
				if(ac.getId()==0){
					ac.setIndex(index++);
					timeSheets.add(ac);
				}
			}
			
		}
		
	}
	
	public void clearData(){
		
		setTimeSheetsData(null);
		setTimeInId(GlobalVar.DEFAULT_START_TIME);
		setTimeOutId(GlobalVar.DEFAULT_END_TIME);
	}
	
	public void addNew(){
		System.out.println("adding row......");
		
		
		
		if(timeSheets!=null){
			int index = timeSheets.size();
			timeSheets.add(createModelActivity(index));
		}else{
			timeSheets = new ArrayList<ActivityTransactions>();
			timeSheets.add(createModelActivity(0));
		}
		
	}
	
	public void deleteRow(ActivityTransactions ac){
		if(ac.getDrums()!=null){
			Application.addMessage(3, "This timesheet cannot be deleted. This timesheet was created in Sprayman Activity Page", "");
		}else{
			timeSheets.remove(ac);
			try{
			ac.delete();
			
			LocationTransactions loc = ac.getLocationTransactions();
			loc.delete();
			
			List<MaterialTransactions> mats = ac.getMaterialTransactions();
			for(MaterialTransactions mat : mats){
				mat.delete();
			}
			
			TimeSheets tme = ac.getEmployeeTimeSheets();
			tme.delete();
			
			}catch(Exception e){}	
			
			List<ActivityTransactions> dataSheets = new ArrayList<ActivityTransactions>();
			int index =0;
			for(ActivityTransactions acs : timeSheets){
				acs.setIndex(index);
				dataSheets.add(acs);
				index++;
			}
			timeSheets = new ArrayList<ActivityTransactions>();
			timeSheets = dataSheets;
		}
		
	}
	
	public void updateSheets(ActivityTransactions ac){
		int index =ac.getIndex();
		timeSheets.get(index).setTagOT(ac.isTagOT());
		System.out.println("Check OT : " + timeSheets.get(index).isTagOT());
	}
	
	public void assignedIndex(int index){
		System.out.println("assigning index " + index);
		setIndex(index);
		
		setSearchEmployee(null);
		setSearchActivity(null);
		setSearchLocation(null);
		setSearchMaterial(null);
		loadEmployee();
		loadActivities();
		loadArea();
		loadMaterials();
	}
	
	private ActivityTransactions createModelActivity(int index){
		
		ActivityTransactions act = new ActivityTransactions();
		act.setIndex(index);
		act.setDateTrans(getDateTrans());
		Activity actname = new Activity();
		actname.setName("Select Activity");
		act.setActivity(actname);
		act.setLoads(null);
		act.setDrums(null);
		act.setBlocks("0");
		act.setCuts("0");
		act.setRemarks("remarks");
		act.setIsActive(1);
		act.setUserDtls(new UserDtls());
		act.setTimeIn("06:00 AM");
		act.setTimeOut("02:00 PM");
		
		Location loc = new Location();
		loc.setId(0);
		loc.setName("Select Area");
		act.setLocation(loc);
		
		Employee em = new Employee();
		em.setFullName("Select Laborer");
		act.setEmployee(em);
		
		act.setMaterialUsed("Select Material");
		
		act.setIsActive(1);
		
		act.setTimeInId(GlobalVar.DEFAULT_START_TIME);
		act.setTimeIns(getTimeInList());
		act.setTimeOutId(GlobalVar.DEFAULT_END_TIME);
		act.setTimeOuts(getTimeOutList());
		
		return act;
	}
	
	private ActivityTransactions replicateActivity(int index, ActivityTransactions copyAct){
		
		ActivityTransactions act = new ActivityTransactions();
		act.setIndex(index);
		act.setDateTrans(DateUtils.convertDate(getNewDateReplicate(), "yyyy-MM-dd"));
		Activity actname = new Activity();
		actname.setName(copyAct.getActivity().getName());
		act.setActivity(copyAct.getActivity());
		act.setLoads(copyAct.getLoads());
		act.setDrums(copyAct.getDrums());
		act.setBlocks(copyAct.getBlocks());
		act.setCuts(copyAct.getCuts());
		act.setRemarks(copyAct.getRemarks());
		act.setIsActive(1);
		act.setUserDtls(getUser());
		act.setTimeIn(copyAct.getTimeIn());
		act.setTimeOut(copyAct.getTimeOut());
		
		Location loc = new Location();
		loc.setId(copyAct.getLocation().getId());
		loc.setName(copyAct.getLocation().getName());
		act.setLocation(copyAct.getLocation());
		
		Employee em = new Employee();
		em.setFullName(copyAct.getEmployee().getFullName());
		act.setEmployee(copyAct.getEmployee());
		
		//act.setMaterialUsed("Select Material");
		
		act.setIsActive(1);
		return act;
	}
	
	
	public void copyPaste(ActivityTransactions ac){
		if(ac.getDrums()!=null){
			Application.addMessage(3, "Timesheet cannot be copied. This timesheet was created in Sprayman Activity Page", "");
		}else{
			ActivityTransactions tr = new ActivityTransactions();
			int index = timeSheets.size();
			tr.setIndex(index);
			tr.setDateTrans(ac.getDateTrans());
			tr.setEmployee(ac.getEmployee());
			tr.setActivity(ac.getActivity());
			tr.setLocation(ac.getLocation());
			tr.setTimeIn(ac.getTimeIn());
			tr.setTimeOut(ac.getTimeOut());
			tr.setBlocks(ac.getBlocks());
			tr.setCuts(ac.getCuts());
			tr.setRemarks(ac.getRemarks());
			tr.setIsActive(1);
			tr.setUserDtls(ac.getUserDtls());
			tr.setMaterialUsed(ac.getMaterialUsed());
			tr.setMaterialTransactions(ac.getMaterialTransactions());
			tr.setTagOT(ac.isTagOT());
			
			tr.setTimeInId(Time.typeId(ac.getTimeIn()));
			tr.setTimeIns(getTimeInList());
			tr.setTimeOutId(Time.typeId(ac.getTimeOut()));
			tr.setTimeOuts(getTimeOutList());
			
			
			timeSheets.add(tr);
		}
	}
	
	public void onCellEdit(CellEditEvent event) {
		
		 Object oldValue = event.getOldValue();
	     Object newValue = event.getNewValue();
	     
	     System.out.println("old Value: " + oldValue);
	     System.out.println("new Value: " + newValue);
	     
	     System.out.println("index " + event.getRowIndex());
	     
	     int index = event.getRowIndex();
	     timeSheets.get(index).setIndex(index);
	     
	     if(getTimeInId()>0){
	    	 double in = timeSheets.get(index).getTimeInId();
	    	 timeSheets.get(index).setTimeIn(Time.typeName(in));
	     }
	     
	     if(getTimeOutId()>0){
	    	 double out = timeSheets.get(index).getTimeOutId();
	    	 timeSheets.get(index).setTimeOut(Time.typeName(out));
	     }
		
	}
	
	public List<ActivityTransactions> getTimeSheets() {
		return timeSheets;
	}
	public void setTimeSheets(List<ActivityTransactions> timeSheets) {
		this.timeSheets = timeSheets;
	}
	public ActivityTransactions getTimeSheetsData() {
		return timeSheetsData;
	}
	public void setTimeSheetsData(ActivityTransactions timeSheetsData) {
		this.timeSheetsData = timeSheetsData;
	}
	
	public void selectedActivity(Activity ac){
		timeSheets.get(getIndex()).setActivity(ac);
	}
	
	public List<String> suggestedActivity(String query){
		String sql = "SELECT acname FROM activity WHERE acname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Activity.retriveNames(sql);
		return result;
	}
	
	public void loadActivities() {
		
		activitySelection = new ArrayList<>();
		
		String sql = "SELECT * FROM activity WHERE isactiveac=1 ORDER BY acname";
		if(getSearchActivity()!=null && !getSearchActivity().isEmpty()){
			sql = "SELECT * FROM activity WHERE isactiveac=1 AND acname like '%"+ getSearchActivity().replace("--", "") +"%' ORDER BY acname";
			activitySelection = Activity.retrieve(sql, new String[0]);
		}else {
			sql += " LIMIT 12";
			activitySelection = Activity.retrieve(sql, new String[0]);
		}
		
		
		
	}
	
	
	public String getSearchMaterial() {
		return searchMaterial;
	}

	public void setSearchMaterial(String searchMaterial) {
		this.searchMaterial = searchMaterial;
	}
	
	public void laborMaterial(ActivityTransactions act){
		setIndex(act.getIndex());
		List<MaterialTransactions> mat = act.getMaterialTransactions();
		if(mat.size()>0){
			setMaterialSelected(mat);
		}else{
			materialSelected = new ArrayList<MaterialTransactions>();
		}
	}
	
	public void selectedMaterial(Materials mat){
		
		boolean isOk = true;
		
		if(getTmpUomId()==0){
			isOk = false;
			Application.addMessage(3, "Please select measurement", "");
		}
		
		if(getHoldQty()>0 && isOk){
			MaterialTransactions mata = new MaterialTransactions();
			mata.setDateTrans(getDateTrans());
			mata.setIsActive(1);
			mata.setMaterials(mat);
			mata.setUom(getUomMap().get(getTmpUomId()));
			mata.setQty(getHoldQty());
			
			if(getMaterialSelected()!=null){
				materialSelected.remove(mata);
				materialSelected.add(mata);
			}else{
				materialSelected.add(mata);
			}
			
			String materials = "Select Material";
			int cnt = 1;
			int size = getMaterialSelected().size();
			for(MaterialTransactions nm : getMaterialSelected()){
				if(cnt==1 && size>1){
					materials = nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName() + ",";
				}else if(cnt==1 && size==1){
					materials = nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName();
				}else if(cnt==size){
					materials += nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName();
				}else{
					materials += nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName() + ",";
				}
				cnt++;
			}
			
			timeSheets.get(getIndex()).setMaterialUsed(materials);
			timeSheets.get(getIndex()).setMaterialTransactions(materialSelected);
			
			materialSelected = new ArrayList<MaterialTransactions>();
			setQuantity(0);
			setHoldQty(0);
			setUomId(0);
			uomMap = new HashMap<Integer, UOM>();
			getUoms();
			Application.addMessage(1, "Successfully added " + mat.getName(), "");
		}else{
			Application.addMessage(3, "Please provide material quantity", "");
		}
	}
	
	public void showSelectedMaterial(ActivityTransactions act){
		setIndex(act.getIndex());
		List<MaterialTransactions> mats = act.getMaterialTransactions();
		materialSelected = new ArrayList<MaterialTransactions>();
		materialSelected = mats;
	}
	
	public void removeSelectedMaterial(MaterialTransactions mat){
		materialSelected.remove(mat);
		try{mat.delete();}catch(Exception e){}
		String materials = "Select Material";
		int cnt = 1;
		int size = getMaterialSelected().size();
		for(MaterialTransactions nm : getMaterialSelected()){
			if(cnt==1 && size>1){
				materials = nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName() + ",";
			}else if(cnt==1 && size==1){
				materials = nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName();
			}else if(cnt==size){
				materials += nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName();
			}else{
				materials += nm.getQty() + " " + nm.getUom().getSymbol() + " of " + nm.getMaterials().getName() + ",";
			}
			cnt++;
		}
		
		timeSheets.get(getIndex()).setMaterialUsed(materials);
		timeSheets.get(getIndex()).setMaterialTransactions(materialSelected);
	}
	
	public List<String> suggestedMaterials(String query){
		String sql = "SELECT matname FROM materials WHERE matname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Materials.retriveNames(sql);
		return result;
	}
	
	public void loadMaterials() {
		
		materialSelection = new ArrayList<Materials>(); 
		
		String sql = "SELECT * FROM materials WHERE isactivemat=1 ORDER BY matname";
		if(getSearchMaterial()!=null && !getSearchMaterial().isEmpty()){
			sql = "SELECT * FROM materials  WHERE isactivemat=1 AND matname like '%"+ getSearchMaterial().replace("--", "") +"%'";
			materialSelection = Materials.retrieve(sql, new String[0]);
		}
		
			
	}
	
	public void inputedQty(){
		setHoldQty(getQuantity());
		setTmpUomId(getUomId());
	}
	
	public List getUoms() {
		
		uoms = new ArrayList<>();
		uomMap = new HashMap<Integer, UOM>();
		uoms.add(new SelectItem(0, "Select"));
		for(UOM u : UOM.retrieve("SELECT * FROM uom WHERE isactiveuom=1", new String[0])){
			uoms.add(new SelectItem(u.getId(), u.getSymbol()));
			uomMap.put(u.getId(), u);
		}
		
		
		return uoms;
	}

	public void setUoms(List uoms) {
		this.uoms = uoms;
	}

	public int getUomId() {
		return uomId;
	}

	public void setUomId(int uomId) {
		this.uomId = uomId;
	}

	public Map<Integer, UOM> getUomMap() {
		return uomMap;
	}

	public void setUomMap(Map<Integer, UOM> uomMap) {
		this.uomMap = uomMap;
	}
	
	public List getTimeIns() {
		
		timeIns = new ArrayList<>();
		timeInMap = new HashMap<Double, String>();
		
		for(Time time : Time.values()){
			if(time.getId()>=6){
			timeIns.add(new SelectItem(time.getId(), time.getValue()));
			timeInMap.put(time.getId(), time.getValue());
			}
		}
		
		
		
		return timeIns;
	}

	public void setTimeIns(List timeIns) {
		this.timeIns = timeIns;
	}

	public double getTimeInId() {
		if(timeInId==0){
			timeInId = GlobalVar.DEFAULT_START_TIME;
		}
		return timeInId;
	}

	public void setTimeInId(double timeInId) {
		this.timeInId = timeInId;
	}

	public List getTimeOuts() {
		
		timeOuts = new ArrayList<>();
		timeOutMap = new HashMap<Double, String>();
		
		for(Time time : Time.values()){
			if(time.getId()>=6){
			timeOuts.add(new SelectItem(time.getId(), time.getValue()));
			timeOutMap.put(time.getId(), time.getValue());
			}
		}
		
		return timeOuts;
	}

	public void setTimeOuts(List timeOuts) {
		this.timeOuts = timeOuts;
	}

	public double getTimeOutId() {
		if(timeOutId==0){
			timeOutId = GlobalVar.DEFAULT_END_TIME;
		}
		return timeOutId;
	}

	public void setTimeOutId(double timeOutId) {
		this.timeOutId = timeOutId;
	}
	
	public String getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public List<Activity> getActivitySelection() {
		return activitySelection;
	}

	public void setActivitySelection(List<Activity> activitySelection) {
		this.activitySelection = activitySelection;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSearchActivity() {
		return searchActivity;
	}

	public void setSearchActivity(String searchActivity) {
		this.searchActivity = searchActivity;
	}
	
	public void selectedArea(Location loc){
		timeSheets.get(getIndex()).setLocation(loc);
	}
	
	public List<String> suggestedArea(String query){
		String sql = "SELECT locname FROM locations WHERE locname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Location.retriveNames(sql);
		return result;
	}
	
	public void loadArea(){
		loctionsSelection = new ArrayList<Location>();
		
		String sql = " AND lc.isactiveloc=1 ORDER BY lc.locname";
		if(getSearchLocation()!=null && !getSearchLocation().isEmpty()){
			sql = " AND lc.isactiveloc=1 AND lc.locname like '%"+ getSearchLocation().replace("--", "") +"%' ORDER BY lc.locname";
			loctionsSelection = Location.retrieve(sql, new String[0]);
		}else {
			sql += " LIMIT 12";
			loctionsSelection = Location.retrieve(sql, new String[0]);
		}
		
			
		
	}
	
	public String getSearchEmployee() {
		return searchEmployee;
	}

	public void setSearchEmployee(String searchEmployee) {
		this.searchEmployee = searchEmployee;
	}

	public List<Employee> getEmployeeSelection() {
		return employeeSelection;
	}

	public void setEmployeeSelection(List<Employee> employeeSelection) {
		this.employeeSelection = employeeSelection;
	}
	
	public void selectedLabor(Employee em){
		
		boolean isOk = Application.checkingDuplicateRecordedForTheSameTime(null, em, getDateTrans(), getTimeInMap().get(getTimeInId()), getTimeOutMap().get(getTimeOutId()));
		
		if(isOk){
		
		timeSheets.get(getIndex()).setEmployee(em);
		
		}else{
			Application.addMessage(3, "Employee name " + em.getFullName() + " has already recorded for this day and time", "");
		}
		
	}
	
	public void loadEmployee(){
		employeeSelection = new ArrayList<Employee>();
		
		String sql = " AND emp.isactiveemp=1";
		String[] params = new String[0];
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()){
			sql += " AND emp.fullname like '%"+ getSearchEmployee().replace("--", "") +"%'";
			//employeeSelection = Employee.retrieve(sql, params);
			
			for(Employee emp : Employee.retrieve(sql, params)){
				try{emp.setTimeRecordedInfo(Application.checkRecordedTime(emp, timeSheets.get(getIndex()).getDateTrans()));}catch(Exception e){
					emp.setTimeRecordedInfo(null);
				}
				employeeSelection.add(emp);
			}
			
		}else {
			sql += " LIMIT 12";
			for(Employee emp : Employee.retrieve(sql, params)){
				try{emp.setTimeRecordedInfo(Application.checkRecordedTime(emp, timeSheets.get(getIndex()).getDateTrans()));}catch(Exception e){}
				employeeSelection.add(emp);
			}
		}
		
		
		
	}

	public String getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(String searchLocation) {
		this.searchLocation = searchLocation;
	}

	public List<Location> getLoctionsSelection() {
		return loctionsSelection;
	}

	public void setLoctionsSelection(List<Location> loctionsSelection) {
		this.loctionsSelection = loctionsSelection;
	}

	public Map<Double, String> getTimeInMap() {
		return timeInMap;
	}

	public void setTimeInMap(Map<Double, String> timeInMap) {
		this.timeInMap = timeInMap;
	}

	public Map<Double, String> getTimeOutMap() {
		return timeOutMap;
	}

	public void setTimeOutMap(Map<Double, String> timeOutMap) {
		this.timeOutMap = timeOutMap;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getHoldQty() {
		return holdQty;
	}

	public void setHoldQty(double holdQty) {
		this.holdQty = holdQty;
	}

	public int getTmpUomId() {
		return tmpUomId;
	}

	public void setTmpUomId(int tmpUomId) {
		this.tmpUomId = tmpUomId;
	}

	public List<Materials> getMaterialSelection() {
		return materialSelection;
	}

	public void setMaterialSelection(List<Materials> materialSelection) {
		this.materialSelection = materialSelection;
	}

	public List<MaterialTransactions> getMaterialSelected() {
		return materialSelected;
	}

	public void setMaterialSelected(List<MaterialTransactions> materialSelected) {
		this.materialSelected = materialSelected;
	}
	
	public Date getTimeSheetFrom() {
		if(timeSheetFrom==null){
			timeSheetFrom = DateUtils.getDateToday();
		}
		return timeSheetFrom;
	}

	public void setTimeSheetFrom(Date timeSheetFrom) {
		this.timeSheetFrom = timeSheetFrom;
	}

	public Date getTimeSheetTo() {
		if(timeSheetTo==null){
			timeSheetTo = DateUtils.getDateToday();
		}
		return timeSheetTo;
	}

	public void setTimeSheetTo(Date timeSheetTo) {
		this.timeSheetTo = timeSheetTo;
	}

	public String getSearchDescription() {
		return searchDescription;
	}

	public void setSearchDescription(String searchDescription) {
		this.searchDescription = searchDescription;
	}

	public List<ActivityTransactions> getReplicateSelectedData() {
		return replicateSelectedData;
	}

	public void setReplicateSelectedData(List<ActivityTransactions> replicateSelectedData) {
		this.replicateSelectedData = replicateSelectedData;
	}

	public Date getNewDateReplicate() {
		return newDateReplicate;
	}

	public void setNewDateReplicate(Date newDateReplicate) {
		this.newDateReplicate = newDateReplicate;
	}
}
























