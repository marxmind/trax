package com.italia.marxmind.trax.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

import com.italia.marxmind.trax.controller.Activity;
import com.italia.marxmind.trax.controller.ActivityTransactions;
import com.italia.marxmind.trax.controller.Employee;
import com.italia.marxmind.trax.controller.Job;
import com.italia.marxmind.trax.controller.LocationTransactions;
import com.italia.marxmind.trax.controller.MaterialOUT;
import com.italia.marxmind.trax.controller.TimeSheets;
import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.enm.JobTitle;
import com.italia.marxmind.trax.enm.Time;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.reader.ReadXML;
import com.italia.marxmind.trax.reader.ReportTag;
import com.italia.marxmind.trax.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 10-09-2020
 *
 */

@Named
@ViewScoped
public class GraphBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 543547686461L;
	private final static double HOUR_IN_DAY = Double.valueOf(ReadConfig.value(Gstrax.NORMAL_RENDERED_HOURS));//8.0;
	private final static double LABOR_SPRAYMAN_DAILY_RATE = Double.valueOf(ReadConfig.value(Gstrax.LABOR_SPRAYMAN_DAILY_RATE));
	private final static double LABOR_HARVESTER_DAILY_RATE = Double.valueOf(ReadConfig.value(Gstrax.LABOR_HARVESTER_DAILY_RATE));
	private final static String HARVESTER = "Harvester";
	private final static double OVERTIME_START = Double.valueOf(ReadConfig.value(Gstrax.OTSTART)); //14.5;//start at 2:30PM
	
	private final static double DRIVER_NORMAL_PER_DRUM_RATE = Double.valueOf(ReadConfig.value(Gstrax.DRIVER_NORMAL_PER_DRUM_RATE));
	private final static int PER_DRUM_DIVIDER = Integer.valueOf(ReadConfig.value(Gstrax.PER_DRUM_DIVIDER));
	private final static double FIELD_SPRAY_DRIVER_RATE = Double.valueOf(ReadConfig.value(Gstrax.FIELD_SPRAY_DRIVER));
	
	private final static String FORCING1 = ReadConfig.value(Gstrax.FORCING1);
	private final static String FORCING2 = ReadConfig.value(Gstrax.FORCING2);
	
	private static final String REPORT_PATH = ReadConfig.value(Gstrax.REPORT);
	private static final String REPORT_NAME_ACTIVITY = ReadXML.value(ReportTag.ACTIVITY);
	private static final String REPORT_NAME_AREA = ReadXML.value(ReportTag.AREA);
	private static final String REPORT_NAME_NAMES = ReadXML.value(ReportTag.NAMES);
	private static final String REPORT_NAME_APPLIED_MATERIALS = ReadXML.value(ReportTag.APPLIED_MATERIALS);
	private static final String REPORT_NAME_WAREHOUSE_MATERIALS = ReadXML.value(ReportTag.WAREHOUSE_MATERIALS);
	private boolean activateDetailsReport;
	
	private String searchDescription;
	
	private int totalMandays;
	
	private Map<Integer, Job> jobs = new LinkedHashMap<Integer, Job>();
	
	
	 private HorizontalBarChartModel hbarModel;
	 private LineChartModel lineModel;
	 private BarChartModel barModel;
	 
	 private Date timeSheetFrom;
	 private Date timeSheetTo;
	 private int modeId;
	 private List modes;
	 
	 public void createBarModel(String modeType, String dateStart, String dateEnd, int topList, List<String> vals) {
	        barModel = new BarChartModel();
	        ChartData data = new ChartData();
	         
	        BarChartDataSet barDataSet = new BarChartDataSet();
	        barDataSet.setLabel("Expenses");
	         
	        List<Number> values = new ArrayList<>();
	        List<String> bgColor = new ArrayList<>();
	        List<String> borderColor = new ArrayList<>();
	        List<String> labels = new ArrayList<>();
	        
	        int i=1;
			int index = vals.size() - 1;
			int rgbColor = 0;
	        String[] rgbs = rgbColors();
	        String[] borders = borderColors();
			while(i<=topList && index>0) {
				double amount = Double.valueOf(vals.get(index).split("=")[1]);
				values.add(amount);
	        	labels.add(vals.get(index).split("=")[0]);
	        	bgColor.add(rgbs[rgbColor]);
	        	borderColor.add(borders[rgbColor++]);
				index--;
				i++;
			}
	        
	        barDataSet.setData(values);
	        barDataSet.setBackgroundColor(bgColor);
	        barDataSet.setBorderColor(borderColor);
	        barDataSet.setBorderWidth(1);
	        data.addChartDataSet(barDataSet);
	        data.setLabels(labels);
	        barModel.setData(data);
	        
	         
	        //Options
	        BarChartOptions options = new BarChartOptions();
	        CartesianScales cScales = new CartesianScales();
	        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
	        CartesianLinearTicks ticks = new CartesianLinearTicks();
	        ticks.setBeginAtZero(false);//true if begin to zero
	        linearAxes.setTicks(ticks);
	        cScales.addYAxesData(linearAxes);
	        options.setScales(cScales);
	         
	        Title title = new Title();
	        title.setDisplay(true);
	        title.setText("Top "+ topList +" list of "+ modeType +" Expense from " + dateStart + " to " + dateEnd);
	        options.setTitle(title);
	 
	        Legend legend = new Legend();
	        legend.setDisplay(true);
	        legend.setPosition("top");
	        LegendLabel legendLabels = new LegendLabel();
	        legendLabels.setFontStyle("bold");
	        legendLabels.setFontColor("#2980B9");
	        legendLabels.setFontSize(24);
	        legend.setLabels(legendLabels);
	        options.setLegend(legend);
	 
	        barModel.setOptions(options);
	    }
	 
	 
	 
	 public void createLineModel(String dateStart, String dateEnd, int topList, List<String> vals) {
	        lineModel = new LineChartModel();
	        ChartData data = new ChartData();
	         
	        LineChartDataSet dataSet = new LineChartDataSet();
	        List<Object> values = new ArrayList<>();
	        
	        List<String> labels = new ArrayList<>();
	        int i=1;
			int index = vals.size() - 1;
			int rgbColor = 0;
		    String[] rgbs = rgbColors();
			while(i<=topList) {
				double amount = Double.valueOf(vals.get(index).split("=")[1]);
				values.add(amount);
	        	labels.add(vals.get(index).split("=")[0]);
				index--;
				i++;
			}
	        
	        
	        dataSet.setData(values);
	        dataSet.setFill(false);
	        dataSet.setLabel("Expenses");
	        dataSet.setBorderColor("rgba(255, 99, 132, 0.2)");
	        dataSet.setLineTension(0.1);
	        
	        
	        data.addChartDataSet(dataSet);
	        data.setLabels(labels);
	         
	        //Options
	        LineChartOptions options = new LineChartOptions();        
	        Title title = new Title();
	        title.setDisplay(true);
	        title.setText("Activities Expense");
	        options.setTitle(title);
	         
	        lineModel.setOptions(options);
	        lineModel.setData(data);
	    }
	 public void createHorizontalBarModel(String dateStart, String dateEnd, int topList, List<String> vals) {
	        hbarModel = new HorizontalBarChartModel();
	        ChartData data = new ChartData();
	         
	        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
	        
	        hbarDataSet.setLabel("Recorded since ");
	         
	        List<Number> values = new ArrayList<>();  
	        List<String> bgColor = new ArrayList<>();
	        List<String> borderColor = new ArrayList<>(); 
	        List<String> labels = new ArrayList<>();
	        
	        int i=1;
			int index = vals.size() - 1;
			int rgbColor = 0;
		    String[] rgbs = rgbColors();
			while(i<=topList) {
				double amount = Double.valueOf(vals.get(index).split("=")[1]);
				values.add(amount);
				bgColor.add(rgbs[rgbColor++]);
	        	borderColor.add(rgbs[rgbColor]);
	        	labels.add(vals.get(index).split("=")[0]);
				index--;
				i++;
			}
	        
	        
	        hbarDataSet.setData(values);
	        hbarDataSet.setBackgroundColor(bgColor);
	        data.addChartDataSet(hbarDataSet);
	        hbarDataSet.setBorderColor(borderColor);
	        hbarDataSet.setBorderWidth(1);
	        data.setLabels(labels);
	        hbarModel.setData(data);
	         
	        //Options
	        BarChartOptions options = new BarChartOptions();
	        CartesianScales cScales = new CartesianScales();
	        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
	        CartesianLinearTicks ticks = new CartesianLinearTicks();
	        ticks.setBeginAtZero(true);
	        linearAxes.setTicks(ticks);
	        cScales.addXAxesData(linearAxes);
	        options.setScales(cScales);
	         
	        Title title = new Title();
	        title.setDisplay(true);
	        title.setText("Top "+ topList + " List of Activity expenses from " + dateStart + " to " + dateEnd);
	        options.setTitle(title);
	         
	        hbarModel.setOptions(options);
	    }
	 
	 
	 public void createBarModelMonthlyExpense(Map<String, Double> expense) {
	        barModel = new BarChartModel();
	        ChartData data = new ChartData();
	         
	        BarChartDataSet barDataSet = new BarChartDataSet();
	        barDataSet.setLabel("Monthly Expenses");
	         
	        List<Number> values = new ArrayList<>();
	        List<String> bgColor = new ArrayList<>();
	        List<String> borderColor = new ArrayList<>();
	        List<String> labels = new ArrayList<>();
	        
	        int rgbColor = 0;
	        String[] rgbs = rgbColors();
	        String[] borders = borderColors();
	        int countMount = 1;
	        for(String key : expense.keySet()) {
				String month = DateUtils.getMonthName(Integer.valueOf(key));
				values.add(expense.get(key));
	        	labels.add(month);
	        	bgColor.add(rgbs[rgbColor]);
	        	borderColor.add(borders[rgbColor++]);
	        	countMount++;
			}
	        for(int i=countMount; i<=12; i++) {
	        	String month = DateUtils.getMonthName(Integer.valueOf(i+""));
				values.add(0);
	        	labels.add(month);
	        	bgColor.add(rgbs[rgbColor]);
	        	borderColor.add(borders[rgbColor++]);
	        }
	        
	        barDataSet.setData(values);
	        barDataSet.setBackgroundColor(bgColor);
	        barDataSet.setBorderColor(borderColor);
	        barDataSet.setBorderWidth(1);
	        data.addChartDataSet(barDataSet);
	        data.setLabels(labels);
	        barModel.setData(data);
	        
	         
	        //Options
	        BarChartOptions options = new BarChartOptions();
	        CartesianScales cScales = new CartesianScales();
	        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
	        CartesianLinearTicks ticks = new CartesianLinearTicks();
	        ticks.setBeginAtZero(false);//true if begin to zero
	        linearAxes.setTicks(ticks);
	        cScales.addYAxesData(linearAxes);
	        options.setScales(cScales);
	         
	        Title title = new Title();
	        title.setDisplay(true);
	        title.setText("Monthly expenses for the current year " + DateUtils.getCurrentYear());
	        options.setTitle(title);
	 
	        Legend legend = new Legend();
	        legend.setDisplay(true);
	        legend.setPosition("top");
	        LegendLabel legendLabels = new LegendLabel();
	        legendLabels.setFontStyle("bold");
	        legendLabels.setFontColor("#2980B9");
	        legendLabels.setFontSize(24);
	        legend.setLabels(legendLabels);
	        options.setLegend(legend);
	 
	        barModel.setOptions(options);
	    }
	 
	 public void createBarModelMonthlyExpenseLastVsCurrentYear(Map<String, Double> lastYear,Map<String, Double> currentYear) {
		 	int year = DateUtils.getCurrentYear();
	        barModel = new BarChartModel();
	        ChartData data = new ChartData();
	        String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"}; 
	        BarChartDataSet barDataSet = new BarChartDataSet();
	        barDataSet.setLabel((year-1)+"");
	         
	        List<Number> values = new ArrayList<>();
	        List<String> bgColor = new ArrayList<>();
	        List<String> borderColor = new ArrayList<>();
	        List<String> labels = new ArrayList<>();
	        
	        int rgbColor = 0;
	        String[] rgbs = rgbColors();
	        String[] borders = borderColors();
	       
	        
	        for(String month : months) {
	        	labels.add(DateUtils.getMonthName(Integer.valueOf(month)));
	        	bgColor.add(rgbs[rgbColor]);
	        	borderColor.add(borders[rgbColor]);
	        	if(lastYear.containsKey(month)) {
	        		values.add(lastYear.get(month));
	        	}else {
	        		values.add(0);
	        	}
	        }
	        
	        barDataSet.setData(values);
	        barDataSet.setBackgroundColor(bgColor);
	        barDataSet.setBorderColor(borderColor);
	        barDataSet.setBorderWidth(1);
	        data.addChartDataSet(barDataSet);
	        data.setLabels(labels);
	        barModel.setData(data);
	        
	        
	        BarChartDataSet barDataSet2 = new BarChartDataSet();
	        barDataSet2.setLabel(year+"");
	        List<Number> values2 = new ArrayList<>();
	        List<String> bgColor2 = new ArrayList<>();
	        List<String> borderColor2 = new ArrayList<>();
	        List<String> labels2 = new ArrayList<>();
	        
	        rgbColor = 4;
	        for(String month : months) {
	        	labels2.add(DateUtils.getMonthName(Integer.valueOf(month)));
	        	bgColor2.add(rgbs[rgbColor]);
	        	borderColor2.add(borders[rgbColor]);
	        	if(currentYear.containsKey(month)) {
	        		values2.add(currentYear.get(month));
	        	}else {
	        		values2.add(0);
	        	}
	        }
	       
	        barDataSet2.setData(values2);
	        barDataSet2.setBackgroundColor(bgColor2);
	        barDataSet2.setBorderColor(borderColor2);
	        barDataSet2.setBorderWidth(1);
	        data.addChartDataSet(barDataSet2);
	        data.setLabels(labels2);
	        barModel.setData(data);
	        
	         
	        //Options
	        BarChartOptions options = new BarChartOptions();
	        CartesianScales cScales = new CartesianScales();
	        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
	        CartesianLinearTicks ticks = new CartesianLinearTicks();
	        ticks.setBeginAtZero(false);//true if begin to zero
	        linearAxes.setTicks(ticks);
	        cScales.addYAxesData(linearAxes);
	        options.setScales(cScales);
	         
	        Title title = new Title();
	        title.setDisplay(true);
	        
	        title.setText("Monthly expenses for year " + (year-1) + " and " + year );
	        options.setTitle(title);
	 
	        Legend legend = new Legend();
	        legend.setDisplay(true);
	        legend.setPosition("top");
	        LegendLabel legendLabels = new LegendLabel();
	        legendLabels.setFontStyle("bold");
	        legendLabels.setFontColor("#2980B9");
	        legendLabels.setFontSize(24);
	        legend.setLabels(legendLabels);
	        options.setLegend(legend);
	 
	        barModel.setOptions(options);
	    }
	 private String[] rgbColors() {
	    	String[] rgb = {"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)",
	    			"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)",
	    			"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)",
	    			"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)",
	    			"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)",
	    			"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)",
	    			"rgba(255, 99, 132, 0.2)",
	    			"rgba(255, 159, 64, 0.2)",
	    			"rgba(255, 205, 86, 0.2)",
	    			"rgba(75, 192, 192, 0.2)",
	    			"rgba(54, 162, 235, 0.2)",
	    			"rgba(153, 102, 255, 0.2)",
	    			"rgba(201, 203, 207, 0.2)"
	    			};
	    			
	    	
	    	return rgb;
	    }
	 private String[] borderColors() {
	    	String[] rgb = {"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
	    			"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
	    			"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
	    			"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
	    			"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
	    			"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
	    			"rgb(255, 99, 132)",
	    			"rgb(255, 159, 64)",
	    			"rgb(255, 205, 86)",
	    			"rgb(75, 192, 192)",
	    			"rgb(54, 162, 235)",
	    			"rgb(153, 102, 255)",
	    			"rgb(201, 203, 207)",
					};
	    	
	    	return rgb;
	    }
	
	@PostConstruct
	public void init() {
		lineModel = new LineChartModel();
		hbarModel = new HorizontalBarChartModel();
		barModel = new BarChartModel();
		loadPositions();
	}
	
	public void loadSearch() {
		switch(getModeId()) {
		case 0 ://activity expense 
			readActivityExppense(DateUtils.convertDate(getTimeSheetFrom(), "yyyy-MM-dd"), DateUtils.convertDate(getTimeSheetTo(), "yyyy-MM-dd"),10);
			break;
		case 1://area expense
			readAreaExppense(DateUtils.convertDate(getTimeSheetFrom(), "yyyy-MM-dd"), DateUtils.convertDate(getTimeSheetTo(), "yyyy-MM-dd"),10);
			break;
		case 2://monthly expense current year
			monthlyExpenseCurrentYear();
			break;
		case 3://monthly expenses previous vs current year
			monthlyExpenseLastVsCurrentYear();
			break;
			
		}
		
	}
	
	private void loadPositions() {
		jobs = new LinkedHashMap<Integer, Job>();
		String sql = "SELECT * FROM jobtitle WHERE isactivejob=1";
		String[] params = new String[0];
		for(Job job : Job.retrieve(sql, params)) {
			jobs.put(job.getJobid(), job);
		}
	}
	
	public void readActivityExppense(String dateStart, String dateEnd, int numberTopListToShow) {
		
		String sql = " AND trn.isactiveactrans=1 AND (trn.actDateTrans>=? AND trn.actDateTrans<=?)";
		String[] params = new String[2];
		params[0] = dateStart;
		params[1] = dateEnd;
		Map<String, Double> activityMapExp = new HashMap<String, Double>();
		for(ActivityTransactions trans : ActivityTransactions.retrieve(sql, params)) {
			double amount = 0d;
			String activityName = trans.getActivity().getName();
			for(TimeSheets time : TimeSheets.retrieveActivity(trans.getId())) {
				amount += calculateSalary(time, trans);
			}
			if(activityMapExp!=null && activityMapExp.size()>0) {
				if(activityMapExp.containsKey(activityName)) {
					double oldAmnt = activityMapExp.get(activityName);
					amount += oldAmnt;
					activityMapExp.put(activityName, amount);
				}else {
					activityMapExp.put(activityName, amount);
				}
			}else {
				activityMapExp.put(activityName, amount);
			}
		}
		
		Map<Double, String> amountMap = new HashMap<Double, String>();
		for(String key : activityMapExp.keySet()) {
			double amount = activityMapExp.get(key);
			if(amountMap!=null && amountMap.size()>0) {
				if(amountMap.containsKey(amount)) {
					amountMap.put(amount+0.01, key);
				}else {
					amountMap.put(amount, key);
				}
			}else {
				amountMap.put(amount, key);
			}
		}
		Map<Double, String> amountMapSorted = new TreeMap<Double, String>(amountMap);
		List<String> ac = new ArrayList<String>();
		for(double d : amountMapSorted.keySet()) {
			ac.add(amountMapSorted.get(d) + "=" + d);
		}
		createBarModel("Activity",dateStart, dateEnd, numberTopListToShow, ac);
		
	}
	
