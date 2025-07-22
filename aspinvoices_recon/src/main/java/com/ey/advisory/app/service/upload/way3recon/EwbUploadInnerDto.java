/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class EwbUploadInnerDto {

	private Long fileId;
	private String docKey;
	private String docType;
	private String supplierGstin;
	private boolean isPsd;
	private String errorCode;
	private String errorDesc;
	
	private String ewbNo;
	private String ewbDate;
	private String supplyType;
	private String docNum;
	private String documentDate;
	private String othPartyGstin;
	private String transporterDetails;
	private String fromGstinInfo;
	private String toGstinInfo;
	private String status;
	private String noOfItems;
	private String hsnMain;
	private String hsnMainDesc;
	private String assessableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String cessNonAdvl;
	private String otherValue;
	private String totalInvoiceValue;
	private String validTillDate;
	private String modeOfGeneration;
	private String cancelledBy;
	private String cancelledDate;
	
}
