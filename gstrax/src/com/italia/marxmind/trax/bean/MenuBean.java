package com.italia.marxmind.trax.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.sessions.IBean;


/**
 * 
 * @author mark italia
 * @since 09/29/2016
 *@version 1.0
 */
@ManagedBean(name="menuBean")
@ViewScoped
public class MenuBean {

	private static final long serialVersionUID = 1098801825228384363L;
	
	public String dataTransfer(){
		if(Login.checkUserStatus()){
			return "datatransfer";
		}else{
			return "login";
		}
	}
	
	private boolean checkUserLogin(){
		if(Login.checkUserStatus()){
			return true;
		}else{
			return false;
		}
	}
	
	public String inventory(){
		if(checkUserLogin()){
			return "inventory";
		}else{
			return "login";
		}
	}
	
	public String position(){
		if(checkUserLogin()){
			return "position";
		}else{
			return "login";
		}	
	}
	
	public String location(){
		if(checkUserLogin()){
			return "location";
		}else{
			return "login";
		}	
	}
	
	public String activty(){
		if(checkUserLogin()){
			return "activity";
		}else{
			return "login";
		}	
	}
	
	public String uom(){
		if(checkUserLogin()){
			return "uom";
		}else{
			return "login";
		}	
	}
	
	public String materials(){
		if(checkUserLogin()){
			return "materials";
		}else{
			return "login";
		}	
	}
	
	public String fieldtimesheet(){
		if(checkUserLogin()){
			return "fieldtimesheet";
		}else{
			return "login";
		}	
	}
	
	public String reports(){
		if(checkUserLogin()){
			return "reports";
		}else{
			return "login";
		}	
	}
	
	public String timesheet(){
		if(checkUserLogin()){
			return "timesheet";
		}else{
			return "login";
		}	
	}
	
	public String adminEmployees(){
		if(checkUserLogin()){
			return "adminEmployees";
		}else{
			return "login";
		}	
	}
	
	public String adminuser(){
		if(checkUserLogin()){
			return "adminuser";
		}else{
			return "login";
		}	
	}
	
	
	public void printAll(){
		
	}
	
	public String pos(){
		
		//load product imagaes
		/**
		 * remove temporary
		 * slowness on loading
		 */
		//products.loadProduct();
		
		//return "pos";
		//if(Features.isEnabled(com.italia.ipos.enm.Features.GROCERRY_CASHIER)){
			return "cashier";
		//}else{
		//	return "feature";
		//}
	}
	
	public String store(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.STORE_TRANSFER)){
			return "store";
		/*}else{
			return "feature";
		}*/
	}
	
	
	
	public String adminproduct(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_PRODUCT)){
			return "adminproduct";
		/*}else{
			return "feature";
		}*/
	}
	
	public String adminsupplier(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_SUPPLIER)){
			return "adminsupplier";
		/*}else{
			return "feature";
		}*/
	}
	
	public String admincustomer(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_CLIENT)){
			return "admincustomer";
		/*}else{
			return "feature";
		}*/
	}
	
	public String adminuom(){
		return "adminuom";
	}
	
	public String invorder(){
		return "invorder";
	}
	
	public String invreturn(){
		return "invreturn";
	}
	
	public String invforecast(){
		return "invforecast";
	}
	
	public String invadjustment(){
		return "invadjustment";
	}
	
	public String incomes(){
		return "incomes";
	}
	
	public String expenses(){
		return "expenses";
	}
	
	public String suppliers(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.SUPPLIERS)){
			return "suppliers";
		/*}else{
			return "feature";
		}*/
	}
	
	public String clients(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.CLIENTS)){
			return "clients";
		/*}else{
			return "feature";
		}*/
	}
	
	public String receivable(){
		return "receivable";
	}
	
	public String payable(){
		return "payable";
	}

	public String accounting(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING)){
			return "accounting";
		/*}else{
			return "feature";
		}*/
	}
	
	
	public String delivery(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.DELIEVRY)){
			return "delivery";
		/*}else{
			return "feature";
		}*/
	}
	
	public String xtras(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.XTRAS)){
			return "xtras";
		/*}else{
			return "feature";
		}*/
	}
	public String monitoring(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.STATISTICS)){
			return "productdeliverymonitoring";
		/*}else{
			return "feature";
		}*/
	}
	public String delReceiptRecording(){
		return "delreceipts";
	}
	
	public String room(){
		return "roommain";
	}
	
	
	public String tableMonitoring(){
		return "tableMonitoring";
	}
	
	public String productExpiration(){
		return "productExpiration";
	}
	
	public String rptTrans(){
		return "trans";
	}
	
	public String rpt(){
		return "rpt";
	}
	
	public String features(){
		return "features";
	}
	
}
