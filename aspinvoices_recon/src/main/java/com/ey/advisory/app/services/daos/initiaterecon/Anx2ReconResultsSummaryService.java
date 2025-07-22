package com.ey.advisory.app.services.daos.initiaterecon;

import java.util.List;

import com.ey.advisory.app.docs.dto.EntityIRDto;

public interface Anx2ReconResultsSummaryService {
	
	List<Anx2ReconResultsSummaryResDto> absoluteMatchSummary(EntityIRDto dto);

	List<Anx2ReconResultsMisMatchSummaryResDto> misMatchSummary(EntityIRDto dto);

	List<Anx2ReconResultsPotentialMatchSummaryResDto> potentialMatchSummary(
			EntityIRDto dto);
	
}

