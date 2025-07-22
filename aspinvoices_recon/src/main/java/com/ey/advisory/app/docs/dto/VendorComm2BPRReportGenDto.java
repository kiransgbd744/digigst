package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.SerializedName;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VendorComm2BPRReportGenDto {
	
	@SerializedName("reason")
	private String mismatchReason;
	
	private String reportCategory;
	
	@SerializedName("reportType")
	private String reportType;
	
	@SerializedName("taxPeriod2A")
	private String taxPeriod2B;
	
	@SerializedName("taxPeriodPR")
	private String taxPeriodPR;
	
	@SerializedName("calendarMonth")
	private String calenderMonth;
	
	@SerializedName("recipientGSTIN2A")
	private String recipientGstin2B;
	
	@SerializedName("recipientGSTINPR")
	private String recipientGstinPR;
	
	@SerializedName("supplierGSTIN2A")
	private String supplierGStin2B;
	
	@SerializedName("supplierGSTINPR")
	private String supplierGStinPR;
	
	@SerializedName("supplierName2A")
	private String supplierTradeName2B;
	
	@SerializedName("supplierNamePR")
	private String supplierNamePR;
	
	@SerializedName("docType2A")
	private String docType2B;
	
	@SerializedName("docTypePR")
	private String docTypePR;
	

	@SerializedName("documentNumber2A")
	private String documentNumber2B;
	
	@SerializedName("documentNumberPR")
	private String documentNumberPR;
	
	@SerializedName("date2A")
	private String date2B;
	
	@SerializedName("datePR")
	private String datePR;
	
	@SerializedName("pos2A")
	private String pos2B;
	
	@SerializedName("posPR")
	private String posPR;
	
	@SerializedName("gstr2APercent")
	private String gSTPercentage2B;
	

	@SerializedName("gstPRPercent")
	private String gSTPercentagePR;
	
	@SerializedName("taxable2A")
	private String taxable2B;
	
	@SerializedName("taxablePR")
	private String taxablePR;
	
	@SerializedName("igst2A")
	private String igst2B;
	
	
	@SerializedName("igstPR")
	private String igstPR;
	
	@SerializedName("cgst2A")
	private String cgst2B;
	
	@SerializedName("cgstPR")
	private String cgstPR;
	
	@SerializedName("sgst2A")
	private String sgst2B;
	
	
	@SerializedName("sgstPR")
	private String sgstPR;
	
	@SerializedName("cess2A")
	private String cess2B;
	
	@SerializedName("cessPR")
	private String cessPR;
	
	@SerializedName("totalTax2A")
	private String totalTax2B;
	
	@SerializedName("totalTaxPR")
	private String totalTaxPR;
	
	@SerializedName("invoiceValue2A")
	private String invoiceValue2B;
	
	@SerializedName("invoiceValuePR")
	private String invoiceValuePR;
	
	@SerializedName("reverseChargeFlag2A")
	private String reverseChargeFlag2B;
	
	@SerializedName("reverseChargeFlagPR")
	private String reverseChargeFlagPR;
	
	@SerializedName("orgDocNumber2A")
	private String orgdocNumber2B;
	
	@SerializedName("orgDocNumberPR")
	private String orgdocNumberPR;
	
	@SerializedName("orgDocDate2A")
	private String orgDate2B;
	
	@SerializedName("orgDocDatePR")
	private String orgDatePR;
	
	@SerializedName("supplyTypePR")
	private String supplyTypePR;
	
	@SerializedName("originalSGSTINPR")
	private String originalSgstinPR;
	
	@SerializedName("gstr1FilingStatus")
	private String gstr1FilingStatus;
	
	@SerializedName("gstr1FilingDate")
	private String gstr1FilingDate;
	
	@SerializedName("gstr1FilingPeriod")
	private String gstr1FilingPeriod;
	
	@SerializedName("gstr3bFilingStatus")
	private String gstr3BFilingStatus;
	
	@SerializedName("gstr3bFilingDate")
	private String gstr3BFilingDate;
	
	@SerializedName("cancellationDate")
	private String cancellationDate;
	
	@SerializedName("itcAvailabilityFlag")
	private String itcAvailFlag;
	
	@SerializedName("reasonForUnavailability")
	private String reasnForItcUnvlbilty;

}
