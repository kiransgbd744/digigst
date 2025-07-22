package com.ey.advisory.app.gstr2b;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2BCompleteReportDto {
	
	 private String returnPeriod;
	 private String recipientGSTIN;
	 private String supplierGSTIN;
	 private String supplierName;
	 private String documentType;
	 private String supplyType;
	 private String documentNumber;
	 private String documentDate;
	 private String taxableValue;
	 private String taxRate;
	 private String igstAmount;
	 private String cgstAmount;
	 private String sgstAmount;
	 private String cessAmount;
	 private String invoiceValue;
	 private String pos;
	 private String stateName;
	 private String lineNumber;
	 private String boeRefDateIceGate;
	 private String boeRecvDateGstin;
	 private String portCode;
	 private String billOfEntryNumber;
	 private String billOfEntryDate;
	 private String boeAmendmentFlag;
	 private String originalDocumentNumber;
	 private String originalDocumentDate;
	 private String originalDocumentType;
	 private String originalInvoiceNumber;
	 private String originalInvoiceDate;
	 private String date2bGenerationDate;
	 private String gstrFilingPeriod;
	 private String gstrFilingDate;
	 private String differentialPercentage;
	 private String reverseChargeFlag;
	 private String itcAvailability;
	 private String reasonForItcAvailability;
	 private String sourceTypeofIRN;
	 private String irnNumber;
	 private String irnGenerationDate;
	 private String tableType;
	 private String remarks;
	 private String imsStatus;
	 private String itcRedReq;
	 private String declaredIgst;
	 private String declaredCgst;
	 private String declaredSgst;
	 private String declaredCess;

}
