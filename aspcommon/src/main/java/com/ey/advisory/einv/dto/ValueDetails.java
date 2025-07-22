
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ValueDetails implements Serializable {

	private static final long serialVersionUID = 1356829370851190341L;
	/**
	 * Total Assessable value of all items
	 *
	 */
	@SerializedName("AssVal")
	@Expose
	public BigDecimal assVal;
	/**
	 * Total CGST value of all items
	 *
	 */
	@SerializedName("CgstVal")
	@Expose
	public BigDecimal cgstVal;
	/**
	 * Total SGST value of all items
	 *
	 */
	@SerializedName("SgstVal")
	@Expose
	public BigDecimal sgstVal;
	/**
	 * Total IGST value of all items
	 *
	 */
	@SerializedName("IgstVal")
	@Expose
	public BigDecimal igstVal;
	/**
	 * Total CESS value of all items
	 *
	 */
	@SerializedName("CesVal")
	@Expose
	public BigDecimal cesVal;
	/**
	 * Total State CESS value of all items
	 *
	 */
	@SerializedName("StCesVal")
	@Expose
	public BigDecimal stCesVal;
	/**
	 * Rounded off amount
	 *
	 */
	@SerializedName("RndOffAmt")
	@Expose
	public BigDecimal rndOffAmt;
	/**
	 * Final Invoice value
	 *
	 */
	@SerializedName("TotInvVal")
	@Expose
	public BigDecimal totInvVal;
	/**
	 * Final Invoice value in Additional Currency
	 *
	 */
	@SerializedName("TotInvValFc")
	@Expose
	public BigDecimal totInvValFc;
	
	@SerializedName("Discount")
	@Expose
	public BigDecimal discount;
	
	@SerializedName("OthChrg")
	@Expose
	public BigDecimal othChrg;
	
	

	public static boolean isEmpty(ValueDetails valDetails) {
		ValueDetails valDtls = new ValueDetails();
		return valDetails.hashCode() == valDtls.hashCode();
	}

}
