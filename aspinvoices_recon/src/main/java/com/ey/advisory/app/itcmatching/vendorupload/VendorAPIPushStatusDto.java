package com.ey.advisory.app.itcmatching.vendorupload;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class VendorAPIPushStatusDto {
	
	@Expose
	private String pushedBy;
	
	@Expose
	private String dateOfPush;
	
	@Expose
	private String apiPushStatus;
	
	@Expose
	private Integer totalRecords;
	
	@Expose
	private Integer processedRecords;
	
	@Expose
	private Integer errorRecords;
	
	@Expose
	private Integer infoRecords;
	
	@Expose
	private String refId;
	
	
}