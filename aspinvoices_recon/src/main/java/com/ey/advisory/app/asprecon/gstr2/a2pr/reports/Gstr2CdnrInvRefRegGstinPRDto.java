package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2CdnrInvRefRegGstinPRDto {
	
	private String taxPeriodCR;
	private String taxPeriodINV;
	private String calenderMonthCR;
	private String calenderMonthINV;
	private String reportTypeCR;
	private String ReportTypeINV;
	private String errors;
	private String supplierNameINV;
	private String supplierGSTINCR;
	private String supplierGSTININV;
	private String recipientGSTINCR;
	private String recipientGSTININV;
	private String docTypeCR;
	private String docTypeINV;
	private String docNoCR;
	private String docNoINV;
	private String docDateCR;
	private String docDateINV;
	private String gSTPerCR;
	private String gSTPerINV;
	private String taxableValueCR;
	private String taxableValueINV;
	private String iGSTCR;
	private String iGSTINV;
	private String cGSTCR;
	private String cGSTINV;
	private String sGSTCR;
	private String sGSTINV;
	private String cESSCR;
	private String cESSINV;
	private String invoiceValueCR;
	private String invoiceValueINV;
	private String pOSCR;
	private String pOSINV;
	private String rCMCR;
	private String rCMINV;
	private String cFSFlagCR;
	private String cFSFlagINV;
	private String tableTypeCR;
	private String tableTypeINV;
	private String cRDRPreGSTCR;
	private String availableIGSTCR;
	private String availableIGSTINV;
	private String availableCGSTCR;
	private String availableCGSTINV;
	private String availableSGSTCR;
	private String availableSGSTINV;
	private String availableCESSCR;
	private String availableCESSINV;
	private String supplyTypeCR;
	private String supplyTypeINV;
	private String requestIDCR;
	private String requestIDINV;
	private String invoiceKeyCR;
	private String invoiceKeyINV;
	private String referenceIDCR;
	private String referenceIDINV;

}
