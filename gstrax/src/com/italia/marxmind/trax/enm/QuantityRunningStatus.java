package com.italia.marxmind.trax.enm;
/**
 * 
 * @author mark italia
 * @since 09/11/2017
 * @version 1.0
 *
 */
public enum QuantityRunningStatus {

	ADDED(1, "ADDED"),
	OUT(2,"OUT"),
	RETURN(3,"RETURN"),
	ADJUSTMENT(4,"ADJUSTMENT"),
	RECALL(5,"RECALL");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private QuantityRunningStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(QuantityRunningStatus type : QuantityRunningStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return QuantityRunningStatus.ADDED.getName();
	}
	public static int typeId(String name){
		for(QuantityRunningStatus type : QuantityRunningStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return QuantityRunningStatus.ADDED.getId();
	}
	
}