public void readAreaExppense(String dateStart, String dateEnd, int numberTopListToShow) {
		
		String sql = " AND lct.isactiveloctrans=1 AND (lct.locDateTrans>=? AND lct.locDateTrans<=?)";
		String[] params = new String[2];
		params[0] = dateStart;
		params[1] = dateEnd;
		Map<String, Double> activityMapExp = new HashMap<String, Double>();
		
		for(LocationTransactions trans : LocationTransactions.retrieve(sql, params)) {
			double amount = 0d;
			String activityName = trans.getLocation().getName();
			for(TimeSheets time : TimeSheets.retrieveActivity(trans.getId())) {
				amount += calculateSalary(time, trans.getActivityTransactions());
			}
			if(activityMapExp!=null && activityMapExp.size()>0) {
				if(activityMapExp.containsKey(activityName)) {
					double oldAmnt = activityMapExp.get(activityName);
					amount += oldAmnt;
					activityMapExp.put(activityName, amount);
				}else {
					activityMapExp.put(activityName, amount);
				}
			}else {
				activityMapExp.put(activityName, amount);
			}
		}
		
		Map<Double, String> amountMap = new HashMap<Double, String>();
		for(String key : activityMapExp.keySet()) {
			double amount = activityMapExp.get(key);
			if(amountMap!=null && amountMap.size()>0) {
				if(amountMap.containsKey(amount)) {
					amountMap.put(amount+0.01, key);
				}else {
					amountMap.put(amount, key);
				}
			}else {
				amountMap.put(amount, key);
			}
		}
		Map<Double, String> amountMapSorted = new TreeMap<Double, String>(amountMap);
		List<String> ac = new ArrayList<String>();
		for(double d : amountMapSorted.keySet()) {
			ac.add(amountMapSorted.get(d) + "=" + d);
		}
		createBarModel("Area",dateStart, dateEnd, numberTopListToShow, ac);
		
	}
	

	private void monthlyExpenseCurrentYear() {
		String sql = " AND tme.isactivetime=1 AND (tme.timeDateTrans>=? AND tme.timeDateTrans<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.getCurrentYear() + "-01-01";
		params[1] = DateUtils.getCurrentYear() + "-12-31";
		double amount = 0d;
		Map<String, Double> mapAmount = new HashMap<String, Double>();
		for(TimeSheets time : TimeSheets.retrieve(sql, params)) {
			ActivityTransactions trans = ActivityTransactions.retrieve(time.getId());
			amount = calculateSalary(time, trans);
			String key = time.getDateTrans().split("-")[1];//month
			if(mapAmount!=null && mapAmount.size()>0) {
				if(mapAmount.containsKey(key)) {
					double newAmnt = mapAmount.get(key) + amount;
					mapAmount.put(key, newAmnt);
				}else {
					mapAmount.put(key, amount);
				}
			}else {
				mapAmount.put(key, amount);
			}
		}
		
		
		List<MaterialOUT> outs = MaterialOUT.materialExpense(DateUtils.getCurrentYear() + "-01-01", DateUtils.getCurrentYear() + "-12-31");
		for(MaterialOUT out : outs) {
			String key = out.getDateTrans().split("-")[1];//month
			if(mapAmount!=null && mapAmount.size()>0) {
				if(mapAmount.containsKey(key)) {
					double newAmnt = mapAmount.get(key) + out.getPriceCost();
					mapAmount.put(key, newAmnt);
				}else {
					mapAmount.put(key, out.getPriceCost());
				}
			}else {
				mapAmount.put(key, out.getPriceCost());
			}
		}
		
		createBarModelMonthlyExpense(mapAmount);
	}
	
	private void monthlyExpenseLastVsCurrentYear() {
		
		int year = DateUtils.getCurrentYear();
		String sql = " AND trn.isactiveactrans=1 AND (trn.actDateTrans>=? AND trn.actDateTrans<=?)";
		String[] params = new String[2];
		//params[0] = (year-1) + "-01-01";
		params[0] = (year-1) + "-01-01";
		params[1] = year + "-12-31";
		String currYear = DateUtils.getCurrentYear()+"";
		String prevYear=(DateUtils.getCurrentYear() - 1)+"";
		
		Map<String, Double> currentYear = new HashMap<String, Double>();
		Map<String, Double> lastYear = new HashMap<String, Double>();
		for(ActivityTransactions trans : ActivityTransactions.retrieve(sql, params)) {
			double amount = 0d;
			for(TimeSheets time : TimeSheets.retrieveActivity(trans.getId())) {
				amount = calculateSalary(time, trans);
				
				String yearNow = time.getDateTrans().split("-")[0];//year
				String month = time.getDateTrans().split("-")[1];//month
				System.out.println("prev year checking: " + prevYear);
				if(prevYear.equalsIgnoreCase(yearNow)) {
					System.out.println("previous year=" + prevYear);
					if(lastYear!=null && lastYear.size()>0) {
						if(lastYear.containsKey(month)) {
							double newAmnt = lastYear.get(month) + amount;
							lastYear.put(month, newAmnt);
						}else {
							lastYear.put(month, amount);
						}
					}else {
						lastYear.put(month, amount);
					}
				}else if(currYear.equalsIgnoreCase(yearNow)) {
					System.out.println("current year=" + yearNow);
					if(currentYear!=null && currentYear.size()>0) {
						if(currentYear.containsKey(month)) {
							double newAmnt = currentYear.get(month) + amount;
							currentYear.put(month, newAmnt);
						}else {
							currentYear.put(month, amount);
						}
					}else {
						currentYear.put(month, amount);
					}
				}
				
				
			}
			
		}
		
		List<MaterialOUT> outs = MaterialOUT.materialExpense(params[0], params[1]);
		for(MaterialOUT out : outs) {
			
			String yearNow = out.getDateTrans().split("-")[0];//year
			String month = out.getDateTrans().split("-")[1];//month
			
			if(prevYear.equalsIgnoreCase(yearNow)) {
				if(lastYear!=null && lastYear.size()>0) {
					if(lastYear.containsKey(month)) {
						double newAmnt = lastYear.get(month) + out.getPriceCost();
						lastYear.put(month, newAmnt);
					}else {
						lastYear.put(month, out.getPriceCost());
					}
				}else {
					lastYear.put(month, out.getPriceCost());
				}
			}else if(currYear.equalsIgnoreCase(yearNow)) {
				if(currentYear!=null && currentYear.size()>0) {
					if(currentYear.containsKey(month)) {
						double newAmnt = currentYear.get(month) + out.getPriceCost();
						currentYear.put(month, newAmnt);
					}else {
						currentYear.put(month, out.getPriceCost());
					}
				}else {
					currentYear.put(month, out.getPriceCost());
				}
			}
			
		}
		
		
		Map<String, Double> lastYearVal = new TreeMap<String, Double>(lastYear);
		Map<String, Double> currentYearVal = new TreeMap<String, Double>(currentYear);
		createBarModelMonthlyExpenseLastVsCurrentYear(lastYearVal, currentYearVal);
	}

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
			
			String forcing = "";
			try{forcing = act.getActivity().getName();}catch(Exception e){
				Activity activity = Activity.retrieve(act.getActivity().getId());
				forcing = activity.getName();
			}
			
			double totalRate = 0d;
			if(FORCING1.equalsIgnoreCase(forcing) || FORCING2.equalsIgnoreCase(forcing)){
				//String sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
				String sql =" AND actransid=?";
				String[] params = new String[1];
				params[0] = act.getId()+"";
				double drums = Double.valueOf(act.getDrums());
				double totalEmployee = TimeSheets.totalEmployee(sql, params);
				//double totalEmployee = TimeSheets.retrieve(sql, params).size();
				
				totalRate = drums * getJobs().get(em.getJob().getJobid()).getSpraySpecialRate();
				double salaryPerEmployee = totalRate / totalEmployee;
				
				
				hourlyRate = totalRate / HOUR_IN_DAY;
				servicesAmnt = salaryPerEmployee;
			
			}else if(act.getDrums()!=null && ("Spray Foliar".equalsIgnoreCase(forcing) 
					|| "Spray Fungicide".equalsIgnoreCase(forcing)
					|| "Spray Herbicide".equalsIgnoreCase(forcing)
					|| "Spray Insecticide".equalsIgnoreCase(forcing)
					|| "Spray Foliar Sucker".equalsIgnoreCase(forcing)
					|| "Spray Ripening".equalsIgnoreCase(forcing))) {	
				
				//String sql =" AND tme.isactivetime=1 AND ac.actransid=? AND ac.isactiveactrans=1";
				String sql =" AND actransid=?";
				String[] params = new String[1];
				params[0] = act.getId()+"";
				double drums = Double.valueOf(act.getDrums());
				double totalEmployee = TimeSheets.totalEmployee(sql, params);
				//double totalEmployee = TimeSheets.retrieve(sql, params).size();
				//double totalRate = drums * OTHER_SPRAY_RATE_PER_DRUM;
				totalRate = drums * getJobs().get(em.getJob().getJobid()).getSprayRegularRate();
				double salaryPerEmployee = totalRate / totalEmployee;
				
				//hourlyRate = OTHER_SPRAY_RATE_PER_DRUM;
				hourlyRate = totalRate / HOUR_IN_DAY;
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

public Map<Integer, Job> getJobs() {
	return jobs;
}

public void setJobs(Map<Integer, Job> jobs) {
	this.jobs = jobs;
}
public HorizontalBarChartModel getHbarModel() {
    return hbarModel;
}

public void setHbarModel(HorizontalBarChartModel hbarModel) {
    this.hbarModel = hbarModel;
}
public LineChartModel getLineModel() {
	return lineModel;
}
public void setLineModel(LineChartModel lineModel) {
	this.lineModel = lineModel;
}

public BarChartModel getBarModel() {
	return barModel;
}

public void setBarModel(BarChartModel barModel) {
	this.barModel = barModel;
}

public Date getTimeSheetFrom() {
	if(timeSheetFrom==null) {
		timeSheetFrom = DateUtils.getDateToday();
	}
	return timeSheetFrom;
}

public void setTimeSheetFrom(Date timeSheetFrom) {
	this.timeSheetFrom = timeSheetFrom;
}

public Date getTimeSheetTo() {
	if(timeSheetTo==null) {
		timeSheetTo = DateUtils.getDateToday();
	}
	return timeSheetTo;
}

public void setTimeSheetTo(Date timeSheetTo) {
	this.timeSheetTo = timeSheetTo;
}

public int getModeId() {
	return modeId;
}

public void setModeId(int modeId) {
	this.modeId = modeId;
}

public List getModes() {
	modes = new ArrayList<>();
	modes.add(new SelectItem(0, "Activity Expense"));
	modes.add(new SelectItem(1, "Area Expense"));
	modes.add(new SelectItem(2, "Monthly Curent Year Expense"));
	modes.add(new SelectItem(3, "Last Year vs Current Year Monthly Expense"));
	return modes;
}

public void setModes(List modes) {
	this.modes = modes;
}
	
}
