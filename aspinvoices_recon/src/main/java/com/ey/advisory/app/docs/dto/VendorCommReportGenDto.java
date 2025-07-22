package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VendorCommReportGenDto {

	@SerializedName("reason")
	private String mismatchReason;
	
	@SerializedName("reportType")
	private String reportType;
	
	@SerializedName("taxPeriod2A")
	private String taxPeriod2A;
	
	@SerializedName("taxPeriodPR")
	private String taxPeriodPR;
	
	@SerializedName("calendarMonth")
	private String calenderMonth;
	
	@SerializedName("supplierName2A")
	private String supplierName2A;
	
	@SerializedName("supplierNamePR")
	private String supplierNamePR;
	
	@SerializedName("supplierGSTIN2A")
	private String supplierGStin2A;
	
	@SerializedName("supplierGSTINPR")
	private String supplierGStin2R;
	
	@SerializedName("recipientGSTIN2A")
	private String recipientGstin2A;
	
	@SerializedName("recipientGSTINPR")
	private String recipientGstinPR;
	
	@SerializedName("docType2A")
	private String docType2A;
	
	@SerializedName("docTypePR")
	private String docTypePR;
	
	@SerializedName("documentNumber2A")
	private String documentNumber2A;
	
	@SerializedName("documentNumberPR")
	private String documentNumberPR;
	
	@SerializedName("date2A")
	private String date2A;
	
	@SerializedName("datePR")
	private String datePR;
	
	@SerializedName("gstr2APercent")
	private String GSTPercentage2A;
	
	@SerializedName("gstPRPercent")
	private String GSTPercentagePR;
	
	@SerializedName("taxable2A")
	private String taxable2A;
	
	@SerializedName("taxablePR")
	private String taxablePR;
	
	@SerializedName("igst2A")
	private String igst2A;
	
	@SerializedName("igstPR")
	private String igstPR;
	
	@SerializedName("cgst2A")
	private String cgst2A;
	
	@SerializedName("cgstPR")
	private String cgstPR;
	
	@SerializedName("sgst2A")
	private String sgst2A;
	
	@SerializedName("sgstPR")
	private String sgstPR;
	
	@SerializedName("cess2A")
	private String cess2A;
	
	@SerializedName("cessPR")
	private String cessPR;
	
	@SerializedName("totalTax2A")
	private String totalTax2A;
	
	@SerializedName("totalTaxPR")
	private String totalTaxPR;
	
	@SerializedName("invoiceValue2A")
	private String invoiceValue2A;
	
	@SerializedName("invoiceValuePR")
	private String invoiceValuePR;
	
	@SerializedName("pos2A")
	private String pos2A;
	
	@SerializedName("posPR")
	private String posPR;
	
	@SerializedName("cfsFlag")
	private String cfsFlag;
	
	@SerializedName("reverseChargeFlag2A")
	private String reverseChargeFlag2A;
	
	@SerializedName("reverseChargeFlagPR")
	private String reverseChargeFlagPR;
	
	@SerializedName("orgDocNumberPR")
	private String orgdocNumberPR;
	
	@SerializedName("orgDocNumber2A")
	private String orgdocNumber2A;
	
	@SerializedName("orgDocDatePR")
	private String orgDatePR;
	
	@SerializedName("orgDocDate2A")
	private String orgDate2A;
	
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
	private String gstr3bFilingStatus;
	
	@SerializedName("gstr3bFilingDate")
	private String gstr3bFilingDate;
	
	@SerializedName("cancellationDate")
	private String suppGstinCancelDate;

}
