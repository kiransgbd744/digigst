package com.ey.advisory.app.services.search.anx2;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchResDto;

public interface Anx2ReconResultsAbsoluteMatchService {

	List<Anx2ReconResultsAbsoluteMatchResDto> fetchAbsoluteDetails(
			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest);
	
	List<Anx2ReconResultsAbsoluteMatchResDto> fetchMismatchDetails(
			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest);
	
	List<Anx2ReconResultsAbsoluteMatchResDto> fetchPotentialMatchDetails(
			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest);
}
