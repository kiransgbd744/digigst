package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

/**
 * 
 * @author Sasidhar
 *
 */
@Data
public class TDSDetailedRespDto {
	
	private String actionSavedatDigiGST;
	private String digiGstRemarks;
	private String digiGstComment;
	private String gstin;
	private String type;
	private String taxPeriod;
	private String month;
	private String gstinOfDeductor;
	private String deductorName;
	private String docNo;
	private String docDate;
	private String orgMonth;
	private String orgDocNo;
	private String orgDocDate;
	private String suppliesCollected;
	private String suppliesReturned;
	private String netSupplies;
	private String iGST;
	private String cGST;
	private String sGST;
	private String invoiceValue;
	private String orgTaxableValue;
	private String orgInvoiceValue;
	private String pos;
	private String chkSum;
	private String actionSavedatGSTN;
	private String gstnRemarks;
	private String gstnComment;
}
