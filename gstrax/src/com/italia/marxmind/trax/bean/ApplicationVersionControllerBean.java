package com.italia.marxmind.trax.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

import com.italia.marxmind.trax.application.AppInfo;
import com.italia.marxmind.trax.application.ApplicationFixes;
import com.italia.marxmind.trax.application.ApplicationVersionController;
import com.italia.marxmind.trax.security.Copyright;
import com.italia.marxmind.trax.security.License;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 * @version 1.0
 */

@ManagedBean(name="versionBean", eager=true)
public class ApplicationVersionControllerBean {

	private static final long serialVersionUID = 1394801825228386363L;
	
	private ApplicationVersionController versionController;
	private ApplicationFixes applicationFixes;
	private List<ApplicationFixes> fixes = new ArrayList<ApplicationFixes>();
	private Copyright copyright;
	private List<License> licenses = new ArrayList<License>();
	
	@PostConstruct
	public void init(){
		Object[] app = AppInfo.getInstance();
		versionController = (ApplicationVersionController)app[0];
		copyright = (Copyright)app[1];
		fixes = (List<ApplicationFixes>)app[2];
		licenses = (List<License>)app[3];
	}
	
	
	
	public ApplicationVersionController getVersionController() {
		return versionController;
	}
	public void setVersionController(ApplicationVersionController versionController) {
		this.versionController = versionController;
	}
	public ApplicationFixes getApplicationFixes() {
		return applicationFixes;
	}
	public void setApplicationFixes(ApplicationFixes applicationFixes) {
		this.applicationFixes = applicationFixes;
	}
	public List<ApplicationFixes> getFixes() {
		return fixes;
	}
	public void setFixes(List<ApplicationFixes> fixes) {
		this.fixes = fixes;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Copyright getCopyright() {
		return copyright;
	}

	public void setCopyright(Copyright copyright) {
		this.copyright = copyright;
	}



	public List<License> getLicenses() {
		return licenses;
	}



	public void setLicenses(List<License> licenses) {
		this.licenses = licenses;
	}
	
}
