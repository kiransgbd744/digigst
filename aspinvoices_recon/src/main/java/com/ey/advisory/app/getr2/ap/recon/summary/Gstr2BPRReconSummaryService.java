package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryMasterDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr2BPRReconSummaryService {

	public Gstr2ReconSummaryMasterDto getReconSummary(Long configId,
			List<String> gstins, String toTaxPeriod, String fromTaxPeriod, 
			String reconType, String toTaxPeriod_2B, String fromTaxPeriod_2B, String criteria);

}