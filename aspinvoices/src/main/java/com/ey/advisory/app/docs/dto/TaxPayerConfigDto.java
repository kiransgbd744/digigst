package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaxPayerConfigDto {
	
	@Expose
	protected String dateOfUpload;
	
	@Expose
	protected String fileName;
	
	@Expose
	protected Long requestId;
	
	@Expose
	protected Long noOfGstins;
	
	@Expose
	protected String status;
	
	@Expose
	protected String uploadedBy;
}
