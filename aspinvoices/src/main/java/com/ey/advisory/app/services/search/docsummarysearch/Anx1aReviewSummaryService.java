package com.ey.advisory.app.services.search.docsummarysearch;

import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Anx1aReviewSummaryService {

	public Anx1aSummaryRespDto getAnx1aReviewSummary(
			Annexure1SummaryReqDto annexure1SummaryRequest);

}
