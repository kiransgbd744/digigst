package com.ey.advisory.app.gstr1.einv;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */


@Data
public class Gstr1EinvInitiateReconReportDto {

	private String response;

	private String previousResponse;

	private String remarks;

	private String mismatchReason;

	private String scoreOutof11;

	private String reportType;

	private String previousReport;

	private String subCategory;
	
	private String reasonForMismatch;

	private String taxPeriodGSTN;

	private String taxPeriodDigiGST;

	private String calenderMonthDigiGST;

	private String supplierGSTINGSTN;

	private String supplierGSTINDigiGST;

	private String recipientGSTINGSTN;

	private String recipientGSTINDigiGST;

	private String recipientNameGSTN;

	private String recipientNameDigiGST;

	private String docTypeGSTN;

	private String docTypeDigiGST;

	private String supplyTypeGSTN;

	private String supplyTypeDigiGST;

	private String documentNumberGSTN;

	private String documentNumberDigiGST;

	private String documentDateGSTN;

	private String documentDateDigiGST;

	private String taxableValueGSTN;

	private String taxableValueDigiGST;

	private String igstGSTN;

	private String igstDigiGST;

	private String cgstGSTN;

	private String cgstDigiGST;

	private String sgstGSTN;

	private String sgstDigiGST;

	private String cessGSTN;

	private String cessDigiGST;

	private String totalTaxGSTN;

	private String totalTaxDigiGST;

	private String invoiceValueGSTN;

	private String invoiceValueDigiGST;

	private String posGSTN;

	private String posDigiGST;

	private String reverseChargeFlagGSTN;

	private String reverseChargeFlagDigiGST;

	private String ecomGSTINGSTN;

	private String ecomGSTINDigiGST;

	private String portCodeGSTN;

	private String portCodeDigiGST;

	private String shippingBillNumberGSTN;

	private String shippingBillNumberDigiGST;

	private String shippingBillDateGSTN;

	private String shippingBillDateDigiGST;

	private String sourceTypeGSTN;

	private String irnGSTN;

	private String irnDigiGST;

	private String irnGenDateGSTN;

	private String irnGenDateDigiGST;

	private String eInvoiceStatus;

	private String autoDraftstatus;

	private String autoDraftedDate;

	private String errorCode;

	private String errorMessage;

	private String tableTypeGSTN;

	private String tableTypeDigiGST;

	private String customerType;

	private String customerCode;

	private String accountingVoucherNumber;

	private String accountingVoucherDate;

	private String companyCode;

	private String recordStatusDigiGST;

	private String eInvoiceGetCallDate;

	private String eInvoiceGetCallTime;

	private String reconID;

	private String reconDate;

	private String reconTime;

	private String docHeaderId;

	private String getCallId;

	private String docKeyDigiGST;

	private String docKeyEINV;

	private String reportCategory;
	
	//added new colums
	private String plantCode;
	
	private String division;
	
	private String subDivision;
	
	private String location;
	
	private String profitCentre1;
	
	private String profitCentre2;
	
	private String profitCentre3;

}
