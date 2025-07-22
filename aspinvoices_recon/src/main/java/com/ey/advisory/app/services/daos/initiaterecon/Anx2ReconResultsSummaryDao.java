package com.ey.advisory.app.services.daos.initiaterecon;

import java.util.List;

public interface Anx2ReconResultsSummaryDao {

	List<Anx2ReconResultsSummaryResDto> reconResultsabsoluteMatchSummary(
			List<String> sgstins, String retPeriod);

	List<Anx2ReconResultsMisMatchSummaryResDto> reconResultmisMatchSummary(
			List<String> selectedSgtins, String returnPeriod);

	List<Anx2ReconResultsPotentialMatchSummaryResDto> reconResultpotentialMatchSummary(
			List<String> selectedSgtins, String returnPeriod);
}
