package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
* @author vishal.verma
*
*/

@Data
public class ImsRequestStatusRespDto {
	
	@Expose
	private String reqId;
	
	@Expose
	private String date;
	
	@Expose
	private String reqStatus;
	
	@Expose
	private String totalRecords;
	
	@Expose
	private String processed;
	
	@Expose
	private String error;
	
	@Expose
	private boolean isErrorDownld;
	
	@Expose
	private String fileId;
	
	@Expose
	private String errFilePath;
	
	@Expose
	private String docId;
	
	@Expose
	private String actionTaken;
}
