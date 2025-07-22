package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class NilInvoices {

	@Expose
	@SerializedName("nil_amt")
	private BigDecimal totalNilRatedOutwordSup;
	
	@Expose
	@SerializedName("expt_amt")
	private BigDecimal totalExemptedOutwordSup;
	
	@Expose
	@SerializedName("ngsup_amt")
	private BigDecimal totalNonGstOutwordSup;
	
	@Expose
	@SerializedName("sply_ty")
	private String natureOfSupType;

}
