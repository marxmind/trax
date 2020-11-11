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



/**
 * 
 * @author mark italia
 * @since 08/20/2017
 * @version 1.0
 *
 */
@ManagedBean(name="matBean", eager=true)
@ViewScoped
public class MaterialBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6669563464361L;

	
	private List<Materials> materials = new ArrayList<Materials>();
	
	private String searchMaterial;
	private String materialName;
	private Materials materialData;
	
	@PostConstruct
	public void init(){
		
		materials = new ArrayList<Materials>();
		String sql = "SELECT * FROM materials WHERE isactivemat=1";
		if(getSearchMaterial()!=null && !getSearchMaterial().isEmpty()){
			sql += " AND matname like '%"+ getSearchMaterial().replace("--", "") +"%'";
		}
		
		materials = Materials.retrieve(sql, new String[0]);
		
		if(materials!=null && materials.size()==1){
			clickItem(materials.get(0));
		}else{
			clear();
			Collections.reverse(materials);
		}
		
	}
	
	public void saveData(){
		Materials mat = new Materials();
		if(getMaterialData()!=null){
			mat = getMaterialData();
		}else{
			mat.setIsActive(1);
		}
		
		
		if(getMaterialName()!=null && !getMaterialName().isEmpty()){
		
		mat.setName(getMaterialName());
		
		mat = Materials.save(mat);
		
		setMaterialData(mat);
		init();
			Application.addMessage(1, "Successfully saved", "");
		}else{
			Application.addMessage(3, "Please provide material name", "");
		}
		
	}
	
	public void deleteRow(Materials mat){
		mat.delete();
		init();
		Application.addMessage(1, "Successfully deleted", "");
	}
	
	public void clickItem(Materials mat){
		clear();
		setMaterialData(mat);
		setMaterialName(mat.getName());
	}
	
	public void clear(){
		setMaterialName(null);
		setSearchMaterial(null);
		setMaterialData(null);
	}
	
	public List<Materials> getMaterials() {
		return materials;
	}
	public void setMaterials(List<Materials> materials) {
		this.materials = materials;
	}
	public String getSearchMaterial() {
		return searchMaterial;
	}
	public void setSearchMaterial(String searchMaterial) {
		this.searchMaterial = searchMaterial;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Materials getMaterialData() {
		return materialData;
	}

	public void setMaterialData(Materials materialData) {
		this.materialData = materialData;
	}
	
}
