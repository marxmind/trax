package com.italia.marxmind.trax.reader;

import java.io.File;
import java.io.IOException;

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
	
	public static void updateTagValue(String tag, String value) {
		try{
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(APPLICATION_FILE);
			
			// Get the module element by tag name directly
			Node module = doc.getElementsByTagName("databaseDetails").item(0);
			
			// loop the module child node
			NodeList list = module.getChildNodes();
			
			for (int i = 0; i < list.getLength(); i++) {
		
		            Node node = list.item(i);			            
						   if (tag.equals(node.getNodeName())) {
							node.setTextContent(value);
						   }
				}
			
			
			// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File(APPLICATION_FILE));
					transformer.transform(source, result);

					System.out.println("Done");

			
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			   } catch (TransformerException tfe) {
				tfe.printStackTrace();
			   } catch (IOException ioe) {
				ioe.printStackTrace();
			   } catch (SAXException sae) {
				sae.printStackTrace();
			   }
	}
	
	public static String getTagValue(String tag){
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
				boolean found = false;
				for(int i=0; i<size; i++){
					Node n = ls.item(i);
					//System.out.println("Current Node: "+ n.getNodeName());
					
					if(n.getNodeType() == Node.ELEMENT_NODE && !found){
						Element e = (Element)n;
						result = e.getElementsByTagName(tag).item(0).getTextContent();
						i=size;
						
						if(!result.isEmpty()) {
							found = true;
						}
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




















	
