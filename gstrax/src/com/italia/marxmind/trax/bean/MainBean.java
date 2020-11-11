package com.italia.marxmind.trax.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.italia.marxmind.trax.controller.Login;

/**
 * 
 * @author Mark Italia
 * @since 10-11-2020
 * @version 1.0
 *
 */
@Named
@ViewScoped
public class MainBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5679875645476786791L;
	private boolean active;
	private Login in = Login.getUserLogin();
	
	@PostConstruct
	public void init() {
		loadUpdate();
	}
	
	private void loadUpdate() {
		setActive(in.getVersionNotice()==1? true: false);
		if(in.getVersionNotice()==1) {
			PrimeFaces pf = PrimeFaces.current();
			pf.executeScript("PF('updateNoticeDlg').show();");
		}
	}
	
	public void changeActive() {
		in.setVersionNotice(isActive()==false? 0 : 1);
		Login.updateVersionNotice(in);
		if(!isActive()) {
			PrimeFaces pf = PrimeFaces.current();
			pf.executeScript("PF('updateNoticeDlg').hide();");
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
