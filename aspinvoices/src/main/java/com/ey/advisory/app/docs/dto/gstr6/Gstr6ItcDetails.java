package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6ItcDetails {

	@Expose
	@SerializedName("des")
	private String des;

	@Expose
	@SerializedName("goLiveFlag")
	private String goLiveFlag;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("camt")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;

	@Expose
	@SerializedName("iamtc")
	private BigDecimal iamtc;

	@Expose
	@SerializedName("iamts")
	private BigDecimal iamts;

	@Expose
	@SerializedName("camti")
	private BigDecimal camti;

	@Expose
	@SerializedName("samti")
	private BigDecimal samti;

	

	@Expose
	@SerializedName("cess")
	private BigDecimal cess;

	@Expose
	@SerializedName("samts")
	private BigDecimal samts;

	@Expose
	@SerializedName("iamti")
	private BigDecimal iamti;

	@Expose
	@SerializedName("camtc")
	private BigDecimal camtc;

	

}
