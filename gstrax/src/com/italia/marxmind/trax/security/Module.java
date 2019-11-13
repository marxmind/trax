package com.italia.marxmind.trax.security;
/**
 * 
 * @author mark italia
 * @since 01/24/2017
 * @version 1.0
 *
 */
public enum Module {

	GSTRAX(1,"GSTRAX");
	
	
	private int id;
	private String name;
	
	public static String moduleName(int id){
		
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m.getName();
			}
		}
		return Module.GSTRAX.getName();
	}
	
	public static int moduleId(String name){
		
		for(Module m : Module.values()){
			if(name.equalsIgnoreCase(m.getName())){
				return m.getId();
			}
		}
		return Module.GSTRAX.getId();
	}
	
	public static Module selected(int id){
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m;
			}
		}
		return Module.GSTRAX;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Module(int id, String name){
		this.id = id;
		this.name = name;
	}
	
}
