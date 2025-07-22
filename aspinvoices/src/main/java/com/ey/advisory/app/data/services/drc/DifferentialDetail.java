package com.ey.advisory.app.data.services.drc;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DifferentialDetail {

	@SerializedName("drc03arn")
	@Expose
	private String drc03arn;

	@SerializedName("challanNo")
	@Expose
	private String challanNo;

	@SerializedName("challanDate")
	@Expose
	private String challanDate;

	@SerializedName("igst")
	@Expose
	private BigDecimal igst;

	@SerializedName("cgst")
	@Expose
	private BigDecimal cgst;

	@SerializedName("sgst")
	@Expose
	private BigDecimal sgst;

	@SerializedName("cess")
	@Expose
	private BigDecimal cess;

	@SerializedName("head")
	@Expose
	private DifferentialHeadDetail head;

}
