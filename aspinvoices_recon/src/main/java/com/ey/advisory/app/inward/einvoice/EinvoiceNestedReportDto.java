/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter	
public class EinvoiceNestedReportDto {

	private String irnGenerationPeriod;
	private String irnNumber;
	private String irnDate;
	private String irnStatus;
	private String supplierGSTIN;
	private String documentNumber;
	private String documentType;
	private String documentDate;
	private String customerGSTIN;
	private String preceedingInvoiceNumber;
	private String preceedingInvoiceDate;
	private String otherReference;
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
}
