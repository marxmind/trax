package com.italia.marxmind.trax.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.trax.application.Application;
import com.italia.marxmind.trax.controller.Reports;
import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;
import com.italia.marxmind.trax.security.SecureChar;
import com.italia.marxmind.trax.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 08/21/2017
 *
 */

@ManagedBean(name="dataBean", eager=true)
@ViewScoped
public class DataTransferBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 655667897641L;
	
	private static final String DOC_PATH = Gstrax.PRIMARY_DRIVE.getName() + 
			Gstrax.SEPERATOR.getName() + 
			Gstrax.APP_FOLDER.getName() + 
			Gstrax.SEPERATOR.getName() + 
			"uploaded" + 
			Gstrax.SEPERATOR.getName();
	private String dataDownload;		
	
	private List<Reports> slqData;
	private StreamedContent sqlFile;
	private StreamedContent uploadedSQLFile;
	
	private static String DATABASE_BACKUP = Gstrax.PRIMARY_DRIVE.getName() + Gstrax.SEPERATOR.getName() +  Gstrax.APP_FOLDER.getName() + Gstrax.SEPERATOR.getName() +"databasebackup" + Gstrax.SEPERATOR.getName();
	
	private void deleteBatFile(File file){
		file.delete();
	}
	
	public void deleteFile(Reports report) {
		File file = new File(DATABASE_BACKUP + report.getF2());
		if(file.isFile()) {
			file.delete();
			Application.addMessage(1, "Success", "Successfully deleted file " + report.getF2() + "." + report.getF3());
			loadFiles();
		}
	}
	
	public void deleteUploadFile(Reports report) {
		File file = new File(DOC_PATH + report.getF2());
		if(file.isFile()) {
			file.delete();
			Application.addMessage(1, "Success", "Successfully deleted file " + report.getF2() + "." + report.getF3());
			loadUploadFiles();
		}
	}
	
	public void loadFiles() {
		slqData = new ArrayList<Reports>();
		File file = new File(DATABASE_BACKUP);
		String[] fileList = file.list();
		int id=1;
		try {
		for(String fileName : fileList){
			String ext = fileName.split("\\.")[1];
			
			if("sql".equalsIgnoreCase(ext)) {
				Reports rpt = new Reports();
				rpt.setF1(""+id);
				rpt.setF2(fileName);
				rpt.setF3(ext);
				slqData.add(rpt);
				id++;
			}
		}
		}catch(Exception e) {}
	}
	
	public void loadUploadFiles() {
		slqData = new ArrayList<Reports>();
		File file = new File(DOC_PATH);
		String[] fileList = file.list();
		int id=1;
		try {
		for(String fileName : fileList){
			String ext = fileName.split("\\.")[1];
			
			if("sql".equalsIgnoreCase(ext)) {
				Reports rpt = new Reports();
				rpt.setF1(""+id);
				rpt.setF2(fileName);
				rpt.setF3(ext);
				slqData.add(rpt);
				id++;
			}
		}
		}catch(Exception e) {}
	}
	
	public void fileDownload(String fileName){
		System.out.println("formDownload " + fileName);
		try{
			InputStream is = new FileInputStream(DATABASE_BACKUP + fileName);
			String ext = fileName.split("\\.")[1];
			sqlFile = new DefaultStreamedContent(is, "application/"+ext,fileName);
			//is.close();
		}catch(FileNotFoundException e){
			
		}catch(IOException eio){}
		
	}
	
	public void fileDownloadUpload(String fileName){
		System.out.println("formDownload " + fileName);
		try{
			InputStream is = new FileInputStream(DOC_PATH + fileName);
			String ext = fileName.split("\\.")[1];
			sqlFile = new DefaultStreamedContent(is, "application/"+ext,fileName);
			//is.close();
		}catch(FileNotFoundException e){
			
		}catch(IOException eio){}
		
	}
	
	public void displayDownload(){
		String bat = "explorer C:\\gstrax\\databasebackup";
		try{
		File file = new File(DOC_PATH +  "display.bat");	
		 PrintWriter pw = new PrintWriter(new FileWriter(file));
		    pw.println(bat);
	        pw.flush();
	        pw.close();
		
		 try{Runtime.getRuntime().exec(DOC_PATH +  "display.bat");}catch(Exception e){}
		 
		}catch(Exception e){}	  
	}
	
	public void downloadData(){
		
		String bat = "cd C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin" + "\n";
			   bat += "mysqldump.exe -e -u"+getUserName()+" -p"+getPassword()+" -hlocalhost "+getDBName()+" > C:\\gstrax\\databasebackup\\"+getDBName()+"_"+ DateUtils.getCurrentDateMMDDYYYYTIMEPlain() +".sql";
		try{	 
			File file = new File(DOC_PATH +  "download.bat");
			   PrintWriter pw = new PrintWriter(new FileWriter(file));
			    pw.println(bat);
		        pw.flush();
		        pw.close();
		        try{Runtime.getRuntime().exec(DOC_PATH +  "download.bat");}catch(Exception e){Application.addMessage(3, "Error", "Error downloading data");}
		        Application.addMessage(1, "Success", "Data has been successfully downloaded");
		        setDataDownload("Data Location: C:\\gstrax\\databasebackup\\gstrax_"+ DateUtils.getCurrentDateMMDDYYYYTIMEPlain() +".sql");
		        
		        loadFiles();
		}catch(Exception e){ Application.addMessage(3, "Error", "Error downloading data");}	        
	}
	
	public void uploadData(FileUploadEvent event){
	
		 try {
			 InputStream stream = event.getFile().getInputStream();
			 //String ext = FilenameUtils.getExtension(event.getFile().getFileName());
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFile(stream)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
				 loadUploadFiles();
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
		
	}
	
	private boolean writeDocToFile(InputStream stream){
		try{
		String filename = "uploaded-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + ".sql";	
		System.out.println("writing... writeDocToFile : " + filename);
		File fileDoc = new File(DOC_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		return loadToDatabase(fileDoc);
		}catch(IOException e){return false;}
	}
	
	private String getUserName(){
		String u_name = ReadConfig.value(Gstrax.USER_NAME);
		   u_name = SecureChar.decode(u_name);
		   u_name = u_name.replaceAll("mark", "");
		   u_name = u_name.replaceAll("rivera", "");
		   u_name = u_name.replaceAll("italia", "");
		   return u_name;
	}
	
	private String getPassword(){
		String pword = ReadConfig.value(Gstrax.USER_PASS);
		   pword =  SecureChar.decode(pword);
		   pword = pword.replaceAll("mark", "");
		   pword = pword.replaceAll("rivera", "");
		   pword = pword.replaceAll("italia", "");	
		   return pword;
	}
	
	private String getDBName(){
		return ReadConfig.value(Gstrax.DB_NAME);
	}
	
	private boolean loadToDatabase(File file){
		String sql = "";
		
		try{
			File fileUp = new File(DOC_PATH +  "uploadData.bat");
	   String bat ="cd C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin" + "\n" +
	    		"mysql -u"+getUserName()+" -p"+getPassword()+" -e \"use "+getDBName()+"; source "+"C:/gstrax/uploaded/"+file.getName()+";"+"\"";   
	    PrintWriter pw = new PrintWriter(new FileWriter(fileUp));
	    pw.println(bat);
        pw.flush();
        pw.close();
	    
        
        try{Runtime.getRuntime().exec(DOC_PATH +  "uploadData.bat");}catch(Exception e){return false;}
        
		return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}

	public String getDataDownload() {
		return dataDownload;
	}

	public void setDataDownload(String dataDownload) {
		this.dataDownload = dataDownload;
	}

	public List<Reports> getSlqData() {
		return slqData;
	}

	public void setSlqData(List<Reports> slqData) {
		this.slqData = slqData;
	}

	public StreamedContent getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(StreamedContent sqlFile) {
		this.sqlFile = sqlFile;
	}

	public StreamedContent getUploadedSQLFile() {
		return uploadedSQLFile;
	}

	public void setUploadedSQLFile(StreamedContent uploadedSQLFile) {
		this.uploadedSQLFile = uploadedSQLFile;
	}
	
	
}
