package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Materials;
import com.italia.marxmind.trax.controller.UOM;


/**
 * 
 * @author mark italia
 * @since 09/02/2017
 * @version 1.0
 *
 */
@ManagedBean(name="uomBean", eager=true)
@ViewScoped
public class UOMBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 543768586861L;
	
	private String searchUOM;
	private String uomName;
	private String symbolName;
	private UOM uomData;
	
	private List<UOM> uoms = Collections.synchronizedList(new ArrayList<UOM>());
	
	@PostConstruct
	public void init(){
		uoms = Collections.synchronizedList(new ArrayList<UOM>());
		String sql = "SELECT * FROM uom WHERE isactiveuom=1";
		if(getSearchUOM()!=null && !getSearchUOM().isEmpty()){
			sql += " AND uomname like '%"+ getSearchUOM().replace("--", "") +"%'";
		}
		
		uoms = UOM.retrieve(sql, new String[0]);
		
		if(uoms!=null && uoms.size()==1){
			clickItem(uoms.get(0));
		}else{
			clear();
			Collections.reverse(uoms);
		}
	}
	
	public void saveData(){
		UOM uom = new UOM();
		if(getUomData()!=null){
			uom = getUomData();
		}else{
			uom.setIsActive(1);
		}
		
		boolean isOk = true;
		
		if(getUomName()==null || getUomName().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide uom name", "");
		}
		
		if(getSymbolName()==null || getSymbolName().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide symbol name", "");
		}
		
		if(isOk){
			
			uom.setName(getUomName());
			uom.setSymbol(getSymbolName());
			uom = UOM.save(uom);
			setUomData(uom);
			init();
			Application.addMessage(1, "Successfully saved", "");
		}
		
	}
	
	public void deleteRow(UOM uom){
		uom.delete();
		init();
		Application.addMessage(1, "Successfully deleted", "");
	}
	
	public void clickItem(UOM uom){
		clear();
		setUomData(uom);
		setUomName(uom.getName());
		setSymbolName(uom.getSymbol());
	}
	
	public void clear(){
		setUomData(null);
		setUomName(null);
		setSymbolName(null);
	}
	
	public String getSearchUOM() {
		return searchUOM;
	}

	public void setSearchUOM(String searchUOM) {
		this.searchUOM = searchUOM;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public String getSymbolName() {
		return symbolName;
	}

	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	public UOM getUomData() {
		return uomData;
	}

	public void setUomData(UOM uomData) {
		this.uomData = uomData;
	}

	public List<UOM> getUoms() {
		return uoms;
	}

	public void setUoms(List<UOM> uoms) {
		this.uoms = uoms;
	}

}
