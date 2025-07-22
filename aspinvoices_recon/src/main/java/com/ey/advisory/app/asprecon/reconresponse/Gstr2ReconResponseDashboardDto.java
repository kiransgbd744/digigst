package com.ey.advisory.app.asprecon.reconresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author sakshi.jain
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2ReconResponseDashboardDto {

	private String gstin;

	private String vendorGstin;

	private String vendorName;
	
	private String reportType;
	
	private String docType;
	
	private String docdate;
	
	private String docNumber;
	
	private String pos;
	
	private String taxablevalue;
	
	private String igst = "0.00";

	private String cgst = "0.00";

	private String sgst = "0.00";

	private String cess = "0.00";

	private String avalIgst = "0.00";

	private String avalCgst = "0.00";

	private String avalSgst = "0.00";

	private String avalCess = "0.00";

	private String totalTax = "0.00";
	
	private String invoiceVale = "0.00";
	
	private String rcmFlag = "N";
	
	private String reconLinkId;
	
	private String returnPeriod;
	
	private String cfs;
	
	private String source;
	
	private String itcReversal;
	
	private Boolean isHideFlag = false;
	
	private String gstin2A;
	
	private String vendrGstin2A;
	
    private String boedate;
	
	private String boeNumber;
	
	

}
