package com.ey.advisory.app.gstr.taxpayerdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaxPayerDetailsDto {
	
	@Expose
	private String gstin;
	
	@Expose
	@SerializedName(value = "ctb")
	private String constOfBuss;
	
	@Expose
	@SerializedName(value = "ctj")
	private String centreJuri;
	
	@Expose
	@SerializedName(value = "dty")
	private String taxPayType;
	
	@Expose
	@SerializedName(value = "lgnm")
	private  String legalBussNam;
	
	@Expose
	@SerializedName(value = "tradenm")
	private String tradeName;
	
	@Expose
	@SerializedName(value = "nba")
	private String natOfBusiness;
	
	@Expose
    @SerializedName(value = "bnm")
    private String buildingName;
	
	@Expose
	@SerializedName(value = "st")
	private String street;
	
	@Expose
	@SerializedName(value = "loc")
	private String location;
	
	@Expose
	@SerializedName(value = "bno")
	private String buildingNo;
	
	@Expose
	@SerializedName(value = "flno")
	private String floorNo;
	
	
	@Expose
	@SerializedName(value = "stcd")
	private String stateName;
	
	@Expose
	@SerializedName(value = "pncd")
	private String pincode;
	
	@Expose
	@SerializedName(value = "rgdt")
	private String dateOfReg;
	
	@Expose
	@SerializedName(value = "stj")
	private String stateJuri;
	
	@Expose
	@SerializedName(value = "cxdt")
	private String dateOfCan;
	
	@Expose
	@SerializedName(value = "sts")
	private String gstnStatus;
	
	@Expose
	@SerializedName(value = "stscd")
	private String gstnStatusCode;
	
	@Expose
	@SerializedName(value = "errorCd")
	private String errorCode;
	
	@Expose
	@SerializedName(value = "errormsg")
	private String errorMsg;
	
	@Expose
	@SerializedName(value = "einvApplicable")
	private String einvApplicable;
	
}
