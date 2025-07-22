package com.ey.advisory.app.asprecon.gstr2.recon.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2ReconResultDto {

	private String gstin;

	private String vendorGstin;

	private String docTypePR;

	private String docType2A;

	private boolean isDocTypeMatch;

	private String docNumberPR;

	private String docNumber2A;

	private boolean isDocNumberMatch;

	private String docDatePR;

	private String docDate2A;

	private boolean isDocDateMatch;

	private String totalTaxPR;

	private String totalTax2A;

	private boolean isTotalTaxMatch;

	private String igstPR;

	private String igst2A;

	private boolean isIgstMatch;

	private String cgstPR;

	private String cgst2A;

	private boolean isCgstMatch;

	private String sgstPR;

	private String sgst2A;

	private boolean isSgstMatch;

	private String cessPR;

	private String cess2A;

	private boolean isCessMatch;

	private String paymentStatus;

	private Long a2Id;

	private Long prId;

	private String mismatchReason;

	private String responseTaken;

	private String respRemarks;

	private String reportType;

	private Long reconLinkId;

	private String gstr3BTaxPeriod;

	private String pos2A;

	private String posPR;

	private String taxableValue2A;

	private String taxablevaluePR;

	private String avalIgst = "0.00";

	private String avalCgst = "0.00";

	private String avalSgst = "0.00";

	private String avalCess = "0.00";

	private String optionOpted;
	
	private String itcReversal; 
	
	private String batchId;
	
	private String gstin2A;

	private String vendorGstin2A;

	private String accVoucherNo;
	
	private String boeNo2A;
	
	private String boeNoPR;
	
	private boolean isBoeNoMatch;
	
	private String boeDate2A;
	
	private String boeDatePR;
	
	private boolean isBoeDateMatch;
	
	private String imsActionGstn="";
	
	private String imsActionDigiGst="";
	
	private String imsUniqId="";
	
}
