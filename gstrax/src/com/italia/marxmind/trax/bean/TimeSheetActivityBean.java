package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.PrimeFaces;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Activity;
import com.italia.marxmind.trax.controller.ActivityTransactions;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.Job;
import com.italia.marxmind.trax.controller.Location;
import com.italia.marxmind.trax.controller.LocationTransactions;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.controller.MaterialTransactions;
import com.italia.marxmind.trax.controller.Materials;
import com.italia.marxmind.trax.controller.TimeSheets;
import com.italia.marxmind.trax.controller.UOM;
import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.enm.JobTitle;
import com.italia.marxmind.trax.enm.Time;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.utils.Currency;
import com.italia.marxmind.trax.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 08/22/2017
 *@version 1.0
 */
@ManagedBean(name="atimeBean", eager=true)
@ViewScoped
public class TimeSheetActivityBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6897781L;
	
	private Date dateTrans;
	private String drums;
	private String loads;
	private String blocks;
	private String cuts;
	private String remarks;
	
	private List timeIns;
	private double timeInId;
	
	private List timeOuts;
	private double timeOutId;
	
	private Map<Double, String> timeInMap = new HashMap<Double, String>();
	private Map<Double, String> timeOutMap = new HashMap<Double, String>();
	
	private List activities;
	private int activityId;
	private Map<Integer, Activity> activityMap = new HashMap<Integer, Activity>();
	
	private String searchMaterial;
	private List<Materials> materialSelection = new ArrayList<Materials>();
	private List<MaterialTransactions> materialSelected = new ArrayList<MaterialTransactions>();
	
	private List locations;
	private int locationId;
	private Map<Integer, Location> locationMap = new HashMap<Integer, Location>();
	
	private String searchEmployee;
	private List<Employee> employeeSelection = new ArrayList<Employee>();
	private List<Employee> employeeSelected = new ArrayList<Employee>();
	
	private List uoms;
	private int uomId;
	private Map<Integer, UOM> uomMap = new HashMap<Integer, UOM>();
	
	private double quantity;
	private double holdQty;
	private int tmpUomId;
	
	private Date timeSheetFrom;
	private Date timeSheetTo;
	private ActivityTransactions activeData;
	private List<ActivityTransactions> activitiesData = new ArrayList<ActivityTransactions>();
	
	private final static double HOUR_IN_DAY = Double.valueOf(ReadConfig.value(Gstrax.NORMAL_RENDERED_HOURS));//8.0;
	private final static double DRIVER_OT = 40.0;
	private final static double LEAD_SPRAY_MAN_OT = 35.0;
	private final static double NORMAL_OT = 35.0;
	private final static double LABOR_SPRAYMAN_DAILY_RATE = Double.valueOf(ReadConfig.value(Gstrax.LABOR_SPRAYMAN_DAILY_RATE));
	private final static double LABOR_HARVESTER_DAILY_RATE = Double.valueOf(ReadConfig.value(Gstrax.LABOR_HARVESTER_DAILY_RATE));
	private final static double OVERTIME_START = Double.valueOf(ReadConfig.value(Gstrax.OTSTART)); //14.5;//start at 2:30PM
	
	private final static double DRIVER_NORMAL_PER_DRUM_RATE = Double.valueOf(ReadConfig.value(Gstrax.DRIVER_NORMAL_PER_DRUM_RATE));
	private final static int PER_DRUM_DIVIDER = Integer.valueOf(ReadConfig.value(Gstrax.PER_DRUM_DIVIDER));
	private final static double FIELD_SPRAY_DRIVER_RATE = Double.valueOf(ReadConfig.value(Gstrax.FIELD_SPRAY_DRIVER));
	
	private final static String FORCING1 = ReadConfig.value(Gstrax.FORCING1);
	private final static String FORCING2 = ReadConfig.value(Gstrax.FORCING2);
	private final static double FORCING_RATE_PER_DRUM = Double.valueOf(ReadConfig.value(Gstrax.FORCING_RATE_PER_DRUM));
	private final static double OTHER_SPRAY_RATE_PER_DRUM = Double.valueOf(ReadConfig.value(Gstrax.OTHER_SPRAY_RATE_PER_DRUM));
	
	//use only for init
	private double NUMBER_OF_FORCING_EMPLOYEE=0d;
	
	private String totalActivityExpenses;
	private String totalServices;
	
	private boolean lockButton;
	
	private ExcelOptions excelOptions;
	private PDFOptions pdfotions;
	
	private Map<Integer, Job> jobs = new LinkedHashMap<Integer, Job>();
	
	@PostConstruct
	public void init(){
		System.out.println("---------INIT------");
		loadOtherComponents();
		loadPositions();//do not interchange this line of codes
		loadActivities();	
	}
	
	private void loadOtherComponents() {
		
		uoms = new ArrayList<>();
		uomMap = new HashMap<Integer, UOM>();
		uoms.add(new SelectItem(0, "Select"));
		for(UOM u : UOM.retrieve("SELECT * FROM uom WHERE isactiveuom=1", new String[0])){
			uoms.add(new SelectItem(u.getId(), u.getSymbol()));
			uomMap.put(u.getId(), u);
		}
		
		timeIns = new ArrayList<>();
		timeInMap = new HashMap<Double, String>();
		
		for(Time time : Time.values()){
			timeIns.add(new SelectItem(time.getId(), time.getValue()));
			timeInMap.put(time.getId(), time.getValue());
		}
		
		timeOuts = new ArrayList<>();
		timeOutMap = new HashMap<Double, String>();
		
		for(Time time : Time.values()){
			timeOuts.add(new SelectItem(time.getId(), time.getValue()));
			timeOutMap.put(time.getId(), time.getValue());
		}
		
	}
	
	private void loadPositions() {
		jobs = new LinkedHashMap<Integer, Job>();
		String sql = "SELECT * FROM jobtitle WHERE isactivejob=1";
		String[] params = new String[0];
		for(Job job : Job.retrieve(sql, params)) {
			jobs.put(job.getJobid(), job);
		}
	}
	
	public void loadActivities(){
		activitiesData = new ArrayList<ActivityTransactions>();
		
		String sql = " AND trn.isactiveactrans=1 AND (trn.actDateTrans>=? AND trn.actDateTrans<=?) AND trn.loads is not null";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getTimeSheetFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getTimeSheetTo(), "yyyy-MM-dd");
		
		double totalActivity = 0d;
		for(ActivityTransactions act : ActivityTransactions.retrieve(sql, params)){
			
		
		sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
		params = new String[1];
		params[0] = act.getId()+"";
		LocationTransactions loc = LocationTransactions.retrieve(sql, params).get(0);
		act.setLocationTransactions(loc);
		
		sql = " AND mt.isactivematrans=1 AND act.actransid=?";
		params = new String[1];
		params[0] = act.getId()+"";
		act.setMaterialTransactions(MaterialTransactions.retrieve(sql, params));
		
		 
		sql = " AND tme.isactivetime=1 AND ac.actransid=?";
		params = new String[1];
		params[0] = act.getId()+"";
		List<TimeSheets> times = TimeSheets.retrieve(sql, params);
		double totalExpenses=0d;
		if(times.size()>0){
			act.setTimeIn(times.get(0).getTimeIn());
			act.setTimeOut(times.get(0).getTimeOut());
			act.setTotalHours(times.get(0).getCountHour());
			act.setTotalMandays(times.size());
			NUMBER_OF_FORCING_EMPLOYEE = times.size();
			for(TimeSheets time : times){
				//double perHour = time.getEmployee().getSalary() / HOUR_IN_DAY;
				//totalExpenses += perHour * act.getTotalHours();
				
				totalExpenses += calculateSalary(time,act);
				//System.out.println("Employee services : " + totalExpenses);
				
			}
			totalActivity +=totalExpenses;
		}
		act.setTotalExpenses(Currency.formatAmount(totalExpenses));
		act.setTimeSheets(times);
		
		activitiesData.add(act);
		
		}
		setTotalActivityExpenses(Currency.formatAmount(totalActivity));
		Collections.reverse(activitiesData);
		
	}
	
	public void clickItem(ActivityTransactions ac){
		
		clear();
		setActiveData(ac);
		
		if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(ac.getDateTrans())){
			setLockButton(false);
		}else{
			setLockButton(true);
		}
		
		setDateTrans(DateUtils.convertDateString(ac.getDateTrans(),"yyyy-MM-dd"));
		setActivityId(ac.getActivity().getId());
		setDrums(ac.getDrums());
		setLoads(ac.getLoads());
		setBlocks(ac.getBlocks());
		setCuts(ac.getCuts());
		setRemarks(ac.getRemarks());
		
		setTimeInId(Time.typeId(ac.getTimeIn()));
		setTimeOutId(Time.typeId(ac.getTimeOut()));
		
		setLocationId(ac.getLocationTransactions().getLocation().getId());
		
		materialSelected = ac.getMaterialTransactions();
		NUMBER_OF_FORCING_EMPLOYEE = ac.getTimeSheets().size();
		for(TimeSheets time : ac.getTimeSheets()){
			Employee emp = time.getEmployee();
			Job job = Job.job(emp.getJob().getJobid());
			emp.setJob(job);
			emp = calculateSalary(emp);
			employeeSelected.add(emp);
		}
		recalculateServices();
	}
	
	public void deleteRow(ActivityTransactions ac){
		if(ac.getDrums()!=null){
			
			try{
			ac.delete();
			
			LocationTransactions loc = ac.getLocationTransactions();
			loc.delete();
			
			List<MaterialTransactions> mats = ac.getMaterialTransactions();
			for(MaterialTransactions mat : mats){
				mat.delete();
			}
			
			List<TimeSheets> times = ac.getTimeSheets();
			for(TimeSheets time : times){
				time.delete();
			}
			}catch(Exception e){}
			
			loadActivities();
			Application.addMessage(1, "Successfully deleted.", "");
		}else{
			Application.addMessage(3, "This timesheet cannot be deleted. This timesheet was created in Field Activity Page.", "");
		}
	}
	
	public void saveData(){
		System.out.println("-----saving-----");
		boolean isOk = true;
		
		//checking duplicate per employee
		if(getEmployeeSelected()!=null && getEmployeeSelected().size()>0){
			
			if(getActiveData()!=null){
				for(Employee em : getEmployeeSelected()){
					boolean isValid = Application.checkingDuplicateRecordedForTheSameTime(getActiveData(), em, DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"), getTimeInMap().get(getTimeInId()), getTimeOutMap().get(getTimeOutId()));
					
					if(!isValid){
						Application.addMessage(3, "Employee name " + em.getFullName() +" has been recorded already for this day and time", "");
						isOk = false;
					}
				}
			}else{
				for(Employee em : getEmployeeSelected()){
					boolean isValid = Application.checkingDuplicateRecordedForTheSameTime(null, em, DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"), getTimeInMap().get(getTimeInId()), getTimeOutMap().get(getTimeOutId()));
					
					if(isValid){
						if(!isValid){
							Application.addMessage(3, "Employee name " + em.getFullName() +" has been recorded already for this day and time", "");
							isOk = false;
						}
					}
				}
			}
		}
		
		
		
		if(getActiveData()!=null){
			if(getActiveData().getDrums()==null){
				isOk = false;
				Application.addMessage(3, "This activity can be only modify in Field Activity Recording page.", "");
			}
			
		}else{
		
		if(getDrums()==null || getDrums().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide number of drums", "");
		}
		
		if(getLoads()==null || getLoads().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide number of loads", "");
		}
		
		}
		
		if(isOk){
		
		ActivityTransactions act = new ActivityTransactions();
		LocationTransactions loc = new LocationTransactions();
		List<MaterialTransactions> mats = new ArrayList<MaterialTransactions>();
		List<TimeSheets> times = new ArrayList<TimeSheets>();
		
		
		
		if(getActiveData()!=null){
			act = getActiveData();
			
			String sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
			String[] params = new String[1];
			params[0] = act.getId()+"";
			loc = LocationTransactions.retrieve(sql, params).get(0);
			
			sql = " AND mt.isactivematrans=1 AND act.actransid=?";
			params[0] = act.getId()+"";
			mats = MaterialTransactions.retrieve(sql, params);
			for(MaterialTransactions tr : mats){
				tr.delete("DELETE FROM materialtrans WHERE matransid=?", params);
			}
			
			sql = " AND tme.isactivetime=1 AND ac.actransid=?";
			params[0] = act.getId()+"";
			times = TimeSheets.retrieve(sql, params);
			for(TimeSheets time : times){
				time.delete("DELETE FROM timesheets WHERE actransid=?", params);
			}
			
		}else{
			act.setIsActive(1);
			loc.setIsActive(1);
		}
		
		act.setDateTrans(DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"));
		act.setActivity(getActivityMap().get(getActivityId()));
		act.setDrums(getDrums());
		act.setLoads(getLoads());
		act.setBlocks(getBlocks());
		act.setCuts(getCuts());
		act.setRemarks(getRemarks());
		act.setUserDtls(Login.getUserLogin().getUserDtls());
		act = ActivityTransactions.save(act);
		
		loc.setDateTrans(DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"));
		loc.setLocation(getLocationMap().get(getLocationId()));
		loc.setActivityTransactions(act);
		
		loc = LocationTransactions.save(loc);
		
		mats = new ArrayList<MaterialTransactions>();
		for(MaterialTransactions tr : getMaterialSelected()){
			tr.setActivityTransactions(act);
			tr = MaterialTransactions.save(tr);
			mats.add(tr);
		}
		
		times = new ArrayList<TimeSheets>();
		System.out.println("Check employee for saving : " + getEmployeeSelected().size());
		for(Employee employee : getEmployeeSelected()){
			
			TimeSheets time = new TimeSheets();
			time.setEmployee(employee);
			time.setDateTrans(DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"));
			time.setIsActive(1);
			time.setTimeIn(getTimeInMap().get(getTimeInId()));
			time.setTimeOut(getTimeOutMap().get(getTimeOutId()));
			
			double renderedHour = getTimeOutId() - getTimeInId();
			
			time.setCountHour(renderedHour);
			time.setActivityTransactions(act);
			time = TimeSheets.save(time);
			times.add(time);
		}
		
		act.setLocationTransactions(loc);
		act.setMaterialTransactions(mats);
		act.setTimeSheets(times);
		setActiveData(act);
		
		Application.addMessage(1, "Successfully saved.", "");
		loadActivities();
		
		
		PrimeFaces pf = PrimeFaces.current();
		pf.executeScript("hideWizard();");
		
		}
		
	}
	
	public void closeActivity(){
		clear();
		loadActivities();
	}
	
	public void reloadEmployeeExpenses(){
		List<Employee> employees = new ArrayList<Employee>();
		if(employeeSelected!=null && employeeSelected.size()>0){
			for(Employee emp : employeeSelected){
				emp = calculateSalary(emp);
				employees.add(emp);
			}
			employeeSelected = employees;
			recalculateServices();
		}
		
	}
	
	public void clear(){
		setDateTrans(null);
		setDrums(null);
		setLoads(null);
		setBlocks(null);
		setCuts(null);
		setRemarks(null);
		
		setTimeInId(6.5);
		setTimeOutId(14.5);
		
		setActivityId(0);
		setLocationId(0);
		setActivityMap(null);
		setLocationMap(null);
		
		setEmployeeSelection(null);
		setEmployeeSelected(null);
		
		setMaterialSelection(null);
		setMaterialSelected(null);
		
		setQuantity(0);
		setTmpUomId(0);
		
		setActiveData(null);
		
		setTotalActivityExpenses(null);
		setTotalServices(null);
		
		setLockButton(false);
		setSearchEmployee(null);
		setSearchMaterial(null);
		
		employeeSelected = new ArrayList<Employee>();
		materialSelected = new ArrayList<MaterialTransactions>();
	}
	
	public List<String> suggestedName(String query){
		String sql = "SELECT fullname FROM employee WHERE fullname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Employee.retriveNames(sql);
		return result;
	}
	
	public void loadEmployee(){
		employeeSelection = new ArrayList<Employee>();
		
		String sql = " AND emp.isactiveemp=1";
		String[] params = new String[0];
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()){
			sql += " AND emp.fullname like '%"+ getSearchEmployee().replace("--", "") +"%'";
			//employeeSelection = Employee.retrieve(sql, params);
			
			for(Employee emp : Employee.retrieve(sql, params)){
				try{emp.setTimeRecordedInfo(Application.checkRecordedTime(emp, DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd")));}catch(Exception e){
					emp.setTimeRecordedInfo(null);
				}
				employeeSelection.add(emp);
			}
			
		}else {
			sql += " LIMIT 12";
			for(Employee emp : Employee.retrieve(sql, params)){
				try{emp.setTimeRecordedInfo(Application.checkRecordedTime(emp, DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd")));}catch(Exception e){}
				employeeSelection.add(emp);
			}
		}
		
	}
	
	private double calculateSalary(TimeSheets time, ActivityTransactions act){
		
		Employee em = time.getEmployee();
		
		double servicesAmnt = 0d;
		double hourlyRate = 0d;
		double dailyRate = 0d;
		double hrsWork = 0d;
		double overTimeAmount = 0d;
		
		try{
			dailyRate = em.getSalary();
			hourlyRate = dailyRate/HOUR_IN_DAY;
			
			Activity activity = Activity.retrieve(act.getActivity().getId());
			String forcing = activity.getName();
			double totalRate = 0d;
			if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
				
				
				double drums = Double.valueOf(act.getDrums());
				totalRate = drums * getJobs().get(em.getJob().getJobid()).getSpraySpecialRate();
				
				double totalEmployee = NUMBER_OF_FORCING_EMPLOYEE;
				//double totalRate = drums * FORCING_RATE_PER_DRUM;
				double salaryPerEmployee = totalRate / totalEmployee;
				
				//hourlyRate = FORCING_RATE_PER_DRUM;
				hourlyRate = totalRate / HOUR_IN_DAY;
				servicesAmnt = salaryPerEmployee;
			
			}else if("Spray Foliar".equalsIgnoreCase(forcing) 
					|| "Spray Fungicide".equalsIgnoreCase(forcing)
					|| "Spray Herbicide".equalsIgnoreCase(forcing)
					|| "Spray Insecticide".equalsIgnoreCase(forcing)
					|| "Spray Foliar Sucker".equalsIgnoreCase(forcing)
					|| "Spray Ripening".equalsIgnoreCase(forcing)) {	
				
				double drums = Double.valueOf(act.getDrums());
				totalRate = drums *  getJobs().get(em.getJob().getJobid()).getSprayRegularRate();
				double totalEmployee = NUMBER_OF_FORCING_EMPLOYEE;
				//double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
				double salaryPerEmployee = totalRate / totalEmployee;
				
				//hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
				hourlyRate = totalRate / HOUR_IN_DAY;
				servicesAmnt = salaryPerEmployee;
				
			}else{
			
			if(JobTitle.DRIVER.getId()==em.getJob().getJobid()){
				
				//change as there is a case where field driver can be use as spray driver
				em.setOvertime(FIELD_SPRAY_DRIVER_RATE);
				//em.setOvertime(getJobs().get(em.getJob().getJobid()).getSpraySpecialRate());
				//overtime
				if("3".equalsIgnoreCase(act.getLoads())){
					
					if("5".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{//normal time
				
					if("5".equalsIgnoreCase(act.getDrums())){
						hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
						servicesAmnt = hourlyRate * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}else{
						
						double timeInAfternoon = Time.typeId(time.getTimeIn());
						double timeOutAfterNoon = Time.typeId(time.getTimeOut());
						hrsWork = time.getCountHour();
						
						if(timeOutAfterNoon>OVERTIME_START){
							servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
							overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
						}else{
							servicesAmnt = hourlyRate * hrsWork;
						}
						
					}
				
				}
				//servicesAmnt = dailyRate;
				servicesAmnt += overTimeAmount;
				
				
			}else if(JobTitle.LEAD_MAN.getId()==em.getJob().getJobid() || JobTitle.SPRAY_MAN.getId()==em.getJob().getJobid()){
				
				
				
				//overtime
				if("3".equalsIgnoreCase(act.getLoads())){
					
					if("5".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
					
					
				}else{//normal time
				
					if("5".equalsIgnoreCase(act.getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}else{
						
						double timeInAfternoon = Time.typeId(time.getTimeIn());
						double timeOutAfterNoon = Time.typeId(time.getTimeOut());
						hrsWork = time.getCountHour();
						
						if(timeOutAfterNoon>OVERTIME_START){
							servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
							overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
						}else{
							servicesAmnt = hourlyRate * hrsWork;
						}
						
						
					}
				
				}
				
				//servicesAmnt = dailyRate;
				servicesAmnt += overTimeAmount;
				
			}else{
				
				//identify labor to sprayman
				if(act.getLoads()!=null && act.getDrums()!=null){
					dailyRate = LABOR_SPRAYMAN_DAILY_RATE;
					hourlyRate = dailyRate/HOUR_IN_DAY;	
				}
				
				//overtime
				if("3".equalsIgnoreCase(act.getLoads())){
					
					if("5".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{
					
					
					if("5".equalsIgnoreCase(act.getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate * 2;
					}else  if("15".equalsIgnoreCase(act.getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}
					
					
				}
				servicesAmnt += overTimeAmount;
			}
				
				
			}
			
			
		}catch(Exception e){}
		
		return servicesAmnt;
	}
	
	private Employee calculateSalary(Employee em){
		
		
		double servicesAmnt = 0d;
		double hourlyRate = 0d;
		double dailyRate = 0d;
		double hrsWork = 0d;
		double overTimeAmount = 0d;
		
		try{
			dailyRate = em.getSalary();
			hourlyRate = dailyRate/HOUR_IN_DAY;
			
			String forcing = getActivityMap().get(getActivityId()).getName();
			double totalRate = 0d;
			if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
				
				double drums = Double.valueOf(getDrums());
				double totalEmployee = NUMBER_OF_FORCING_EMPLOYEE;
				//double totalRate = drums * FORCING_RATE_PER_DRUM;
				totalRate = drums * getJobs().get(em.getJob().getJobid()).getSpraySpecialRate();
				double salaryPerEmployee = totalRate / totalEmployee;
				
				//hourlyRate = FORCING_RATE_PER_DRUM;
				hourlyRate = totalRate / HOUR_IN_DAY;
				servicesAmnt = salaryPerEmployee;
				
			}else if("Spray Foliar".equalsIgnoreCase(forcing) 
					|| "Spray Fungicide".equalsIgnoreCase(forcing)
					|| "Spray Herbicide".equalsIgnoreCase(forcing)
					|| "Spray Insecticide".equalsIgnoreCase(forcing)
					|| "Spray Foliar Sucker".equalsIgnoreCase(forcing)
					|| "Spray Ripening".equalsIgnoreCase(forcing)) {	
				
				double drums = Double.valueOf(getDrums());
				double totalEmployee = NUMBER_OF_FORCING_EMPLOYEE;
				//double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
				totalRate = drums * getJobs().get(em.getJob().getJobid()).getSprayRegularRate();
				double salaryPerEmployee = totalRate / totalEmployee;
				
				//hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
				hourlyRate = totalRate / HOUR_IN_DAY;
				servicesAmnt = salaryPerEmployee;	
				
				
			}else{
			
			if(JobTitle.DRIVER.getId()==em.getJob().getJobid()){
				
				em.setOvertime(FIELD_SPRAY_DRIVER_RATE);//change as there is a case where field driver can be use as spray driver
				
				//overtime
				if("3".equalsIgnoreCase(getLoads())){
					
					if("5".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{
					
						if("5".equalsIgnoreCase(getDrums())){
							hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
							servicesAmnt = DRIVER_NORMAL_PER_DRUM_RATE;
						}else if("10".equalsIgnoreCase(getDrums())){
							hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
							servicesAmnt = DRIVER_NORMAL_PER_DRUM_RATE * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}
					
				
				}
				
				//servicesAmnt = dailyRate;
				servicesAmnt += overTimeAmount;
				
			}else if(JobTitle.LEAD_MAN.getId()==em.getJob().getJobid() || JobTitle.SPRAY_MAN.getId()==em.getJob().getJobid()){
				
				//overtime
				if("3".equalsIgnoreCase(getLoads())){
					
					if("5".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{
				
					if("5".equalsIgnoreCase(getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate * 2;
					}else if("15".equalsIgnoreCase(getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}
				
				}
				//servicesAmnt = dailyRate;
				servicesAmnt += overTimeAmount;
			}else{
			
				//labor sprayman
				//identify labor to sprayman
				if(getLoads()!=null && getDrums()!=null){	
					dailyRate = LABOR_SPRAYMAN_DAILY_RATE;
					hourlyRate = dailyRate/HOUR_IN_DAY;	
					
				}
				
				//overtime
				if("3".equalsIgnoreCase(getLoads())){
					
					if("5".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{
				
					if("5".equalsIgnoreCase(getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate * 2;
					}else  if("15".equalsIgnoreCase(getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}
				
				}
				
				servicesAmnt += overTimeAmount;
			}
			
		}
			
			em.setDailyRate(Currency.formatAmount(dailyRate));
			em.setHourlyRate(Currency.formatAmount(hourlyRate));
			em.setOverTimeRate(Currency.formatAmount(em.getOvertime()));
			
			em.setPayableServices(Currency.formatAmount(servicesAmnt));
			
		}catch(Exception e){}
		
		return em;
	}
	
	public void recalculateServices(){
		double total = 0.0;
		
		for(Employee em : employeeSelected){
			
			double servicesAmnt = 0d;
			double hourlyRate = 0d;
			double dailyRate = 0d;
			double hrsWork = 0d;
			double overTimeAmount = 0d;
			try{
				dailyRate = em.getSalary();
				hourlyRate = dailyRate/HOUR_IN_DAY;
				
				String forcing = "";
				try{forcing = getActivityMap().get(getActivityId()).getName();}catch(Exception e){
					Activity activity = Activity.retrieve(getActivityId());
					forcing = activity.getName();
					System.out.println("Error fall forcing : " + forcing);
				}
				double totalRate = 0d;
				if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
					
					double drums = Double.valueOf(getDrums());
					double totalEmployee = NUMBER_OF_FORCING_EMPLOYEE;
					//double totalRate = drums * FORCING_RATE_PER_DRUM;
					totalRate = drums * getJobs().get(em.getJob().getJobid()).getSpraySpecialRate();
					double salaryPerEmployee = totalRate / totalEmployee;
					
					//hourlyRate = FORCING_RATE_PER_DRUM;
					hourlyRate = totalRate / HOUR_IN_DAY;
					servicesAmnt = salaryPerEmployee;
					
					System.out.println("Pasok sa forcing : " + forcing);
					
				}else if("Spray Foliar".equalsIgnoreCase(forcing) 
						|| "Spray Fungicide".equalsIgnoreCase(forcing)
						|| "Spray Herbicide".equalsIgnoreCase(forcing)
						|| "Spray Insecticide".equalsIgnoreCase(forcing)
						|| "Spray Foliar Sucker".equalsIgnoreCase(forcing)
						|| "Spray Ripening".equalsIgnoreCase(forcing)) {	
					
					double drums = Double.valueOf(getDrums());
					double totalEmployee = NUMBER_OF_FORCING_EMPLOYEE;
					//double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
					totalRate = drums * getJobs().get(em.getJob().getJobid()).getSprayRegularRate();
					double salaryPerEmployee = totalRate / totalEmployee;
					
					//hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
					hourlyRate = totalRate / HOUR_IN_DAY;
					servicesAmnt = salaryPerEmployee;		
					
				}else{
				
				if(JobTitle.DRIVER.getId()==em.getJob().getJobid()){
					
					em.setOvertime(FIELD_SPRAY_DRIVER_RATE);//change as there is a case where field driver can be use as spray driver
					
					//overtime
					if("3".equalsIgnoreCase(getLoads())){
						
						if("5".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 1;
						}else if("10".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 3;
						}else if("20".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 4;
						}else if("25".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 5;
						}else if("30".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 6;
						}else if("35".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 7;
						}else if("40".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 8;
						}else if("45".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 9;
						}else if("50".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 10;
						}else if("55".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 11;
						}else if("60".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 12;
						}else if("65".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 13;
						}else if("70".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 14;
						}else if("75".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 15;
						}else if("80".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 16;
						}
						
					}else{
						
						if("5".equalsIgnoreCase(getDrums())){
							hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
							servicesAmnt = DRIVER_NORMAL_PER_DRUM_RATE;
						}else if("10".equalsIgnoreCase(getDrums())){
							hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
							servicesAmnt = DRIVER_NORMAL_PER_DRUM_RATE * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}
					
					}
					//servicesAmnt = dailyRate;
					servicesAmnt += overTimeAmount;
					
				}else if(JobTitle.LEAD_MAN.getId()==em.getJob().getJobid() || JobTitle.SPRAY_MAN.getId()==em.getJob().getJobid()){
					
					//overtime
					if("3".equalsIgnoreCase(getLoads())){
						
						if("5".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 1;
						}else if("10".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 3;
						}else if("20".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 4;
						}else if("25".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 5;
						}else if("30".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 6;
						}else if("35".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 7;
						}else if("40".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 8;
						}else if("45".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 9;
						}else if("50".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 10;
						}else if("55".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 11;
						}else if("60".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 12;
						}else if("65".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 13;
						}else if("70".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 14;
						}else if("75".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 15;
						}else if("80".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 16;
						}
						
					}else{
						
						if("5".equalsIgnoreCase(getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate;
						}else if("10".equalsIgnoreCase(getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}
					
					}
					
					//servicesAmnt = dailyRate;
					servicesAmnt += overTimeAmount;
				}else{
					
					//labor sprayman
					//identify labor to sprayman
					if(getLoads()!=null && getDrums()!=null){
						dailyRate = LABOR_SPRAYMAN_DAILY_RATE;
						hourlyRate = dailyRate/HOUR_IN_DAY;	
						
					}
					//overtime
					if("3".equalsIgnoreCase(getLoads())){
						
						if("5".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 1;
						}else if("10".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 3;
						}else if("20".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 4;
						}else if("25".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 5;
						}else if("30".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 6;
						}else if("35".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 7;
						}else if("40".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 8;
						}else if("45".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 9;
						}else if("50".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 10;
						}else if("55".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 11;
						}else if("60".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 12;
						}else if("65".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 13;
						}else if("70".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 14;
						}else if("75".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 15;
						}else if("80".equalsIgnoreCase(getDrums())){
							overTimeAmount = em.getOvertime() * 16;
						}
						
					}else{
					
						if("5".equalsIgnoreCase(getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate;
						}else if("10".equalsIgnoreCase(getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate * 2;
						}else if("15".equalsIgnoreCase(getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}
					
					}
					servicesAmnt += overTimeAmount;
					
				}
				
				}
				
				em.setDailyRate(Currency.formatAmount(dailyRate));
				em.setHourlyRate(Currency.formatAmount(hourlyRate));
				em.setOverTimeRate(Currency.formatAmount(em.getOvertime()));
				
				em.setPayableServices(Currency.formatAmount(servicesAmnt));
				
				
				total += servicesAmnt;
			}catch(Exception e){}
			
		}
		
		setTotalServices(Currency.formatAmount(total));
	}
	
	public void selectedLabor(Employee em){
		System.out.println("Selected : --------");
		
		boolean isOk = Application.checkingDuplicateRecordedForTheSameTime(null, em, DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"), getTimeInMap().get(getTimeInId()), getTimeOutMap().get(getTimeOutId()));
		
		if(isOk){
		
		setSearchEmployee(null);
		em = calculateSalary(em);
		
		if(employeeSelected!=null){
			employeeSelected.remove(em);
			employeeSelected.add(em);
		}else{
			employeeSelected.add(em);
		}
		NUMBER_OF_FORCING_EMPLOYEE = employeeSelected.size();
		recalculateServices();
		System.out.println("Selected Employee size " + employeeSelected.size());
		Application.addMessage(1, "Employee name " + em.getFullName() + " has been added.", "");
		
		}else{
			Application.addMessage(3, "Employee name " + em.getFullName() + " has already recorded for this day and time", "");
		}
	}
	
	public void deleteRowEmployee(Employee em){
		
		//if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(getDateTrans())){
			employeeSelected.remove(em);
			if(getActiveData()!=null){
				
				ActivityTransactions act = getActiveData();
				String[] params = new String[2];
				params[0] = em.getId()+"";
				params[1] = act.getId()+"";
				TimeSheets.delete("UPDATE timesheets set isactivetime=0 WHERE empid=? AND actransid=?", params);
			
			}
			NUMBER_OF_FORCING_EMPLOYEE = employeeSelected.size();
			recalculateServices();
			Application.addMessage(1, "Successfully deleted.", "");
		/*}else{
			Application.addMessage(3, "Deletion of employee is not allowed.", "");
		}*/
	}
	
	public Date getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getDateToday();
		}
		return dateTrans;
	}
	public void setDateTrans(Date dateTrans) {
		this.dateTrans = dateTrans;
	}
	public String getDrums() {
		return drums;
	}
	public void setDrums(String drums) {
		this.drums = drums;
	}
	public String getLoads() {
		return loads;
	}
	public void setLoads(String loads) {
		this.loads = loads;
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
	public List getActivities() {
		activityMap = new HashMap<Integer, Activity>();
		activities = new ArrayList<>();
		
		String sql = "SELECT * FROM activity WHERE isactiveac=1 ORDER BY acname";
		for(Activity ac : Activity.retrieve(sql, new String[0])){
			activities.add(new SelectItem(ac.getId(), ac.getName()));
			activityMap.put(ac.getId(), ac);
		}
		return activities;
	}
	public void setActivities(List activities) {
		this.activities = activities;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public Map<Integer, Activity> getActivityMap() {
		return activityMap;
	}
	public void setActivityMap(Map<Integer, Activity> activityMap) {
		this.activityMap = activityMap;
	}
	
	public List<String> suggestedMaterials(String query){
		String sql = "SELECT matname FROM materials WHERE matname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Materials.retriveNames(sql);
		return result;
	}
	
	public void loadMaterials() {
		
		materialSelection = Collections.synchronizedList(new ArrayList<Materials>()); 
		
		String sql = "SELECT * FROM materials WHERE isactivemat=1 ORDER BY matname";
		if(getSearchMaterial()!=null && !getSearchMaterial().isEmpty()){
			sql = "SELECT * FROM materials  WHERE isactivemat=1 AND matname like '%"+ getSearchMaterial().replace("--", "") +"%'";
		}
		materialSelection = Materials.retrieve(sql, new String[0]);
			
	}
	
	public void inputedQty(){
		setHoldQty(getQuantity());
		setTmpUomId(getUomId());
	}
	
	public void selectedMaterial(Materials mat){
		
		boolean isOk = true;
		
		if(getTmpUomId()==0){
			isOk = false;
			Application.addMessage(3, "Please provide UOM", "");
		}
		
		if(getHoldQty()>0 && isOk){
			MaterialTransactions mata = new MaterialTransactions();
			mata.setDateTrans(DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"));
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
			
			setQuantity(0);
			setHoldQty(0);
			setUomId(0);
			uomMap = new HashMap<Integer, UOM>();
			getUoms();
			Application.addMessage(1, "Material " + mat.getName() + " has been added.", "");
		}else{
			Application.addMessage(3, "Please provide quantity", "");
		}
	}
	
	public void deleteRowMaterial(MaterialTransactions mat){
		//if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(getDateTrans())){
			materialSelected.remove(mat);
			if(getActiveData()!=null){
				ActivityTransactions act = getActiveData();
				String[] params = new String[2];
				params[0] = mat.getId()+"";
				params[1] = act.getId()+"";
				MaterialTransactions.delete("UPDATE materialtrans set isactivematrans=0 WHERE matransid=? AND actransid=?", params);
			}
			Application.addMessage(1, "Successfully deleted.", "");
		/*}else{
			Application.addMessage(3, "Deletion of material is not allowed.", "");
		}*/
	}
	
	public List<MaterialTransactions> getMaterialSelected() {
		return materialSelected;
	}
	public void setMaterialSelected(List<MaterialTransactions> materialSelected) {
		this.materialSelected = materialSelected;
	}
	public List getLocations() {
		locationMap = new HashMap<Integer, Location>();
		locations = new ArrayList<>();
		
		String sql = " AND lc.isactiveloc=1 ORDER BY lc.locname";
		for(Location loc : Location.retrieve(sql, new String[0])){
			locations.add(new SelectItem(loc.getId(), loc.getName()));
			locationMap.put(loc.getId(), loc);
		}
		return locations;
	}
	public void setLocations(List locations) {
		this.locations = locations;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public Map<Integer, Location> getLocationMap() {
		return locationMap;
	}
	public void setLocationMap(Map<Integer, Location> locationMap) {
		this.locationMap = locationMap;
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
	public List<Employee> getEmployeeSelected() {
		return employeeSelected;
	}
	public void setEmployeeSelected(List<Employee> employeeSelected) {
		this.employeeSelected = employeeSelected;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List getTimeIns() {
		return timeIns;
	}

	public void setTimeIns(List timeIns) {
		this.timeIns = timeIns;
	}

	public double getTimeInId() {
		if(timeInId==0){
			timeInId = 6.5;
		}
		return timeInId;
	}

	public void setTimeInId(double timeInId) {
		this.timeInId = timeInId;
	}

	public List getTimeOuts() {
		return timeOuts;
	}

	public void setTimeOuts(List timeOuts) {
		this.timeOuts = timeOuts;
	}

	public double getTimeOutId() {
		if(timeOutId==0){
			timeOutId = 14.5;
		}
		return timeOutId;
	}

	public void setTimeOutId(double timeOutId) {
		this.timeOutId = timeOutId;
	}

	public String getSearchMaterial() {
		return searchMaterial;
	}

	public void setSearchMaterial(String searchMaterial) {
		this.searchMaterial = searchMaterial;
	}

	public List<Materials> getMaterialSelection() {
		return materialSelection;
	}

	public void setMaterialSelection(List<Materials> materialSelection) {
		this.materialSelection = materialSelection;
	}

	public List getUoms() {
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

	public ActivityTransactions getActiveData() {
		return activeData;
	}

	public void setActiveData(ActivityTransactions activeData) {
		this.activeData = activeData;
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



	public List<ActivityTransactions> getActivitiesData() {
		return activitiesData;
	}



	public void setActivitiesData(List<ActivityTransactions> activitiesData) {
		this.activitiesData = activitiesData;
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
			timeSheetTo =DateUtils.getDateToday();
		}
		return timeSheetTo;
	}

	public void setTimeSheetTo(Date timeSheetTo) {
		this.timeSheetTo = timeSheetTo;
	}

	public String getTotalActivityExpenses() {
		return totalActivityExpenses;
	}

	public void setTotalActivityExpenses(String totalActivityExpenses) {
		this.totalActivityExpenses = totalActivityExpenses;
	}

	public String getTotalServices() {
		return totalServices;
	}

	public void setTotalServices(String totalServices) {
		this.totalServices = totalServices;
	}

	public boolean isLockButton() {
		return lockButton;
	}

	public void setLockButton(boolean lockButton) {
		this.lockButton = lockButton;
	}
	
	public void postProcessXLS(Object document){
		HSSFWorkbook wb = (HSSFWorkbook)document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		
		cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		
		for(int i=0; i<header.getPhysicalNumberOfCells(); i++){
			HSSFCell cell = header.getCell(i);
			
			cell.setCellStyle(cellStyle);
		}
	}
	
	public ExcelOptions getExcelOptions() {
		
		excelOptions = new ExcelOptions();
		excelOptions.setFacetBgColor("#F88017");
		excelOptions.setFacetFontStyle("BOLD");
		excelOptions.setCellFontSize("10");
		
		return excelOptions;
	}

	public void setExcelOptions(ExcelOptions excelOptions) {
		this.excelOptions = excelOptions;
	}

	public PDFOptions getPdfotions() {
		return pdfotions;
	}

	public void setPdfotions(PDFOptions pdfotions) {
		this.pdfotions = pdfotions;
	}

	public Map<Integer, Job> getJobs() {
		return jobs;
	}

	public void setJobs(Map<Integer, Job> jobs) {
		this.jobs = jobs;
	}

}


















