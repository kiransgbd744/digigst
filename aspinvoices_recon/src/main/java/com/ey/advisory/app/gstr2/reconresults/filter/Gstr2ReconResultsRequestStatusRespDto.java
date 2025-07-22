package com.ey.advisory.app.gstr2.reconresults.filter;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
* @author Sakshi.jain
*
*/

@Data
public class Gstr2ReconResultsRequestStatusRespDto {
	
	@Expose
	private String reqId;
	
	@Expose
	private String reconType;
	
	@Expose
	private String reconResponseAction;
	
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
}
