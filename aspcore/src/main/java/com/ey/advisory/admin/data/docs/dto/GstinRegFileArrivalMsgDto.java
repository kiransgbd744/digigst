package com.ey.advisory.admin.data.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class contains all the information required to process the file Arrival
 * Message.
 * 
 * @author Sai.Pakanati
 *
 */
public class GstinRegFileArrivalMsgDto {
	
	
	@Expose @SerializedName("filePath")
	private String filePath;
	
	@Expose @SerializedName("fileName")
	private String fileName;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "FileArrivalMessage [filePath=" + filePath 
				+ ", fileName=" + fileName + "]";
	}
}
