package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class contains all the information required to process the file Arrival
 * Message.
 * 
 * @author Sai.Pakanati
 *
 */
@Setter
@Getter
@ToString
public class FileArrivalMsgDto {

	@Expose
	@SerializedName("filePath")
	private String filePath;

	@Expose
	@SerializedName("fileName")
	private String fileName;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("docId")
	private String docId;
	
	@Expose
	@SerializedName("fileId")
	private String fileId;


}
