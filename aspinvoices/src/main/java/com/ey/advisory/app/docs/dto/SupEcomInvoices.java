package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SupEcomInvoices {

	@Expose
	@SerializedName("oetin")
	private String oetin;

	@Expose
	@SerializedName("etin")
	private String etin;

	@Expose
	@SerializedName("suppval")
	private BigDecimal suppval;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess;

	@Expose
	@SerializedName("flag")
	private String flag;

}
