package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AutoReconSummaryGrandTotalDto {

	@Expose
	private String reportCategory;

	@Expose
	private String reportType;

	@Expose
	private Long prCount = 0L;

	@Expose
	private String prTotalTax;

	@Expose
	private String prTotalTaxPer;

	@Expose
	private String prIgst;

	@Expose
	private String prCgst;

	@Expose
	private String prSgst;

	@Expose
	private String prCess;

	@Expose
	private Long a2Count = 0L;

	@Expose
	private String a2TotalTax;

	@Expose
	private String a2TotalTaxPer;

	@Expose
	private String a2Igst;

	@Expose
	private String a2Cgst;

	@Expose
	private String a2Sgst;

	@Expose
	private String a2Cess;

	@Expose
	private String level = "L2";

	@Expose
	private String orderPosition;

	@Expose
	private String lockStatus;
}
