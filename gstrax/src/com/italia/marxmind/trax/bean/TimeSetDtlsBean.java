package com.italia.marxmind.trax.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 10-10-2020
 *
 */

@Named
@ViewScoped
public class TimeSetDtlsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 346455679795686661L;
	
	private double hoursPerDay;
	private double overtimeStartAt;
	private double laborSprayManDailyRate;
	private double laborHarvesterDailyRate;
	
	
	
	public void loadTimeDtls() {
		setHoursPerDay(Double.valueOf(ReadConfig.getTagValue("hourperday")));
		setOvertimeStartAt(Double.valueOf(ReadConfig.getTagValue("otstart")));
		setLaborSprayManDailyRate(Double.valueOf(ReadConfig.getTagValue("laborspraymandailyrate")));
		setLaborHarvesterDailyRate(Double.valueOf(ReadConfig.getTagValue("laborharvesterdailyrate")));
	}
	
	public void updateLaborSprayManDailyRate() {
		if(getLaborSprayManDailyRate()>0) {
			ReadConfig.updateTagValue("laborspraymandailyrate", getLaborSprayManDailyRate()+"");
			Application.addMessage(1, "Success", "Successfully updating the value of hours");
		}else {
			Application.addMessage(3, "Error", "Please provide value");
		}
	}
	
	public void updateLaborHavesterDailyRate() {
		if(getLaborHarvesterDailyRate()>0) {
			ReadConfig.updateTagValue("laborharvesterdailyrate", getLaborHarvesterDailyRate()+"");
			Application.addMessage(1, "Success", "Successfully updating the value of hours");
		}else {
			Application.addMessage(3, "Error", "Please provide value");
		}
	}
	
	public void updateHoursPerDay() {
		if(getHoursPerDay()>0) {
			ReadConfig.updateTagValue("hourperday", getHoursPerDay()+"");
			Application.addMessage(1, "Success", "Successfully updating the value of hours");
		}else {
			Application.addMessage(3, "Error", "Please provide value");
		}
	}
	
	public void updateOvertimeStartAt() {
		if(getOvertimeStartAt()>0) {
			ReadConfig.updateTagValue("otstart", getOvertimeStartAt()+"");
			Application.addMessage(1, "Success", "Successfully updating the value of overtime");
		}else {
			Application.addMessage(3, "Error", "Please provide value");
		}
	}
	
	public double getHoursPerDay() {
		return hoursPerDay;
	}

	public void setHoursPerDay(double hoursPerDay) {
		this.hoursPerDay = hoursPerDay;
	}

	public double getOvertimeStartAt() {
		return overtimeStartAt;
	}

	public void setOvertimeStartAt(double overtimeStartAt) {
		this.overtimeStartAt = overtimeStartAt;
	}

	public double getLaborSprayManDailyRate() {
		return laborSprayManDailyRate;
	}

	public void setLaborSprayManDailyRate(double laborSprayManDailyRate) {
		this.laborSprayManDailyRate = laborSprayManDailyRate;
	}

	public double getLaborHarvesterDailyRate() {
		return laborHarvesterDailyRate;
	}

	public void setLaborHarvesterDailyRate(double laborHarvesterDailyRate) {
		this.laborHarvesterDailyRate = laborHarvesterDailyRate;
	}
}