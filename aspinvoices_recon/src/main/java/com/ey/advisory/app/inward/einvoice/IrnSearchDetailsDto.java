package com.ey.advisory.app.inward.einvoice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IrnSearchDetailsDto {

	@Expose
	@SerializedName(value = "irn",alternate={"Irn"})
	public  String irn;
	
	@Expose
	@SerializedName(value = "irnStatus", alternate = {"Status"})
	public String irnSts;
	
	@Expose
	@SerializedName(value = "ackNo", alternate = {"AckNo"})
	public Long ackNo;

	@Expose
	@SerializedName(value = "ackDt",alternate={"AckDt"} )
	public String ackDate;
	
	@Expose
	@SerializedName(value = "cnldt", alternate = {"Cnldt"})
	public String cnlDate;
	
	@Expose
	@SerializedName(value = "ISS", alternate = {"iss"})
	public String iss;
	
	@Expose
	@SerializedName(value = "errorCd")
	private String errCode;
	
	@Expose
	@SerializedName(value = "errormsg")
	private String errMsg;

	@Expose
	private String payload;
}
