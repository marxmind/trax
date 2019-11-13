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

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Barangay;
import com.italia.marxmind.trax.controller.Location;
import com.italia.marxmind.trax.controller.Municipality;
import com.italia.marxmind.trax.controller.Province;

/**
 * 
 * @author mark italia
 * @since 09/02/2017
 * @version 1.0
 *
 */
@ManagedBean(name="locBean", eager=true)
@ViewScoped
public class LocationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 165456879906L;
	
	private Location locData;
	private List<Location> locs = Collections.synchronizedList(new ArrayList<Location>());
	private String searchName;
	private String locName;
	
	private int bgId;
	private List bgs;
	private Map<Integer, Barangay> bgMap = Collections.synchronizedMap(new HashMap<Integer, Barangay>());
	
	private int munId;
	private List muns;
	private Map<Integer, Municipality> munMap = Collections.synchronizedMap(new HashMap<Integer, Municipality>());
	
	private int provId;
	private List provs;
	private Map<Integer, Province> provMap = Collections.synchronizedMap(new HashMap<Integer, Province>());
	
	@PostConstruct
	public void init(){
		
		locs = Collections.synchronizedList(new ArrayList<Location>());
		String sql = " AND lc.isactiveloc=1";
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			sql += " AND lc.locname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		
		locs = Location.retrieve(sql, new String[0]);
		
		if(locs!=null && locs.size()==1){
			clickItem(locs.get(0));
		}else{
			clear();
			Collections.reverse(locs);
		}
	}
	
	public void saveData(){
		  
		Location loc = new Location();
		if(getLocData()!=null){
			loc = getLocData();
		}else{
			loc.setIsActive(1);
		}
		
		boolean isOk = true;
		
		if(getLocName()==null || getLocName().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide name", "");
		}
		
		if(isOk){
			
			loc.setName(getLocName());
			loc.setBarangay(getBgMap().get(getBgId()));
			loc.setMunicipality(getMunMap().get(getMunId()));
			loc.setProvince(getProvMap().get(getProvId()));
			loc = Location.save(loc);
			//setLocData(loc);
			init();
			Application.addMessage(1, "Successfully saved", "");
		}
		
	}
	
	public void deleteRow(Location loc){
		loc.delete();
		init();
		Application.addMessage(1, "Successfully deleted", "");
	}
	
	public void clickItem(Location loc){
		System.out.println("CLICK id: " + loc.getId());
		clear();
		setLocData(loc);
		setLocName(loc.getName());
		setBgId(loc.getBarangay().getId());
		setMunId(loc.getMunicipality().getId());
		setProvId(loc.getProvince().getId());
	}
	
	public void clear(){
		setLocData(null);
		setSearchName(null);
		setLocName(null);
		setBgId(0);
		setMunId(0);
		setProvId(0);
	}
	
	
	
	public Location getLocData() {
		return locData;
	}
	public void setLocData(Location locData) {
		this.locData = locData;
	}
	public List<Location> getLocs() {
		return locs;
	}
	public void setLocs(List<Location> locs) {
		this.locs = locs;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public String getLocName() {
		return locName;
	}
	public void setLocName(String locName) {
		this.locName = locName;
	}
	public int getBgId() {
		return bgId;
	}
	public void setBgId(int bgId) {
		this.bgId = bgId;
	}
	public List getBgs() {
		
		bgs = new ArrayList<>();
		
		for(Barangay bg : Barangay.retrieve("SELECT * FROM barangay WHERE bgisactive=1", new String[0])){
			bgs.add(new SelectItem(bg.getId(), bg.getName()));
			bgMap.put(bg.getId(), bg);
		}
		
		return bgs;
	}
	public void setBgs(List bgs) {
		this.bgs = bgs;
	}
	public Map<Integer, Barangay> getBgMap() {
		return bgMap;
	}
	public void setBgMap(Map<Integer, Barangay> bgMap) {
		this.bgMap = bgMap;
	}
	public int getMunId() {
		return munId;
	}
	public void setMunId(int munId) {
		this.munId = munId;
	}
	public List getMuns() {
		
		muns = new ArrayList<>();
		
		for(Municipality bg : Municipality.retrieve("SELECT * FROM municipality WHERE munisactive=1", new String[0])){
			muns.add(new SelectItem(bg.getId(), bg.getName()));
			munMap.put(bg.getId(), bg);
		}
		
		return muns;
	}
	public void setMuns(List muns) {
		this.muns = muns;
	}
	public Map<Integer, Municipality> getMunMap() {
		return munMap;
	}
	public void setMunMap(Map<Integer, Municipality> munMap) {
		this.munMap = munMap;
	}
	public int getProvId() {
		return provId;
	}
	public void setProvId(int provId) {
		this.provId = provId;
	}
	public List getProvs() {
		
		provs = new ArrayList<>();
		
		for(Province bg : Province.retrieve("SELECT * FROM province WHERE provisactive=1", new String[0])){
			provs.add(new SelectItem(bg.getId(), bg.getName()));
			provMap.put(bg.getId(), bg);
		}
		
		return provs;
	}
	public void setProvs(List provs) {
		this.provs = provs;
	}
	public Map<Integer, Province> getProvMap() {
		return provMap;
	}
	public void setProvMap(Map<Integer, Province> provMap) {
		this.provMap = provMap;
	}
	
}












