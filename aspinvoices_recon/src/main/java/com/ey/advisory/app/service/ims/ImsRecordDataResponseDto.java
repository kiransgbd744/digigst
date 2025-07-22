package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Vishal.Verma
 *
 */

@Data
public class ImsRecordDataResponseDto {

	@Expose
	@SerializedName("gstinRecipient")
	public String gstinRecipient;

	@Expose
	@SerializedName("gstinSupplier")
	public String gstinSupplier;

	@Expose
	@SerializedName("actionGstn")
	private String actionGstn;

	@Expose
	@SerializedName("actionDigiGst")
	private String actionDigiGst;

	@Expose
	@SerializedName("tableType")
	public String tableType;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docNumber")
	private String docNumber;

	@Expose
	@SerializedName("imsUniqueId")
	private String imsUniqueId;

	@Expose
	@SerializedName("docDate")
	private String docDate;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("supplierLegalName")
	private String supplierLegalName;

	@Expose
	@SerializedName("supplierTradeName")
	private String supplierTradeName;

	@Expose
	@SerializedName("formType")
	private String formType;

	@Expose
	@SerializedName("gstr1FillingStatus")
	private String gstr1FillingStatus;

	@Expose
	@SerializedName("gstr1FillingPeriod")
	private String gstr1FillingPeriod;

	@Expose
	@SerializedName("orgDocNumber")
	private String orgDocNumber;

	@Expose
	@SerializedName("orgDocDate")
	private String orgDocDate;

	@Expose
	@SerializedName("pendingActionBlocked")
	private String pendingActionBlocked;

	@Expose
	@SerializedName("checkSum")
	private String checkSum;

	@Expose
	@SerializedName("getCallDateTime")
	private String getCallDateTime;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalTax")
	private BigDecimal totalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue = BigDecimal.ZERO;

	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("docKey")
	private String dockey;
	
	private Integer totalCount = 0;
	
	@Expose
	@SerializedName("actionDigiGstTimestamp")
	private String actionDigiGstTimestamp;
	
	@Expose
	@SerializedName("actionGstnTimestamp")
	private String actionGstnTimestamp;
	
	@Expose
	@SerializedName("itcReductionRequired")
	private String itcReductionRequired;
	
	@Expose
	@SerializedName("igstDeclToReduceItc")
	private BigDecimal igstDeclToReduceItc = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cgstDeclToReduceItc")
	private BigDecimal cgstDeclToReduceItc = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("sgstDeclToReduceItc")
	private BigDecimal sgstDeclToReduceItc = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cessDeclToReduceItc")
	private BigDecimal cessDeclToReduceItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("imsResponseRemarks")
	private String imsResponseRemarks;
}
