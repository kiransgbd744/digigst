package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class HSNSummaryInvoices {
	
	@Expose
	@SerializedName("flag")
	private String flag;
	
	@Expose
	@SerializedName("chksum")
	private String invoiceCheckSum;
		
	@Expose
	@SerializedName("data")
	private List<HSNSummaryInvData> hsnSummaryInvData;
	
	@Expose
	@SerializedName("hsn_b2b")
	private List<HSNSummaryInvData> hsnB2b;

	@Expose
	@SerializedName("hsn_b2c")
	private List<HSNSummaryInvData> hsnB2c;
	
	@Expose
	@SerializedName("error_msg")
	private String errorMsg;
	
	@Expose
	@SerializedName("error_cd")
	private String errorCode;

}
