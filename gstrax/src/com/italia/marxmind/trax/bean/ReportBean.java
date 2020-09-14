package com.italia.marxmind.trax.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Activity;
import com.italia.marxmind.trax.controller.ActivityTransactions;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.LocationTransactions;
import com.italia.marxmind.trax.controller.Login;
import com.italia.marxmind.trax.controller.MaterialOUT;
import com.italia.marxmind.trax.controller.MaterialProperties;
import com.italia.marxmind.trax.controller.Materials;
import com.italia.marxmind.trax.controller.Reports;
import com.italia.marxmind.trax.controller.TimeSheets;
import com.italia.marxmind.trax.controller.UserDtls;
import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.enm.JobTitle;
import com.italia.marxmind.trax.enm.Time;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.reader.ReadXML;
import com.italia.marxmind.trax.reader.ReportCompiler;
import com.italia.marxmind.trax.reader.ReportTag;
import com.italia.marxmind.trax.utils.Currency;
import com.italia.marxmind.trax.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 08/23/2017
 *@version 1.0
 */
@ManagedBean(name="rptBean", eager=true)
@ViewScoped
public class ReportBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5456867891L;
	
	private List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
	
	private List rptTypes;
	private int typeId;
	
	private Date dateFrom;
	private Date dateTo;
	private boolean summary;
	private final static double HOUR_IN_DAY = Double.valueOf(ReadConfig.value(Gstrax.NORMAL_RENDERED_HOURS));//8.0;
	private final static double BREAK_TIME = 1.0; //12NN
	private final static double DRIVER_OT = 40.0;
	private final static double LEAD_SPRAY_MAN_OT = 35.0;
	private final static double NORMAL_OT = 35.0;
	private final static double LABOR_SPRAYMAN_DAILY_RATE = Double.valueOf(ReadConfig.value(Gstrax.LABOR_SPRAYMAN_DAILY_RATE));
	private final static double LABOR_HARVESTER_DAILY_RATE = Double.valueOf(ReadConfig.value(Gstrax.LABOR_HARVESTER_DAILY_RATE));
	private final static String HARVESTER = "Harvester";
	private final static double OVERTIME_START = Double.valueOf(ReadConfig.value(Gstrax.OTSTART)); //14.5;//start at 2:30PM
	
	private final static double DRIVER_NORMAL_PER_DRUM_RATE = Double.valueOf(ReadConfig.value(Gstrax.DRIVER_NORMAL_PER_DRUM_RATE));
	private final static int PER_DRUM_DIVIDER = Integer.valueOf(ReadConfig.value(Gstrax.PER_DRUM_DIVIDER));
	private final static double FIELD_SPRAY_DRIVER_RATE = Double.valueOf(ReadConfig.value(Gstrax.FIELD_SPRAY_DRIVER));
	
	private final static String FORCING1 = ReadConfig.value(Gstrax.FORCING1);
	private final static String FORCING2 = ReadConfig.value(Gstrax.FORCING2);
	private final static double FORCING_RATE_PER_DRUM = Double.valueOf(ReadConfig.value(Gstrax.FORCING_RATE_PER_DRUM));
	private final static double OTHER_SPRAY_RATE_PER_DRUM = Double.valueOf(ReadConfig.value(Gstrax.OTHER_SPRAY_RATE_PER_DRUM));
	
	//use only for init
	private double NUMBER_OF_FORCING_EMPLOYEE=0d;
	
	private String totalActivityCost;
	private ExcelOptions excelOptions;
	private PDFOptions pdfotions;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Gstrax.REPORT);
	private static final String REPORT_NAME_ACTIVITY = ReadXML.value(ReportTag.ACTIVITY);
	private static final String REPORT_NAME_AREA = ReadXML.value(ReportTag.AREA);
	private static final String REPORT_NAME_NAMES = ReadXML.value(ReportTag.NAMES);
	private static final String REPORT_NAME_APPLIED_MATERIALS = ReadXML.value(ReportTag.APPLIED_MATERIALS);
	private static final String REPORT_NAME_WAREHOUSE_MATERIALS = ReadXML.value(ReportTag.WAREHOUSE_MATERIALS);
	
	private String columnName1;
	private String columnName2;
	private String columnName3;
	private String columnName4;
	private String columnName5;
	private String columnName6;
	private String columnName7;
	private String columnName8;
	
	private boolean colDate;
	private boolean colCuts;
	private boolean colTimeInOut;
	
	private boolean activateDetailsReport;
	
	private String searchDescription;
	
	private int totalMandays;
	
	@PostConstruct
	public void init(){
		
		if(!getActivateDetailsReport()){
			setColDate(false);
		}
		
		if(1==getTypeId()){//activity
			//loadActivity();
			if(getActivateDetailsReport()){
				loadActivtyDetailed();
			}else{
				loadActivitiesSummary();
			}
			setColumnName1("Activity");
			setColumnName2("Area");
			setColumnName3("Blocks");
			setColumnName4("Mandays");
			setColumnName5("Date");
			setColumnName6("Time IN - OUT");
			setColumnName7("Cuts");
			setColumnName8("Total Cost");
		}else if(2==getTypeId()){//locations
			if(getActivateDetailsReport()){
				loadLocationSort();
			}else{
				loadAreaSummary();
			}
			
			setColumnName1("Area");
			setColumnName2("Activity");
			setColumnName3("Blocks");
			setColumnName4("Mandays");
			setColumnName5("Date");
			setColumnName6("Time IN - OUT");
			setColumnName7("Cuts");
			setColumnName8("Total Cost");
		}else if(3==getTypeId()){//names
			if(getActivateDetailsReport()){
				loadNamesSort();
			}else{
				loadNamesSummary();
			}
			setColumnName1("Name");
			setColumnName2("Activity");
			setColumnName3("Area");
			setColumnName4("Blocks");
			setColumnName5("Date");
			setColumnName6("Time IN - OUT");
			setColumnName7("Cuts");
			setColumnName8("Total Cost");
		}else if(4==getTypeId()){//material in	
			
			setColumnName1("Materials");
			setColumnName5("Date");
			setColumnName2("Price");
			setColumnName3("Quantity");
			setColumnName4("Unit");
			setColumnName8("Total Cost");
			
			setColCuts(false);
			setColTimeInOut(false);
			if(getActivateDetailsReport()){
				setColDate(true);
			}else{
				setColDate(false);
			}
			
			loadWarehouse();
			
		}else if(5==getTypeId()){//material out
			setColumnName1("Materials");
			setColumnName5("Date");
			setColumnName2("Applied Area");
			setColumnName3("Quantity");
			setColumnName4("Unit");
			setColumnName8("Total Cost");
			
			setColCuts(false);
			
			setColTimeInOut(true);
			setColumnName6("Price");
			if(getActivateDetailsReport()){
				setColDate(true);
			}else{
				setColDate(false);
			}
			loadMaterialsSummary();

		}
	}
	
	public void activateDetails(){
		String msg = "";
		
		/*if(4==getTypeId()){//materials
			setColDate(true);
			setColCuts(false);
			setColTimeInOut(false);
		}else{	*/
			if(getActivateDetailsReport()){
				msg = "Activating Details Report";
				setColDate(true);
				setColCuts(true);
				setColTimeInOut(true);
			}else{
				msg = "Deactivating Details Report";
				setColDate(false);
				setColCuts(false);
				setColTimeInOut(false);
			}
				
		Application.addMessage(1, msg, "");
		
		//}
		
		init();
		
	}
	
	public void loadWarehouse(){
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		List<MaterialProperties> props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
		//UOM
		Map<Integer, List<MaterialProperties>> uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialProperties>>());
		//Dates
		Map<String,Map<Integer,List<MaterialProperties>>> dates = Collections.synchronizedMap(new HashMap<String,Map<Integer,List<MaterialProperties>>>());
		//Materials
		Map<Long, Map<String,Map<Integer,List<MaterialProperties>>>> mats = Collections.synchronizedMap(new HashMap<Long, Map<String,Map<Integer,List<MaterialProperties>>>>());
		
		
		String sql = " AND prop.isactiveprop=1 AND prop.currentqty!=0 AND (prop.propdate>=? AND prop.propdate<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND mat.matname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		for(MaterialProperties prop : MaterialProperties.retrieve(sql, params)){
			
			long id = prop.getId();
			String date = prop.getDateTrans();
			int uomId = prop.getUom().getId();
			
			if(mats!=null && mats.size()>0){
				
				if(mats.containsKey(id)){
					
					if(mats.get(id).containsKey(date)){
						
						if(mats.get(id).get(date).containsKey(uomId)){
							
							
							
							
							if(getActivateDetailsReport()){
								
								props = mats.get(id).get(date).get(uomId);
								props.add(prop);
								mats.get(id).get(date).put(uomId, props);
								
							}else{	
								
								props = mats.get(id).get(date).get(uomId);
								
								double qty=0d;
								for(MaterialProperties p : props){
									qty+=p.getCurrentQty();
								}
								qty += prop.getCurrentQty();
								MaterialProperties pr = props.get(0);
								pr.setCurrentQty(qty);
								props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
								props.add(pr);
								
								mats.get(id).get(date).put(uomId, props);
							}
							
							
						}else{
							props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
							props.add(prop);
							
							mats.get(id).get(date).put(uomId, props);
						}
						
					}else{
						props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
						props.add(prop);
						uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialProperties>>());
						uoms.put(uomId, props);
						
						mats.get(id).put(date, uoms);
					}
					
				}else{
					
					props = Collections.synchronizedList(new ArrayList<MaterialProperties>());
					props.add(prop);
					uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialProperties>>());
					uoms.put(uomId, props);
					dates = Collections.synchronizedMap(new HashMap<String,Map<Integer,List<MaterialProperties>>>());
					dates.put(date, uoms);
					
					mats.put(id, dates);
				}
				
			}else{
				props.add(prop);
				uoms.put(uomId, props);
				dates.put(date, uoms);
				mats.put(id, dates);
			}
			
		}
		
		double grandTotal = 0d;
		
		if(getActivateDetailsReport()){
			for(Long idMaterial : mats.keySet()){
				
				int cnt = 1;
				
				for(String date : mats.get(idMaterial).keySet()){
						
						for(Integer idUom : mats.get(idMaterial).get(date).keySet()){
							
							for(MaterialProperties out : mats.get(idMaterial).get(date).get(idUom)){
								
								Reports rpt = new Reports();
								
								double totalCost = 0d;
								totalCost = out.getCurrentQty() * out.getPrice();
								if(cnt==1){
									rpt.setF1(out.getMaterials().getName());
									rpt.setF6(out.getDateTrans());
									rpt.setF2(Currency.formatAmount(out.getPrice()));
									rpt.setF3(out.getCurrentQty()+"");
									rpt.setF4(out.getUom().getSymbol());
									rpt.setF5(Currency.formatAmount(totalCost));
									
									
								}else{
									rpt = new Reports();
									rpt.setF1("");
									rpt.setF6(out.getDateTrans());
									rpt.setF2(Currency.formatAmount(out.getPrice()));
									rpt.setF3(out.getCurrentQty()+"");
									rpt.setF4(out.getUom().getSymbol());
									rpt.setF5(Currency.formatAmount(totalCost));
								}
								grandTotal += totalCost;
								reports.add(rpt);
								cnt++;
								
							}
							
						}
						
					}
					
				}
				
		}else{
			
			
			Map<Integer, MaterialProperties> mapOuts = Collections.synchronizedMap(new HashMap<Integer,MaterialProperties>());
			Map<Long, Map<Integer,MaterialProperties>> mapMat = Collections.synchronizedMap(new HashMap<Long,Map<Integer,MaterialProperties>>());
			
			for(Long matId : mats.keySet()){
				for(String date : mats.get(matId).keySet()){
						for(int uomId : mats.get(matId).get(date).keySet()){
							for(MaterialProperties out : mats.get(matId).get(date).get(uomId)){
								if(mapMat!=null){
									
									if(mapMat.containsKey(matId)){
										if(mapMat.get(matId).containsKey(uomId)){
											MaterialProperties newOut = mapMat.get(matId).get(uomId);
											double qty = out.getCurrentQty() + newOut.getCurrentQty();
											double cost = qty * out.getPrice();
											newOut.setCurrentQty(qty);
											mapMat.get(matId).put(uomId, newOut);
										}else{
											//out.setPriceCost(out.getPriceCost()*out.getQuantity());
											mapMat.get(matId).put(uomId, out);
										}
									}else{
										//out.setPriceCost(out.getPriceCost()*out.getQuantity());
										mapOuts = Collections.synchronizedMap(new HashMap<Integer,MaterialProperties>());
										mapOuts.put(uomId, out);
										mapMat.put(matId, mapOuts);
									}
									
								}else{
									//out.setPriceCost(out.getPriceCost()*out.getQuantity());
									mapOuts.put(uomId, out);
									mapMat.put(matId, mapOuts);
								}
							}
						}
					}
					
				}
			
			
			for(Long matId : mapMat.keySet()){
				int cnt = 1;
				for(int uomId : mapMat.get(matId).keySet()){
					MaterialProperties out = mapMat.get(matId).get(uomId);
					Reports rpt = new Reports();
					
					double totalCost = 0d;
					totalCost = out.getPrice() * out.getCurrentQty();
					if(cnt==1){
						rpt.setF1(out.getMaterials().getName());
						rpt.setF6(out.getDateTrans());
						rpt.setF2(Currency.formatAmount(out.getPrice()));
						rpt.setF3(out.getCurrentQty()+"");
						rpt.setF4(out.getUom().getSymbol());
						rpt.setF5(Currency.formatAmount(totalCost));
						
						
					}else{
						rpt = new Reports();
						rpt.setF1("");
						rpt.setF6(out.getDateTrans());
						rpt.setF2(Currency.formatAmount(out.getPrice()));
						rpt.setF3(out.getCurrentQty()+"");
						rpt.setF4(out.getUom().getSymbol());
						rpt.setF5(Currency.formatAmount(totalCost));
					}
					grandTotal += totalCost;
					reports.add(rpt);
					cnt++;
				}
			}
			
			}
			
			setTotalActivityCost(Currency.formatAmount(grandTotal));
		
	}
	
	public void loadMaterialsSummary(){
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		List<MaterialOUT> outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
		//UOM
		Map<Integer, List<MaterialOUT>> uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialOUT>>());
		//Area
		Map<Integer, Map<Integer,List<MaterialOUT>>> areas = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,List<MaterialOUT>>>());
		//Dates
		Map<String,Map<Integer,Map<Integer,List<MaterialOUT>>>> dates = Collections.synchronizedMap(new HashMap<String,Map<Integer,Map<Integer,List<MaterialOUT>>>>());
		//Materials
		Map<Integer, Map<String,Map<Integer,Map<Integer,List<MaterialOUT>>>>> materials = Collections.synchronizedMap(new HashMap<Integer,Map<String,Map<Integer,Map<Integer,List<MaterialOUT>>>>>());
		
		
		
		
		String sql = " AND ot.isactiveout=1 AND ot.matqty!=0 AND (ot.outdate>=? AND ot.outdate<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND mat.matname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		//sql+=" ORDER BY ot.outid ASC";
		
		for(MaterialOUT out : MaterialOUT.retrieve(sql, params)){
			
			if(materials!=null){
				
				int idMaterial = out.getMaterials().getId();
				int idUom = out.getUom().getId();
				int idArea = out.getLocation().getId();
				String date = out.getDateTrans();
				
				if(materials.containsKey(idMaterial)){
					
					if(materials.get(idMaterial).containsKey(date)){
						
						
						if(materials.get(idMaterial).get(date).containsKey(idArea)){
							
							if(materials.get(idMaterial).get(date).get(idArea).containsKey(idUom)){
								
								if(getActivateDetailsReport()){
								
									outs = materials.get(idMaterial).get(date).get(idArea).get(idUom);
									outs.add(out);
									materials.get(idMaterial).get(date).get(idArea).put(idUom,outs);
									
								}else{	
									
									outs = materials.get(idMaterial).get(date).get(idArea).get(idUom);
									
									double qty=0d;
									for(MaterialOUT o : outs){
										qty+=o.getQuantity();
									}
									qty += out.getQuantity();
									MaterialOUT mat = outs.get(0);
									mat.setQuantity(qty);
									outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
									outs.add(mat);
									
									materials.get(idMaterial).get(date).get(idArea).put(idUom, outs);
								}
									
								
							}else{
								
								outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
								outs.add(out);
								//uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialOUT>>());
								//uoms.put(idUom, outs);
								//areas.put(idArea, uoms);
								//dates.put(date, areas);
								//materials.put(idMaterial, dates);
								materials.get(idMaterial).get(date).get(idArea).put(idUom, outs);
							}
							
						}else{
							
							outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
							outs.add(out);
							uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialOUT>>());
							uoms.put(idUom, outs);
							//areas = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,List<MaterialOUT>>>());
							//areas.put(idArea, uoms);
							
							//dates.put(date, areas);
							//materials.put(idMaterial, dates);
							materials.get(idMaterial).get(date).put(idArea, uoms);
						}
						
						
					}else{
						
						outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
						outs.add(out);
						uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialOUT>>());
						uoms.put(idUom, outs);
						areas = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,List<MaterialOUT>>>());
						areas.put(idArea, uoms);
						//dates = Collections.synchronizedMap(new HashMap<String,Map<Integer,Map<Integer,List<MaterialOUT>>>>());
						//dates.put(date, areas);
						
						//materials.put(idMaterial, dates);
						materials.get(idMaterial).put(date, areas);
					}
					
				}else{
					
					outs = Collections.synchronizedList(new ArrayList<MaterialOUT>());
					outs.add(out);
					uoms = Collections.synchronizedMap(new HashMap<Integer,List<MaterialOUT>>());
					uoms.put(idUom, outs);
					areas = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,List<MaterialOUT>>>());
					areas.put(idArea, uoms);
					dates = Collections.synchronizedMap(new HashMap<String,Map<Integer,Map<Integer,List<MaterialOUT>>>>());
					dates.put(date, areas);
					
					materials.put(idMaterial, dates);
				}
				
			}else{
				
				outs.add(out);
				uoms.put(out.getUom().getId(), outs);
				areas.put(out.getLocation().getId(), uoms);
				dates.put(out.getDateTrans(), areas);
				materials.put(out.getMaterials().getId(), dates);
				
			}
			
		}
	
		double grandTotal = 0d;
		
		if(getActivateDetailsReport()){
			for(Integer idMaterial : materials.keySet()){
				
				int cnt = 1;
				
				for(String date : materials.get(idMaterial).keySet()){
					
					//Map<Integer,Map<Integer,List<MaterialOUT>>> sortedDate = new TreeMap<Integer, Map<Integer,List<MaterialOUT>>>(materials.get(idMaterial).get(date)); 
					
					for(Integer idArea : materials.get(idMaterial).get(date).keySet()){
					//for(Integer idArea : sortedDate.keySet()){	
						
						//Map<Integer, List<MaterialOUT>> sortedArea = new TreeMap<Integer, List<MaterialOUT>>(sortedDate.get(idArea));
						
						for(Integer idUom : materials.get(idMaterial).get(date).get(idArea).keySet()){
						//for(Integer idUom : sortedArea.keySet()){
							
							
							for(MaterialOUT out : materials.get(idMaterial).get(date).get(idArea).get(idUom)){
							//for(MaterialOUT out : sortedArea.get(idUom)){
								
								Reports rpt = new Reports();
								
								double totalCost = 0d;
								totalCost = out.getQuantity() * out.getPriceCost();
								if(cnt==1){
									rpt.setF1(out.getMaterials().getName());
									rpt.setF6(out.getDateTrans());
									rpt.setF2(out.getLocation().getName());
									rpt.setF3(out.getQuantity()+"");
									rpt.setF4(out.getUom().getSymbol());
									rpt.setF5(Currency.formatAmount(totalCost));
									
									rpt.setF8(Currency.formatAmount(out.getPriceCost()));
								}else{
									rpt = new Reports();
									rpt.setF1("");
									rpt.setF6(out.getDateTrans());
									rpt.setF2(out.getLocation().getName());
									rpt.setF3(out.getQuantity()+"");
									rpt.setF4(out.getUom().getSymbol());
									rpt.setF5(Currency.formatAmount(totalCost));
									
									rpt.setF8(Currency.formatAmount(out.getPriceCost()));
								}
								grandTotal += totalCost;
								reports.add(rpt);
								cnt++;
								
							}
							
						}
						
					}
					
				}
				
			}
		}else{
		
		Map<Integer, MaterialOUT> mapOuts = Collections.synchronizedMap(new HashMap<Integer,MaterialOUT>());
		Map<Integer, Map<Integer,MaterialOUT>> mapMat = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,MaterialOUT>>());
		
		for(int matId : materials.keySet()){
			for(String date : materials.get(matId).keySet()){
				for(int locId : materials.get(matId).get(date).keySet()){
					for(int uomId : materials.get(matId).get(date).get(locId).keySet()){
						for(MaterialOUT out : materials.get(matId).get(date).get(locId).get(uomId)){
							if(mapMat!=null){
								
								if(mapMat.containsKey(matId)){
									if(mapMat.get(matId).containsKey(locId)){
										MaterialOUT newOut = mapMat.get(matId).get(locId);
										double qty = out.getQuantity() + newOut.getQuantity();
										double cost = qty * out.getPriceCost();
										newOut.setQuantity(qty);
										//newOut.setPriceCost(cost);
										mapMat.get(matId).put(locId, newOut);
									}else{
										//out.setPriceCost(out.getPriceCost()*out.getQuantity());
										mapMat.get(matId).put(locId, out);
									}
								}else{
									//out.setPriceCost(out.getPriceCost()*out.getQuantity());
									mapOuts = Collections.synchronizedMap(new HashMap<Integer,MaterialOUT>());
									mapOuts.put(locId, out);
									mapMat.put(matId, mapOuts);
								}
								
							}else{
								//out.setPriceCost(out.getPriceCost()*out.getQuantity());
								mapOuts.put(locId, out);
								mapMat.put(matId, mapOuts);
							}
						}
					}
				}
				
			}
		}
		
		
		
		for(int matId : mapMat.keySet()){
			int cnt = 1;
			for(int locId : mapMat.get(matId).keySet()){
				MaterialOUT out = mapMat.get(matId).get(locId);
				Reports rpt = new Reports();
				
				double totalCost = 0d;
				totalCost = out.getPriceCost() * out.getQuantity();
				if(cnt==1){
					rpt.setF1(out.getMaterials().getName());
					rpt.setF6(out.getDateTrans());
					rpt.setF2(out.getLocation().getName());
					rpt.setF3(out.getQuantity()+"");
					rpt.setF4(out.getUom().getSymbol());
					rpt.setF5(Currency.formatAmount(totalCost));
					
					rpt.setF8(Currency.formatAmount(out.getPriceCost()));
				}else{
					rpt = new Reports();
					rpt.setF1("");
					rpt.setF6(out.getDateTrans());
					rpt.setF2(out.getLocation().getName());
					rpt.setF3(out.getQuantity()+"");
					rpt.setF4(out.getUom().getSymbol());
					rpt.setF5(Currency.formatAmount(totalCost));
					
					rpt.setF8(Currency.formatAmount(out.getPriceCost()));
				}
				grandTotal += totalCost;
				reports.add(rpt);
				cnt++;
			}
		}
		
		}
		
		setTotalActivityCost(Currency.formatAmount(grandTotal));
	}
	
	@Deprecated
	public void loadMaterials(){
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		Map<Integer, List<MaterialOUT>> materials = Collections.synchronizedMap(new HashMap<Integer, List<MaterialOUT>>());
		List<MaterialOUT> material = new ArrayList<MaterialOUT>();
		
		String sql = " AND ot.isactiveout=1 AND ot.matqty!=0 AND (ot.outdate>=? AND ot.outdate<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND mat.matname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		for(MaterialOUT out : MaterialOUT.retrieve(sql, params)){
			
			if(materials!=null){
				
				int id = out.getMaterials().getId();
				
				if(materials.containsKey(id)){
					
					material = materials.get(id);
					material.add(out);
					materials.remove(id);
					materials.put(out.getMaterials().getId(), material);
					
				}else{
					material = new ArrayList<MaterialOUT>();
					material.add(out);
					materials.put(out.getMaterials().getId(), material);
				}
				
				
			}else{
				material.add(out);
				materials.put(out.getMaterials().getId(), material);
			}
			
		}
		double grandTotal = 0d;
		for(Integer id : materials.keySet()){
			
			List<MaterialOUT> outList = materials.get(id);
			int cnt = 1;
			 
			for(MaterialOUT out : outList){
				
				Reports rpt = new Reports();
				double totalCost = 0d;
				totalCost = out.getQuantity() * out.getPriceCost();
				if(cnt==1){
					rpt.setF1(out.getMaterials().getName());
					rpt.setF6(out.getDateTrans());
					rpt.setF2(out.getLocation().getName());
					rpt.setF3(out.getQuantity()+"");
					rpt.setF4(out.getUom().getSymbol());
					rpt.setF5(Currency.formatAmount(totalCost));
				}else{
					rpt.setF1("");
					rpt.setF6(out.getDateTrans());
					rpt.setF2(out.getLocation().getName());
					rpt.setF3(out.getQuantity()+"");
					rpt.setF4(out.getUom().getSymbol());
					rpt.setF5(Currency.formatAmount(totalCost));
				}
				grandTotal += totalCost;
				reports.add(rpt);
				cnt++;
				
			}
			
		}
		setTotalActivityCost(Currency.formatAmount(grandTotal));
		
		
	}
	
	public void loadNamesSummary(){
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		//Transactions
		List<ActivityTransactions> trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
		//Area
		Map<Integer, List<ActivityTransactions>> areas = Collections.synchronizedMap(new HashMap<Integer,List<ActivityTransactions>>());
		//activity
		Map<Integer, Map<Integer,List<ActivityTransactions>>> activites = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,List<ActivityTransactions>>>());
		//Employee
		Map<Long, Map<Integer,Map<Integer,List<ActivityTransactions>>>> employees = Collections.synchronizedMap(new HashMap<Long, Map<Integer,Map<Integer,List<ActivityTransactions>>>>());
		
		String sql = " AND tme.isactivetime=1 AND ac.isactiveactrans=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND emp.fullname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			params = new String[1];
			sql = "SELECT * FROM activity WHERE acid=?";
			params[0] = time.getActivityTransactions().getActivity().getId()+"";
			
				for(Activity act : Activity.retrieve(sql, params)){
					
					params = new String[1];
					sql =  " AND act.actransid=? AND act.isactiveactrans=1";
					params[0] = time.getActivityTransactions().getId()+"";
					List<LocationTransactions> loctran = LocationTransactions.retrieve(sql, params);
					
					
					ActivityTransactions activity = time.getActivityTransactions();
					activity.setActivity(act);
					activity.setLocation(loctran.get(0).getLocation());
					activity.setLocationTransactions(loctran.get(0));
					List<TimeSheets> tme = Collections.synchronizedList(new ArrayList<TimeSheets>());
					tme.add(time);
					activity.setTimeSheets(tme);
					activity.setEmployeeTimeSheets(time);
					activity.setEmployee(time.getEmployee());
					
					long empId = time.getEmployee().getId();
					int locId = activity.getLocation().getId();
					int acId = act.getId();
					
					if(employees!=null && employees.size()>0){
						
						if(employees.containsKey(empId)){
							
							if(employees.get(empId).containsKey(acId)){
								
								if(employees.get(empId).get(acId).containsKey(locId)){
									
									/*trans = employees.get(empId).get(acId).get(locId);
									trans.add(activity);
									employees.get(empId).get(acId).put(locId, trans);*/
									
									
									String drums = activity.getDrums();
									String cuts = activity.getCuts();
									String blocks = activity.getBlocks();
									
									
									Object[] obj = calculateSalaryEmployee(activity);
									double expenses = (Double)obj[2];
									
									for(ActivityTransactions at : employees.get(empId).get(acId).get(locId)){
										drums +=","+at.getDrums();
										cuts +=","+at.getCuts();
										blocks +=","+at.getBlocks();
										expenses += at.getTotalActivityExpenses();
									}
									
									activity.setDrums(drums);
									activity.setCuts(cuts);
									activity.setBlocks(blocks);
									activity.setTotalActivityExpenses(expenses);
									trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
									trans.add(activity);
									employees.get(empId).get(acId).put(locId, trans);
									
								}else{
									
									Object[] obj = calculateSalaryEmployee(activity);
									double expenses = (Double)obj[2];
									activity.setTotalActivityExpenses(expenses);
									
									trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
									trans.add(activity);
									employees.get(empId).get(acId).put(locId, trans);
								}
								
							}else{
								
								Object[] obj = calculateSalaryEmployee(activity);
								double expenses = (Double)obj[2];
								activity.setTotalActivityExpenses(expenses);
								
								trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
								trans.add(activity);
								areas = Collections.synchronizedMap(new HashMap<Integer,List<ActivityTransactions>>());
								areas.put(locId, trans);
								
								employees.get(empId).put(acId, areas);
							}
							
						}else{
							
							Object[] obj = calculateSalaryEmployee(activity);
							double expenses = (Double)obj[2];
							activity.setTotalActivityExpenses(expenses);
							
							trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
							trans.add(activity);
							areas = Collections.synchronizedMap(new HashMap<Integer,List<ActivityTransactions>>());
							areas.put(locId, trans);
							activites = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,List<ActivityTransactions>>>());
							activites.put(acId, areas);
							
							employees.put(empId, activites);
						}
						
					}else{
						
						Object[] obj = calculateSalaryEmployee(activity);
						double expenses = (Double)obj[2];
						activity.setTotalActivityExpenses(expenses);
						
						trans.add(activity);
						areas.put(locId, trans);
						activites.put(acId, areas);
						employees.put(empId, activites);
					}
					
				}
			
		}
		
		double totalCost = 0d;
		for(long empId : employees.keySet()){
			
			int cntLoc = 1;
			double totalpayable = 0d;
			for(int actId : employees.get(empId).keySet()){
				int cntAct = 1;
				for(int locId : employees.get(empId).get(actId).keySet()){
					
					for(ActivityTransactions ac : employees.get(empId).get(actId).get(locId)){
						
						Reports rpt = new Reports();
						
							if(cntLoc==1){
								rpt.setF1(ac.getEmployee().getFullName());
							}else{
								rpt.setF1("");
							}
							
							if(cntAct==1){
								rpt.setF2(ac.getActivity().getName());
							}else{
								rpt.setF2("");
							}
							
							rpt.setF3(ac.getLocation().getName());
							rpt.setF4(orderNumber(ac.getBlocks()));
							rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
							totalCost +=ac.getTotalActivityExpenses();
							totalpayable +=ac.getTotalActivityExpenses();
						
							reports.add(rpt);
							cntLoc++;
					}
					
					cntAct++;
					
				}
				
			}
			Reports rpt = new Reports();
			rpt.setF1("Total");
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			rpt.setF5("Php "+Currency.formatAmount(totalpayable));
			reports.add(rpt);
		}
		setTotalActivityCost(Currency.formatAmount(totalCost));
		
	}
	
	public void loadActivitiesSummary(){
		
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		//Transactions
		List<ActivityTransactions> trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
		//Employee
		Map<Long, List<ActivityTransactions>> emps = Collections.synchronizedMap(new HashMap<Long,List<ActivityTransactions>>());
		//Area
		Map<Integer,Map<Long,List<ActivityTransactions>>> areas = Collections.synchronizedMap(new HashMap<Integer,Map<Long,List<ActivityTransactions>>>());
		//Activities
		Map<Integer,Map<Integer,Map<Long,List<ActivityTransactions>>>> acs = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,Map<Long,List<ActivityTransactions>>>>());
		
		String sql = " AND tme.isactivetime=1 AND ac.isactiveactrans=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			
			params = new String[1];
			sql = "SELECT * FROM activity WHERE acid=?";
			params[0] = time.getActivityTransactions().getActivity().getId()+"";
			if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
				sql += " AND acname like '%"+ getSearchDescription().replace("--", "") +"%'";
			}
			for(Activity act : Activity.retrieve(sql, params)){
				
				params = new String[1];
				sql =  " AND act.actransid=? AND act.isactiveactrans=1";
				params[0] = time.getActivityTransactions().getId()+"";
				List<LocationTransactions> loctran = LocationTransactions.retrieve(sql, params);
				
				
				ActivityTransactions activity = time.getActivityTransactions();
				activity.setActivity(act);
				activity.setLocation(loctran.get(0).getLocation());
				activity.setLocationTransactions(loctran.get(0));
				List<TimeSheets> tme = Collections.synchronizedList(new ArrayList<TimeSheets>());
				tme.add(time);
				activity.setTimeSheets(tme);
				activity.setEmployeeTimeSheets(time);
				
				long empId = time.getEmployee().getId();
				int locId = activity.getLocation().getId();
				int acId = act.getId();
				
				if(acs!=null && acs.size()>0){
					
					if(acs.containsKey(acId)){
						
						if(acs.get(acId).containsKey(locId)){
							
							if(acs.get(acId).get(locId).containsKey(empId)){
								
								String drums = activity.getDrums();
								String cuts = activity.getCuts();
								String blocks = activity.getBlocks();
								
								
								Object[] obj = calculateSalaryEmployee(activity);
								double expenses = (Double)obj[2];
								int man = 0;
								
								for(ActivityTransactions at : acs.get(acId).get(locId).get(empId)){
									drums +=","+at.getDrums();
									cuts +=","+at.getCuts();
									blocks +=","+at.getBlocks();
									expenses += at.getTotalActivityExpenses();
									man +=at.getTotalMandays();
								}
								
								activity.setTotalMandays(man + 1);
								activity.setDrums(drums);
								activity.setCuts(cuts);
								activity.setBlocks(blocks);
								activity.setTotalActivityExpenses(expenses);
								trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
								trans.add(activity);
								acs.get(acId).get(locId).remove(empId);
								acs.get(acId).get(locId).put(empId, trans);
							
								
							}else{
								
								Object[] obj = calculateSalaryEmployee(activity);
								double expenses = (Double)obj[2];
								activity.setTotalActivityExpenses(expenses);
								activity.setTotalMandays(1);
								
								trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
								trans.add(activity);
								acs.get(acId).get(locId).put(empId, trans);
								
								
							}
							
						}else{
							
							Object[] obj = calculateSalaryEmployee(activity);
							double expenses = (Double)obj[2];
							activity.setTotalActivityExpenses(expenses);
							activity.setTotalMandays(1);
							
							trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
							trans.add(activity);
							emps = Collections.synchronizedMap(new HashMap<Long,List<ActivityTransactions>>());
							emps.put(empId, trans);
							acs.get(acId).put(locId, emps);
							
							
						}
						
					}else{
						
						Object[] obj = calculateSalaryEmployee(activity);
						double expenses = (Double)obj[2];
						activity.setTotalActivityExpenses(expenses);
						activity.setTotalMandays(1);
						
						trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
						trans.add(activity);
						emps = Collections.synchronizedMap(new HashMap<Long,List<ActivityTransactions>>());
						emps.put(empId, trans);
						areas = Collections.synchronizedMap(new HashMap<Integer,Map<Long,List<ActivityTransactions>>>());
						areas.put(locId, emps);
						
						acs.put(acId, areas);
						
						
					}
					
				}else{
					
					Object[] obj = calculateSalaryEmployee(activity);
					double expenses = (Double)obj[2];
					activity.setTotalActivityExpenses(expenses);
					activity.setTotalMandays(1);
					
					trans.add(activity);
					emps.put(empId, trans);
					areas.put(locId, emps);
					acs.put(acId, areas);
					
				}
				
				
			}
			
		}
		
		Map<Integer, ActivityTransactions> areaMap = Collections.synchronizedMap(new HashMap<Integer,ActivityTransactions>());
		Map<Integer, Map<Integer,ActivityTransactions>> actMap = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,ActivityTransactions>>());
		
		for(int acxId : acs.keySet()){
			
			for(int locxId : acs.get(acxId).keySet()){
				
				for(long emxId : acs.get(acxId).get(locxId).keySet()){
					
					for(ActivityTransactions actX : acs.get(acxId).get(locxId).get(emxId)){
						
						if(areaMap!=null && areaMap.size()>0){
							
							if(actMap.containsKey(acxId)){
								
								if(actMap.get(acxId).containsKey(locxId)){
									
									String drums = actX.getDrums();
									String cuts = actX.getCuts();
									String blocks = actX.getBlocks();
									double cost = actX.getTotalActivityExpenses();
									int mandays = actX.getTotalMandays();
									
									ActivityTransactions actNow = actMap.get(acxId).get(locxId);
									drums +="," + actNow.getDrums();
									cuts +="," + actNow.getCuts();
									blocks +="," + actNow.getBlocks();
									cost += actNow.getTotalActivityExpenses();
									mandays += actNow.getTotalMandays();
									
									blocks = orderNumber(blocks);
									cuts = orderNumber(cuts);
									
									actNow.setDrums(drums);
									actNow.setCuts(cuts);
									actNow.setBlocks(blocks);
									actNow.setTotalActivityExpenses(cost);
									actNow.setTotalMandays(mandays);
									
									actMap.get(acxId).put(locxId, actNow);
									
								}else{
									String blocks = actX.getBlocks();
									String cuts = actX.getCuts();
									blocks = orderNumber(blocks);
									cuts = orderNumber(cuts);
									actX.setBlocks(blocks);
									actX.setCuts(cuts);
									
									//actX.setTotalMandays(1);
									actMap.get(acxId).put(locxId, actX);
									
								}
								
							}else{
								
								String blocks = actX.getBlocks();
								String cuts = actX.getCuts();
								blocks = orderNumber(blocks);
								cuts = orderNumber(cuts);
								actX.setBlocks(blocks);
								actX.setCuts(cuts);
								
								//actX.setTotalMandays(1);
								areaMap = Collections.synchronizedMap(new HashMap<Integer,ActivityTransactions>());
								areaMap.put(locxId, actX);
								actMap.put(acxId, areaMap);
							}
							
						}else{
							
							String blocks = actX.getBlocks();
							String cuts = actX.getCuts();
							blocks = orderNumber(blocks);
							cuts = orderNumber(cuts);
							actX.setBlocks(blocks);
							actX.setCuts(cuts);
							
							//actX.setTotalMandays(1);
							areaMap.put(locxId, actX);
							actMap.put(acxId, areaMap);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		double totalCost = 0d;
		int totalMan = 0;
		
		
		for(int activityId : actMap.keySet()){
			int cnt = 1;
			for(int locationId : actMap.get(activityId).keySet()){
				
				ActivityTransactions ac = actMap.get(activityId).get(locationId);
					
				Reports rpt = new Reports();
				
				if(cnt==1){
					rpt.setF1(ac.getActivity().getName());
					rpt.setF2(ac.getLocation().getName());
					rpt.setF3(ac.getBlocks());
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(ac.getCuts());
					rpt.setF8(ac.getEmployeeTimeSheets().getTimeIn() + "-"+ ac.getEmployeeTimeSheets().getTimeOut());
					rpt.setF4(ac.getTotalMandays()+"");
					rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
					totalCost +=ac.getTotalActivityExpenses();
					totalMan +=ac.getTotalMandays();
					
				}else{
					
					rpt.setF1("");
					rpt.setF2(ac.getLocation().getName());
					
					rpt.setF3(ac.getBlocks());
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(ac.getCuts());
					rpt.setF8(ac.getEmployeeTimeSheets().getTimeIn() + "-"+ ac.getEmployeeTimeSheets().getTimeOut());
					rpt.setF4(ac.getTotalMandays()+"");
					rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
					totalCost +=ac.getTotalActivityExpenses();
					totalMan +=ac.getTotalMandays();
					
				}
				reports.add(rpt);
				cnt++;
				
			}
			
		}
		
		
		/*double totalCost = 0d;
		int totalMan = 0;
		
		for(long idActivity : acs.keySet()){
			int cnt = 1;
			for(int idLocation : acs.get(idActivity).keySet()){
				
				for(long idEmployee : acs.get(idActivity).get(idLocation).keySet()){
					
					
					for(ActivityTransactions ac : acs.get(idActivity).get(idLocation).get(idEmployee)){
						
						Reports rpt = new Reports();
						
						if(cnt==1){
							rpt.setF1(ac.getActivity().getName());
							rpt.setF2(ac.getLocation().getName());
							rpt.setF3(ac.getBlocks());
							rpt.setF6(ac.getDateTrans());
							rpt.setF7(ac.getCuts());
							rpt.setF8(ac.getEmployeeTimeSheets().getTimeIn() + "-"+ ac.getEmployeeTimeSheets().getTimeOut());
							rpt.setF4("1");
							rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
							totalCost +=ac.getTotalActivityExpenses();
							totalMan +=1;
							
						}else{
							
							rpt.setF1("");
							rpt.setF2(ac.getLocation().getName());
							
							rpt.setF3(ac.getBlocks());
							rpt.setF6(ac.getDateTrans());
							rpt.setF7(ac.getCuts());
							rpt.setF8(ac.getEmployeeTimeSheets().getTimeIn() + "-"+ ac.getEmployeeTimeSheets().getTimeOut());
							rpt.setF4("1");
							rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
							totalCost +=ac.getTotalActivityExpenses();
							totalMan +=1;
							
						}
						reports.add(rpt);
						cnt++;
						
					}
					
				}
				
			}
			
		}*/
		setTotalMandays(totalMan);
		setTotalActivityCost(Currency.formatAmount(totalCost));
		
	}
	
	public void loadAreaSummary(){
		
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		//Transactions
		List<ActivityTransactions> trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
		//Employee
		Map<Long, List<ActivityTransactions>> emps = Collections.synchronizedMap(new HashMap<Long,List<ActivityTransactions>>());
		//Area
		Map<Integer,Map<Long,List<ActivityTransactions>>> areas = Collections.synchronizedMap(new HashMap<Integer,Map<Long,List<ActivityTransactions>>>());
		//Activities
		Map<Integer,Map<Integer,Map<Long,List<ActivityTransactions>>>> acs = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,Map<Long,List<ActivityTransactions>>>>());
		
		String sql = " AND tme.isactivetime=1 AND ac.isactiveactrans=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			
			params = new String[1];
			sql = "SELECT * FROM activity WHERE acid=?";
			params[0] = time.getActivityTransactions().getActivity().getId()+"";
			if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
				sql += " AND acname like '%"+ getSearchDescription().replace("--", "") +"%'";
			}
			for(Activity act : Activity.retrieve(sql, params)){
				
				params = new String[1];
				sql =  " AND act.actransid=? AND act.isactiveactrans=1";
				params[0] = time.getActivityTransactions().getId()+"";
				List<LocationTransactions> loctran = LocationTransactions.retrieve(sql, params);
				
				
				ActivityTransactions activity = time.getActivityTransactions();
				activity.setActivity(act);
				activity.setLocation(loctran.get(0).getLocation());
				activity.setLocationTransactions(loctran.get(0));
				List<TimeSheets> tme = Collections.synchronizedList(new ArrayList<TimeSheets>());
				tme.add(time);
				activity.setTimeSheets(tme);
				activity.setEmployeeTimeSheets(time);
				
				long empId = time.getEmployee().getId();
				int locId = activity.getLocation().getId();
				int acId = act.getId();
				
				if(acs!=null && acs.size()>0){
					
					if(acs.containsKey(locId)){
						
						if(acs.get(locId).containsKey(acId)){
							
							if(acs.get(locId).get(acId).containsKey(empId)){
								
								String drums = activity.getDrums();
								String cuts = activity.getCuts();
								String blocks = activity.getBlocks();
								
								
								Object[] obj = calculateSalaryEmployee(activity);
								double expenses = (Double)obj[2];
								int man = 0;
								for(ActivityTransactions at : acs.get(locId).get(acId).get(empId)){
									drums +=","+at.getDrums();
									cuts +=","+at.getCuts();
									blocks +=","+at.getBlocks();
									expenses += at.getTotalActivityExpenses();
									man += at.getTotalMandays();
								}
								
								activity.setTotalMandays(man+1);
								activity.setDrums(drums);
								activity.setCuts(cuts);
								activity.setBlocks(blocks);
								activity.setTotalActivityExpenses(expenses);
								trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
								trans.add(activity);
								acs.get(locId).get(acId).remove(empId);
								acs.get(locId).get(acId).put(empId, trans);
								
								
							}else{
								
								Object[] obj = calculateSalaryEmployee(activity);
								double expenses = (Double)obj[2];
								activity.setTotalActivityExpenses(expenses);
								activity.setTotalMandays(1);
								
								trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
								trans.add(activity);
								acs.get(locId).get(acId).put(empId, trans);
								
							}
							
						}else{
							
							Object[] obj = calculateSalaryEmployee(activity);
							double expenses = (Double)obj[2];
							activity.setTotalActivityExpenses(expenses);
							activity.setTotalMandays(1);
							
							trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
							trans.add(activity);
							emps = Collections.synchronizedMap(new HashMap<Long,List<ActivityTransactions>>());
							emps.put(empId, trans);
							acs.get(locId).put(acId, emps);
							
						}
						
					}else{
						
						Object[] obj = calculateSalaryEmployee(activity);
						double expenses = (Double)obj[2];
						activity.setTotalActivityExpenses(expenses);
						activity.setTotalMandays(1);
						
						trans = Collections.synchronizedList(new ArrayList<ActivityTransactions>());
						trans.add(activity);
						emps = Collections.synchronizedMap(new HashMap<Long,List<ActivityTransactions>>());
						emps.put(empId, trans);
						areas = Collections.synchronizedMap(new HashMap<Integer,Map<Long,List<ActivityTransactions>>>());
						areas.put(acId, emps);
						
						acs.put(locId, areas);
						
					}
					
				}else{
					
					Object[] obj = calculateSalaryEmployee(activity);
					double expenses = (Double)obj[2];
					activity.setTotalActivityExpenses(expenses);
					activity.setTotalMandays(1);
					
					trans.add(activity);
					emps.put(empId, trans);
					areas.put(acId, emps);
					acs.put(locId, areas);
					
				}
				
				
			}
			
		}
		
		Map<Integer, ActivityTransactions> areaMap = Collections.synchronizedMap(new HashMap<Integer,ActivityTransactions>());
		Map<Integer, Map<Integer,ActivityTransactions>> actMap = Collections.synchronizedMap(new HashMap<Integer,Map<Integer,ActivityTransactions>>());
		
		for(int acxId : acs.keySet()){
			
			for(int locxId : acs.get(acxId).keySet()){
				
				for(long emxId : acs.get(acxId).get(locxId).keySet()){
					
					for(ActivityTransactions actX : acs.get(acxId).get(locxId).get(emxId)){
						
						if(areaMap!=null && areaMap.size()>0){
							
							if(actMap.containsKey(acxId)){
								
								if(actMap.get(acxId).containsKey(locxId)){
									
									String drums = actX.getDrums();
									String cuts = actX.getCuts();
									String blocks = actX.getBlocks();
									double cost = actX.getTotalActivityExpenses();
									int mandays = actX.getTotalMandays();
									
									ActivityTransactions actNow = actMap.get(acxId).get(locxId);
									drums +="," + actNow.getDrums();
									cuts +="," + actNow.getCuts();
									blocks +="," + actNow.getBlocks();
									cost += actNow.getTotalActivityExpenses();
									mandays += actNow.getTotalMandays();
									
									blocks = orderNumber(blocks);
									cuts = orderNumber(cuts);
									
									actNow.setDrums(drums);
									actNow.setCuts(cuts);
									actNow.setBlocks(blocks);
									actNow.setTotalActivityExpenses(cost);
									actNow.setTotalMandays(mandays);
									
									actMap.get(acxId).put(locxId, actNow);
									
								}else{
									String blocks = actX.getBlocks();
									String cuts = actX.getCuts();
									blocks = orderNumber(blocks);
									cuts = orderNumber(cuts);
									actX.setBlocks(blocks);
									actX.setCuts(cuts);
									
									//actX.setTotalMandays(1);
									actMap.get(acxId).put(locxId, actX);
									
								}
								
							}else{
								
								String blocks = actX.getBlocks();
								String cuts = actX.getCuts();
								blocks = orderNumber(blocks);
								cuts = orderNumber(cuts);
								actX.setBlocks(blocks);
								actX.setCuts(cuts);
								
								//actX.setTotalMandays(1);
								areaMap = Collections.synchronizedMap(new HashMap<Integer,ActivityTransactions>());
								areaMap.put(locxId, actX);
								actMap.put(acxId, areaMap);
							}
							
						}else{
							
							String blocks = actX.getBlocks();
							String cuts = actX.getCuts();
							blocks = orderNumber(blocks);
							cuts = orderNumber(cuts);
							actX.setBlocks(blocks);
							actX.setCuts(cuts);
							
							//actX.setTotalMandays(1);
							areaMap.put(locxId, actX);
							actMap.put(acxId, areaMap);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		double totalCost = 0d;
		int totalMan = 0;
		
		
		for(int activityId : actMap.keySet()){
			int cnt = 1;
			for(int locationId : actMap.get(activityId).keySet()){
				
				ActivityTransactions ac = actMap.get(activityId).get(locationId);
					
				Reports rpt = new Reports();
				
				if(cnt==1){
					rpt.setF1(ac.getLocation().getName());
					rpt.setF2(ac.getActivity().getName());
					rpt.setF3(ac.getBlocks());
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(ac.getCuts());
					rpt.setF8(ac.getEmployeeTimeSheets().getTimeIn() + "-"+ ac.getEmployeeTimeSheets().getTimeOut());
					rpt.setF4(ac.getTotalMandays()+"");
					rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
					totalCost +=ac.getTotalActivityExpenses();
					totalMan +=ac.getTotalMandays();
					
				}else{
					
					rpt.setF1("");
					rpt.setF2(ac.getActivity().getName());
					
					rpt.setF3(ac.getBlocks());
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(ac.getCuts());
					rpt.setF8(ac.getEmployeeTimeSheets().getTimeIn() + "-"+ ac.getEmployeeTimeSheets().getTimeOut());
					rpt.setF4(ac.getTotalMandays()+"");
					rpt.setF5(Currency.formatAmount(ac.getTotalActivityExpenses()));
					totalCost +=ac.getTotalActivityExpenses();
					totalMan +=ac.getTotalMandays();
					
				}
				reports.add(rpt);
				cnt++;
				
			}
			
		}
		
		
		
		setTotalMandays(totalMan);
		setTotalActivityCost(Currency.formatAmount(totalCost));
		
	}
	
	private String orderNumber(String val){
		String values = "";
		String others = "";
		try{
			val = val.trim();
			Map<Integer, Integer> valMap = Collections.synchronizedMap(new HashMap<Integer,Integer>());
			Map<String, String> othMap = Collections.synchronizedMap(new HashMap<String,String>());
			
			for(String bl : val.split(",")){
				try{
				int id = Integer.valueOf(bl);
				if(id>0){
				if(valMap!=null){
					if(valMap.containsKey(id)){
						//nothing
					}else{
						valMap.put(id, id);
					}
				}else{
					valMap.put(id, id);
				}
				}
				
				}catch(NumberFormatException e){othMap.put(bl,bl);}
			}
			
			
			Map<Integer, Integer> sortValues = new TreeMap<Integer, Integer>(valMap);
			
			
			int size = sortValues.size();
			int cnt = 1;
			
			for(int s : sortValues.values()){
				
					if(cnt==size){
						values +=s;
					}else{
						values +=s+",";
					}
					cnt++;
				
			}
			
			//check if there is number added
			boolean isWithNumber = false;
			if(size>0){
				isWithNumber = true;
			}
			
			Map<String, String> sortOth = new TreeMap<String, String>(othMap);
			size = sortOth.size();
			if(size>0 && isWithNumber){
				others +=",";
			}	
			cnt = 1;
			for(String o : sortOth.values()){
				if(cnt==size){
					others +=o;
				}else{
					others +=o+",";
				}
				cnt++;
			}
			
			}catch(Exception e){values = val;}
		return values+others;
	}
	
	public void loadActivtyDetailed(){
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		Map<Integer, List<ActivityTransactions>> actives = Collections.synchronizedMap(new HashMap<Integer, List<ActivityTransactions>>()); 
		List<ActivityTransactions> acs = new ArrayList<ActivityTransactions>();
		
		String sql = " AND trn.isactiveactrans=1 AND (trn.actDateTrans>=? AND trn.actDateTrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND ac.acname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		for(ActivityTransactions ac : ActivityTransactions.retrieve(sql, params)){
			
			if(actives!=null){
				int id = ac.getActivity().getId();
				
				if(actives.containsKey(id)){
					
					acs = actives.get(id);
					acs.add(ac);
					/*acs = new ArrayList<ActivityTransactions>();
					boolean found = false;
					Employee em = ac.getEmployee();
					for(ActivityTransactions acx : actives.get(id)){
						if(em.getId()==acx.getEmployee().getId()){
							found = true;
							double hours = ac.getTotalHours() + acx.getTotalHours();
							acx.setTotalHours(hours);
							acs.add(acx);
						}else{
							acs.add(acx);
						}
					}
					
					if(!found){
						acs.add(ac);
					}*/
					
					actives.remove(id);
					actives.put(id, acs);
					
				}else{
					
					acs = new ArrayList<ActivityTransactions>();
					acs.add(ac);
					actives.put(id, acs);
					
				}
				
			}else{
				
				acs.add(ac);
				actives.put(ac.getActivity().getId(), acs);
				
			}
			
		}
		
		
		double totalCost = 0d;
		int totalMan = 0;
		for(Integer id : actives.keySet()){
			
			List<ActivityTransactions> acList = actives.get(id);
			int cnt = 1;
			for(ActivityTransactions ac : acList){
				
				Reports rpt = new Reports();
				
				if(cnt==1){
					rpt.setF1(ac.getActivity().getName());
					//rpt.setF2(ac.getDateTrans());
					
					sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
					params = new String[1];
					params[0] = ac.getId()+"";
					List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
					if(locs.size()>0){
						rpt.setF2(locs.get(0).getLocation().getName());
					}
					
					rpt.setF3(orderNumber(ac.getBlocks()));
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(orderNumber(ac.getCuts()));
					
					Object[] obj = calculateSalary(ac);
					
					if(obj.length>0){
						/*rpt.setF4((String)obj[3]+"");
						rpt.setF5((String)obj[4]+"");
						rpt.setF6((Double)obj[5]+"");*/
						rpt.setF4((Integer)obj[0]+"");
						rpt.setF5(Currency.formatAmount((Double)obj[2]));
						totalCost += (Double)obj[2];
						totalMan += (Integer)obj[0];
						rpt.setF8((String)obj[3] + "-"+ (String)obj[4]);
					}
					
					
				}else{
					
					rpt.setF1("");
					//rpt.setF2("");
					
					sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
					params = new String[1];
					params[0] = ac.getId()+"";
					List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
					if(locs.size()>0){
						rpt.setF2(locs.get(0).getLocation().getName());
					}
					
					rpt.setF3(orderNumber(ac.getBlocks()));
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(orderNumber(ac.getCuts()));
					Object[] obj = calculateSalary(ac);
					
					if(obj.length>0){
						/*rpt.setF4((String)obj[3]+"");
						rpt.setF5((String)obj[4]+"");
						rpt.setF6((Double)obj[5]+"");*/
						rpt.setF4((Integer)obj[0]+"");
						rpt.setF5(Currency.formatAmount((Double)obj[2]));
						totalCost += (Double)obj[2];
						totalMan += (Integer)obj[0];
						rpt.setF8((String)obj[3] + "-"+ (String)obj[4]);
					}
					
				}
				reports.add(rpt);
				cnt++;
			}
			
		}
		//Collections.reverse(reports);
		setTotalMandays(totalMan);
		setTotalActivityCost(Currency.formatAmount(totalCost));
	}
	
	@Deprecated
	public void loadActivity(){
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		Map<Integer, Map<String, List<ActivityTransactions>>> activeMap = Collections.synchronizedMap(new HashMap<Integer, Map<String, List<ActivityTransactions>>>());
		Map<String, List<ActivityTransactions>> actives = Collections.synchronizedMap(new HashMap<String, List<ActivityTransactions>>()); 
		List<ActivityTransactions> acs = new ArrayList<ActivityTransactions>();
		
		String sql = " AND trn.isactiveactrans=1 AND (trn.actDateTrans>=? AND trn.actDateTrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		for(ActivityTransactions ac : ActivityTransactions.retrieve(sql, params)){
			
			if(activeMap!=null){
				
				int id = ac.getActivity().getId();
				
				if(activeMap.containsKey(id)){
					
					String date = ac.getDateTrans();
					
					if(activeMap.get(id).containsKey(date)){
						
						acs = activeMap.get(id).get(date);
						acs.add(ac);
						
						activeMap.remove(id);
						actives.put(date, acs);
						activeMap.put(id, actives);
						
					}else{
						
						acs = new ArrayList<ActivityTransactions>();
						acs.add(ac);
						activeMap.get(id).put(date, acs);
					}
					
					
				}else{
					actives = Collections.synchronizedMap(new HashMap<String, List<ActivityTransactions>>());
					acs = new ArrayList<ActivityTransactions>();
					acs.add(ac);
					actives.put(ac.getDateTrans(), acs);
					activeMap.put(ac.getActivity().getId(), actives);
				}
				
			}else{
				acs.add(ac);
				actives.put(ac.getDateTrans(), acs);
				activeMap.put(ac.getActivity().getId(), actives);
				
			}
			
		}
		double totalCost = 0d;
		for(Integer id : activeMap.keySet()){
			
			Map<String, List<ActivityTransactions>> activity = activeMap.get(id);
			
			for(String date : activity.keySet()){
				
				List<ActivityTransactions> acList = activeMap.get(id).get(date);
				int cnt = 1;
				for(ActivityTransactions ac : acList){
					
					Reports rpt = new Reports();
					
					if(cnt==1){
						rpt.setF1(ac.getActivity().getName());
						rpt.setF2(ac.getDateTrans());
						
						sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
						params = new String[1];
						params[0] = ac.getId()+"";
						List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
						if(locs.size()>0){
							rpt.setF3(locs.get(0).getLocation().getName());
						}
						
						rpt.setF4(ac.getBlocks());
						
						Object[] obj = calculateSalary(ac);
						
						if(obj.length>0){
							/*rpt.setF4((String)obj[3]+"");
							rpt.setF5((String)obj[4]+"");
							rpt.setF6((Double)obj[5]+"");*/
							rpt.setF5((Integer)obj[0]+"");
							rpt.setF6(Currency.formatAmount((Double)obj[2]));
							totalCost += (Double)obj[2];
						}
						
						
					}else{
						
						rpt.setF1("");
						rpt.setF2("");
						
						sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
						params = new String[1];
						params[0] = ac.getId()+"";
						List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
						if(locs.size()>0){
							rpt.setF3(locs.get(0).getLocation().getName());
						}
						
						rpt.setF4(ac.getBlocks());
						
						Object[] obj = calculateSalary(ac);
						
						if(obj.length>0){
							/*rpt.setF4((String)obj[3]+"");
							rpt.setF5((String)obj[4]+"");
							rpt.setF6((Double)obj[5]+"");*/
							rpt.setF5((Integer)obj[0]+"");
							rpt.setF6(Currency.formatAmount((Double)obj[2]));
							totalCost += (Double)obj[2];
						}
						
					}
					reports.add(rpt);
					cnt++;
				}
				
			}
			
			
		}
		
		setTotalActivityCost(Currency.formatAmount(totalCost));
		
		/*for(ActivityTransactions ac : ActivityTransactions.retrieve(sql, params)){
			Reports rpt = new Reports();
			
			rpt.setF1(ac.getActivity().getName());
			
			sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
			params = new String[1];
			params[0] = ac.getId()+"";
			List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
			if(locs.size()>0){
				rpt.setF2(locs.get(0).getLocation().getName());
			}
			
			Object[] obj = calculateSalary(ac);
			
			if(obj.length>0){
				rpt.setF3((Integer)obj[0]+"");
				rpt.setF4(Currency.formatAmount((Double)obj[2]));
			}
			
			reports.add(rpt);
		}*/
		
	}
	
	public void loadLocationSort(){
		
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		Map<Integer, List<ActivityTransactions>> locations = Collections.synchronizedMap(new HashMap<Integer, List<ActivityTransactions>>()); 
		List<ActivityTransactions> area = new ArrayList<ActivityTransactions>();
		
		String sql = " AND lct.isactiveloctrans=1 AND (lct.locDateTrans>=? AND lct.locDateTrans<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND lc.locname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		for(LocationTransactions loc : LocationTransactions.retrieve(sql, params)){
			
			ActivityTransactions ac = ActivityTransactions.retrieve(loc.getActivityTransactions().getId());
			ac.setLocationTransactions(loc);
			if(locations!=null){
				
				int id = loc.getLocation().getId();
				
				if(locations.containsKey(id)){
					
					area = locations.get(id);
					area.add(ac);
					locations.remove(id);
					locations.put(id, area);
					
				}else{
					
					area = new ArrayList<ActivityTransactions>();
					area.add(ac);
					locations.put(loc.getLocation().getId(), area);
					
				}
				
			}else{
				
				area.add(ac);
				locations.put(loc.getLocation().getId(), area);
				
			}
			
		}
		
		double totalCost = 0d;
		int totalMan = 0;
		for(Integer id : locations.keySet()){
			
			List<ActivityTransactions> acList = locations.get(id);
			int cnt = 1;
			for(ActivityTransactions ac : acList){
				
				Reports rpt = new Reports();
				
				if(cnt==1){
					rpt.setF1(ac.getLocationTransactions().getLocation().getName());
					rpt.setF2(ac.getActivity().getName());
					rpt.setF3(orderNumber(ac.getBlocks()));
					
					Object[] obj = calculateSalary(ac);
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(orderNumber(ac.getCuts()));
					if(obj.length>0){
						/*rpt.setF4((String)obj[3]+"");
						rpt.setF5((String)obj[4]+"");
						rpt.setF6((Double)obj[5]+"");*/
						rpt.setF4((Integer)obj[0]+"");
						rpt.setF5(Currency.formatAmount((Double)obj[2]));
						totalCost += (Double)obj[2];
						totalMan += (Integer)obj[0];
						rpt.setF8((String)obj[3]+"-"+(String)obj[4]);
					}
					
					
				}else{
					
					rpt.setF1("");
					rpt.setF2(ac.getActivity().getName());
					
					rpt.setF3(orderNumber(ac.getBlocks()));
					
					Object[] obj = calculateSalary(ac);
					rpt.setF6(ac.getDateTrans());
					rpt.setF7(orderNumber(ac.getCuts()));
					if(obj.length>0){
						/*rpt.setF4((String)obj[3]+"");
						rpt.setF5((String)obj[4]+"");
						rpt.setF6((Double)obj[5]+"");*/
						rpt.setF4((Integer)obj[0]+"");
						rpt.setF5(Currency.formatAmount((Double)obj[2]));
						totalCost += (Double)obj[2];
						totalMan += (Integer)obj[0];
						rpt.setF8((String)obj[3]+"-"+(String)obj[4]);
					}
					
				}
				reports.add(rpt);
				cnt++;
			}
			
		}
		//Collections.reverse(reports);
		setTotalMandays(totalMan);
		setTotalActivityCost(Currency.formatAmount(totalCost));
	}
	
	public void loadNamesSort(){

		
		reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		Map<Long, List<ActivityTransactions>> names = Collections.synchronizedMap(new HashMap<Long, List<ActivityTransactions>>()); 
		List<ActivityTransactions> activity = new ArrayList<ActivityTransactions>();
		
		String sql = " AND tme.isactivetime=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND emp.fullname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		for(TimeSheets time : TimeSheets.retrieve(sql, params)){
			
			ActivityTransactions ac = null;
			
			try{ac = ActivityTransactions.retrieve(time.getActivityTransactions().getId());
			ac.setEmployeeTimeSheets(time);}catch(Exception e){}
			
			//Activity acty = Activity.retrieve(ac.getActivity().getId());
			//ac.setActivity(acty);
			if(names!=null && ac!=null){
				
				long id = time.getEmployee().getId();
				
				if(names.containsKey(id)){
					
					activity = names.get(id);
					activity.add(ac);
					names.remove(id);
					names.put(id, activity);
					
				}else{
					
					activity = new ArrayList<ActivityTransactions>();
					activity.add(ac);
					names.put(time.getEmployee().getId(), activity);
					
				}
				
			}else{
				
				activity.add(ac);
				names.put(time.getEmployee().getId(), activity);
				
			}
			
		}
		
		double totalCost = 0d;
		for(Long id : names.keySet()){
			
			List<ActivityTransactions> acList = names.get(id);
			int cnt = 1;
			int size = acList.size();
			double totalpayable = 0d;
			for(ActivityTransactions acs : acList){
				
				Reports rpt = new Reports();
				
				if(cnt==1){
					String name = acs.getEmployeeTimeSheets().getEmployee().getFirstName() + " " + acs.getEmployeeTimeSheets().getEmployee().getLastName();
					rpt.setF1(name);
					try{rpt.setF2(acs.getActivity().getName());}catch(Exception e){rpt.setF2("ERROR IF");}
					
					sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
					params = new String[1];
					params[0] = acs.getId()+"";
					List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
					if(locs.size()>0){
						rpt.setF3(locs.get(0).getLocation().getName());
					}
					
					rpt.setF4(orderNumber(acs.getBlocks()));
					double serviceAmount = calculateSalary(acs.getEmployeeTimeSheets(),acs);
					rpt.setF5(Currency.formatAmount(serviceAmount));
					totalCost += serviceAmount;
					totalpayable += serviceAmount; 
					
					rpt.setF6(acs.getDateTrans());
					rpt.setF7(orderNumber(acs.getCuts()));
					rpt.setF8(acs.getEmployeeTimeSheets().getTimeIn()+"-"+acs.getEmployeeTimeSheets().getTimeOut());
				}else{
					
					rpt.setF1("");
					try{rpt.setF2(acs.getActivity().getName());}catch(Exception e){rpt.setF2("ERROR ELSE");}
					
					sql = " AND lct.isactiveloctrans=1 AND act.actransid=?";
					params = new String[1];
					params[0] = acs.getId()+"";
					List<LocationTransactions> locs = LocationTransactions.retrieve(sql, params);
					if(locs.size()>0){
						rpt.setF3(locs.get(0).getLocation().getName());
					}
					
					rpt.setF4(orderNumber(acs.getBlocks()));
					
					double serviceAmount = calculateSalary(acs.getEmployeeTimeSheets(),acs);
					rpt.setF5(Currency.formatAmount(serviceAmount));
					totalCost += serviceAmount;
					totalpayable += serviceAmount;
					rpt.setF6(acs.getDateTrans());
					rpt.setF7(orderNumber(acs.getCuts()));
					rpt.setF8(acs.getEmployeeTimeSheets().getTimeIn()+"-"+acs.getEmployeeTimeSheets().getTimeOut());
				}
				
				reports.add(rpt);
				
				if(cnt==size){
					rpt = new Reports();
					rpt.setF1("Total");
					rpt.setF2("");
					rpt.setF3("");
					rpt.setF4("");
					rpt.setF5("Php "+Currency.formatAmount(totalpayable));
					reports.add(rpt);
				}
				
				
				cnt++;
			}
			
		}	
		setTotalActivityCost(Currency.formatAmount(totalCost));
	
	}
	
	/*private double calculateSalary(TimeSheets time){
		
		Employee em = time.getEmployee();
		
		double servicesAmnt = 0d;
		double hourlyRate = 0d;
		double dailyRate = 0d;
		double hrsWork = 0d;
		try{
			dailyRate = em.getSalary();
			hourlyRate = dailyRate/HOUR_IN_DAY;
			hrsWork = time.getCountHour();
			servicesAmnt = hourlyRate * hrsWork;
			//em.setDailyRate(Currency.formatAmount(dailyRate));
			//em.setHourlyRate(Currency.formatAmount(hourlyRate));
			//em.setPayableServices(Currency.formatAmount(servicesAmnt));
			
		}catch(Exception e){}
		
		return servicesAmnt;
	}*/
	
private double calculateSalary(TimeSheets time, ActivityTransactions act){
		
		Employee em = time.getEmployee();
		
		double servicesAmnt = 0d;
		double hourlyRate = 0d;
		double dailyRate = 0d;
		double hrsWork = 0d;
		double overTimeAmount = 0d;
		
		try{
			dailyRate = em.getSalary();
			hourlyRate = dailyRate/HOUR_IN_DAY;
			//hrsWork = getTimeOutId() - getTimeInId();
			//servicesAmnt = hourlyRate * hrsWork;
			//em.setDailyRate(Currency.formatAmount(dailyRate));
			//em.setHourlyRate(Currency.formatAmount(hourlyRate));
			
			String forcing = "";
			try{forcing = act.getActivity().getName();}catch(Exception e){
				Activity activity = Activity.retrieve(act.getActivity().getId());
				forcing = activity.getName();
			}
			
			
			if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
				String sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
				String[] params = new String[1];
				params[0] = act.getId()+"";
				double drums = Double.valueOf(act.getDrums());
				double totalEmployee = TimeSheets.retrieve(sql, params).size();
				double totalRate = drums * FORCING_RATE_PER_DRUM;
				double salaryPerEmployee = totalRate / totalEmployee;
				
				hourlyRate = FORCING_RATE_PER_DRUM;
				servicesAmnt = salaryPerEmployee;
			
			}else if((act.getDrums()!=null) && ("Spray Foliar".equalsIgnoreCase(forcing) 
					|| "Spray Fungicide".equalsIgnoreCase(forcing)
					|| "Spray Herbicide".equalsIgnoreCase(forcing)
					|| "Spray Insecticide".equalsIgnoreCase(forcing)
					|| "Spray Foliar Sucker".equalsIgnoreCase(forcing))) {	
				
				String sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
				String[] params = new String[1];
				params[0] = act.getId()+"";
				double drums = Double.valueOf(act.getDrums());
				double totalEmployee = TimeSheets.retrieve(sql, params).size();
				double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
				double salaryPerEmployee = totalRate / totalEmployee;
				
				hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
				servicesAmnt = salaryPerEmployee;
				
				
			}else{
			
			if(JobTitle.DRIVER.getId()==em.getJob().getJobid()){
				
				if(act.getDrums()!=null){
					em.setOvertime(FIELD_SPRAY_DRIVER_RATE);//change as there is a case where field driver can be use as spray driver
				}
				
				//overtime
				if("3".equalsIgnoreCase(act.getLoads())){
					
					if("5".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{//normal time
				
					if("5".equalsIgnoreCase(act.getDrums())){
						hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
						servicesAmnt = hourlyRate * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}else{
						
						double timeInAfternoon = Time.typeId(time.getTimeIn());
						double timeOutAfterNoon = Time.typeId(time.getTimeOut());
						hrsWork = time.getCountHour();
						
						//tag as overtime
						if(time.getIsOvertime()==1){
						
							servicesAmnt = hrsWork * em.getOvertime();
						
						}else{
						
							if(timeOutAfterNoon>OVERTIME_START){
								servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
								overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
							}else{
								servicesAmnt = hourlyRate * hrsWork;
							}
						
						}
						/*hrsWork = time.getCountHour();
						//normal driver OT in FIELD
						if(hrsWork>HOUR_IN_DAY){
							servicesAmnt = HOUR_IN_DAY * hrsWork;
							overTimeAmount = (hrsWork - HOUR_IN_DAY) * NORMAL_OT;
						}else{
							servicesAmnt = hourlyRate * hrsWork;
						}*/
					
					}
				
				}
				
				//servicesAmnt = dailyRate;
				servicesAmnt += overTimeAmount;
				
			}else if(JobTitle.LEAD_MAN.getId()==em.getJob().getJobid() || JobTitle.SPRAY_MAN.getId()==em.getJob().getJobid()){
				
				//overtime
				if("3".equalsIgnoreCase(act.getLoads())){
					
					if("5".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{//normal time
				
					if("5".equalsIgnoreCase(act.getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						hourlyRate = dailyRate/PER_DRUM_DIVIDER;
						servicesAmnt = hourlyRate * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						hrsWork = 4.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 2;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						hrsWork = 8.0;
						servicesAmnt = hourlyRate * hrsWork;
						overTimeAmount = em.getOvertime() * 3;
					}else{
					
					double timeInAfternoon = Time.typeId(time.getTimeIn());
					double timeOutAfterNoon = Time.typeId(time.getTimeOut());
					hrsWork = time.getCountHour();
					
					//tag as overtime
					if(time.getIsOvertime()==1){
					
						servicesAmnt = hrsWork * em.getOvertime();
					
					}else{
					
						if(timeOutAfterNoon>OVERTIME_START){
							servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
							overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
						}else{
							servicesAmnt = hourlyRate * hrsWork;
						}
					
					}
					/*hrsWork = time.getCountHour();
					//normal LEAD ans SPRAY OT in FIELD
					if(hrsWork>HOUR_IN_DAY){
						servicesAmnt = HOUR_IN_DAY * hrsWork;
						overTimeAmount = (hrsWork - HOUR_IN_DAY) * NORMAL_OT;
					}else{
						servicesAmnt = hourlyRate * hrsWork;
					}*/
					
				}
				
				}
				
				//servicesAmnt = dailyRate;
				servicesAmnt += overTimeAmount;
			}else{
				
				//identify labor to sprayman
				if(act.getLoads()!=null && act.getDrums()!=null){
					dailyRate = LABOR_SPRAYMAN_DAILY_RATE;
					hourlyRate = dailyRate/HOUR_IN_DAY;	
				}
				
				if(HARVESTER.equalsIgnoreCase(act.getActivity().getName())){
					dailyRate = LABOR_HARVESTER_DAILY_RATE;
					hourlyRate = dailyRate/HOUR_IN_DAY;
				}
				
				//overtime
				if("3".equalsIgnoreCase(act.getLoads())){
					
					if("5".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 1;
					}else if("10".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 2;
					}else if("15".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 3;
					}else if("20".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 4;
					}else if("25".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 5;
					}else if("30".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 6;
					}else if("35".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 7;
					}else if("40".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 8;
					}else if("45".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 9;
					}else if("50".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 10;
					}else if("55".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 11;
					}else if("60".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 12;
					}else if("65".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 13;
					}else if("70".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 14;
					}else if("75".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 15;
					}else if("80".equalsIgnoreCase(act.getDrums())){
						overTimeAmount = em.getOvertime() * 16;
					}
					
				}else{//normal time
					
					//spray activity
					if("1".equalsIgnoreCase(act.getLoads()) || "2".equalsIgnoreCase(act.getLoads())){
						
						if("5".equalsIgnoreCase(act.getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate;
						}else if("10".equalsIgnoreCase(act.getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate * 2;
						}else  if("15".equalsIgnoreCase(act.getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(act.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(act.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(act.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}
						
					}else{
					//field activity
					
					double timeInAfternoon = Time.typeId(time.getTimeIn());
					double timeOutAfterNoon = Time.typeId(time.getTimeOut());
					hrsWork = time.getCountHour();
					
					//tag as overtime
					if(time.getIsOvertime()==1){
					
						servicesAmnt = hrsWork * em.getOvertime();
					
					}else{
					
						if(timeOutAfterNoon>OVERTIME_START){
							servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
							overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
						}else{
							servicesAmnt = hourlyRate * hrsWork;
						}
					
					}
					
					}
				}
				
			
				
				servicesAmnt += overTimeAmount;
			}
			
			}
			
			
		}catch(Exception e){}
		
		return servicesAmnt;
	}
	
	/*private Employee calculateSalary(TimeSheets time){
		
		Employee em = time.getEmployee();
		
		double servicesAmnt = 0d;
		double hourlyRate = 0d;
		double dailyRate = 0d;
		double hrsWork = 0d;
		try{
			dailyRate = em.getSalary();
			hourlyRate = dailyRate/HOUR_IN_DAY;
			hrsWork = time.getMorningCountHour();
			servicesAmnt = hourlyRate * hrsWork;
			em.setDailyRate(Currency.formatAmount(dailyRate));
			em.setHourlyRate(Currency.formatAmount(hourlyRate));
			em.setPayableServices(Currency.formatAmount(servicesAmnt));
			
		}catch(Exception e){}
		
		return em;
	}*/
	
	private Object[] calculateSalaryEmployee(ActivityTransactions trans){
	
	Object[] objs = new Object[6];
	
	
	//List<TimeSheets> times = TimeSheets.retrieve(sql, params);
	List<TimeSheets> timesheets = Collections.synchronizedList(new ArrayList<TimeSheets>());
	double totalExpenses = 0d;
	int size = trans.getTimeSheets().size();
	String timeIn = "";
	String timeOut = "";
	int cnt = 1;
	double totalHr = 0d;
	for(TimeSheets time : trans.getTimeSheets()){
		
		if(cnt==1){
			timeIn = time.getTimeIn();
			timeOut = time.getTimeOut();
			totalHr = time.getCountHour();
		}
		cnt++;
		
		Employee em = time.getEmployee();
			double servicesAmnt = 0d;
			double hourlyRate = 0d;
			double dailyRate = 0d;
			double hrsWork = 0d;
			double overTimeAmount = 0d;
			try{
				
				dailyRate = em.getSalary();
				hourlyRate = dailyRate/HOUR_IN_DAY;
				//hrsWork = time.getCountHour();
				//servicesAmnt = hourlyRate * hrsWork;
				/*em.setDailyRate(Currency.formatAmount(dailyRate));
				em.setHourlyRate(Currency.formatAmount(hourlyRate));*/
				
				
				
				//hrsWork = getTimeOutId() - getTimeInId();
				//servicesAmnt = hourlyRate * hrsWork;
				//em.setDailyRate(Currency.formatAmount(dailyRate));
				//em.setHourlyRate(Currency.formatAmount(hourlyRate));
				
				
				
				String forcing = "";
				try{forcing = trans.getActivity().getName();}catch(Exception e){
					Activity activity = Activity.retrieve(trans.getActivity().getId());
					forcing = activity.getName();
				}
				
				
				if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
					String sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
					String[] params = new String[1];
					params[0] = trans.getId()+"";
					double drums = Double.valueOf(trans.getDrums());
					double totalEmployee = TimeSheets.retrieve(sql, params).size();
					double totalRate = drums * FORCING_RATE_PER_DRUM;
					double salaryPerEmployee = totalRate / totalEmployee;
					
					hourlyRate = FORCING_RATE_PER_DRUM;
					servicesAmnt = salaryPerEmployee;
				
				}else if((trans.getDrums()!=null) && ("Spray Foliar".equalsIgnoreCase(forcing) 
						|| "Spray Fungicide".equalsIgnoreCase(forcing)
						|| "Spray Herbicide".equalsIgnoreCase(forcing)
						|| "Spray Insecticide".equalsIgnoreCase(forcing)
						|| "Spray Foliar Sucker".equalsIgnoreCase(forcing))) {	
					
					
					String sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
					String[] params = new String[1];
					params[0] = trans.getId()+"";
					double drums = Double.valueOf(trans.getDrums());
					double totalEmployee = TimeSheets.retrieve(sql, params).size();
					double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
					double salaryPerEmployee = totalRate / totalEmployee;
					
					hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
					servicesAmnt = salaryPerEmployee;
					
				}else{
				
				
				if(JobTitle.DRIVER.getId()==em.getJob().getJobid()){
					
					if(trans.getDrums()!=null){
						em.setOvertime(FIELD_SPRAY_DRIVER_RATE);//change as there is a case where field driver can be use as spray driver
					}
					
					//overtime
					if("3".equalsIgnoreCase(trans.getLoads())){
						
						if("5".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 1;
						}else if("10".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 2;
						}else if("15".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 3;
						}else if("20".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 4;
						}else if("25".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 5;
						}else if("30".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 6;
						}else if("35".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 7;
						}else if("40".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 8;
						}else if("45".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 9;
						}else if("50".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 10;
						}else if("55".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 11;
						}else if("60".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 12;
						}else if("65".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 13;
						}else if("70".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 14;
						}else if("75".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 15;
						}else if("80".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 16;
						}
						
					}else{//normal time
					
						if("5".equalsIgnoreCase(trans.getDrums())){
							hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
							servicesAmnt = hourlyRate;
						}else if("10".equalsIgnoreCase(trans.getDrums())){
							hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
							servicesAmnt = hourlyRate * 2;
						}else if("15".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}else{
							
							double timeInAfternoon = Time.typeId(time.getTimeIn());
							double timeOutAfterNoon = Time.typeId(time.getTimeOut());
							hrsWork = time.getCountHour();
							
							//tag as overtime
							if(time.getIsOvertime()==1){
							
								servicesAmnt = hrsWork * em.getOvertime();
							
							}else{
								
								if(timeOutAfterNoon>OVERTIME_START){
									servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
									overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
								}else{
									servicesAmnt = hourlyRate * hrsWork;
								}
								
							}
							
							/*hrsWork = time.getCountHour();
							//normal driver OT in FIELD
							if(hrsWork>HOUR_IN_DAY){
								servicesAmnt = HOUR_IN_DAY * hrsWork;
								overTimeAmount = (hrsWork - HOUR_IN_DAY) * NORMAL_OT;
							}else{
								servicesAmnt = hourlyRate * hrsWork;
							}*/
						
						}
					
					}
					
					//servicesAmnt = dailyRate;
					servicesAmnt += overTimeAmount;
					
				}else if(JobTitle.LEAD_MAN.getId()==em.getJob().getJobid() || JobTitle.SPRAY_MAN.getId()==em.getJob().getJobid()){
					
					//overtime
					if("3".equalsIgnoreCase(trans.getLoads())){
						
						if("5".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 1;
						}else if("10".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 2;
						}else if("15".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 3;
						}else if("20".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 4;
						}else if("25".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 5;
						}else if("30".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 6;
						}else if("35".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 7;
						}else if("40".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 8;
						}else if("45".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 9;
						}else if("50".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 10;
						}else if("55".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 11;
						}else if("60".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 12;
						}else if("65".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 13;
						}else if("70".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 14;
						}else if("75".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 15;
						}else if("80".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 16;
						}
						
					}else{//normal time
					
						if("5".equalsIgnoreCase(trans.getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate;
						}else if("10".equalsIgnoreCase(trans.getDrums())){
							hourlyRate = dailyRate/PER_DRUM_DIVIDER;
							servicesAmnt = hourlyRate * 2;
						}else if("15".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 4.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("30".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
						}else if("40".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 2;
						}else if("45".equalsIgnoreCase(trans.getDrums())){
							hrsWork = 8.0;
							servicesAmnt = hourlyRate * hrsWork;
							overTimeAmount = em.getOvertime() * 3;
						}else{
							
							double timeInAfternoon = Time.typeId(time.getTimeIn());
							double timeOutAfterNoon = Time.typeId(time.getTimeOut());
							hrsWork = time.getCountHour();
							
							//tag as overtime
							if(time.getIsOvertime()==1){
							
								servicesAmnt = hrsWork * em.getOvertime();
							
							}else{
							
								if(timeOutAfterNoon>OVERTIME_START){
									servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
									overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
								}else{
									servicesAmnt = hourlyRate * hrsWork;
								}
							
							}
							
							/*hrsWork = time.getCountHour();
							//normal LEAD ans SPRAY OT in FIELD
							if(hrsWork>HOUR_IN_DAY){
								servicesAmnt = HOUR_IN_DAY * hrsWork;
								overTimeAmount = (hrsWork - HOUR_IN_DAY) * em.getOvertime();
							}else{
								servicesAmnt = hourlyRate * hrsWork;
							}*/
							
						}
					
					}
					//servicesAmnt = dailyRate;
					servicesAmnt += overTimeAmount;
				}else{
					
					//identify labor to sprayman
					if(trans.getLoads()!=null && trans.getDrums()!=null){
						dailyRate = LABOR_SPRAYMAN_DAILY_RATE;
						hourlyRate = dailyRate/HOUR_IN_DAY;	
					}
					
					if(HARVESTER.equalsIgnoreCase(trans.getActivity().getName())){
						dailyRate = LABOR_HARVESTER_DAILY_RATE;
						hourlyRate = dailyRate/HOUR_IN_DAY;
					}
					
					//overtime
					if("3".equalsIgnoreCase(trans.getLoads())){
						
						if("5".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 1;
						}else if("10".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 2;
						}else if("15".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 3;
						}else if("20".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 4;
						}else if("25".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 5;
						}else if("30".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 6;
						}else if("35".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 7;
						}else if("40".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 8;
						}else if("45".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 9;
						}else if("50".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 10;
						}else if("55".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 11;
						}else if("60".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 12;
						}else if("65".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 13;
						}else if("70".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 14;
						}else if("75".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 15;
						}else if("80".equalsIgnoreCase(trans.getDrums())){
							overTimeAmount = em.getOvertime() * 16;
						}
						
					}else{//normal time
						
						//spray activity
						if("1".equalsIgnoreCase(trans.getLoads()) || "2".equalsIgnoreCase(trans.getLoads())){
							
							if("5".equalsIgnoreCase(trans.getDrums())){
								hourlyRate = dailyRate/PER_DRUM_DIVIDER;
								servicesAmnt = hourlyRate;
							}else if("10".equalsIgnoreCase(trans.getDrums())){
								hourlyRate = dailyRate/PER_DRUM_DIVIDER;
								servicesAmnt = hourlyRate * 2;
							}else  if("15".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 4.0;
								servicesAmnt = hourlyRate * hrsWork;
							}else if("30".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
							}else if("40".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
								overTimeAmount = em.getOvertime() * 2;
							}else if("45".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
								overTimeAmount = em.getOvertime() * 3;
							}
							
						}else{
						//field activity
							double timeInAfternoon = Time.typeId(time.getTimeIn());
							double timeOutAfterNoon = Time.typeId(time.getTimeOut());
							hrsWork = time.getCountHour();
							
							//tag as overtime
							if(time.getIsOvertime()==1){
							
								servicesAmnt = hrsWork * em.getOvertime();
							
							}else{
							
								if(timeOutAfterNoon>OVERTIME_START){
									servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
									overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
								}else{
									servicesAmnt = hourlyRate * hrsWork;
								}
							
							}
						
						}
					
					}
					
					servicesAmnt += overTimeAmount;
				}
				
				}
				
				em.setDailyRate(Currency.formatAmount(dailyRate));
				em.setHourlyRate(Currency.formatAmount(hourlyRate));
				
				em.setPayableServices(Currency.formatAmount(servicesAmnt));
				
				totalExpenses += servicesAmnt;
				time.setEmployee(em);
				timesheets.add(time);
			}catch(Exception e){}
	
	}
	
	objs[0] = size;
	objs[1] = timesheets;
	objs[2] = totalExpenses;
	objs[3] = timeIn;
	objs[4] = timeOut;
	objs[5] = totalHr;
	
	return objs;
}

	private Object[] calculateSalary(ActivityTransactions trans){
		
		Object[] objs = new Object[6];
		
		String sql = " AND tme.isactivetime=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?) AND ac.actransid=?";
		String[] params = new String[3];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		params[2] = trans.getId()+"";
		
		List<TimeSheets> times = TimeSheets.retrieve(sql, params);
		List<TimeSheets> timesheets = Collections.synchronizedList(new ArrayList<TimeSheets>());
		double totalExpenses = 0d;
		int size = times.size();
		String timeIn = "";
		String timeOut = "";
		int cnt = 1;
		double totalHr = 0d;
		for(TimeSheets time : times){
			
			if(cnt==1){
				timeIn = time.getTimeIn();
				timeOut = time.getTimeOut();
				totalHr = time.getCountHour();
			}
			cnt++;
			
			Employee em = time.getEmployee();
				double servicesAmnt = 0d;
				double hourlyRate = 0d;
				double dailyRate = 0d;
				double hrsWork = 0d;
				double overTimeAmount = 0d;
				try{
					
					dailyRate = em.getSalary();
					hourlyRate = dailyRate/HOUR_IN_DAY;
					//hrsWork = time.getCountHour();
					//servicesAmnt = hourlyRate * hrsWork;
					/*em.setDailyRate(Currency.formatAmount(dailyRate));
					em.setHourlyRate(Currency.formatAmount(hourlyRate));*/
					
					
					
					//hrsWork = getTimeOutId() - getTimeInId();
					//servicesAmnt = hourlyRate * hrsWork;
					//em.setDailyRate(Currency.formatAmount(dailyRate));
					//em.setHourlyRate(Currency.formatAmount(hourlyRate));
					
					String forcing = "";
					try{forcing = trans.getActivity().getName();}catch(Exception e){
						Activity activity = Activity.retrieve(trans.getActivity().getId());
						forcing = activity.getName();
					}
					
					
					if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
						sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
						params = new String[1];
						params[0] = trans.getId()+"";
						double drums = Double.valueOf(trans.getDrums());
						double totalEmployee = TimeSheets.retrieve(sql, params).size();
						double totalRate = drums * FORCING_RATE_PER_DRUM;
						double salaryPerEmployee = totalRate / totalEmployee;
						
						hourlyRate = FORCING_RATE_PER_DRUM;
						servicesAmnt = salaryPerEmployee;
					
					}else if((trans.getDrums()!=null) && ("Spray Foliar".equalsIgnoreCase(forcing) 
							|| "Spray Fungicide".equalsIgnoreCase(forcing)
							|| "Spray Herbicide".equalsIgnoreCase(forcing)
							|| "Spray Insecticide".equalsIgnoreCase(forcing)
							|| "Spray Foliar Sucker".equalsIgnoreCase(forcing))) {	
						
						
						sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
						params = new String[1];
						params[0] = trans.getId()+"";
						double drums = Double.valueOf(trans.getDrums());
						double totalEmployee = TimeSheets.retrieve(sql, params).size();
						double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
						double salaryPerEmployee = totalRate / totalEmployee;
						
						hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
						servicesAmnt = salaryPerEmployee;
						
					}else{
					
					if(JobTitle.DRIVER.getId()==em.getJob().getJobid()){
						
						if(trans.getDrums()!=null){
							em.setOvertime(FIELD_SPRAY_DRIVER_RATE);//change as there is a case where field driver can be use as spray driver
						}
						
						//overtime
						if("3".equalsIgnoreCase(trans.getLoads())){
							
							if("5".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 1;
							}else if("10".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 2;
							}else if("15".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 3;
							}else if("20".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 4;
							}else if("25".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 5;
							}else if("30".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 6;
							}else if("35".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 7;
							}else if("40".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 8;
							}else if("45".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 9;
							}else if("50".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 10;
							}else if("55".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 11;
							}else if("60".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 12;
							}else if("65".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 13;
							}else if("70".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 14;
							}else if("75".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 15;
							}else if("80".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 16;
							}
							
						}else{//normal time
						
							if("5".equalsIgnoreCase(trans.getDrums())){
								hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
								servicesAmnt = hourlyRate;
							}else if("10".equalsIgnoreCase(trans.getDrums())){
								hourlyRate = DRIVER_NORMAL_PER_DRUM_RATE;
								servicesAmnt = hourlyRate * 2;
							}else if("15".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 4.0;
								servicesAmnt = hourlyRate * hrsWork;
							}else if("30".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
							}else if("40".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
								overTimeAmount = em.getOvertime() * 2;
							}else if("45".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
								overTimeAmount = em.getOvertime() * 3;
							}else{
								
								double timeInAfternoon = Time.typeId(time.getTimeIn());
								double timeOutAfterNoon = Time.typeId(time.getTimeOut());
								hrsWork = time.getCountHour();
								
								//tag as overtime
								if(time.getIsOvertime()==1){
								
									servicesAmnt = hrsWork * em.getOvertime();
								
								}else{
									
									if(timeOutAfterNoon>OVERTIME_START){
										servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
										overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
									}else{
										servicesAmnt = hourlyRate * hrsWork;
									}
									
								}
								
								/*hrsWork = time.getCountHour();
								//normal driver OT in FIELD
								if(hrsWork>HOUR_IN_DAY){
									servicesAmnt = HOUR_IN_DAY * hrsWork;
									overTimeAmount = (hrsWork - HOUR_IN_DAY) * NORMAL_OT;
								}else{
									servicesAmnt = hourlyRate * hrsWork;
								}*/
							
							}
						
						}
						
						//servicesAmnt = dailyRate;
						servicesAmnt += overTimeAmount;
						
					}else if(JobTitle.LEAD_MAN.getId()==em.getJob().getJobid() || JobTitle.SPRAY_MAN.getId()==em.getJob().getJobid()){
						
						//overtime
						if("3".equalsIgnoreCase(trans.getLoads())){
							
							if("5".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 1;
							}else if("10".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 2;
							}else if("15".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 3;
							}else if("20".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 4;
							}else if("25".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 5;
							}else if("30".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 6;
							}else if("35".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 7;
							}else if("40".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 8;
							}else if("45".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 9;
							}else if("50".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 10;
							}else if("55".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 11;
							}else if("60".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 12;
							}else if("65".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 13;
							}else if("70".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 14;
							}else if("75".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 15;
							}else if("80".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 16;
							}
							
						}else{//normal time
						
							if("5".equalsIgnoreCase(trans.getDrums())){
								hourlyRate = dailyRate/PER_DRUM_DIVIDER;
								servicesAmnt = hourlyRate;
							}else if("10".equalsIgnoreCase(trans.getDrums())){
								hourlyRate = dailyRate/PER_DRUM_DIVIDER;
								servicesAmnt = hourlyRate * 2;
							}else if("15".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 4.0;
								servicesAmnt = hourlyRate * hrsWork;
							}else if("30".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
							}else if("40".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
								overTimeAmount = em.getOvertime() * 2;
							}else if("45".equalsIgnoreCase(trans.getDrums())){
								hrsWork = 8.0;
								servicesAmnt = hourlyRate * hrsWork;
								overTimeAmount = em.getOvertime() * 3;
							}else{
								
								double timeInAfternoon = Time.typeId(time.getTimeIn());
								double timeOutAfterNoon = Time.typeId(time.getTimeOut());
								hrsWork = time.getCountHour();
								
								//tag as overtime
								if(time.getIsOvertime()==1){
								
									servicesAmnt = hrsWork * em.getOvertime();
								
								}else{
								
									if(timeOutAfterNoon>OVERTIME_START){
										servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
										overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
									}else{
										servicesAmnt = hourlyRate * hrsWork;
									}
								
								}
								
								/*hrsWork = time.getCountHour();
								//normal LEAD ans SPRAY OT in FIELD
								if(hrsWork>HOUR_IN_DAY){
									servicesAmnt = HOUR_IN_DAY * hrsWork;
									overTimeAmount = (hrsWork - HOUR_IN_DAY) * em.getOvertime();
								}else{
									servicesAmnt = hourlyRate * hrsWork;
								}*/
								
							}
						
						}
						//servicesAmnt = dailyRate;
						servicesAmnt += overTimeAmount;
					}else{
						
						//identify labor to sprayman
						if(trans.getLoads()!=null && trans.getDrums()!=null){
							dailyRate = LABOR_SPRAYMAN_DAILY_RATE;
							hourlyRate = dailyRate/HOUR_IN_DAY;	
						}
						
						if(HARVESTER.equalsIgnoreCase(trans.getActivity().getName())){
							dailyRate = LABOR_HARVESTER_DAILY_RATE;
							hourlyRate = dailyRate/HOUR_IN_DAY;
						}
						
						//overtime
						if("3".equalsIgnoreCase(trans.getLoads())){
							
							if("5".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 1;
							}else if("10".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 2;
							}else if("15".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 3;
							}else if("20".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 4;
							}else if("25".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 5;
							}else if("30".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 6;
							}else if("35".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 7;
							}else if("40".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 8;
							}else if("45".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 9;
							}else if("50".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 10;
							}else if("55".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 11;
							}else if("60".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 12;
							}else if("65".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 13;
							}else if("70".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 14;
							}else if("75".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 15;
							}else if("80".equalsIgnoreCase(trans.getDrums())){
								overTimeAmount = em.getOvertime() * 16;
							}
							
						}else{//normal time
							
							//spray activity
							if("1".equalsIgnoreCase(trans.getLoads()) || "2".equalsIgnoreCase(trans.getLoads())){
								
								if("5".equalsIgnoreCase(trans.getDrums())){
									hourlyRate = dailyRate/PER_DRUM_DIVIDER;
									servicesAmnt = hourlyRate;
								}else if("10".equalsIgnoreCase(trans.getDrums())){
									hourlyRate = dailyRate/PER_DRUM_DIVIDER;
									servicesAmnt = hourlyRate * 2;
								}else  if("15".equalsIgnoreCase(trans.getDrums())){
									hrsWork = 4.0;
									servicesAmnt = hourlyRate * hrsWork;
								}else if("30".equalsIgnoreCase(trans.getDrums())){
									hrsWork = 8.0;
									servicesAmnt = hourlyRate * hrsWork;
								}else if("40".equalsIgnoreCase(trans.getDrums())){
									hrsWork = 8.0;
									servicesAmnt = hourlyRate * hrsWork;
									overTimeAmount = em.getOvertime() * 2;
								}else if("45".equalsIgnoreCase(trans.getDrums())){
									hrsWork = 8.0;
									servicesAmnt = hourlyRate * hrsWork;
									overTimeAmount = em.getOvertime() * 3;
								}
								
							}else{
							//field activity
							
								double timeInAfternoon = Time.typeId(time.getTimeIn());
								double timeOutAfterNoon = Time.typeId(time.getTimeOut());
								hrsWork = time.getCountHour();
								
								//tag as overtime
								if(time.getIsOvertime()==1){
								
									servicesAmnt = hrsWork * em.getOvertime();
								
								}else{
								
									if(timeOutAfterNoon>OVERTIME_START){
										servicesAmnt = (OVERTIME_START - timeInAfternoon) * hourlyRate;
										overTimeAmount = (timeOutAfterNoon - OVERTIME_START) * em.getOvertime();
									}else{
										servicesAmnt = hourlyRate * hrsWork;
									}
								
								}
							
							}
							
						}
						
						servicesAmnt += overTimeAmount;
					}
					
					}
					
					em.setDailyRate(Currency.formatAmount(dailyRate));
					em.setHourlyRate(Currency.formatAmount(hourlyRate));
					
					em.setPayableServices(Currency.formatAmount(servicesAmnt));
					
					totalExpenses += servicesAmnt;
					time.setEmployee(em);
					timesheets.add(time);
				}catch(Exception e){}
		
		}
		
		objs[0] = size;
		objs[1] = timesheets;
		objs[2] = totalExpenses;
		objs[3] = timeIn;
		objs[4] = timeOut;
		objs[5] = totalHr;
		
		return objs;
	}
	
	public void print(){
		
		String REPORT_NAME = "";
		HashMap param = new HashMap();
		UserDtls user = Login.getUserLogin().getUserDtls();
		String preparedBy = user.getFirstname() + " " + user.getLastname();
		
		param.put("PARAM_SOFTWARE_DEVELOPER_INFO", "Developed By: MARXMIND I.T. SOLUTIONS This document is generated by GS Trax");
		
		String companyLogo = REPORT_PATH + "logo.png";
		try{File logo = new File(companyLogo);
		FileInputStream logostream = new FileInputStream(logo);
		param.put("PARAM_COMPANY_LOGO",logostream);
		}catch(Exception e){}
		
		String companyLogotrans = REPORT_PATH + "logotrans.png";
		try{File logotrans = new File(companyLogotrans);
		FileInputStream logostreamtrans = new FileInputStream(logotrans);
		param.put("PARAM_COMPANY_LOGO_TRANS",logostreamtrans);
		}catch(Exception e){}
		
		param.put("PARAM_COMPANY", "GS FRUITS");
		param.put("PARAM_COMPANY_ADDRESS", "Tboli, South Cotabato");
		
		String from = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd");
		String to = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd");
		String dateReport = "";
		if(from.equalsIgnoreCase(to)) {
			dateReport = DateUtils.convertDateToMonthDayYear(from);
		}else {
			String[] dateFrom = from.split("-");
			String[] dateTo = to.split("-");
			
			if(dateFrom[0].equalsIgnoreCase(dateTo[0])) {//check same year
				if(dateFrom[1].equalsIgnoreCase(dateTo[1])) { //check same month
					dateReport = DateUtils.getMonthName(Integer.valueOf(dateFrom[1])) + " " + dateFrom[2] + "-" + dateTo[2] + ", " + dateFrom[0];
				}else {
					dateReport = DateUtils.getMonthName(Integer.valueOf(dateFrom[1])) + " " + dateFrom[2] + "-" + DateUtils.getMonthName(Integer.valueOf(dateTo[1])) + " " + dateTo[2] + ", " + dateFrom[0];
				}
			}else {
				dateReport = DateUtils.convertDateToMonthDayYear(from) +" - " + DateUtils.convertDateToMonthDayYear(to);
			}
			
			
		}
		
		param.put("PARAM_DATE", dateReport);
		
		param.put("PARAM_DATE_PRINTED", "Printed: "+DateUtils.getCurrentDateMMMMDDYYYY());
		param.put("PARAM_PREPARED_BY","Prepared By: "+preparedBy);
		
		if(1==getTypeId()){
			if(getActivateDetailsReport()){
				REPORT_NAME = REPORT_NAME_ACTIVITY + "dtls";
			}else{
				REPORT_NAME = REPORT_NAME_ACTIVITY;
			}
			param.put("PARAM_TOTAL_COST", getTotalActivityCost());
			param.put("PARAM_TOTAL_MANDAYS",getTotalMandays());
		}else if(2==getTypeId()){//locations
			if(getActivateDetailsReport()){
				REPORT_NAME = REPORT_NAME_AREA+"dtls";
			}else{
				REPORT_NAME = REPORT_NAME_AREA;
			}
			param.put("PARAM_TOTAL_COST", getTotalActivityCost());
			param.put("PARAM_TOTAL_MANDAYS",getTotalMandays());
		}else if(3==getTypeId()){//name
			if(getActivateDetailsReport()){
				REPORT_NAME = REPORT_NAME_NAMES+"dtls";
			}else{
				REPORT_NAME = REPORT_NAME_NAMES;
			}
			param.put("PARAM_TOTAL_COST", "Grand Total: Php "+getTotalActivityCost());
		}else if(4==getTypeId()){//Warehouse materials	
			
			if(getActivateDetailsReport()){
				REPORT_NAME = REPORT_NAME_WAREHOUSE_MATERIALS+"dtls";
			}else{
				REPORT_NAME = REPORT_NAME_WAREHOUSE_MATERIALS;
			}
			param.put("PARAM_TOTAL_COST", getTotalActivityCost());
			
		}else if(5==getTypeId()){//Applied materials
			if(getActivateDetailsReport()){
				REPORT_NAME = REPORT_NAME_APPLIED_MATERIALS+"dtls";
			}else{
				REPORT_NAME = REPORT_NAME_APPLIED_MATERIALS;
			}
			param.put("PARAM_TOTAL_COST", getTotalActivityCost());
		}
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		
		
		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
	  		}catch(Exception e){e.printStackTrace();}
		
				try{
					System.out.println("REPORT_PATH:" + REPORT_PATH + "REPORT_NAME: " + REPORT_NAME);
		  		 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
		
		
	}
	
	private void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
	}
	
	public List<Reports> getReports() {
		return reports;
	}
	public void setReports(List<Reports> reports) {
		this.reports = reports;
	}
	public List getRptTypes() {
		
		rptTypes = new ArrayList<>();
		rptTypes.add(new SelectItem(1, "Activity"));
		rptTypes.add(new SelectItem(2, "Area"));
		rptTypes.add(new SelectItem(3, "Names"));
		rptTypes.add(new SelectItem(4, "Warehouse Materials"));
		rptTypes.add(new SelectItem(5, "Applied Materials"));
		//rptTypes.add(new SelectItem(6, "Labor Requested Materials"));
		//rptTypes.add(new SelectItem(7, "Labor Returned Materials"));
		
		return rptTypes;
	}
	public void setRptTypes(List rptTypes) {
		this.rptTypes = rptTypes;
	}
	public int getTypeId() {
		if(typeId==0){
			typeId = 1;
		}
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public Date getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getDateToday();
		}
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		if(dateTo==null){
			dateTo = DateUtils.getDateToday();
		}
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public boolean isSummary() {
		return summary;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}
	
	public void postProcessXLS(Object document){
		HSSFWorkbook wb = (HSSFWorkbook)document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		

		/*int lastRow = sheet.getLastRowNum();
		HSSFSheet sheetfooter = wb.getSheetAt(lastRow);
		HSSFRow footer = sheetfooter.getRow(lastRow);
		//footer.setRowNum(footer.getPhysicalNumberOfCells());
		HSSFCell cellfooter = footer.getCell(footer.getPhysicalNumberOfCells());
		cellfooter.setCellValue("Php ");*/
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		//cellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		//cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		for(int i=0; i<header.getPhysicalNumberOfCells(); i++){
			HSSFCell cell = header.getCell(i);
			
			cell.setCellStyle(cellStyle);
		}
	}
	
	public ExcelOptions getExcelOptions() {
		
		excelOptions = new ExcelOptions();
		excelOptions.setFacetBgColor("#F88017");
		//excelOptions.setFacetFontSize("10");
		//excelOptions.setFacetFontColor("#0000ff");
		excelOptions.setFacetFontStyle("BOLD");
		//excelOptions.setCellFontColor("#00ff00");
		excelOptions.setCellFontSize("10");
		
		return excelOptions;
	}
	
	public void setExcelOptions(ExcelOptions excelOptions) {
		this.excelOptions = excelOptions;
	}

	public PDFOptions getPdfotions() {
		return pdfotions;
	}

	public void setPdfotions(PDFOptions pdfotions) {
		this.pdfotions = pdfotions;
	}
	
	public String getTotalActivityCost() {
		return totalActivityCost;
	}

	public void setTotalActivityCost(String totalActivityCost) {
		this.totalActivityCost = totalActivityCost;
	}

	public String getColumnName1() {
		if(columnName1==null){
			columnName1 = "Activity";
		}
		return columnName1;
	}

	public void setColumnName1(String columnName1) {
		this.columnName1 = columnName1;
	}

	public String getColumnName2() {
		if(columnName2==null){
			columnName2 = "Area";
		}
		return columnName2;
	}

	public void setColumnName2(String columnName2) {
		this.columnName2 = columnName2;
	}

	public String getColumnName3() {
		if(columnName3==null){
			columnName3 = "Blocks";
		}
		return columnName3;
	}

	public void setColumnName3(String columnName3) {
		this.columnName3 = columnName3;
	}

	public String getColumnName4() {
		if(columnName4==null){
			columnName4 = "Mandays";
		}
		return columnName4;
	}

	public void setColumnName4(String columnName4) {
		this.columnName4 = columnName4;
	}

	public String getSearchDescription() {
		return searchDescription;
	}

	public void setSearchDescription(String searchDescription) {
		this.searchDescription = searchDescription;
	}

	public int getTotalMandays() {
		return totalMandays;
	}

	public void setTotalMandays(int totalMandays) {
		this.totalMandays = totalMandays;
	}

	public boolean isColDate() {
		return colDate;
	}

	public void setColDate(boolean colDate) {
		this.colDate = colDate;
	}

	public boolean isColCuts() {
		return colCuts;
	}

	public void setColCuts(boolean colCuts) {
		this.colCuts = colCuts;
	}

	public boolean getActivateDetailsReport() {
		return activateDetailsReport;
	}

	public void setActivateDetailsReport(boolean activateDetailsReport) {
		this.activateDetailsReport = activateDetailsReport;
	}

	public boolean isColTimeInOut() {
		return colTimeInOut;
	}

	public void setColTimeInOut(boolean colTimeInOut) {
		this.colTimeInOut = colTimeInOut;
	}

	public String getColumnName5() {
		if(columnName5==null){
			columnName5 = "Date";
		}
		return columnName5;
	}

	public void setColumnName5(String columnName5) {
		this.columnName5 = columnName5;
	}

	public String getColumnName6() {
		if(columnName6==null){
			columnName6 = "Time IN - OUT";
		}
		return columnName6;
	}

	public void setColumnName6(String columnName6) {
		this.columnName6 = columnName6;
	}

	public String getColumnName7() {
		if(columnName7==null){
			columnName7 = "Cuts";
		}
		return columnName7;
	}

	public void setColumnName7(String columnName7) {
		this.columnName7 = columnName7;
	}

	public String getColumnName8() {
		if(columnName8==null){
			columnName8 = "Total Cost";
		}
		return columnName8;
	}

	public void setColumnName8(String columnName8) {
		this.columnName8 = columnName8;
	}
	
}
