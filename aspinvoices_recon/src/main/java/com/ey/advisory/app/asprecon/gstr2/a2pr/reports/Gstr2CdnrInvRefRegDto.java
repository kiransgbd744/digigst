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
public class Gstr2CdnrInvRefRegDto {

	private String taxPeriodCR;
	private String taxPeriodNV;
	private String reportTypeCR;
	private String reportTypeINV;
	private String errors;
	private String SupplierNameINV;
	private String supplierGSTINCR;
	private String supplierGSTININV;
	private String recipientGSTINCR;
	private String recipientGSTININV;
	private String docTYPECR;
	private String docTYPEINV;
	private String docNoCR;
	private String docNoINV;
	private String docDateCR;
	private String docDateINV;
	private String gstPercentCR;
	private String gstPercentINV;
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
	private String requestIDCR;
	private String requestIDINV;
	private String invoiceKeyCR;
	private String invoiceKeyINV;
	private String referenceIDCR;
	private String referenceIDINV;

}
