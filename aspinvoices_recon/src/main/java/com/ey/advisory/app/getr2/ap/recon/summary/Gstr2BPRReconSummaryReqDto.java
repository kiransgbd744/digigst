package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class Gstr2BPRReconSummaryReqDto {

	private Long configId;
	
	private List<String> gstins;

	private String fromTaxPeriod_PR;
	
	private String toTaxPeriod_PR;
	
	private String reconType;
	
	private Long entityId;
	
	private String fromTaxPeriod_2B;
	
	private String toTaxPeriod_2B;
	
	private String criteria;

}
