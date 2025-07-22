/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReconResponseDto {
	
	
	 private String forceMatchResponse;
	 private String taxPeriodforGSTR3B;
	 private String comments;
	 
	 private String recipientGstin2A;
	 private String recipientGstinPR;
	 private String supplierGstin2A;
	 private String supplierGstinPR;
	 
	 private String docType2A;
	 private String docTypePR;
	 private String documentNumber2A;
	 private String documentNumberPR;
	 private String documentDate2A;
	 private String documentDatePR;
	 
	 private String cGST2A;
	 private String cGSTPR;
	 private String sGST2A;
	 private String sGSTPR;
	 private String cess2A;
	 private String cessPR;
	
	 
	 private String taxableValue2A;
	 private String taxableValuePR;
	 private String iGST2A;
	 private String iGSTPR;
	 
	 private String InvoiceKeyPR;
	 private String InvoiceKeyA2;
	 private String iDPR;
	 private String iD2A;
	 private String gstr1FillingStatus;
	 
	 private String availableIGST;
	 private String availableCGST;
	 private String availableSGST;
	 private String availableCESS;

	 private String taxPeriod2A;
	 private String taxPeriodPR;
	 
	 private String tableType2A;
	 
	 private String configId;
	 
	 private String userResponse;
	 private String suggestedResponse;
	 
	 private String reversChargeRegisgter;

}
