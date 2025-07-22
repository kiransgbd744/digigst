package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Data
public class TcsaLineItem {

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("stin")
	private String supplierGstin;
	
	@Expose
	@SerializedName("ostin")
	private String originalSupplierGstin;

	@Expose
	@SerializedName("ofp")
	private String originalFinancialPeriod;

	@Expose
	@SerializedName("supR")
	private BigDecimal grossSuppliesMadeRegistered;

	@Expose
	@SerializedName("retsupR")
	private BigDecimal grossSuppliesReturnedRegistered;

	@Expose
	@SerializedName("supU")
	private BigDecimal grossSuppliesMadeUnRegistered;

	@Expose
	@SerializedName("retsupU")
	private BigDecimal grossSuppliesReturnedUnRegistered;

	@Expose
	@SerializedName("amt")
	private BigDecimal netAmount;

	@Expose
	@SerializedName("camt")
	private BigDecimal centralAmount;

	@Expose
	@SerializedName("samt")
	private BigDecimal stateTaxAmount;

	@Expose
	@SerializedName("iamt")
	private BigDecimal integratedTaxAmount;

	@Expose
	@SerializedName("source")
	private String source;
	
	@Expose
	@SerializedName("actn")
	private String action;
	
	@Expose
	@SerializedName("stin_name")
	private String supplierGstinLegalName;
	
	@Expose
	@SerializedName("ostin_name")
	private String originalSupplierGstinLegalName;
	
	@Expose
	@SerializedName("pos")
	private String pos;
	
	@Expose
	@SerializedName("opos")
	private String opos;
	
}
