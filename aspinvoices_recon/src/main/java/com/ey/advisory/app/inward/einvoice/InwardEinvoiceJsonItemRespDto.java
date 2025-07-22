package com.ey.advisory.app.inward.einvoice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InwardEinvoiceJsonItemRespDto {
	
	@Expose
	@SerializedName(value = "ackNo")
	private String ackNo;

	@Expose
	@SerializedName(value = "ackDt")
	private String ackDt;

	@Expose
	@SerializedName(value = "irn")
	private String irn;

	@Expose
	@SerializedName(value = "signedInvoice")
	private String signedInvoice;

	@Expose
	@SerializedName(value = "signedQRCode")
	private String signedQRCode;

	@Expose
	@SerializedName(value = "status")
	private String status;
	
	@Expose
	@SerializedName(value = "ewbNo")
	private String ewbNo;
	
	@Expose
	@SerializedName(value = "ewbDt")
	private String ewbDt;
	
	@Expose
	@SerializedName(value = "ewbValidTill")
	private String ewbValidTill;
	
	@Expose
	@SerializedName(value = "remarks")
	private String remarks;
	
	@Expose
	@SerializedName(value = "cnldt")
	private String cnldt;

	@Expose
	@SerializedName(value = "cnlRsn")
	private String cnlRsn;
	
	@Expose
	@SerializedName(value = "cnlRem")
	private String cnlRem;

}
