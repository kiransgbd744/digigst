package com.ey.advisory.app.returnfiling;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReturnFilingCounterPartyStatusDto {
	
	@Expose
	protected BigInteger requestId;
	
	@Expose
	protected String dateOfUpload;
	
	@Expose
	protected BigInteger noOfGstins;
	
	@Expose
	protected String status;
	
	@Expose
	protected String uploadedBy;

}
