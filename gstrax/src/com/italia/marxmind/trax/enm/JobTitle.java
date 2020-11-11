package com.italia.marxmind.trax.enm;

/**
 * 
 * @author mark italia
 * @since 08/23/2017
 * @version 1.0
 *
 */
public enum JobTitle {

	 DEVELOPER(1,"DEVELOPER"),
	 OWNER(2,"OWNER"), 
	 CEO(3,"CEO"),
	 PRESIDENT(4,"PRESIDENT"),
	 VICE_PRESIDENT(5,"VICE-PRESIDENT"),
	 SECRETARY(6,"SECRETARY"),
	 TREASURER(7,"TREASURER"),
	 MANAGER(8,"MANAGER"),
	 ACCOUNTANT(9,"ACCOUNTANT"),
	 SUPERVISOR(10,"SUPERVISOR"),
	 CASHIER(11,"CASHIER"),
	 CLERK(12,"CLERK"),
	 JOB_ORDER(13,"JOB-ORDER"),
	 DRIVER(14,"DRIVER"),
	 LEAD_MAN(15,"LEAD MAN"),
	 SPRAY_MAN(16,"SPRAY MAN"),
	 FRUIT_GUARD(17,"FRUIT GUARD"),
	 LABORER(18,"LABORER"),
	 SPRAY_TRUCK_DRIVER(19, "SPRAY TRUCK DRIVER");
	
	private double id;
	private String value;
	
	private JobTitle(double id, String value){
		this.id = id;
		this.value = value;
	}
	
	public double getId() {
		return id;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String typeName(double id){
		for(JobTitle type : JobTitle.values()){
			if(id==type.getId()){
				return type.getValue();
			}
		}
		return JobTitle.LABORER.getValue();
	}
	public static double typeId(String name){
		for(JobTitle type : JobTitle.values()){
			if(name.equalsIgnoreCase(type.getValue())){
				return type.getId();
			}
		}
		return JobTitle.LABORER.getId();
	}
}

