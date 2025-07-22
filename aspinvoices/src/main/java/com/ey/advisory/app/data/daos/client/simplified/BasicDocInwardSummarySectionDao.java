package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface BasicDocInwardSummarySectionDao {
	
	public abstract List<Annexure1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);


}
