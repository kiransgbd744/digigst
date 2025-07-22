package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table17ItemsReqDto {
	
	@SerializedName("desc")
	@Expose
	private String desc;
	/**
	 * Flag to identify concession
	 *
	 */
	@SerializedName("isconcesstional")
	@Expose
	private String isconcesstional ="";
	/**
	 * HSN Code
	 *
	 */
	@SerializedName("hsn_sc")
	@Expose
	private String hsnSc ="";
	/**
	 * UQC
	 *
	 */
	@SerializedName("uqc")
	@Expose
	private String uqc;
	/**
	 * Total Quantity
	 *
	 */
	@SerializedName("qty")
	@Expose
	private BigDecimal qty;
	/**
	 * Taxable Value
	 *
	 */
	@SerializedName("txval")
	@Expose
	private BigDecimal txval = new BigDecimal("0.00");
	/**
	 * Rate of Tax
	 *
	 */
	@SerializedName("rt")
	@Expose
	private BigDecimal rt = new BigDecimal("0.00");
	/**
	 * Integrated Tax
	 *
	 */
	@SerializedName("iamt")
	@Expose
	private BigDecimal iamt = new BigDecimal("0.00");
	/**
	 * Central Tax
	 *
	 */
	@SerializedName("camt")
	@Expose
	private BigDecimal camt = new BigDecimal("0.00");
	/**
	 * State/UT Tax
	 *
	 */
	@SerializedName("samt")
	@Expose
	private BigDecimal samt = new BigDecimal("0.00");
	/**
	 * Cess
	 *
	 */
	@SerializedName("csamt")
	@Expose
	private BigDecimal csamt = new BigDecimal("0.00");

}
