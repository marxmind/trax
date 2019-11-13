package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
/**
 * 
 * @author mark italia
 * @since 09/11/2017
 * @version 1.0
 *
 */
import javax.faces.model.SelectItem;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.Location;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.controller.MaterialOUT;
import com.italia.marxmind.trax.controller.MaterialProperties;
import com.italia.marxmind.trax.controller.MaterialRETURN;
import com.italia.marxmind.trax.controller.Materials;
import com.italia.marxmind.trax.controller.QuantityRunning;
import com.italia.marxmind.trax.controller.UOM;
import com.italia.marxmind.trax.controller.UserDtls;
import com.italia.marxmind.trax.enm.QuantityRunningStatus;
import com.italia.marxmind.trax.utils.DateUtils;
@ManagedBean(name="invBean", eager=true)
@ViewScoped
public class InventoryBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 980789776541L;
	
	//Adding or editing materials pricing and quantity
	private List<MaterialProperties> props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
	private String searchProps;
	private Materials materialSelected;
	private MaterialProperties propsData;
	private double propsPrice;
	private double propsQty;
	private Date dateTrans;
	
	private String searchMaterial;
	private List<Materials> materialSelection = Collections.synchronizedList(new ArrayList<Materials>());
	
	private List uoms;
	private int uomId;
	private Map<Integer, UOM> uomMap = Collections.synchronizedMap(new HashMap<Integer, UOM>());
	
	
	private double inputOutQty;
	
	private String searchLocation;
	private List<Location> loctionsSelection = Collections.synchronizedList(new ArrayList<Location>());
	private Location locationSelected;
	private String searchEmployee;
	private List<Employee> employeeSelection = Collections.synchronizedList(new ArrayList<Employee>());
	private Employee employeeSelected;
	private String uomOut;
	
	private List<MaterialOUT> outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
	private MaterialOUT selectedOut;
	private Date dateFrom;
	private Date dateTo;
	private double returnQty;
	private Date dateTransReturn;
	
	private boolean withZeroQty;
	
	private UserDtls userLogin;
	
	private double allowedQty;
	
	@PostConstruct
	public void init(){
		
		loadProperties();
	}
	
	public void onTabChangeView(TabChangeEvent event) {
		
		if("Material Adjustment".equalsIgnoreCase(event.getTab().getTitle())){
			clearProps();
			loadProperties();
		}else if("Material Out".equalsIgnoreCase(event.getTab().getTitle())){
			clearProps();
			loadOut();
		}else if("Material Return".equalsIgnoreCase(event.getTab().getTitle())){
			clearProps();
			loadReturn();
		}
		
	}
	
	public void loadProperties(){
		props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
		
		String sql = " AND prop.isactiveprop=1";
		String[] params = new String[0]; 
		if(getSearchProps()!=null && !getSearchProps().isEmpty()){
			sql += " AND mat.matname like '%"+ getSearchProps().replace("--", "") +"%'";
		}
		
		props = MaterialProperties.retrieve(sql, params);
		Collections.reverse(props);
	}
	
	public void loadOut(){
		props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
		
		String sql = " AND prop.currentqty!=0 AND prop.isactiveprop=1";
		String[] params = new String[0]; 
		if(getSearchProps()!=null && !getSearchProps().isEmpty()){
			sql += " AND mat.matname like '%"+ getSearchProps().replace("--", "") +"%'";
		}
		
		for(MaterialProperties prop : MaterialProperties.retrieve(sql, params)){
			
			
			params = new String[1];
			sql = " AND ot.isactiveout=1 AND ot.matqty!=0 AND prop.propid=? ORDER BY ot.outid DESC limit 50";
			params[0] = prop.getId()+"";
			List<MaterialOUT> outs = MaterialOUT.retrieve(sql, params);
			prop.setOutList(outs);
			
			props.add(prop);
		}
		
		Collections.reverse(props);
	}
	
	public void loadReturn(){
		outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
		
		String sql = " AND ot.isactiveout=1 AND (ot.outdate>=? AND ot.outdate<=?)";
		
		//if(getWithZeroQty()){
		//	sql +=" AND ot.matqty=0 ";
		//}else{
			sql += " AND ot.matqty!=0";
		//}
		
		System.out.println("Load return " + sql);
		
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		if(getSearchProps()!=null && !getSearchProps().isEmpty()){
			sql += " AND mat.matname like '%"+ getSearchProps().replace("--", "") +"%'";
		}
		
		for(MaterialOUT out : MaterialOUT.retrieve(sql, params)){
			
			params = new String[2];
			sql = " AND ret.isactivereturn=1 AND prop.propid=? AND ot.outid=? AND ot.isactiveout=1 ORDER BY ret.retid DESC limit 50";
			params[0] = out.getMaterialProperties().getId()+"";
			params[1] = out.getId()+"";
			List<MaterialRETURN> rets = MaterialRETURN.retrieve(sql, params);
			out.setReturnList(rets);
			
			outs.add(out);
		}
		
		Collections.reverse(outs);
		
	}
	
	public void saveOut(){
		boolean isOk = true;
		
		if(getEmployeeSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error No Employee", "Please provide employee name");
		}
		
		if(getInputOutQty()>getAllowedQty()){
			isOk = false;
			Application.addMessage(3, "Error Quantity", "Please provide quantity not greater to current quantity.");
		}else{
		
			if(getInputOutQty()<=0){
				isOk = false;
				Application.addMessage(3, "Error Quantity", "Please provide quantity");
			}
		
		}
		
		if(getPropsData()==null || getMaterialSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error Material", "Please provide material");
		}
		
		if(getLocationSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error Area", "Please provide area");
		}
		
		
		
		if(isOk){
			MaterialProperties prop = getPropsData();
			MaterialOUT out = new MaterialOUT();
			out.setDateTrans(DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"));
			out.setIsActive(1);
			out.setMaterialProperties(prop);
			out.setMaterials(prop.getMaterials());
			out.setUom(prop.getUom());
			out.setPriceCost(prop.getPrice());
			out.setQuantity(getInputOutQty());
			
			out.setLocation(getLocationSelected());
			
			out.setRequestedBy(getEmployeeSelected());
			
			out.setUserDtls(getUserLogin());
			
			out = MaterialOUT.save(out);
			quantityTrack(prop, out, QuantityRunningStatus.OUT);
			clearProps();
			loadOut();
			Application.addMessage(1, "Success", "Successfully saved");
		}
	}
	
	public void deleteOutRow(MaterialOUT out){
		MaterialProperties prop = out.getMaterialProperties();
		prop.setMaterials(out.getMaterials());
		prop.setUom(out.getUom());
		prop.setUserDtls(out.getUserDtls());
		quantityTrack(prop, out, QuantityRunningStatus.RECALL);
		out.delete();
		loadOut();
		Application.addMessage(1, "Success", "Successfully recalled");
	}
	
	public void deleteReturnRow(MaterialRETURN ret){
		MaterialProperties prop = ret.getMaterialProperties();
		prop.setMaterials(ret.getMaterials());
		prop.setUom(ret.getUom());
		prop.setUserDtls(ret.getUserDtls());
		quantityTrack(prop, ret, QuantityRunningStatus.RECALL);
		ret.delete();
		loadReturn();
		Application.addMessage(1, "Success", "Successfully recalled");
	}
	
	
	private void quantityTrack(MaterialProperties prop, Object obj, QuantityRunningStatus status){
		
		double oldQty = 0d;
		double newQty = 0d;
		double qty = 0d;
		String dateTransactions = DateUtils.getCurrentDateYYYYMMDD();
		oldQty = prop.getCurrentQty();
		
		if(obj instanceof MaterialOUT){
			MaterialOUT out = (MaterialOUT)obj;
			dateTransactions = DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd");
			qty = out.getQuantity();
			
			if(QuantityRunningStatus.OUT.getId()==status.getId()){
			
				if(oldQty>qty){
					newQty = oldQty - qty;
				}else{
					if(oldQty==qty){
						newQty = 0;
					}else{
						newQty = oldQty;
					}
				}
				
				qty = -qty;
			//this is if recalling quantity
			}else if(QuantityRunningStatus.RECALL.getId()==status.getId()){
				
				newQty = oldQty + qty;
				
			}
			
		}else if(obj instanceof MaterialRETURN){
			MaterialRETURN ret = (MaterialRETURN)obj;
			dateTransactions = DateUtils.convertDate(getDateTransReturn(),"yyyy-MM-dd");
			qty = ret.getQuantity();
			
			if(QuantityRunningStatus.RETURN.getId()==status.getId()){
			
				newQty = oldQty + qty;
				
				
				MaterialOUT out = ret.getMaterialOut();
				double outQty = out.getQuantity();
				out.setQuantity(outQty-qty);
				out.save();
			
			}else if(QuantityRunningStatus.RECALL.getId()==status.getId()){
				
				newQty = oldQty - qty;
				
				MaterialOUT out = MaterialOUT.retrieve(ret.getMaterialOut().getId());
				double outQty = out.getQuantity();
				/*out.setMaterialProperties(prop);
				out.setMaterials(prop.getMaterials());
				out.setUom(prop.getUom());
				out.setUserDtls(prop.getUserDtls());*/
				out.setQuantity(outQty+qty);
				out.save();
				
				
			}
						
		}
		
		//update quantity in Material Properties
		prop.setCurrentQty(newQty);
		prop.setPreviousQty(oldQty);
		prop.save();
		
		//record quantity running
		QuantityRunning run = new QuantityRunning();
		run.setTransactionType(status.getId());
		run.setDateTrans(dateTransactions);
		run.setMaterialProperties(prop);
		run.setMaterials(prop.getMaterials());
		run.setUom(prop.getUom());
		run.setPriceCost(prop.getPrice());
		run.setQuantity(qty);
		run.setUserDtls(getUserLogin());
		run.save();
		
	}
	
	public void saveProp(){
		boolean isOk = true;
		if(getMaterialSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error", "Please select material");
		}
		
		if(getUomId()==0){
			isOk = false;
			Application.addMessage(3, "Error", "Please select unit");
		}
		
		if(getPropsPrice()==0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide price");
		}
		
		if(getPropsQty()==0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide quantity");
		}
		
		if(isOk){
			MaterialProperties prop = new MaterialProperties();
			
			if(getPropsData()!=null){
				prop = getPropsData();
				double qty = 0d;
				double newQty=0d;
				double oldQty = 0d;
				//qty = prop.getCurrentQty();
				//oldQty = prop.getCurrentQty();
				//newQty = qty + getPropsQty();
				newQty =  getPropsQty();
				
				prop.setCurrentQty(newQty);
				//prop.setPreviousQty(oldQty);
			}else{
				prop.setIsActive(1);
				prop.setIsUsed(1);
				
				prop.setCurrentQty(getPropsQty());
				prop.setPreviousQty(0);
				
			}
			prop.setDateTrans(DateUtils.convertDate(getDateTrans(),"yyyy-MM-dd"));
			prop.setPrice(getPropsPrice());
			
			
			prop.setMaterials(getMaterialSelected());
			prop.setUom(getUomMap().get(getUomId()));
			
			prop.setUserDtls(getUserLogin());
			
			prop = MaterialProperties.save(prop);
			
			QuantityRunning run = new QuantityRunning();
			
			run.setMaterialProperties(prop);
			run.setDateTrans(prop.getDateTrans());
			
			run.setMaterials(prop.getMaterials());
			run.setUom(prop.getUom());
			
			run.setUserDtls(getUserLogin());
			
			//double totalCost = getPropsPrice() * getPropsQty(); 
			
			run.setPriceCost(getPropsPrice());
			
			run.setQuantity(getPropsQty());
			if(getPropsQty()>0){
				run.setTransactionType(QuantityRunningStatus.ADDED.getId());
			}else{
				run.setTransactionType(QuantityRunningStatus.ADJUSTMENT.getId());
			}
			run.save();
			
			Application.addMessage(1, "Success", "Successfully saved");
			clearProps();
			loadProperties();
		}
		
	}
	
	public void saveReturn(){
		boolean isOk = true;
		
		if(getEmployeeSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error No Employee", "Please provide employee name");
		}
		
		if(getReturnQty()<=0){
			isOk = false;
			Application.addMessage(3, "Error Quantity", "Please provide quantity");
		}
		
		if(getSelectedOut()==null){
			isOk = false;
			Application.addMessage(3, "Error Material", "Please provide material");
		}
		
		if(getLocationSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error Area", "Please provide area");
		}
		
		if(isOk){
			MaterialProperties prop = new MaterialProperties();
			MaterialOUT out = getSelectedOut();
			prop = out.getMaterialProperties();
			prop.setUserDtls(out.getUserDtls());
			prop.setUom(out.getUom());
			prop.setMaterials(out.getMaterials());
			
			MaterialRETURN ret = new MaterialRETURN();
			ret.setMaterialOut(out);
			ret.setDateTrans(DateUtils.convertDate(getDateTransReturn(),"yyyy-MM-dd"));
			ret.setIsActive(1);
			ret.setMaterialProperties(prop);
			ret.setMaterials(out.getMaterials());
			ret.setUom(out.getUom());
			ret.setPriceCost(out.getPriceCost());
			ret.setQuantity(getReturnQty());
			
			ret.setLocation(getLocationSelected());
			
			ret.setReturnBy(getEmployeeSelected());
			
			ret.setUserDtls(getUserLogin());
			
			ret = MaterialRETURN.save(ret);
			quantityTrack(prop, ret, QuantityRunningStatus.RETURN);
			clearProps();
			loadReturn();
			Application.addMessage(1, "Success", "Successfully saved");
		}
	}
	
	public void clearProps(){
		//props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
		setMaterialSelected(null);
		setPropsData(null);
		setPropsPrice(0);
		setPropsQty(0);
		setDateTrans(null);
		setSearchMaterial(null);
		setSearchProps(null);
		
		setInputOutQty(0);
		setSearchLocation(null);
		setLocationSelected(null);
		
		setSearchEmployee(null);
		setEmployeeSelected(null);
		setUomOut(null);
		
		setDateFrom(null);
		setDateTo(null);
		setReturnQty(0);
		setDateTransReturn(null);
		
		setAllowedQty(0);
	}
	
	public List<String> suggestedName(String query){
		String sql = "SELECT fullname FROM employee WHERE fullname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Employee.retriveNames(sql);
		return result;
	}
	
	public void selectedLabor(Employee em){
		setEmployeeSelected(em);
	}
	
	public void loadEmployee(){
		employeeSelection = Collections.synchronizedList(new ArrayList<Employee>());
		
		String sql = " AND emp.isactiveemp=1";
		String[] params = new String[0];
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()){
			sql += " AND emp.fullname like '%"+ getSearchEmployee().replace("--", "") +"%'";
		}
		
		employeeSelection = Employee.retrieve(sql, params);
		
	}
	
	public List<String> suggestedMaterials(String query){
		String sql = "SELECT matname FROM materials WHERE matname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Materials.retriveNames(sql);
		return result;
	}
	
	public void selectedMaterial(Materials mat){
		setMaterialSelected(mat);
	}
	
	public void loadMaterials() {
		
		materialSelection = Collections.synchronizedList(new ArrayList<Materials>()); 
		
		String sql = "SELECT * FROM materials WHERE isactivemat=1 ORDER BY matname";
		if(getSearchMaterial()!=null && !getSearchMaterial().isEmpty()){
			sql = "SELECT * FROM materials  WHERE isactivemat=1 AND matname like '%"+ getSearchMaterial().replace("--", "") +"%'";
		}
		materialSelection = Materials.retrieve(sql, new String[0]);
			
	}
	
	public void clickPropItem(MaterialProperties prop){
		setPropsData(prop);
		setMaterialSelected(prop.getMaterials());
		setPropsPrice(prop.getPrice());
		setDateTrans(DateUtils.convertDateString(prop.getDateTrans(),"yyyy-MM-dd"));
		setUomId(prop.getUom().getId());
		setPropsQty(prop.getCurrentQty());
	}
	
	public void clickOutItem(MaterialProperties prop){
		setPropsData(prop);
		setMaterialSelected(prop.getMaterials());
		setUomOut(prop.getUom().getName());
		setAllowedQty(prop.getCurrentQty());
	}	
	
	public void clickReturnItem(MaterialOUT out){
		setSelectedOut(out);
		setUomOut(out.getUom().getName());
		setLocationSelected(out.getLocation());
		setReturnQty(out.getQuantity());
	}
	
	public void deletePropRow(MaterialProperties prop){
		boolean isOk = true;
		String sql = " AND prop.propid=? AND ot.isactiveout=1";
		String[] params = new String[1];
		params[0] = prop.getId()+"";
		List<MaterialOUT> outs = MaterialOUT.retrieve(sql, params);
		if(outs.size()>0){
			isOk = false;
			Application.addMessage(3, "Error", "Deletion is not allowed anymore");
		}
		
		
		if(isOk){
			prop.delete();
			loadProperties();
			Application.addMessage(1, "Success", "Successfully deleted");
		}
	}
	
	public void selectedOutArea(Location loc){
		setLocationSelected(loc);
	}
	
	public List<String> suggestedArea(String query){
		String sql = "SELECT locname FROM locations WHERE locname like '%"+ query.replace("--", "") +"%'";
		List<String> result = new ArrayList<>();
		result = Location.retriveNames(sql);
		return result;
	}
	
	public void loadArea(){
		loctionsSelection = Collections.synchronizedList(new ArrayList<Location>());
		
		String sql = " AND lc.isactiveloc=1 ORDER BY lc.locname";
		if(getSearchLocation()!=null && !getSearchLocation().isEmpty()){
			sql = " AND lc.isactiveloc=1 AND lc.locname like '%"+ getSearchLocation().replace("--", "") +"%' ORDER BY lc.locname";
		}
		loctionsSelection = Location.retrieve(sql, new String[0]);
			
		
	}
	
	public List<MaterialProperties> getProps() {
		return props;
	}
	public void setProps(List<MaterialProperties> props) {
		this.props = props;
	}
	public String getSearchProps() {
		return searchProps;
	}
	public void setSearchProps(String searchProps) {
		this.searchProps = searchProps;
	}
	public MaterialProperties getPropsData() {
		return propsData;
	}
	public void setPropsData(MaterialProperties propsData) {
		this.propsData = propsData;
	}
	public double getPropsPrice() {
		return propsPrice;
	}
	public void setPropsPrice(double propsPrice) {
		this.propsPrice = propsPrice;
	}
	public double getPropsQty() {
		return propsQty;
	}
	public void setPropsQty(double propsQty) {
		this.propsQty = propsQty;
	}

	public Materials getMaterialSelected() {
		return materialSelected;
	}

	public void setMaterialSelected(Materials materialSelected) {
		this.materialSelected = materialSelected;
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
		uomMap = Collections.synchronizedMap(new HashMap<Integer, UOM>());
		uoms = new ArrayList<>();
		for(UOM uom : UOM.retrieve("SELECT * FROM uom WHERE isactiveuom=1", new String[0])){
			uoms.add(new SelectItem(uom.getId(), uom.getName()));
			uomMap.put(uom.getId(), uom);
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

	public Date getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getDateToday();
		}
		return dateTrans;
	}

	public void setDateTrans(Date dateTrans) {
		this.dateTrans = dateTrans;
	}

	public double getInputOutQty() {
		return inputOutQty;
	}

	public void setInputOutQty(double inputOutQty) {
		this.inputOutQty = inputOutQty;
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

	public Location getLocationSelected() {
		return locationSelected;
	}

	public void setLocationSelected(Location locationSelected) {
		this.locationSelected = locationSelected;
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

	public Employee getEmployeeSelected() {
		return employeeSelected;
	}

	public void setEmployeeSelected(Employee employeeSelected) {
		this.employeeSelected = employeeSelected;
	}

	public String getUomOut() {
		return uomOut;
	}

	public void setUomOut(String uomOut) {
		this.uomOut = uomOut;
	}

	public UserDtls getUserLogin() {
		
		userLogin = Login.getUserLogin().getUserDtls();
		
		return userLogin;
	}

	public void setUserLogin(UserDtls userLogin) {
		this.userLogin = userLogin;
	}

	public List<MaterialOUT> getOuts() {
		return outs;
	}

	public void setOuts(List<MaterialOUT> outs) {
		this.outs = outs;
	}

	public MaterialOUT getSelectedOut() {
		return selectedOut;
	}

	public void setSelectedOut(MaterialOUT selectedOut) {
		this.selectedOut = selectedOut;
	}

	public Date getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getDateToday();
		}
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		if(dateTo==null){
			dateTo = DateUtils.getDateToday();
		}
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public boolean getWithZeroQty() {
		return withZeroQty;
	}

	public void setWithZeroQty(boolean withZeroQty) {
		this.withZeroQty = withZeroQty;
	}

	public double getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(double returnQty) {
		this.returnQty = returnQty;
	}

	public Date getDateTransReturn() {
		if(dateTransReturn==null){
			dateTransReturn = DateUtils.getDateToday();
		}
		return dateTransReturn;
	}

	public void setDateTransReturn(Date dateTransReturn) {
		this.dateTransReturn = dateTransReturn;
	}

	public double getAllowedQty() {
		return allowedQty;
	}

	public void setAllowedQty(double allowedQty) {
		this.allowedQty = allowedQty;
	}
	
	
}
