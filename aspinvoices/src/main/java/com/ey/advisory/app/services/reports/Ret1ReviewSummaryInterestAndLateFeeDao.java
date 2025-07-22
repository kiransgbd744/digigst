package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Ret1ReviewSummaryInterestAndLateFeeDao {
	List<Ret1SummaryRespDto> getRet1ReviewSummScreenDownload(
			Annexure1SummaryReqDto request);
}
