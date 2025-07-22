package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReturnFilingGstnResponseDto {

	@Expose
	public String arnNo;

	@Expose
	public String retPeriod;

	@Expose
	public String filingDate;

	@Expose
	public String gstin;

	@Expose
	public String vendorName;

	@Expose
	public String retType;

	@Expose
	public String status;
	
	@Expose
	public String errCode;
	
	@Expose
	public String errMsg;
	

}
