package com.ey.advisory.app.gstr1.einv;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostReconSummaryDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("section")
	private String section;

	@Expose
	@SerializedName("digigstStatus")
	private String digigstStatus;

	@Expose
	@SerializedName("gstnStatus")
	private String gstnStatus;

	@Expose
	@SerializedName("einvcount")
	private Long einvcount = 0L;
	@Expose
	@SerializedName("einvpercentage")
	private BigDecimal einvpercentage = BigDecimal.ZERO;
	@Expose
	@SerializedName("einvtaxablevalue")
	private BigDecimal einvtaxablevalue = BigDecimal.ZERO;

	@Expose
	@SerializedName("einvtotaltax")
	private BigDecimal einvtotaltax = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("salesRegcount")
	private Long salesRegcount = 0L;

	@Expose
	@SerializedName("salesRegpercentage")
	private BigDecimal salesRegpercentage = BigDecimal.ZERO;

	@Expose
	@SerializedName("salesRegtaxablevalue")
	private BigDecimal salesRegtaxablevalue = BigDecimal.ZERO;

	@Expose
	@SerializedName("salesRegtotaltax")
	private BigDecimal salesRegtotaltax = BigDecimal.ZERO;

	@Expose
	@SerializedName("level")
	private String level = "L2";

	@Expose
	@SerializedName("orderPosition")
	private String orderPosition;
	
	@Expose
	@SerializedName("particulars")
	private String particulars;

}
