/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter
public class EWB3WayInitiateReconDropOutRecordsDto {

	private String ewbNo;
	private String ewbDate;
	private String supplyType;
	private String docNo;
	private String docDate;
	private String otherPartyGstin;
	private String transporterDetails;
	private String fromGstinInfo;
	private String toGstinInfo;
	private String status;
	private String noOfItems;
	private String hsnCode;
	private String hsnDescription;
	private String assessableValue;
	private String sgstValue;
	private String cgstValue;
	private String igstValue;
	private String cessValue;
	private String cessNonAdvolValue;
	private String otherValue;
	private String totalInvoiceValue;
	private String validTillDate;
	private String modeOfGeneration;
	private String cancelledBy;
	private String cancelledDate;
	private String modeOfDataProcessing;
	
	
}
