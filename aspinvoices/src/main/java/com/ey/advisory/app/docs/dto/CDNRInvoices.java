package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CDNRInvoices {

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
	
	@Expose
	@SerializedName("ctin")
	private String cpGstin;

	@Expose
	@SerializedName("cfs")
	private String counFillStatus;
	
	@Expose
	@SerializedName("nt")
	private List<CreditDebitNote> creditDebitNoteDetails;
	
	/**
	 * version v2.0 changes
	 */
	@Expose
	@SerializedName("cfs3b")
	private String cfsGstr3b;
	
	@Expose
	@SerializedName("dtcancel")
	private String cancelDate;
	
	@Expose
	@SerializedName("fldtr1")
	private String filedDate;
	
	@Expose
	@SerializedName("flprdr1")
	private String filedPeriod;
	
	/**
	 * Get-EInvoices changes
	 */
	
	@Expose
	@SerializedName("trdname")
	private String tradeName;

}
