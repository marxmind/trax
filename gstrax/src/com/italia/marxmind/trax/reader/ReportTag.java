package com.italia.marxmind.trax.reader;

public enum ReportTag {
	
	ACTIVITY("activity"),
	AREA("area"),
	NAMES("names"),
	APPLIED_MATERIALS("materials"),
	WAREHOUSE_MATERIALS("warehousemat");
	
	private String name;
	
	public String getName(){
		return name;
	}
	
	private ReportTag(String name){
		this.name = name;
	}
	
}