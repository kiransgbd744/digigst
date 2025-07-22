package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2APAndNonAPReconSummaryDao {

	List<Gstr2ReconSummaryDto> findReconSummary(Long configId,
			List<String> gstins, Integer toTaxPeriod, Integer fromTaxPeriod,
			String reconType, Integer toTaxPeriod_2A, Integer fromTaxPeriod_2A,
			String criteria);

}
