package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryMasterDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2APAndNonAPReconSummaryService {

	public Gstr2ReconSummaryMasterDto getReconSummary(Long configId,
			List<String> gstins, String toTaxPeriod, String fromTaxPeriod, 
			String reconType, String toTaxPeriod_A2, String fromTaxPeriod_A2, String criteria);

}