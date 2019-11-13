package com.italia.marxmind.trax.reader;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.italia.marxmind.trax.enm.Gstrax;

/**
 * 
 * @author mark italia
 * @since 9/27/2016
 * Description: This class is use for reading the configuration file of the application
 *
 */
public class ReadConfig {

	private static final String APPLICATION_FILE = Gstrax.PRIMARY_DRIVE.getName() + 
			Gstrax.SEPERATOR.getName() + 
			Gstrax.APP_FOLDER.getName() + 
			Gstrax.SEPERATOR.getName() + 
			Gstrax.APP_CONFIG_FLDR.getName() + 
			Gstrax.SEPERATOR.getName() +
			Gstrax.APP_CONFIG_FILE.getName();
	
	
	public static String value(Gstrax icoopConf){
		try{
			File xmlFile = new File(APPLICATION_FILE);
			String result="";
			if(xmlFile.exists()){
				
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
				Document doc = documentBuilder.parse(xmlFile);
				
				/////////////normalize
				doc.getDocumentElement().normalize();
				//System.out.println("Reading conf......");
				
				NodeList ls = doc.getElementsByTagName("databaseDetails");
				int size=ls.getLength();
				for(int i=0; i<size; i++){
					Node n = ls.item(i);
					//System.out.println("Current Node: "+ n.getNodeName());
					
					if(n.getNodeType() == Node.ELEMENT_NODE){
						Element e = (Element)n;
						result = e.getElementsByTagName(icoopConf.getName()).item(0).getTextContent();
						i=size;
					}
					
				}
				//System.out.println("completed reading conf......");
			}else{
				System.out.println("File is not exist...");
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		System.out.println(ReadConfig.value(Gstrax.DB_URL));
		
	}
}




















	
