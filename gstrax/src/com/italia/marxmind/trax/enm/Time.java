package com.italia.marxmind.trax.enm;
/**
 * 
 * @author mark italia
 * @since 08/23/2017
 * @version 1.0
 *
 */
public enum Time {

	TIME_ZERO_AM(0.0,"00:00 AM"),
	TIME_30_AM(0.5,"00:30 AM"),
	TIME_1_AM(1,"01:00 AM"),
	TIME_1_30_AM(1.5,"01:30 AM"),
	TIME_2_AM(2.0,"02:00 AM"),
	TIME_2_30_AM(2.5,"02:30 AM"),
	TIME_3_AM(3.0,"03:00 AM"),
	TIME_3_30_AM(3.5,"03:30 AM"),
	TIME_4_AM(4.0,"04:00 AM"),
	TIME_4_30_AM(4.5,"04:30 AM"),
	TIME_5_AM(5.0,"05:00 AM"),
	TIME_5_30_AM(5.5,"05:30 AM"),
	TIME_6_AM(6.0,"06:00 AM"),
	TIME_6_30_AM(6.5,"06:30 AM"),
	TIME_7_AM(7.0,"07:00 AM"),
	TIME_7_30_AM(7.5,"07:30 AM"),
	TIME_8_AM(8.0,"08:00 AM"),
	TIME_8_30_AM(8.5,"08:30 AM"),
	TIME_9_AM(9.0,"09:00 AM"),
	TIME_9_30_AM(9.5,"09:30 AM"),
	TIME_10_AM(10.0,"10:00 AM"),
	TIME_10_30_AM(10.5,"10:30 AM"),
	TIME_11_AM(11.0,"11:00 AM"),
	TIME_11_30_AM(11.5,"11:30 AM"),
	TIME_12_NN(12.0,"12:00 NN"),
	TIME_12_30_PM(12.5,"12:30 PM"),
	TIME_1_PM(13.0,"01:00 PM"),
	TIME_1_30_PM(13.5,"01:30 PM"),
	TIME_2_PM(14.0,"02:00 PM"),
	TIME_2_30_PM(14.5,"02:30 PM"),
	TIME_3_PM(15.0,"03:00 PM"),
	TIME_3_30_PM(15.5,"03:30 PM"),
	TIME_4_PM(16.0,"04:00 PM"),
	TIME_4_30_PM(16.5,"04:30 PM"),
	TIME_5_PM(17.0,"05:00 PM"),
	TIME_5_30_PM(17.5,"05:30 PM"),
	TIME_6_PM(18.0,"06:00 PM"),
	TIME_6_30_PM(18.5,"06:30 PM"),
	TIME_7_PM(19.0,"07:00 PM"),
	TIME_7_30_PM(19.5,"07:30 PM"),
	TIME_8_PM(20.0,"08:00 PM"),
	TIME_8_30_PM(20.5,"08:30 PM"),
	TIME_9_PM(21.0,"09:00 PM"),
	TIME_9_30_PM(21.5,"09:30 PM"),
	TIME_10_PM(22.0,"10:00 PM"),
	TIME_10_30_PM(22.5,"10:30 PM"),
	TIME_11_PM(23.0,"11:00 PM"),
	TIME_11_30_PM(23.5,"11:30 PM"),
	TIME_12_MN(24,"12:00 MN");
	
	private double id;
	private String value;
	
	private Time(double id, String value){
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
		for(Time type : Time.values()){
			if(id==type.getId()){
				return type.getValue();
			}
		}
		return Time.TIME_ZERO_AM.getValue();
	}
	public static double typeId(String name){
		for(Time type : Time.values()){
			if(name.equalsIgnoreCase(type.getValue())){
				return type.getId();
			}
		}
		return Time.TIME_ZERO_AM.getId();
	}
}
