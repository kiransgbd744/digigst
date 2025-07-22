package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GstinValidatorAndReturnFilingReportDto {

	@Expose
	private String gstin;
	
	@Expose
	private String retPeriod;
	
	@Expose
	private String filingFreq;
	
	@Expose
	private String retType;
	
	@Expose
	private String filingDate;
	
	@Expose
	private String arnNo;
	
	@Expose
	private String status;
	
	@Expose
	@SerializedName(value = "lgnm")
	private  String legalBussNam;
	
	@Expose
	@SerializedName(value = "tradenm")
	private String tradeName;
	
	@Expose
	@SerializedName(value = "sts")
	private String gstnStatus;
	
	@Expose
	@SerializedName(value = "rgdt")
	private String dateOfReg;
	
	@Expose
	@SerializedName(value = "einvApplicable")
	private String einvApplicable;
	
	@Expose
	@SerializedName(value = "dty")
	private String taxPayType;
	
	@Expose
	@SerializedName(value = "cxdt")
	private String dateOfCan;
	
	@Expose
	public String errCode;
	
	@Expose
	public String errMsg;
	

}
