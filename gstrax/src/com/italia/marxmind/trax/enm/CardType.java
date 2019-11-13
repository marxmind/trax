package com.italia.marxmind.trax.enm;

/**
 * 
 * @author mark italia
 * @since 12/29/2017
 * @version 1.0
 *
 */

public enum CardType {

	 SSS(1,"SSS"),
	 PHILHEALTH(2,"PHILHEALTH"), 
	 TIN(3,"TIN"),
	 PAG_IBIG(4,"PAG-IBIG"),
	 HEALTH_CARD(5,"HEALTH CARD");
	
	private int id;
	private String value;
	
	private CardType(int id, String value){
		this.id = id;
		this.value = value;
	}
	
	public double getId() {
		return id;
	}
	
	public String getValue() {
		return value;
	}
	
	public static String typeName(int id){
		for(CardType type : CardType.values()){
			if(id==type.getId()){
				return type.getValue();
			}
		}
		return CardType.SSS.getValue();
	}
	public static double typeId(String name){
		for(CardType type : CardType.values()){
			if(name.equalsIgnoreCase(type.getValue())){
				return type.getId();
			}
		}
		return CardType.SSS.getId();
	}
	
}
