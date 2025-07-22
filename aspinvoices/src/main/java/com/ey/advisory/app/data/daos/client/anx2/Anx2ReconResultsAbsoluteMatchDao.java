package com.ey.advisory.app.data.daos.client.anx2;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchResDto;

public interface Anx2ReconResultsAbsoluteMatchDao {

	List<Anx2ReconResultsAbsoluteMatchResDto> fetchAbsoluteDetails(List<String> sgstins,
			String retPeriod);
	
	List<Anx2ReconResultsAbsoluteMatchResDto> fetchMismatchDetails(List<String> sgstins,
			String retPeriod);
	
	List<Anx2ReconResultsAbsoluteMatchResDto> fetchPotentialMatchDetails(List<String> sgstins,
			String retPeriod);
}
