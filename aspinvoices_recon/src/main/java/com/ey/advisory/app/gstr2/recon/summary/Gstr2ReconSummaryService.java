package com.ey.advisory.app.gstr2.recon.summary;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2ReconSummaryService {

	public Gstr2ReconSummaryMasterDto getReconSummary(Long configId,
			List<String> gstins, List<String> returnPeriod, String reconType );

}