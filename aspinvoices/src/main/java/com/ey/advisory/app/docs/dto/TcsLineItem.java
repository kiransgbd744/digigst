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
public class TcsLineItem {

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("stin")
	private String supplierGstin;

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
	@SerializedName("stin_name")
	private String supplierGstinLegelName;
	
	@Expose
	@SerializedName("pos")
	private String pos;
}
