/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 * 
 *
 */

@Data
public class PaymentReferenceReqDto {

	@Expose
	@SerializedName("actionType")
	private String actionType = "";

	@Expose
	@SerializedName("customerGSTIN")
	private String customerGSTIN = "";

	@Expose
	@SerializedName("supplierGSTIN")
	private String supplierGSTIN = "";

	@Expose
	@SerializedName("supplierName")
	private String supplierName = "";

	@Expose
	@SerializedName("supplierCode")
	private String supplierCode = "";

	@Expose
	@SerializedName("documentType")
	private String documentType = "";

	@Expose
	@SerializedName("documentNumber")
	private String documentNumber = "";

	@Expose
	@SerializedName("documentDate")
	private String documentDate = "";

	@Expose
	@SerializedName("invoiceValue")
	private String invoiceValue = "";

	@Expose
	@SerializedName("statuDedApplicabl")
	private String statutoryDeductionsApplicable = "";

	@Expose
	@SerializedName("statuDedAmount")
	private String statutoryDeductionAmount = "";

	@Expose
	@SerializedName("anyOthDedAmount")
	private String anyOtherDeductionAmount = "";

	@Expose
	@SerializedName("remarksforDed")
	private String remarksforDeductions = "";

	@Expose
	@SerializedName("dueDateofPayment")
	private String dueDateofPayment = "";

	@Expose
	@SerializedName("payReferenceNo")
	private String paymentReferenceNumber = "";

	@Expose
	@SerializedName("payReferenceDate")
	private String paymentReferenceDate = "";

	@Expose
	@SerializedName("payDescription")
	private String paymentDescription = "";

	@Expose
	@SerializedName("paymentStatus")
	private String paymentStatus = "";

	@Expose
	@SerializedName("paidAmttoSupplier")
	private String paidAmounttoSupplier = "";

	@Expose
	@SerializedName("currencyCode")
	private String currencyCode = "";

	@Expose
	@SerializedName("exchangeRate")
	private String exchangeRate = "";

	@Expose
	@SerializedName("unpaidAmttoSuplr")
	private String unpaidAmounttoSupplier = "";

	@Expose
	@SerializedName("postingDate")
	private String postingDate = "";

	@Expose
	@SerializedName("plantCode")
	private String plantCode = "";

	@Expose
	@SerializedName("profitCentre")
	private String profitCentre = "";

	@Expose
	@SerializedName("division")
	private String division = "";

	@Expose
	@SerializedName("userDefinField1")
	private String userDefinedField1 = "";

	@Expose
	@SerializedName("userDefinField2")
	private String userDefinedField2 = "";

	@Expose
	@SerializedName("userDefinField3")
	private String userDefinedField3 = "";

}
