/**
 * 
 */
package com.ey.advisory.app.docs.dto.ret;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class RetItemDetailsDto {

	/*
	 * @Expose
	 * 
	 * @SerializedName("rt") private BigDecimal rate;
	 */

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("igst")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("cess")
	private BigDecimal cessAmount;

	@Expose
	@SerializedName("trancd")
	private BigDecimal trancd;

	@Expose
	@SerializedName("latefee")
	private LateFee lateFee;

	@Expose
	@SerializedName("trans_desc")
	private String transDesc;

	@Expose
	@SerializedName("paymenttax")
	private RetPaymentTaxDto retPaymentTaxDto;

	@Expose
	@SerializedName("tx_paid")
	private RetTaxPaidItemDetailsDto taxPaid;

	@Expose
	@SerializedName("intalert")
	private RetIntAlertDto retIntAlertDto;

}
