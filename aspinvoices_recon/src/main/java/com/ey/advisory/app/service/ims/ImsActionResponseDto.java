/**
 * 
 */
package com.ey.advisory.app.service.ims;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ImsActionResponseDto {

	private String actionResponse;
	private String responseRemarks;
	private String actionGSTN;
	private String actionDigiGST;
	private String actionDigiGSTDateTime;
	private String savedToGSTN;
	private String tableType;
	private String recipientGSTIN;
	private String supplierGSTIN;
	private String supplierLegalName;
	private String supplierTradeName;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String totalTax;
	private String invoiceValue;
	private String pos;
	private String formType;
	private String gstr1FilingStatus;
	private String gstr1FilingPeriod;
	private String originalDocumentNumber;
	private String originalDocumentDate;
	private String pendingActionBlocked;
	private String checksum;
	private String getCallDateTime;
	private String imsUniqueID;
	private String docKey;
	private Long fileId;
	private boolean isPsd;
	private String errorDesc;
	private String availableInIms;
	private String tableTypeDerived;
	
	//V1.1
	private String itcRedReq;
	private String declaredIgst;
	private String declaredCgst;
	private String declaredSgst;
	private String declaredCess;

}
