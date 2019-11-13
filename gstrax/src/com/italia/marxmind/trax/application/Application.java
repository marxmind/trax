package com.italia.marxmind.trax.application;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.italia.marxmind.trax.controller.ActivityTransactions;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.TimeSheets;

public class Application {
	
	/**
	 * 
	 * @param severityLevel 1-INFO 2-WARN 3-ERROR 4-FATAL
	 * @param summary
	 * @param detail
	 */
	public static void addMessage(int severityLevel,String summary, String detail) {
		FacesMessage message = null;
		switch(severityLevel){
		case 1:
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 2:
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 3:
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 4:
			message = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;		
		}
    }
	
	public static boolean checkingDuplicateRecordedForTheSameTime(ActivityTransactions act, Employee emp, String dateTrans, String timeIn, String timeOut){
		
		String sql = " AND tme.isactivetime=1 AND tme.timeDateTrans=? AND emp.empid=? "
				+ " AND tme.timein=? AND tme.timeout=?";
		String[] params = new String[4];
		params[0] = dateTrans;
		params[1] = emp.getId()+"";
		params[2] = timeIn;
		params[3] = timeOut;
		
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			System.out.println("Error Employee has already registered for this time....");
			if(act!=null && act.getId()!=0 && act.getId()==time.getActivityTransactions().getId()){
				return true;//allow saving if data has been recorded already
			}else{
				return false;
			}
		}
		
		return true;
	}
	//, String timeIn, String timeOut
	public static String checkRecordedTime(Employee emp, String dateTrans){
		//AND tme.timein=? AND tme.timeout=?
		String sql = " AND tme.isactivetime=1 AND tme.timeDateTrans=? AND emp.empid=?";
		String val = "";
		String[] params = new String[2];
		params[0] = dateTrans;
		params[1] = emp.getId()+"";
		/*params[2] = timeIn;
		params[3] = timeOut;*/
		
		int cnt = 1;
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			if(cnt==1){
				val ="Warning: Conflict Time for Date: "+ dateTrans + "; " + time.getTimeIn() + "-" + time.getTimeOut();
			}else if(cnt>1){
				val +=", "+ time.getTimeIn() + "-" + time.getTimeOut();
			}
			cnt++;
		}
		
		return val;
	}
	
	
}
