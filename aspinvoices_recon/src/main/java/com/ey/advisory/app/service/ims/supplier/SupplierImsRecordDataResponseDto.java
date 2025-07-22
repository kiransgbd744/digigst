package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsRecordDataResponseDto {

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
	@SerializedName("returnPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("returnType")
	private String returnType;
	
	/*@Expose
	@SerializedName("actionDigiGst")
	private String actionDigiGst;*/

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

	/*@Expose
	@SerializedName("pos")
	private String pos;*/

	@Expose
	@SerializedName("supplierName")
	private String supplierLegalName;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;
	
	@Expose
	@SerializedName("recipientName")
	private String recipientName;

	/*@Expose
	@SerializedName("formType")
	private String formType;*/

	@Expose
	@SerializedName("gstr1FillingStatus")
	private String gstr1FillingStatus;
	
	@Expose
	@SerializedName("gstrRecipient3BStatus")
	private String gstrRecipient3BStatus;

	@Expose
	@SerializedName("estimatedGstr3BPeriod")
	private String estimatedGstr3BPeriod;
	
	@Expose
	@SerializedName("gstr1FillingPeriod")
	private String gstr1FillingPeriod;

	@Expose
	@SerializedName("orgDocNumber")
	private String orgDocNumber;

	@Expose
	@SerializedName("orgDocDate")
	private String orgDocDate;

	/*@Expose
	@SerializedName("pendingActionBlocked")
	private String pendingActionBlocked;*/

	@Expose
	@SerializedName("checkSum")
	private String checkSum;

	/*@Expose
	@SerializedName("getCallDateTime")
	private String getCallDateTime;*/

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
/*
	@Expose
	@SerializedName("actionDigiGstTimestamp")
	private String actionDigiGstTimestamp;

	@Expose
	@SerializedName("actionGstnTimestamp")
	private String actionGstnTimestamp;*/

}
