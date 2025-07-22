package com.ey.advisory.app.getr2.ap.recon.summary;

import java.util.List;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr2BPRReconSummaryDao {

	List<Gstr2ReconSummaryDto> findReconSummary(Long configId,
			List<String> gstins, Integer toTaxPeriod, Integer fromTaxPeriod,
			String reconType, Integer toTaxPeriod_2B, Integer fromTaxPeriod_2B,
			String criteria);

}
