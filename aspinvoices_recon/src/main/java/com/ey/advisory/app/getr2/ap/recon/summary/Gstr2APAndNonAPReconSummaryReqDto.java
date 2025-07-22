package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr2APAndNonAPReconSummaryReqDto {

	private Long configId;
	
	private List<String> gstins;

	private String fromTaxPeriod;
	
	private String toTaxPeriod;
	
	private String reconType;
	
	private Long entityId;
	
	private String fromTaxPeriod_2A;
	
	private String toTaxPeriod_2A;
	
	private String criteria;

}
