package com.ey.advisory.app.docs.dto.anx1a;

import java.util.ArrayList;
import java.util.List;

public class GetGstr2aReviewSummaryRespTableDto {
	private List<GetGstr2aReviewSummaryRespDto> summaryResponse = new ArrayList<>();

	public List<GetGstr2aReviewSummaryRespDto> getSummaryResponse() {
		return summaryResponse;
	}

	public void setSummaryResponse(
			List<GetGstr2aReviewSummaryRespDto> summaryResponse) {
		this.summaryResponse = summaryResponse;
	}

}
