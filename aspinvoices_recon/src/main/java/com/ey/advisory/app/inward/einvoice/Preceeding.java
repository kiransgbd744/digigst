/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Preceeding {

	private String documentType;

	private String supplierGSTIN;

	private String documentNumber;

	private String documentDate;

	private String IRN;

	private String precedingInvoiceNo;

	private String precedingInvoiceDt;

	private String otherReference;
	

	//non preceeding elements 
	private String receiptAdviceReference;

	private String receiptAdviceDate;

	private String tenderReference;

	private String contractReference;

	private String externalReference;

	private String projectReference;

	private String customerPOReferenceNumber;

	private String customerPOReferenceDate;

	private String supportingDocURL;

	private String supportingDocument;

	private String additionalInformation;

	private String attributeName;

	private String attributeValue;
	
	private String lineNumber;

}





