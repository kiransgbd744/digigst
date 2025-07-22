package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
public class B2BInvoices {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	@Expose
	@SerializedName("cfs")
	private String cfs;

	@Expose
	@SerializedName("inv")
	private List<B2BInvoiceData> b2bInvoiceData;
	
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
