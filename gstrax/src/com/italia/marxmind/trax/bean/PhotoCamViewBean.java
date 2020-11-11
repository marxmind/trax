package com.italia.marxmind.trax.bean;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.enterprise.context.SessionScoped;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;

import org.primefaces.event.CaptureEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
/**
 * 
 * @author mark italia
 * @since 06/16/2017
 * @version 1.0
 *
 */

import com.italia.marxmind.trax.enm.Gstrax;
import com.italia.marxmind.trax.reader.ReadConfig;

@Named
@SessionScoped
public class PhotoCamViewBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4667696653556878L;
	private String filename;
	private String capturedImagePathName;
	private final static String IMAGE_PATH = ReadConfig.value(Gstrax.APP_IMG_FILE); 
	
	 private CroppedImage croppedImage;
     
	    private String newImageName;
	 
	    public CroppedImage getCroppedImage() {
	        return croppedImage;
	    }
	 
	    public void setCroppedImage(CroppedImage croppedImage) {
	        this.croppedImage = croppedImage;
	    }
	
    private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
 
    public String getFilename() {
        return filename;
    }
     
    public void oncapture(CaptureEvent captureEvent) {
        filename = getRandomImageName();
        System.out.println("Set picture name " + filename);
        byte[] data = captureEvent.getData();
 
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String driveImage =  IMAGE_PATH + filename + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(driveImage));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();    
            
            
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            capturedImagePathName = contextImageLoc + filename + ".jpg";
            System.out.println("capture path " + capturedImagePathName.replace("\\", "/"));
            setCapturedImagePathName(capturedImagePathName.replace("\\", "/"));
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }
	
    
    public void crop() {
        if(croppedImage == null) {
            return;
        }
         
        setNewImageName(getRandomImageNameCrop());
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String newFileName = externalContext.getRealPath("") + File.separator + "resources" + 
                    File.separator + "images" + File.separator + "crop" + File.separator + getNewImageName() + ".jpg";
         
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
            imageOutput.close();
            
            
            File file = new File(newFileName);
            try{
    			Files.copy(file.toPath(), (new File(IMAGE_PATH + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cropping failed."));
            return;
        }
         
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Cropping finished."));
    }
     
    private String getRandomImageNameCrop() {
        int i = (int) (Math.random() * 100000);
         
        return String.valueOf(i);
    }
     
    public String getNewImageName() {
        return newImageName;
    }
 
    public void setNewImageName(String newImageName) {
        this.newImageName = newImageName;
    }

	public String getCapturedImagePathName() {
		return capturedImagePathName;
	}

	public void setCapturedImagePathName(String capturedImagePathName) {
		this.capturedImagePathName = capturedImagePathName;
	}
    
}
