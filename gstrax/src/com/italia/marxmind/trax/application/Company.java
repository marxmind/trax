package com.italia.marxmind.trax.application;

import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;

/**
 * 
 * @author mark italia
 * @since created 10/05/2016
 * @version 1.0
 *
 */
public class Company {
	
	/**
	 * This method use for lock and unlock specific pages/menu/tag/component and alike
	 * @return
	 */
	public static boolean validateCompanyType(){
		String businessType = ReadConfig.value(Gstrax.APP_BUSINESS_TYPE);
		if("0".equalsIgnoreCase(businessType)){
			return false;
		}else if("1".equalsIgnoreCase(businessType)){
			return true;
		}
		return false;
	}
	
	
}
